package com.pin.recommend.ui.main

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SmoothScroller
import com.pin.recommend.R
import com.pin.recommend.databinding.FragmentEventListBinding
import com.pin.recommend.ui.adapter.DateSeparatedEventAdapter
import com.pin.recommend.ui.character.CharacterDetailActivity
import com.pin.recommend.ui.character.CharacterDetailsViewModel
import com.pin.recommend.ui.component.DeleteDialogFragment
import com.pin.recommend.ui.component.DialogActionListener
import com.pin.recommend.ui.event.EventCreateActivity
import com.pin.recommend.ui.event.EventEditActivity
import com.pin.recommend.util.TimeUtil
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.OnDateLongClickListener
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import java.util.Calendar
import java.util.Date


class EventDetailsFragment : Fragment(), OnDateSelectedListener, OnMonthChangedListener,
    OnDateLongClickListener {

    private val vm: CharacterDetailsViewModel by lazy {
        ViewModelProvider(this)[CharacterDetailsViewModel::class.java]
    }

    private lateinit var binding: FragmentEventListBinding

    private lateinit var adapter: DateSeparatedEventAdapter

    private lateinit var calendarView: MaterialCalendarView

    private lateinit var smoothScroller: SmoothScroller

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val characterId =
            requireActivity().intent.getLongExtra(CharacterDetailActivity.INTENT_CHARACTER, -1)

        vm.setCharacterId(characterId)
        vm.setCurrentEventDate(Date())

        vm.observe(this)

        adapter = DateSeparatedEventAdapter(this, onDelete = {
            val dialog =
                DeleteDialogFragment(object :
                    DialogActionListener<DeleteDialogFragment> {
                    override fun onDecision(dialog: DeleteDialogFragment) {
                        vm.deleteEvent(it)
                    }

                    override fun onCancel() {
                    }
                })
            dialog.show(requireActivity().supportFragmentManager, TAG)
        })

        smoothScroller = object : LinearSmoothScroller(context) {
            override fun getVerticalSnapPreference(): Int {
                return SNAP_TO_START
            }
        }

        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEventListBinding.inflate(inflater, container, false)

        calendarView = binding.calendarView

        vm.state.asLiveData().observe(viewLifecycleOwner) {
            adapter.setDates(it.events.monthlyData.days)
            adapter.setEvents(it.events.monthlyData.result)

            val events = mutableListOf<CalendarDay>()
            it.events.monthlyData.result.keys.forEach { key ->
                if (it.events.monthlyData.dayHasEvents(key)) {
                    val c = Calendar.getInstance()
                    c.time = key
                    val day = CalendarDay.from(
                        c[Calendar.YEAR],
                        c[Calendar.MONTH] + 1,
                        c[Calendar.DAY_OF_MONTH]
                    )
                    Log.d("calendar", "$day")
                    events.add(day)
                }
            }
            val eventDecorator = EventDecorator(Color.RED, events)
            calendarView.addDecorator(eventDecorator)

            val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(activity)
            binding.eventRecycleView.layoutManager = layoutManager
            binding.eventRecycleView.adapter = adapter

            val resetDate = TimeUtil.resetDate(it.events.selectedDate)
            val position = adapter.getSectionItemPosition(resetDate)
            if (position != -1) {
                smoothScroller.targetPosition = position
                (binding.eventRecycleView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(
                    position,
                    0
                )
            }

            adapter.isEditMode = it.isDeleteModeEvents
        }

        adapter.setOnSectionClickListener {
            calendarView.clearSelection()
            vm.setCurrentEventDate(it)
        }

        adapter.setOnSectionAddClickListener {
            calendarView.clearSelection()
            vm.setCurrentEventDate(it)

            val intent = Intent(activity, EventCreateActivity::class.java)
            val characterId = requireActivity().intent.getLongExtra(
                CharacterDetailActivity.INTENT_CHARACTER,
                -1
            )
            intent.putExtra(EventCreateActivity.INTENT_CREATE_EVENT_CHARACTER, characterId)
            intent.putExtra(EventCreateActivity.INTENT_CREATE_EVENT_DATE, it.time)
            startActivity(intent)
        }

        adapter.setOnEventClickListener {
            calendarView.clearSelection()
            vm.setCurrentEventDate(TimeUtil.resetDate(it.date))

            val intent = Intent(activity, EventEditActivity::class.java)
            intent.putExtra(EventEditActivity.INTENT_EDIT_EVENT_ID, it.toJson())
            startActivity(intent)
        }


        vm.state.asLiveData().observe(viewLifecycleOwner) {
            val c = Calendar.getInstance()
            c.time = it.events.selectedDate
            val inDay =
                CalendarDay.from(c[Calendar.YEAR], c[Calendar.MONTH] + 1, c[Calendar.DAY_OF_MONTH])
            calendarView.setDateSelected(inDay, true)
        }

        calendarView.setOnDateChangedListener(this);
        calendarView.setOnDateLongClickListener(this);
        calendarView.setOnMonthChangedListener(this);
        calendarView.selectionMode = MaterialCalendarView.SELECTION_MODE_SINGLE

        return binding.root
    }

    override fun onDateLongClick(widget: MaterialCalendarView, date: CalendarDay) {

    }

    override fun onDateSelected(
        widget: MaterialCalendarView,
        date: CalendarDay,
        selected: Boolean
    ) {
        val c = Calendar.getInstance()
        c[Calendar.YEAR] = date.year
        c[Calendar.MONTH] = date.month - 1
        c[Calendar.DAY_OF_MONTH] = date.day

        calendarView.clearSelection()
        vm.setCurrentEventDate(c.time)
    }

    override fun onMonthChanged(widget: MaterialCalendarView, date: CalendarDay) {
        val c = Calendar.getInstance()
        c[Calendar.YEAR] = date.year
        c[Calendar.MONTH] = date.month - 1
        c[Calendar.DAY_OF_MONTH] = date.day

        calendarView.clearSelection()
        vm.setCurrentEventDate(c.time)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.edit_mode, menu)
        val editMode = menu.findItem(R.id.edit_mode)
        vm.state.asLiveData().observe(this) {
            if (it.isDeleteModeEvents) {
                editMode.title = "完了"
            } else {
                editMode.title = "編集"
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.edit_mode -> {
                vm.toggleEditModeEvent()
                return true
            }
        }
        return true
    }

    companion object {
        const val TAG = "com.pin.recommend.main.EventDetailsFragment"
        private const val ARG_SECTION_NUMBER = "section_number"

        @JvmStatic
        fun newInstance(index: Int) =
            EventDetailsFragment().apply {
                val bundle = Bundle()
                bundle.putInt(ARG_SECTION_NUMBER, index)
                arguments = bundle
            }
    }
}


class EventDecorator(private val color: Int, dates: Collection<CalendarDay?>?) : DayViewDecorator {
    private val dates: HashSet<CalendarDay?> = HashSet(dates)
    override fun shouldDecorate(day: CalendarDay): Boolean {
        return dates.contains(day)
    }

    override fun decorate(view: DayViewFacade) {
        view.addSpan(DotSpan(5F, color))
    }
}