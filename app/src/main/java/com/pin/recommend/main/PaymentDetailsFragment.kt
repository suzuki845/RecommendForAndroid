package com.pin.recommend.main

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.pin.recommend.*
import com.pin.recommend.adapter.DateSeparatedPaymentAdapter
import com.pin.recommend.databinding.FragmentPaymentDetailsBinding
import com.pin.recommend.dialog.ColorPickerDialogFragment
import com.pin.recommend.dialog.DeleteDialogFragment
import com.pin.recommend.dialog.DialogActionListener
import com.pin.recommend.dialog.ToolbarSettingDialogFragment
import com.pin.recommend.model.entity.Account
import com.pin.recommend.model.entity.RecommendCharacter
import com.pin.recommend.model.viewmodel.PaymentDetailsViewModel
import com.pin.recommend.model.viewmodel.RecommendCharacterViewModel
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PaymentDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PaymentDetailsFragment : Fragment() {


    private val paymentViewModel: PaymentDetailsViewModel by lazy {
        ViewModelProvider(this).get(PaymentDetailsViewModel::class.java)
    }

    private val characterViewModel: RecommendCharacterViewModel by lazy {
        ViewModelProvider(this).get(RecommendCharacterViewModel::class.java)
    }

    private lateinit var character: RecommendCharacter

    private lateinit var  binding: FragmentPaymentDetailsBinding

    private lateinit var adapter: DateSeparatedPaymentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        character = requireActivity().intent.getParcelableExtra(CharacterDetailActivity.INTENT_CHARACTER)
        if(character != null){
            //paymentViewModel.characterId.value = character.id
            paymentViewModel.setCharacterId(character.id)
        }
        paymentViewModel.setCurrentDate(Date())
        adapter = DateSeparatedPaymentAdapter(this, onDelete = {
            val dialog = DeleteDialogFragment(object : DialogActionListener<DeleteDialogFragment> {
                override fun onDecision(dialog: DeleteDialogFragment) {
                    paymentViewModel.deletePayment(it)
                }

                override fun onCancel() {
                }
            })
            dialog.show(requireActivity().supportFragmentManager, TAG)
        })
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentPaymentDetailsBinding.inflate(inflater, container, false)

        with(binding) {
            lifecycleOwner = viewLifecycleOwner
            vm = paymentViewModel
            owner = this@PaymentDetailsFragment
            paymentViewModel.monthlyPayment.observe(viewLifecycleOwner, Observer {
                adapter.setList(it)
                val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(activity)
                //val dividerItemDecoration = DividerItemDecoration(requireContext(), LinearLayoutManager(requireContext()).orientation)
                //paymentRecycleView.addItemDecoration(dividerItemDecoration)
                paymentRecycleView.layoutManager = layoutManager
                //paymentRecycleView.setHasFixedSize(false)
                paymentRecycleView.adapter = adapter
            })

            paymentViewModel.isEditMode.observe(viewLifecycleOwner, Observer {
                adapter.isEditMode = it
            })
        }

        val fab: FloatingActionButton = binding.root.findViewById(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(activity, CreatePaymentActivity::class.java)
            val character = requireActivity().intent.getParcelableExtra<RecommendCharacter>(CharacterDetailActivity.INTENT_CHARACTER)
            intent.putExtra(CreatePaymentActivity.INTENT_CREATE_PAYMENT, character?.id)
            startActivity(intent)
        }

        return binding.root
    }

    fun onNextMonth(){
        paymentViewModel.nextMonth()
    }

    fun onPrevMonth(){
        paymentViewModel.prevMonth()
    }

    fun toWholePeriodPaymentAmountView(){
        val intent = Intent(requireContext(), WholePeriodPaymentActivity::class.java)
        intent.putExtra(WholePeriodPaymentActivity.INTENT_WHOLE_PERIOD_PAYMENT_CHARACTER, paymentViewModel.characterId.value)
        startActivity(intent)
    }

    fun toWholePeriodSavingsAmountView(){
        val intent = Intent(requireContext(), WholePeriodSavingsActivity::class.java)
        intent.putExtra(WholePeriodSavingsActivity.INTENT_WHOLE_PERIOD_SAVINGS_CHARACTER, paymentViewModel.characterId.value)
        startActivity(intent)
    }

    private fun initializeText(character: RecommendCharacter){
    }

    private fun accountToolbarTextColor(account: Account?): Int {
        return account?.getToolbarTextColor() ?: Color.parseColor("#ffffff")
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.edit_mode, menu)
        val editMode = menu.findItem(R.id.edit_mode)
        val account = MyApplication.getAccountViewModel(activity as AppCompatActivity?).accountLiveData.value
        val textColor = character.getToolbarTextColor(context, accountToolbarTextColor(account))
        paymentViewModel.isEditMode.observe(this, Observer<Boolean> { mode ->
            if (mode) {
                editMode.title = "完了"
            } else {
                editMode.title = "編集"
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.change_body_text_color -> {
                val bodyTextPickerDialogFragment = ColorPickerDialogFragment(object : DialogActionListener<ColorPickerDialogFragment?> {
                    override fun onDecision(dialog: ColorPickerDialogFragment?) {
                        character.homeTextColor = dialog?.color
                        characterViewModel.update(character)
                    }

                    override fun onCancel() {}
                })
                bodyTextPickerDialogFragment.setDefaultColor(character.getHomeTextColor())
                bodyTextPickerDialogFragment.show(requireActivity().supportFragmentManager, ToolbarSettingDialogFragment.TAG)
                return true
            }
            R.id.edit_mode -> {
                paymentViewModel.isEditMode.value = paymentViewModel.isEditMode.value != true
                return true
            }
        }
        return true
    }

    companion object {
        const val TAG = "com.pin.recommend.main.PaymentDetailsFragment"
        private const val ARG_SECTION_NUMBER = "section_number"
        const val INTENT_PAYMENT_DETAILS = "com.pin.recommend.PaymentDetailsFragment.INTENT_PAYMENT_DETAILS"
        const val INTENT_CREATE_PAYMENT = "com.pin.recommend.PaymentDetailsFragment.INTENT_CREATE_PAYMENT"

        @JvmStatic
        fun newInstance(index: Int) =
                PaymentDetailsFragment().apply {
                    val bundle = Bundle()
                    bundle.putInt(ARG_SECTION_NUMBER, index)
                    arguments = bundle
                }
    }
}