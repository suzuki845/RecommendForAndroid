package com.pin.recommend.ui.component.composable

import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.OnDateLongClickListener

@Composable
fun MaterialCalendar(
    init: ((MaterialCalendarView) -> Unit)? = null,
    update: ((MaterialCalendarView) -> Unit)? = null,
    decorators: List<DayViewDecorator> = emptyList(),
    onDateSelected: (MaterialCalendarView, CalendarDay) -> Unit,
    onMonthChanged: ((MaterialCalendarView, CalendarDay) -> Unit)? = null,
    onDateLongClicked: ((MaterialCalendarView, CalendarDay) -> Unit)? = null
) {
    AndroidView(
        factory = { ctx ->
            val v = MaterialCalendarView(ctx).apply {
                selectionMode = MaterialCalendarView.SELECTION_MODE_SINGLE
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                )
                layoutParams.height = 500
                tileWidth = FrameLayout.LayoutParams.MATCH_PARENT
                // デコレーターを追加
                decorators.forEach { decorator ->
                    addDecorator(decorator)
                }

                // 日付選択リスナー
                setOnDateChangedListener { widget, date, selected ->
                    onDateSelected(widget, date)
                }

                // 月変更リスナー（オプション）
                onMonthChanged?.let { listener ->
                    setOnMonthChangedListener { widget, date ->
                        listener(widget, date)
                    }
                }

                // 日付長押しリスナー（オプション）
                onDateLongClicked?.let { listener ->
                    setOnDateLongClickListener(OnDateLongClickListener { widget, date ->
                        listener(widget, date)
                    })
                }
            }
            init?.invoke(v)
            v
        },
        update = { view ->
            update?.invoke(view)
            // デコレーターを再設定する場合
            decorators.forEach { decorator ->
                view.addDecorator(decorator)
            }
        }
    )
}
