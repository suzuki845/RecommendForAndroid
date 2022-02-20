package com.pin.recommend.main

import android.widget.TextView
import com.pin.recommend.model.viewmodel.RecommendCharacterViewModel
import com.pin.recommend.model.viewmodel.EditStateViewModel
import com.pin.recommend.model.entity.RecommendCharacter
import com.pin.recommend.model.entity.AnniversaryManager
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
import com.pin.recommend.model.entity.Account
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
    private val now = Calendar.getInstance()
    private lateinit var characterViewModel: RecommendCharacterViewModel
    private lateinit var character: RecommendCharacter
    private lateinit var anniversaryManager: AnniversaryManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel::class.java)
        var index = 1
        if (arguments != null) {
            index = requireArguments().getInt(ARG_SECTION_NUMBER)
        }
        pageViewModel.setIndex(index)
        characterViewModel = ViewModelProvider(requireActivity()).get(RecommendCharacterViewModel::class.java)
        character = requireActivity().intent.getParcelableExtra(CharacterDetailActivity.INTENT_CHARACTER)!!
        anniversaryManager = AnniversaryManager(character)
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
            intent.putExtra(ScreenShotActivity.INTENT_SCREEN_SHOT, character.id)
            startActivity(intent)
        }
        contentWrapperView = root.findViewById(R.id.content_wrapper)
        contentWrapperView.setOnClickListener{
            val intent = Intent(requireActivity(), ScreenShotActivity::class.java);
            intent.putExtra(ScreenShotActivity.INTENT_SCREEN_SHOT, character.id)
            startActivity(intent)
        }

        initializeText(character)
        val characterLiveData = characterViewModel.getCharacter(character.id)
        characterLiveData.observe(viewLifecycleOwner, Observer { character ->
            if (character == null) return@Observer
            this@HomeFragment.character = character
            val image = character.getIconImage(context, 500, 500)
            if (image != null) {
                iconImageView.setImageBitmap(image)
            }
            initializeText(character)
        })
        return root
    }

    private fun initializeText(character: RecommendCharacter) {
        firstText.text = character.getAboveText()
        firstText.setTextColor(character.getHomeTextColor())
        firstText.setShadowLayer(4f, 0f, 0f, character.getHomeTextShadowColor())
        dateView.text = character.getBelowText()
        dateView.setTextColor(character.getHomeTextColor())
        dateView.setShadowLayer(4f, 0f, 0f, character.getHomeTextShadowColor())
        elapsedView.setTextColor(character.getHomeTextColor())
        elapsedView.text = character.getDiffDays(now)
        elapsedView.setShadowLayer(4f, 0f, 0f, character.getHomeTextShadowColor())
        characterNameView.text = character.name
        characterNameView.setTextColor(character.getHomeTextColor())
        characterNameView.setShadowLayer(4f, 0f, 0f, character.getHomeTextShadowColor())
        anniversaryManager.initialize(character)
        anniversaryView.text = anniversaryManager.nextOrIsAnniversary(now.time)
        anniversaryView.setTextColor(character.getHomeTextColor())
        anniversaryView.setShadowLayer(4f, 0f, 0f, character.getHomeTextShadowColor())
        try {
            if (character.fontFamily != null && character.fontFamily != "default") {
                val font = Typeface.createFromAsset(requireActivity().assets, "fonts/" + character.fontFamily + ".ttf")
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
            println("font missing " + character.fontFamily)
        }
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

        val s = SpannableString("編集")
        s.setSpan(ForegroundColorSpan(textColor), 0, s.length, 0)
        editMode.title = s
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.edit_mode -> {
                val intent = Intent(context, EditCharacterActivity::class.java)
                intent.putExtra(EditCharacterActivity.INTENT_EDIT_CHARACTER, character)
                startActivity(intent)
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