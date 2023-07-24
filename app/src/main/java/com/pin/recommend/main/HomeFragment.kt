package com.pin.recommend.main

import android.widget.TextView
import com.pin.recommend.model.viewmodel.RecommendCharacterViewModel
import com.pin.recommend.model.viewmodel.EditStateViewModel
import com.pin.recommend.model.entity.RecommendCharacter
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.pin.recommend.main.HomeFragment
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.content.Intent
import android.graphics.Color
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.pin.recommend.*
import com.pin.recommend.model.CharacterDetailsState
import com.pin.recommend.model.entity.Account
import com.pin.recommend.model.viewmodel.CharacterDetailsViewModel
import de.hdodenhof.circleimageview.CircleImageView
import java.lang.RuntimeException
import java.text.SimpleDateFormat
import java.util.*

/**
 * A placeholder fragment containing a simple view.
 */
class HomeFragment : Fragment() {
    private lateinit var pageViewModel: PageViewModel
    private lateinit var contentWrapperView: View;
    private lateinit var iconImageView: CircleImageView
    private lateinit var characterNameView: TextView
    private lateinit var firstText: TextView
    private lateinit var dateView: TextView
    private lateinit var elapsedView: TextView
    private lateinit var anniversaryView: TextView
    private lateinit var characterDetailsVM: CharacterDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel::class.java)
        var index = 1
        if (arguments != null) {
            index = requireArguments().getInt(ARG_SECTION_NUMBER)
        }
        pageViewModel.setIndex(index)
        characterDetailsVM = ViewModelProvider(requireActivity()).get(CharacterDetailsViewModel::class.java)
        val character: RecommendCharacter? = requireActivity().intent.getParcelableExtra(CharacterDetailActivity.INTENT_CHARACTER)
        characterDetailsVM.setId(character?.id)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_character_detail, container, false)
        iconImageView = root.findViewById(R.id.character_icon)
        dateView = root.findViewById(R.id.created)
        firstText = root.findViewById(R.id.first_text)
        elapsedView = root.findViewById(R.id.elapsedTime)
        characterNameView = root.findViewById(R.id.character_name)
        anniversaryView = root.findViewById(R.id.anniversary)
        iconImageView.setOnClickListener{
            val intent = Intent(requireActivity(), ScreenShotActivity::class.java);
            intent.putExtra(ScreenShotActivity.INTENT_SCREEN_SHOT, characterDetailsVM.state.value?.characterId)
            startActivity(intent)
        }
        contentWrapperView = root.findViewById(R.id.content_wrapper)
        contentWrapperView.setOnClickListener{
            val intent = Intent(requireActivity(), ScreenShotActivity::class.java);
            intent.putExtra(ScreenShotActivity.INTENT_SCREEN_SHOT, characterDetailsVM.state.value?.characterId)
            startActivity(intent)
        }

        characterDetailsVM.state.observe(viewLifecycleOwner, Observer {
            val icon = it.iconImage
            if (icon != null) {
                iconImageView.setImageBitmap(icon)
            }

            initializeText(it)
        })

        return root
    }

    private fun initializeText(state: CharacterDetailsState) {
        firstText.text = state.topText
        firstText.setTextColor(state.textColor)
        firstText.setShadowLayer(4f, 0f, 0f, state.textShadowColor)
        dateView.text = state.bottomText
        dateView.setTextColor(state.textColor)
        dateView.setShadowLayer(4f, 0f, 0f, state.textShadowColor)
        elapsedView.setTextColor(state.textColor)
        //elapsedView.text = character.getDiffDays(now)
        elapsedView.text = state.elapsedDays.toString()
        elapsedView.setShadowLayer(4f, 0f, 0f, state.textShadowColor)
        characterNameView.text = state.characterName
        characterNameView.setTextColor(state.textColor)
        characterNameView.setShadowLayer(4f, 0f, 0f, state.textShadowColor)

        anniversaryView.text = state.anniversaryMessage
        anniversaryView.setTextColor(state.textColor)
        anniversaryView.setShadowLayer(4f, 0f, 0f, state.textShadowColor)
        try {
            if (state.fontFamily != null && state.fontFamily != "default") {
                val font = Typeface.createFromAsset(requireActivity().assets, "fonts/" + state.fontFamily + ".ttf")
                firstText.typeface = font
                dateView.typeface = font
                elapsedView.typeface = font
                characterNameView.typeface = font
                anniversaryView.typeface = font
            } else {
                firstText.typeface = null
                dateView.typeface = null
                elapsedView.typeface = null
                characterNameView.typeface = null
                anniversaryView.typeface = null
            }
        } catch (e: RuntimeException) {
            println("font missing " + state.fontFamily)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.change_anniversary, menu)

        inflater.inflate(R.menu.edit_mode, menu)
        val editMode = menu.findItem(R.id.edit_mode)
        val s = SpannableString("編集")
        editMode.title = s

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.edit_mode -> {
                val intent = Intent(context, EditCharacterActivity::class.java)
                intent.putExtra(EditCharacterActivity.INTENT_EDIT_CHARACTER, characterDetailsVM.state.value?.characterId)
                startActivity(intent)
                return true
            }
            R.id.change_anniversary -> {
                return true
            }
        }
        return true
    }

    companion object {
        private val FORMAT = SimpleDateFormat("yyyy年MM月dd日")
        private const val ARG_SECTION_NUMBER = "section_number"
        @JvmStatic
        fun newInstance(index: Int): HomeFragment {
            val fragment = HomeFragment()
            val bundle = Bundle()
            bundle.putInt(ARG_SECTION_NUMBER, index)
            fragment.arguments = bundle
            return fragment
        }
    }
}