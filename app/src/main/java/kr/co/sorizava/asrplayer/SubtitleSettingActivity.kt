package kr.co.sorizava.asrplayer

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.method.MovementMethod
import android.text.method.ScrollingMovementMethod
import android.text.style.BackgroundColorSpan
import android.util.Log
import android.util.TypedValue
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import com.sorizava.asrplayer.extension.appConfig
import com.warkiz.tickseekbar.OnSeekChangeListener
import com.warkiz.tickseekbar.SeekParams
import com.warkiz.tickseekbar.TickSeekBar
import kr.co.sorizava.asrplayer.helper.PlayerCaptionHelper
import kr.co.sorizava.asrplayer.websocket.WsManager
import kr.co.sorizava.asrplayer.widget.FontPickerDialog
import org.mozilla.focus.R
import petrov.kristiyan.colorpicker.ColorPicker
import petrov.kristiyan.colorpicker.ColorPicker.OnChooseColorListener


class SubtitleSettingActivity : BaseActivity(){

    private val TAG = "SubtitleSettingActivity"

    var mSubtitleOnOffGroup: RadioGroup? = null
    var mSubtitleSyncGroup: RadioGroup? = null
    var mSubtitlePositionGroup: RadioGroup? = null
    var mSubtitleLineGroup: RadioGroup? = null

    private var mSubtitleFontSizeSeekBar: TickSeekBar? = null

    var mSubtitleFontName: TextView? = null
    //var mSubtitleForegroundColorImage: ImageView? = null
    lateinit var mSubtitleForegroundColorImage: ImageView
    private var mSubtitleTransparencySeekBar: TickSeekBar? = null
    private var mSzSubtitleView: TextView? = null
    private var mColorArray: TypedArray? = null
    private var mSubtitle_spekaer_onoff_CB: CheckBox? = null
    private val mSubtitleSettingActivity : SubtitleSettingActivity = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subtitle_setting)

        //initActionBar(getResources().getString(R.string.back_detail), getResources().getString(R.string.setting_subtitle));
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        toolbar.title = resources.getString(R.string.preference_category_asr_caption)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        // 자막 ON/OFF
        mSubtitleOnOffGroup = findViewById<View>(R.id.radio_group_subtitle_onoff) as RadioGroup
        (mSubtitleOnOffGroup!!.getChildAt(if (appConfig.prefSubtitleOnOff)0 else 1) as RadioButton).isChecked = true
        mSubtitleOnOffGroup!!.setOnCheckedChangeListener { _, i ->
            when (i) {
                R.id.subtitle_on -> appConfig.prefSubtitleOnOff = true
                R.id.subtitle_off ->

                    // 자막 보기 해제 선택시 알림창 팝업
                    // jhong
                    // since 210719
                    AlertDialog.Builder(this@SubtitleSettingActivity)
                        .setTitle(R.string.menu_settings)
                        .setMessage(R.string.txt_subtitle_off)
                        .setPositiveButton(R.string.action_ok
                        ) { _, _ ->
                            appConfig.prefSubtitleOnOff = false
                        }
                        .setNegativeButton(R.string.action_cancel
                        ) { _, _ ->
                            (findViewById<View>(R.id.subtitle_on) as RadioButton).isChecked = true
                        }.create().show()
            }
        }

        // 자막 위치
        mSubtitlePositionGroup = findViewById<View>(R.id.radio_group_subtitle_position) as RadioGroup
        (mSubtitlePositionGroup!!.getChildAt(appConfig.prefSubtitlePosition) as RadioButton).isChecked =
            true
        mSubtitlePositionGroup!!.setOnCheckedChangeListener { _, i ->
            when (i) {
                R.id.subtitle_position_top -> appConfig.prefSubtitlePosition  = AppConfig.SUBTITLE_POSITION_TOP
                R.id.subtitle_position_bottom -> appConfig.prefSubtitlePosition = AppConfig.SUBTITLE_POSITION_BOTTOM
            }
            setupSubtitleView()
        }

        // 자막 라인 수
        mSubtitleLineGroup = findViewById<View>(R.id.radio_group_subtitle_line) as RadioGroup
        (mSubtitleLineGroup!!.getChildAt(appConfig.prefSubtitleLine - 2) as RadioButton).isChecked =
            true
        mSubtitleLineGroup!!.setOnCheckedChangeListener { radioGroup, i ->
            when (i) {
                R.id.subtitle_line_2 -> appConfig.prefSubtitleLine = AppConfig.SUBTITLE_LINE_2
                R.id.subtitle_line_3 -> appConfig.prefSubtitleLine = AppConfig.SUBTITLE_LINE_3
                R.id.subtitle_line_4 -> appConfig.prefSubtitleLine = AppConfig.SUBTITLE_LINE_4
            }
            setupSubtitleView()
        }

        // 자막 폰트 크기
        mSubtitleFontSizeSeekBar =
            findViewById<View>(R.id.subtitle_font_size_seekbar) as TickSeekBar
        mSubtitleFontSizeSeekBar!!.setProgress(appConfig.prefSubtitleFontSize.toFloat())
        mSubtitleFontSizeSeekBar!!.setOnSeekChangeListener(object : OnSeekChangeListener {
            override fun onSeeking(seekParams: SeekParams) {
                appConfig.prefSubtitleFontSize = seekParams.progress
                setupSubtitleView()
            }

            override fun onStartTrackingTouch(seekBar: TickSeekBar) {}
            override fun onStopTrackingTouch(seekBar: TickSeekBar) {}
        })

        // 자막 폰트
        mSubtitleFontName = findViewById<View>(R.id.subtitle_font_name) as TextView
        val tface = appConfig.convertPrefSubtitleFontTypeface(this, appConfig.prefSubtitleFont)
        mSubtitleFontName!!.typeface = tface
        mSubtitleFontName!!.text = appConfig.convertPrefSubtitleFontName(this, appConfig.prefSubtitleFont)
        val subtitleFontButton = findViewById<View>(R.id.subtitle_font_button) as Button

        subtitleFontButton.setOnClickListener {
            val dialog = FontPickerDialog.newInstance(
                mSubtitleFont,
                object : FontPickerDialog.FontPickerDialogListener
                {
                    override fun onFontSelected(dialog: FontPickerDialog?, index: Int) {
                        val tface = appConfig.convertPrefSubtitleFontTypeface(this@SubtitleSettingActivity, index)
                        mSubtitleFontName!!.typeface = tface
                        mSubtitleFontName!!.text = appConfig.convertPrefSubtitleFontName(this@SubtitleSettingActivity, index)
                        appConfig.prefSubtitleFont = index
                        setupSubtitleView()
                    }
                }
            )

            dialog.show(supportFragmentManager, "dialog")
        }

        // 자막 색상
        mColorArray = resources.obtainTypedArray(R.array.subtitle_default_colors)
        mSubtitleForegroundColorImage = findViewById(R.id.subtitle_foregroundcolor_preview)
        mSubtitleForegroundColorImage.setColorFilter(appConfig.getPrefSubtitleForegroundColor())

        val subtitleForegroundColorButton = findViewById<Button>(R.id.subtitle_foregroundcolor_button)
        subtitleForegroundColorButton.setOnClickListener {
            val colorPicker = ColorPicker(this@SubtitleSettingActivity)
            val int_colors = IntArray(mColorArray!!.length())
            for (i in 0 until mColorArray!!.length()) {
                int_colors[i] = mColorArray!!.getColor(i, 0)
            }
            colorPicker
                .setTitle(resources.getString(R.string.subtitle_foreground_color))
                .setColors(*int_colors)
                .setDefaultColorButton(appConfig.getPrefSubtitleForegroundColor())
                .setColumns(5)
                .setRoundColorButton(false)
                .setOnChooseColorListener(object : OnChooseColorListener {
                    override fun onChooseColor(position: Int, color: Int) {
                        if (position != -1) {
                            mSubtitleForegroundColorImage.setColorFilter(color)
                            //mSubtitleForegroundColorImage.setBackgroundColor(color);
                            appConfig.setPrefSubtitleForegroundColor(color)

                            setupSubtitleView()
                        }
                    }

                    override fun onCancel() {}
                }).show()
            colorPicker.positiveButton.text = resources.getText(R.string.action_ok)
            colorPicker.negativeButton.text = resources.getText(R.string.action_cancel)
        }

        // 자막 배경 투명도
        mSubtitleTransparencySeekBar = findViewById<View>(R.id.subtitle_transparency_seekbar) as TickSeekBar
        mSubtitleTransparencySeekBar!!.setProgress(appConfig.getPrefSubtitleTransparency().toFloat())
        mSubtitleTransparencySeekBar!!.onSeekChangeListener = object : OnSeekChangeListener {
            override fun onSeeking(seekParams: SeekParams) {
                appConfig.setPrefSubtitleTransparency(seekParams.progress)
                setupSubtitleView()
            }

            override fun onStartTrackingTouch(seekBar: TickSeekBar) {}
            override fun onStopTrackingTouch(seekBar: TickSeekBar) {}
        }
        mSzSubtitleView = findViewById(R.id.sz_subtitle_view)
        setupSubtitleView()

        //화자분리 적용 여부 on off
        mActivityContext = this
        mSubtitle_spekaer_onoff_CB = findViewById<View>(R.id.subtitle_spekaer_onoff_CB) as CheckBox

        if( appConfig.prefSubtitleSpeakerOnOff == null ) appConfig.prefSubtitleSpeakerOnOff = true
        else mSubtitle_spekaer_onoff_CB!!.isChecked = appConfig.prefSubtitleSpeakerOnOff

        mSubtitle_spekaer_onoff_CB!!.setOnCheckedChangeListener(Subtitle_spekaer_onoff_CB_Listener())
    }
    var mActivityContext : Context? = null

    inner class Subtitle_spekaer_onoff_CB_Listener : CompoundButton.OnCheckedChangeListener{
        override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {

            val isChecked :Boolean = mSubtitle_spekaer_onoff_CB!!.isChecked

            //1. preference 에 저장하고.
            appConfig.prefSubtitleSpeakerOnOff = mSubtitle_spekaer_onoff_CB!!.isChecked

            //2. 로딩화면을 켜주고
            //로딩화면 만들기
            //로딩창 객체 생성
            val customProgressDialog : dialog_progress  = dialog_progress(mActivityContext!!)
            customProgressDialog.setCancelable(false) //모달창(클릭무시)
            customProgressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) //로딩창을 투명하게
            customProgressDialog.show() // 로딩창 보여주기

            //3. 커넥션을 다시 끊고
            WsManager.getInstance()?.reconnect() //테스트용 코드

            //4. 로딩화면을 꺼준다.
            Handler(Looper.getMainLooper()).postDelayed({
                //실행할 코드
                customProgressDialog.dismiss() //숨기기
            }, 3000)
        }
    }
    inner class dialog_progress(context: Context) : Dialog(context) {
        init {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.dialog_progress)
        }
    }

    private var mSubtitlePoistion = 0
    private var mSubtitleFont = 0
    private var mSubtitleFontSize = 0
    private var mSubtitleLine = 0
    private var mSubtitleForegroundColor = 0
    private var mSubtitleTransparency = 0

    // subtitle setup
    private fun setupSubtitleView() {
        mSubtitlePoistion = appConfig.prefSubtitlePosition
        mSubtitleLine = appConfig.prefSubtitleLine
        mSubtitleFont = appConfig.prefSubtitleFont
        mSubtitleFontSize = appConfig.prefSubtitleFontSize
        mSubtitleForegroundColor = appConfig.getPrefSubtitleForegroundColor()
        mSubtitleTransparency = appConfig.getPrefSubtitleTransparency()
        val textSize = UIUtils.dp2px(
            this,
            appConfig.convertPrefSubtitleFontSize(mSubtitleFontSize).toFloat()
        ).toFloat()

        // 자막 setup
        mSzSubtitleView!!.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
        mSzSubtitleView!!.maxLines = mSubtitleLine
        mSzSubtitleView!!.setTextColor(mSubtitleForegroundColor) //
        mSzSubtitleView!!.setBackgroundColor(Color.TRANSPARENT) // 전체 투명
        mSzSubtitleView!!.setShadowLayer(4f, 0f, 0f, Color.BLACK)
        mSzSubtitleView!!.setLineSpacing(0f, 1.0f) // 줄간
        mSzSubtitleView!!.movementMethod = createMovementMethod(this)
        mSzSubtitleView!!.setTextIsSelectable(false)
        PlayerCaptionHelper.setSubtitleViewFont(
            this,
            mSzSubtitleView,
            appConfig.convertPrefSubtitleFontPath(this, mSubtitleFont)
        )
        setupSubtitlePositon()
        onSubtitleTextOut("가나다라마바사\n아자차카파타하\n가나다라마바사\n아자차카파타하")
    }

    private fun createMovementMethod(context: Context): MovementMethod? {
        val detector = GestureDetector(context, object : SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent): Boolean {
                return true
            }

            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                return true
            }
        })
        return object : ScrollingMovementMethod() {
            override fun onTouchEvent(
                widget: TextView,
                buffer: Spannable,
                event: MotionEvent,
            ): Boolean {
                return true
            }
        }
    }

    private fun setupSubtitlePositon() {
        val containerHeight = UIUtils.dp2px(this, 240f).toFloat() // 240dp
        val layoutParams = mSzSubtitleView!!.layoutParams as RelativeLayout.LayoutParams
        val controlBarHeight = 10f // gap test
        if (mSubtitlePoistion == AppConfig.SUBTITLE_POSITION_TOP) {
            layoutParams.topMargin = controlBarHeight.toInt()
        } else {
            val textSize = UIUtils.dp2px(
                this,
                appConfig.convertPrefSubtitleFontSize(mSubtitleFontSize).toFloat()
            ).toFloat()
            val maxSubtitleHeight = (mSubtitleLine + 1) * textSize
            layoutParams.topMargin = (containerHeight - maxSubtitleHeight - controlBarHeight).toInt()
        }
        mSzSubtitleView!!.layoutParams = layoutParams
    }

    private fun onSubtitleTextOut(msg: String?) {
        if (msg == null) {
            mSzSubtitleView!!.text = ""
        }
        val spannable: Spannable = SpannableString(msg)
        val backgroundColorSpan = BackgroundColorSpan(
            PlayerCaptionHelper.getColorWithAlpha(
                Color.BLACK,
                appConfig.convertPrefSubtitleTransparency(mSubtitleTransparency)
            )
        )
        spannable.setSpan(
            backgroundColorSpan,
            0,
            msg!!.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        mSzSubtitleView!!.text = spannable
    }
}
