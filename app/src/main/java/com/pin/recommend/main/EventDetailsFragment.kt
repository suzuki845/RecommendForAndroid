package com.pin.recommend.main
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SmoothScroller
import com.pin.recommend.*
import com.pin.recommend.R
import com.pin.recommend.adapter.DateSeparatedEventAdapter
import com.pin.recommend.databinding.FragmentEventDetailsBinding
import com.pin.recommend.dialog.DeleteDialogFragment
import com.pin.recommend.dialog.DialogActionListener
import com.pin.recommend.model.entity.Account
import com.pin.recommend.model.entity.RecommendCharacter
import com.pin.recommend.model.viewmodel.CharacterDetailsViewModel
import com.pin.recommend.model.viewmodel.EventDetailsViewModel
import com.pin.recommend.util.TimeUtil
import com.prolificinteractive.materialcalendarview.*
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import java.util.*


class EventDetailsFragment : Fragment(), OnDateSelectedListener, OnMonthChangedListener, OnDateLongClickListener {


    private val eventViewModel: EventDetailsViewModel by lazy {
        ViewModelProvider(this).get(EventDetailsViewModel::class.java)
    }

    private val detailsVM: CharacterDetailsViewModel by lazy {
        ViewModelProvider(this).get(CharacterDetailsViewModel::class.java)
    }

    private lateinit var  binding: FragmentEventDetailsBinding

    private lateinit var adapter: DateSeparatedEventAdapter

    private lateinit var calendarView: MaterialCalendarView

    private lateinit var smoothScroller: SmoothScroller

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val characterId = requireActivity().intent.getLongExtra(CharacterDetailActivity.INTENT_CHARACTER, -1)
        eventViewModel.setCharacter(characterId)
        eventViewModel.setCurrentDate(Date())
        adapter = DateSeparatedEventAdapter(this, onDelete = {
            val dialog = DeleteDialogFragment(object : DialogActionListener<DeleteDialogFragment> {
                override fun onDecision(dialog: DeleteDialogFragment) {
                    eventViewModel.deleteEvent(it)
                }

                override fun onCancel() {
                }
            })
            dialog.show(requireActivity().supportFragmentManager, TAG)
        })

        smoothScroller = object : LinearSmoothScroller(context) {
            override fun getVerticalSnapPreference(): Int {
                return LinearSmoothScroller.SNAP_TO_START
            }
        }

        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentEventDetailsBinding.inflate(inflater, container, false)

        calendarView = binding.calendarView

        with(binding) {
            lifecycleOwner = viewLifecycleOwner
            vm = eventViewModel
            owner = this@EventDetailsFragment
            eventViewModel.selectedMonthlyEvent.observe(viewLifecycleOwner, Observer {
                adapter.setDates(it.monthlyEvent.days)
                adapter.setEvents(it.monthlyEvent.events)
                var events = mutableListOf<CalendarDay>()
                it.monthlyEvent.events.keys.forEach { key ->
                    if (it.monthlyEvent.dayHasEvents(key)) {
                        val c = Calendar.getInstance()
                        c.time = key
                        val day = CalendarDay.from(c[Calendar.YEAR], c[Calendar.MONTH] + 1, c[Calendar.DAY_OF_MONTH])
                        Log.d("calendar", "$day")
                        events.add(day)
                    }
                }
                val eventDecorator = EventDecorator(Color.RED, events)
                calendarView.addDecorator(eventDecorator)
                /*
                val dividerItemDecoration = DividerItemDecoration(activity, LinearLayoutManager(activity).orientation)
                eventRecycleView.addItemDecoration(dividerItemDecoration)
                 */
                val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(activity)
                eventRecycleView.layoutManager = layoutManager
                eventRecycleView.adapter = adapter

                val resetDate = TimeUtil.resetDate(it.selectedDate)
                val position = adapter.getSectionItemPosition(resetDate)
                if (position != -1) {
                    //val s = SimpleDateFormat("yyyy/MM/dd");
                    //Log.d("scrollPos", "$position => ${resetDate}")
                    smoothScroller.targetPosition = position
                    (binding.eventRecycleView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(position, 0)
                }
            })
            adapter.setOnSectionClickListener {
                calendarView.clearSelection()
                eventViewModel.setCurrentDate(it)
            }
            adapter.setOnSectionAddClickListener {
                calendarView.clearSelection()
                eventViewModel.setCurrentDate(it)

                val intent = Intent(activity, CreateEventActivity::class.java)
                val characterId = requireActivity().intent.getLongExtra(CharacterDetailActivity.INTENT_CHARACTER, -1)
                intent.putExtra(CreateEventActivity.INTENT_CREATE_EVENT_CHARACTER, characterId)
                intent.putExtra(CreateEventActivity.INTENT_CREATE_EVENT_DATE, it.time)
                startActivity(intent)
            }
            adapter.setOnEventClickListener {
                calendarView.clearSelection()
                eventViewModel.setCurrentDate(TimeUtil.resetDate(it.date))

                val intent = Intent(activity, EditEventActivity::class.java)
                intent.putExtra(EditEventActivity.INTENT_EDIT_EVENT_ID, it.id)
                startActivity(intent)
            }
            eventViewModel.isEditMode.observe(viewLifecycleOwner, Observer {
                adapter.isEditMode = it
            })
        }


        eventViewModel.currentDate.observe(viewLifecycleOwner) {
            val c = Calendar.getInstance()
            c.time = it
            val inDay = CalendarDay.from(c[Calendar.YEAR], c[Calendar.MONTH] + 1, c[Calendar.DAY_OF_MONTH])
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

    override fun onDateSelected(widget: MaterialCalendarView, date: CalendarDay, selected: Boolean) {
        val c = Calendar.getInstance()
        c[Calendar.YEAR] = date.year
        c[Calendar.MONTH] = date.month - 1
        c[Calendar.DAY_OF_MONTH] = date.day

        calendarView.clearSelection()
        eventViewModel.setCurrentDate(c.time)
    }

    override fun onMonthChanged(widget: MaterialCalendarView, date: CalendarDay) {
        val c = Calendar.getInstance()
        c[Calendar.YEAR] = date.year
        c[Calendar.MONTH] = date.month - 1
        c[Calendar.DAY_OF_MONTH] = date.day

        calendarView.clearSelection()
        eventViewModel.setCurrentDate(c.time)
    }

    fun onNextMonth(){
        eventViewModel.nextMonth()
    }

    fun onPrevMonth(){
        eventViewModel.prevMonth()
    }

    private fun initializeText(character: RecommendCharacter){
    }

    private fun accountToolbarTextColor(account: Account?): Int {
        return account?.toolbarTextColor ?: Color.parseColor("#ffffff")
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.edit_mode, menu)
        val editMode = menu.findItem(R.id.edit_mode)
        eventViewModel.isEditMode.observe(this, Observer<Boolean> { mode ->
            if (mode) {
                editMode.title = "完了"
            } else {
                editMode.title = "編集"
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.edit_mode -> {
                eventViewModel.isEditMode.value = eventViewModel.isEditMode.value != true
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
    private val dates: HashSet<CalendarDay>
    override fun shouldDecorate(day: CalendarDay): Boolean {
        return dates.contains(day)
    }

    override fun decorate(view: DayViewFacade) {
        view.addSpan(DotSpan(5F, color))
    }

    init {
        this.dates = HashSet(dates)
    }
}