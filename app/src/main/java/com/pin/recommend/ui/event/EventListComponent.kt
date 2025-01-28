package com.pin.recommend.ui.event

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.pin.recommend.domain.entity.Event
import com.pin.recommend.domain.model.DateWithEvents
import com.pin.recommend.ui.character.CharacterDetailsViewModel
import com.pin.recommend.ui.character.CharacterDetailsViewModelState
import com.pin.recommend.ui.component.DeleteDialogFragment
import com.pin.recommend.ui.component.DialogActionListener
import com.pin.recommend.ui.component.composable.MaterialCalendar
import com.pin.recommend.ui.component.composable.Section
import com.pin.recommend.util.TimeUtil
import com.pin.recommend.util.toMdString
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import java.util.Calendar
import java.util.Date

class EventDecorator(private val color: Int, dates: Collection<CalendarDay?>?) : DayViewDecorator {
    private val dates: HashSet<CalendarDay?> = HashSet(dates)
    override fun shouldDecorate(day: CalendarDay): Boolean {
        return dates.contains(day)
    }

    override fun decorate(view: DayViewFacade) {
        view.addSpan(DotSpan(5F, color))
    }
}

@Composable
fun EventListComponent(vm: CharacterDetailsViewModel, state: CharacterDetailsViewModelState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind { // 親の背景を描画
                drawRect(Color.White.copy(alpha = 0.5f))
            }
    ) {
        EventCalendar(vm, state)
        List(vm, state)
    }
}

private var calendarView: MaterialCalendarView? = null

@Composable
fun EventCalendar(vm: CharacterDetailsViewModel, state: CharacterDetailsViewModelState) {
    val events = mutableListOf<CalendarDay>()
    state.events.monthlyData.days.forEach { key ->
        if (state.events.monthlyData.dayHasEvents(key)) {
            val c = Calendar.getInstance()
            c.time = key
            val day = CalendarDay.from(
                c[Calendar.YEAR],
                c[Calendar.MONTH] + 1,
                c[Calendar.DAY_OF_MONTH]
            )
            events.add(day)
        }
    }
    val eventDecorator = EventDecorator(MaterialTheme.colors.primary.toArgb(), events)
    MaterialCalendar(
        init = {
            calendarView = it
        },
        update = {
            val c = Calendar.getInstance()
            c.time = state.events.selectedDate
            val inDay =
                CalendarDay.from(c[Calendar.YEAR], c[Calendar.MONTH] + 1, c[Calendar.DAY_OF_MONTH])
            it.setDateSelected(inDay, true)
        },
        decorators = listOf(eventDecorator),
        onMonthChanged = { view, date ->
            val c = Calendar.getInstance()
            c[Calendar.YEAR] = date.year
            c[Calendar.MONTH] = date.month - 1
            c[Calendar.DAY_OF_MONTH] = date.day
            view.clearSelection()
            vm.setCurrentEventDate(c.time)
        },
        onDateSelected = { view, date ->
            val c = Calendar.getInstance()
            c[Calendar.YEAR] = date.year
            c[Calendar.MONTH] = date.month - 1
            c[Calendar.DAY_OF_MONTH] = date.day
            view.clearSelection()
            vm.setCurrentEventDate(c.time)
        },
    )

}

@Composable
fun List(vm: CharacterDetailsViewModel, state: CharacterDetailsViewModelState) {
    val listState = rememberLazyListState()
    var scrollToIndex by remember { mutableIntStateOf(0) }
    state.events.indexOfSelectedDate()?.let {
        scrollToIndex = it
    }
    LaunchedEffect(scrollToIndex) {
        listState.scrollToItem(scrollToIndex)
    }
    LazyColumn(state = listState) {
        items(state.events.monthlyData.result) {
            Item(vm, state, it)
        }
    }
}

@Composable
fun Item(
    vm: CharacterDetailsViewModel,
    state: CharacterDetailsViewModelState,
    item: DateWithEvents
) {
    val activity = LocalContext.current as AppCompatActivity
    Column {
        Section {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.height(20.dp)
            ) {
                Text(item.date.toMdString())
                Spacer(Modifier.weight(1f))
                TextButton(contentPadding = PaddingValues(0.dp), onClick = {
                    onAddEvent(activity, vm, state, item.date)
                }) {
                    Icon(
                        modifier = Modifier.padding(0.dp),
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                }
            }
        }
        if (item.events.isEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .padding(6.dp)
            ) {
                Text("イベントはありません")
            }
        }
        repeat(item.events.size) { i ->
            item.events.getOrNull(i)?.let {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .padding(6.dp)
                ) {
                    Text(
                        modifier = Modifier
                            .clickable {
                                onTapEvent(activity, vm, state, it)
                            }
                            .weight(0.7f),
                        color = MaterialTheme.colors.primary,
                        text = it.title ?: ""
                    )
                    if (state.isDeleteModeEvents) {
                        IconButton(
                            modifier = Modifier
                                .height(30.dp)
                                .padding(end = 5.dp),
                            onClick = {
                                deleteEvent(vm, activity, it)
                            }) {
                            Icon(Icons.Default.Delete, null)
                        }
                    }
                }
            }
            Divider()
        }
    }
}

private fun onTapEvent(
    context: Context,
    vm: CharacterDetailsViewModel,
    state: CharacterDetailsViewModelState,
    item: Event
) {
    calendarView?.clearSelection()
    vm.setCurrentEventDate(TimeUtil.resetDate(item.date))
    val intent = Intent(context, EventEditActivity::class.java)
    intent.putExtra(EventEditActivity.INTENT_EDIT_EVENT, item.toJson())
    context.startActivity(intent)
}

private fun onAddEvent(
    context: Context,
    vm: CharacterDetailsViewModel,
    state: CharacterDetailsViewModelState,
    date: Date,
) {
    calendarView?.clearSelection()
    vm.setCurrentEventDate(date)

    val intent = Intent(context, EventCreateActivity::class.java)
    intent.putExtra(EventCreateActivity.INTENT_CREATE_EVENT_CHARACTER, state.character?.id)
    intent.putExtra(EventCreateActivity.INTENT_CREATE_EVENT_DATE, date.time)
    context.startActivity(intent)
}

private fun deleteEvent(
    vm: CharacterDetailsViewModel,
    context: AppCompatActivity,
    item: Event
) {
    val dialog =
        DeleteDialogFragment(object :
            DialogActionListener<DeleteDialogFragment> {
            override fun onDecision(dialog: DeleteDialogFragment) {
                vm.deleteEvent(item)
            }

            override fun onCancel() {
            }
        })
    dialog.show(context.supportFragmentManager, "deleteEvent")
}