package com.pin.recommend

import androidx.appcompat.app.AppCompatActivity

class EditCharacterActivity : AppCompatActivity() {
    companion object {
        @JvmField
        val INTENT_EDIT_CHARACTER = "com.pin.recommend.EditCharacterActivity.INTENT_EDIT_CHARACTER"
        val REQUEST_CODE_CREATE_ANNIVERSARY = 2983179
        val REQUEST_CODE_EDIT_ANNIVERSARY = 3982432
    }

}

/*
class EditCharacterActivity : AppCompatActivity() {

    companion object {
        @JvmField
        val INTENT_EDIT_CHARACTER = "com.pin.recommend.EditCharacterActivity.INTENT_EDIT_CHARACTER"
        val REQUEST_CODE_CREATE_ANNIVERSARY = 2983179
        val REQUEST_CODE_EDIT_ANNIVERSARY = 3982432
    }

    private val REQUEST_PICK_ICON = 2000
    private val REQUEST_PICK_BACKGROUND = 2001

    private val FORMAT = SimpleDateFormat("yyyy年MM月dd日")

    private val vm: CharacterEditorViewModel by lazy {
        ViewModelProvider(this).get(CharacterEditorViewModel::class.java)
    }

    private var id = -1L

    private lateinit var binding: ActivityEditCharacterBinding
    private lateinit var listView: RecyclerView
    private lateinit var scrollView: ScrollView

    private lateinit var adMobManager: AdMobAdaptiveBannerManager
    private lateinit var adViewContainer: ViewGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_character)

        adViewContainer = findViewById(R.id.ad_container)

        adMobManager =
            AdMobAdaptiveBannerManager(
                this,
                adViewContainer,
                getString(R.string.banner_id)
            )
        adMobManager.setAllowAdClickLimit(6)
        adMobManager.setAllowRangeOfAdClickByTimeAtMinute(3)
        adMobManager.setAllowAdLoadByElapsedTimeAtMinute(24 * 60 * 14)
        val reward = RemoveAdReward.getInstance(this)
        reward.isBetweenRewardTime.observe(
            this
        ) { isBetweenRewardTime ->
            adMobManager.setEnable(!isBetweenRewardTime!!)
            adMobManager.checkFirst()
        }

        val json = intent.getStringExtra(INTENT_EDIT_CHARACTER) ?: ""
        val cwa =
            CharacterWithAnniversaries.fromJson(json)

        id = cwa.id
        vm.initialize(cwa)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_character)
        binding.vm = vm
        binding.lifecycleOwner = this

        binding.imageOpacity.max = 100
        binding.imageOpacity.progress = (cwa.character.backgroundImageOpacity * 100).toInt()
        binding.imageOpacity.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar, progress: Int, fromUser: Boolean
            ) {
                val o = progress * 0.01f
                vm.backgroundImageOpacity.value = o
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        binding.previewBackgroundImage.setOnLongClickListener {
            setOnBackgroundClear(it)
        }

        binding.previewBackgroundColor.setOnLongClickListener {
            setOnBackgroundColorClear(it)
        }

        binding.previewTextColor.setOnClickListener {
            setOnTextColor(it)
        }

        binding.previewTextShadow.setOnClickListener {
            setOnTextShadowColor(it)
        }

        scrollView = binding.scrollView

        listView = binding.anniversaries
        val adapter = AnniversariesDraftAdapter(this)
        listView.adapter = adapter
        adapter.setOnItemClickListener {
            val intent = Intent(this, EditAnniversaryActivity::class.java)
            intent.putExtra(INTENT_EDIT_ANNIVERSARY, it.toJson())
            startActivityForResult(intent, REQUEST_CODE_EDIT_ANNIVERSARY)
        }
        vm.anniversaries.observeForever {
            adapter.setItems(it)
        }
        listView.layoutManager = LinearLayoutManager(this)

        val simpleItemTouchCallback: ItemTouchHelper.SimpleCallback = object :
            ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT or ItemTouchHelper.DOWN or ItemTouchHelper.UP
            ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                val position = viewHolder.adapterPosition
                vm.removeAnniversary(position)
                adapter.notifyDataSetChanged()
            }
        }
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(listView)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        initializeToolbar(toolbar)

    }

    private fun initializeToolbar(toolbar: Toolbar) {
        toolbar.title = "編集"
        setSupportActionBar(toolbar)
    }

    override fun onResume() {
        super.onResume()
        //adMobManager.checkAndLoad()
    }

    fun onSetBackgroundColor(v: View?) {
        val dialog =
            ColorPickerDialogFragment(object :
                DialogActionListener<ColorPickerDialogFragment> {
                override fun onDecision(dialog: ColorPickerDialogFragment) {
                    vm.backgroundColor.value = dialog.color
                }

                override fun onCancel() {}
            })

        dialog.setDefaultColor(vm.backgroundColor.value ?: CharacterEditor.defaultBackgroundColor)
        dialog.show(supportFragmentManager, ColorPickerDialogFragment.TAG)
    }

    fun setOnBackgroundClear(v: View): Boolean {
        val popup = PopupMenu(this, binding.previewBackgroundImage)
        val inflater = popup.menuInflater
        inflater.inflate(R.menu.pic_story_picture_popup, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.remove -> {
                    vm.backgroundImage.value = null
                }
            }
            false
        }
        popup.show()
        return true
    }

    fun setOnBackgroundColorClear(v: View): Boolean {
        val popup = PopupMenu(this, binding.previewBackgroundColor)
        val inflater = popup.menuInflater
        inflater.inflate(R.menu.pic_story_picture_popup, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.remove -> {
                    vm.backgroundColor.value = Color.WHITE
                }
            }
            false
        }
        popup.show()
        return true
    }

    fun setOnTextColor(v: View) {
        val dialog = ColorPickerDialogFragment(object :
            DialogActionListener<ColorPickerDialogFragment> {
            override fun onCancel() {}
            override fun onDecision(dialog: ColorPickerDialogFragment?) {
                dialog?.let {
                    vm.homeTextColor.value = it.color
                }
            }
        })
        dialog.setDefaultColor(vm.homeTextColor.value ?: CharacterEditor.defaultTextColor)
        dialog.show(supportFragmentManager, ColorPickerDialogFragment.TAG)
    }

    fun setOnTextShadowColor(v: View) {
        val dialog = ColorPickerDialogFragment(object :
            DialogActionListener<ColorPickerDialogFragment> {
            override fun onCancel() {}
            override fun onDecision(dialog: ColorPickerDialogFragment?) {
                dialog?.let {
                    vm.homeTextShadowColor.value = it.color
                }
            }
        })
        vm.homeTextShadowColor.value?.let {
            dialog.setDefaultColor(it)
        }
        dialog.show(supportFragmentManager, ColorPickerDialogFragment.TAG)
    }

    fun onAddAnniversary(v: View) {
        if ((vm.anniversaries.value?.size ?: 0) >= 2) {
            Toast.makeText(this, "記念日は2個以上設定できません。", Toast.LENGTH_LONG).show()
            return
        }

        val intent = Intent(this, CreateAnniversaryActivity::class.java)
        intent.putExtra(CreateAnniversaryActivity.INTENT_CHARACTER_ID, id)
        startActivityForResult(intent, REQUEST_CODE_CREATE_ANNIVERSARY)
    }

    fun onShowFontDialog(view: View?) {
        val adapter = FontAdapter(this)
        val listView = ListView(this)
        listView.adapter = adapter
        val builder: AlertDialog.Builder =
            AlertDialog.Builder(this).setTitle("選択してくだい。").setView(listView)
        builder.setNegativeButton("キャンセル") { d, _ ->
            d.cancel()
        }

        val dialog = builder.create()
        listView.setOnItemClickListener { parent, view, pos, id ->
            vm.fontFamily.value = adapter.getItem(pos).name
            dialog.cancel()
        }

        dialog.show()
    }

    fun onShowDatePickerDialog(view: View?) {
        val calendar = Calendar.getInstance()
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]
        val dayOfMonth = calendar[Calendar.DAY_OF_MONTH]
        val datePickerDialog =
            DatePickerDialog(this, OnDateSetListener { dialog, year, month, dayOfMonth ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT && !dialog.isShown) {
                    return@OnDateSetListener
                    //api19はクリックするとonDateSetが２回呼ばれるため
                }
                val newCalender = Calendar.getInstance()
                newCalender[year, month] = dayOfMonth
                val date = newCalender.time
                vm.created.value = date
            }, year, month, dayOfMonth)
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        datePickerDialog.show()
    }


    fun onSetIcon(v: View?) {
        if (!PermissionChecker.requestPermissions(
                this, MyApplication.REQUEST_PICK_IMAGE, PermissionRequests().requestImages()
            )
        ) {
            return
        }

        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_PICK_ICON)
    }

    fun onSetBackground(v: View?) {
        if (!PermissionChecker.requestPermissions(
                this, MyApplication.REQUEST_PICK_IMAGE, PermissionRequests().requestImages()
            )
        ) {
            return
        }

        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_PICK_BACKGROUND)
    }

    private
    var pickMode = 0
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        result: Intent?
    ) {
        if (requestCode == REQUEST_PICK_ICON && resultCode == RESULT_OK) {
            result?.let { beginCropIcon(it.data) }
            pickMode = REQUEST_PICK_ICON
            intent.putExtra(Constants.PICK_IMAGE, true)
        } else if (pickMode == REQUEST_PICK_ICON) {
            result?.let { handleCropIcon(resultCode, it) }
            pickMode = 0
            intent.putExtra(Constants.PICK_IMAGE, true)
        }

        if (requestCode == REQUEST_PICK_BACKGROUND && resultCode == RESULT_OK) {
            result?.let { beginCropBackground(it.data) }
            pickMode = REQUEST_PICK_BACKGROUND
            intent.putExtra(Constants.PICK_IMAGE, true)
        } else if (pickMode == REQUEST_PICK_BACKGROUND) {
            result?.let { handleCropBackground(resultCode, it) }
            pickMode = 0
            intent.putExtra(Constants.PICK_IMAGE, true)
        }

        if (requestCode == REQUEST_CODE_CREATE_ANNIVERSARY && resultCode == RESULT_OK) {
            result?.let {
                it.getStringExtra(INTENT_CREATE_ANNIVERSARY)?.let {
                    val anniversary = CustomAnniversary.Draft.fromJson(it ?: "")
                    vm.addAnniversary(anniversary)
                    scrollView.post {
                        scrollView.fullScroll(View.FOCUS_DOWN)
                    }
                    binding.root.requestFocus()
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
                }
            }
        }

        if (requestCode == REQUEST_CODE_EDIT_ANNIVERSARY && resultCode == RESULT_OK) {
            result?.let {
                it.getStringExtra(INTENT_EDIT_ANNIVERSARY)?.let {
                    val anniversary = CustomAnniversary.Draft.fromJson(it ?: "")
                    vm.replaceAnniversary(anniversary)
                }
            }
        }

        return super.onActivityResult(requestCode, resultCode, result)
    }

    private fun beginCropIcon(source: Uri?) {
        val destination = Uri.fromFile(File(this.getCacheDir(), "cropped"))
        Crop.of(source, destination).asSquare().start(this);
    }

    private fun handleCropIcon(resultCode: Int, result: Intent) {
        if (resultCode == RESULT_OK) {
            val uri = Crop.getOutput(result)
            val bitmap = BitmapUtility.decodeUri(
                this, uri, 500, 500
            )
            vm.iconImage.value = bitmap
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).message, Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun beginCropBackground(source: Uri?) {
        val destination = Uri.fromFile(File(this.cacheDir, "cropped"))
        val displaySize = DisplaySizeCheck.getDisplaySize(this)
        Crop.of(source, destination).withAspect(displaySize.x, displaySize.y)
            .start(this)
    }

    private fun handleCropBackground(resultCode: Int, result: Intent) {
        println("test!!! handleCropBackground")
        if (resultCode == RESULT_OK) {
            val uri = Crop.getOutput(result)
            val bitmap = BitmapUtility.decodeUri(this, uri)
            vm.backgroundImage.value = bitmap
            println("test!!! handleCropBackground")
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).message, Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_edit_character, menu)
        return true
    }

    private fun save() {
        val ad = Interstitial(resources.getString(R.string.interstitial_f_id))
        val progress = ProgressDialog(this).apply {
            setTitle("少々お待ちください...")
            setCancelable(false)
        }
        ad.show(this, InterstitialAdStateAction({
            progress.show()
        }, {
            progress.dismiss()
        }, {
            progress.dismiss()
            saveInner()
        }, {
            saveInner()
        }, {
            progress.dismiss()
            saveInner()
        }))
    }

    private fun saveInner() {
        vm.save(Progress({
            val updateWidgetRequest = Intent("android.appwidget.action.APPWIDGET_UPDATE")
            sendBroadcast(updateWidgetRequest)
        }, {
            finish()
        }, { e ->
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_save -> {
                save()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

}
*/