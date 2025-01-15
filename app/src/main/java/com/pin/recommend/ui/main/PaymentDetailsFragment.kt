package com.pin.recommend.ui.main

import android.content.Intent
import android.os.Bundle
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
import androidx.recyclerview.widget.RecyclerView
import com.pin.recommend.R
import com.pin.recommend.databinding.FragmentPaymentDetailsBinding
import com.pin.recommend.ui.adapter.DateSeparatedPaymentAdapter
import com.pin.recommend.ui.character.CharacterDetailActivity
import com.pin.recommend.ui.character.CharacterDetailsViewModel
import com.pin.recommend.ui.component.DeleteDialogFragment
import com.pin.recommend.ui.component.DialogActionListener
import com.pin.recommend.ui.payment.PaymentCreateActivity
import com.pin.recommend.ui.payment.PaymentWholePeriodActivity
import com.pin.recommend.ui.payment.SavingsWholePeriodActivity
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

    private val vm: CharacterDetailsViewModel by lazy {
        ViewModelProvider(this)[CharacterDetailsViewModel::class.java]
    }

    private lateinit var binding: FragmentPaymentDetailsBinding

    private lateinit var adapter: DateSeparatedPaymentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val characterId =
            requireActivity().intent.getLongExtra(CharacterDetailActivity.INTENT_CHARACTER, -1)
        vm.observe(this)
        vm.setCharacterId(characterId)
        vm.setCurrentPaymentDate(Date())
        adapter = DateSeparatedPaymentAdapter(this, onDelete = {
            val dialog =
                DeleteDialogFragment(object :
                    DialogActionListener<DeleteDialogFragment> {
                    override fun onDecision(dialog: DeleteDialogFragment) {
                        vm.deletePayment(it)
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
    ): View {
        binding = FragmentPaymentDetailsBinding.inflate(inflater, container, false)
        binding.fragment = this

        vm.state.asLiveData().observe(viewLifecycleOwner) {
            adapter.setList(it.payments.payments)
            adapter.isEditMode = it.isDeleteModePayments
            val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(activity)
            binding.paymentRecycleView.layoutManager = layoutManager
            binding.paymentRecycleView.adapter = adapter
        }
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    fun onNextMonth() {
        vm.nextPaymentMonth()
    }

    fun onPrevMonth() {
        vm.prevPaymentMonth()
    }

    fun toWholePeriodPaymentAmountView() {
        val intent = Intent(requireContext(), PaymentWholePeriodActivity::class.java)
        intent.putExtra(
            PaymentWholePeriodActivity.INTENT_WHOLE_PERIOD_PAYMENT_CHARACTER,
            vm.state.asLiveData().value?.character?.id
        )
        startActivity(intent)
    }

    fun toWholePeriodSavingsAmountView() {
        val intent = Intent(requireContext(), SavingsWholePeriodActivity::class.java)
        intent.putExtra(
            SavingsWholePeriodActivity.INTENT_WHOLE_PERIOD_SAVINGS_CHARACTER,
            vm.state.asLiveData().value?.character?.id
        )
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.edit_mode, menu)
        val editMode = menu.findItem(R.id.edit_mode)
        vm.state.asLiveData().observe(this) {
            if (it.isDeleteModePayments) {
                editMode.title = "完了"
            } else {
                editMode.title = "編集"
            }
        }
        inflater.inflate(R.menu.create, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.edit_mode -> {
                vm.toggleEditModePayment()
                return true
            }

            R.id.create -> {
                val intent = Intent(activity, PaymentCreateActivity::class.java)
                val characterId = requireActivity().intent.getLongExtra(
                    CharacterDetailActivity.INTENT_CHARACTER,
                    -1
                )
                intent.putExtra(PaymentCreateActivity.INTENT_CREATE_PAYMENT, characterId)
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