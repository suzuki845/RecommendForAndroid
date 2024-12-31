package com.pin.recommend.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pin.recommend.CharacterDetailActivity
import com.pin.recommend.CreatePaymentActivity
import com.pin.recommend.R
import com.pin.recommend.WholePeriodPaymentActivity
import com.pin.recommend.WholePeriodSavingsActivity
import com.pin.recommend.adapter.DateSeparatedPaymentAdapter
import com.pin.recommend.databinding.FragmentPaymentDetailsBinding
import com.pin.recommend.dialog.DeleteDialogFragment
import com.pin.recommend.dialog.DialogActionListener
import com.pin.recommend.viewmodel.CharacterDetailsViewModel
import com.pin.recommend.viewmodel.PaymentDetailsViewModel
import java.util.Date

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

    private val detailsVM: CharacterDetailsViewModel by lazy {
        ViewModelProvider(this).get(CharacterDetailsViewModel::class.java)
    }

    private lateinit var binding: FragmentPaymentDetailsBinding

    private lateinit var adapter: DateSeparatedPaymentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val characterId =
            requireActivity().intent.getLongExtra(CharacterDetailActivity.INTENT_CHARACTER, -1)
        paymentViewModel.setCharacterId(characterId)
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPaymentDetailsBinding.inflate(inflater, container, false)

        with(binding) {
            lifecycleOwner = viewLifecycleOwner
            vm = paymentViewModel
            owner = this@PaymentDetailsFragment
            paymentViewModel.monthlyPayment.observe(viewLifecycleOwner, Observer {
                adapter.setList(it)
                val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(activity)
                paymentRecycleView.layoutManager = layoutManager
                paymentRecycleView.adapter = adapter
            })

            paymentViewModel.isEditMode.observe(viewLifecycleOwner, Observer {
                adapter.isEditMode = it
            })
        }

        return binding.root
    }

    fun onNextMonth() {
        paymentViewModel.nextMonth()
    }

    fun onPrevMonth() {
        paymentViewModel.prevMonth()
    }

    fun toWholePeriodPaymentAmountView() {
        val intent = Intent(requireContext(), WholePeriodPaymentActivity::class.java)
        intent.putExtra(
            WholePeriodPaymentActivity.INTENT_WHOLE_PERIOD_PAYMENT_CHARACTER,
            paymentViewModel.characterId.value
        )
        startActivity(intent)
    }

    fun toWholePeriodSavingsAmountView() {
        val intent = Intent(requireContext(), WholePeriodSavingsActivity::class.java)
        intent.putExtra(
            WholePeriodSavingsActivity.INTENT_WHOLE_PERIOD_SAVINGS_CHARACTER,
            paymentViewModel.characterId.value
        )
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.edit_mode, menu)
        val editMode = menu.findItem(R.id.edit_mode)
        paymentViewModel.isEditMode.observe(this, Observer<Boolean> { mode ->
            if (mode) {
                editMode.title = "完了"
            } else {
                editMode.title = "編集"
            }
        })
        inflater.inflate(R.menu.create, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.edit_mode -> {
                paymentViewModel.isEditMode.value = paymentViewModel.isEditMode.value != true
                return true
            }

            R.id.create -> {
                val intent = Intent(activity, CreatePaymentActivity::class.java)
                val characterId = requireActivity().intent.getLongExtra(
                    CharacterDetailActivity.INTENT_CHARACTER,
                    -1
                )
                intent.putExtra(CreatePaymentActivity.INTENT_CREATE_PAYMENT, characterId)
                startActivity(intent)
            }
        }
        return true
    }

    companion object {
        const val TAG = "com.pin.recommend.main.PaymentDetailsFragment"
        private const val ARG_SECTION_NUMBER = "section_number"
        const val INTENT_PAYMENT_DETAILS =
            "com.pin.recommend.PaymentDetailsFragment.INTENT_PAYMENT_DETAILS"
        const val INTENT_CREATE_PAYMENT =
            "com.pin.recommend.PaymentDetailsFragment.INTENT_CREATE_PAYMENT"

        @JvmStatic
        fun newInstance(index: Int) =
            PaymentDetailsFragment().apply {
                val bundle = Bundle()
                bundle.putInt(ARG_SECTION_NUMBER, index)
                arguments = bundle
            }
    }
}