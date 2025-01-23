package com.pin.recommend.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.pin.recommend.R
import com.pin.recommend.ui.character.CharacterDetailActivity
import com.pin.recommend.ui.character.CharacterDetailsViewModel
import com.pin.recommend.ui.character.CharacterDetailsViewModelState
import com.pin.recommend.ui.payment.PaymentCreateActivity
import com.pin.recommend.ui.payment.PaymentListComponent
import java.util.Date

class PaymentListFragment : Fragment() {

    private val vm by lazy {
        ViewModelProvider(this)[CharacterDetailsViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val characterId =
            requireActivity().intent.getLongExtra(CharacterDetailActivity.INTENT_CHARACTER, -1)
        vm.observe(this)
        vm.setCharacterId(characterId)
        vm.setCurrentPaymentDate(Date())
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val state = vm.state.collectAsState(CharacterDetailsViewModelState()).value
                PaymentListComponent(vm, state)
            }
        }
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
            PaymentListFragment().apply {
                val bundle = Bundle()
                bundle.putInt(ARG_SECTION_NUMBER, index)
                arguments = bundle
            }
    }
}