package kr.co.sorizava.asrplayer


import android.graphics.Color
import android.os.Build
import android.util.TypedValue
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import org.mozilla.focus.R

open class BaseActivity : AppCompatActivity() {

    private val TAG = "BaseActivity"


    /**
     * Custom ActionBar
     *
     * @param backTitle   뒤로가기 텍스트
     * @param title       title
     */
    fun initActionBar(backTitle: String?, title: String?) {
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(resources.getDrawable(R.drawable.action_bar))
            actionBar.setDisplayShowTitleEnabled(false)
            actionBar.setHomeButtonEnabled(false)
            actionBar.setDisplayShowHomeEnabled(false)
            actionBar.setDisplayHomeAsUpEnabled(false)
            actionBar.setDisplayShowCustomEnabled(false)

            // 디자인 한대로 나오게 하기 위해서 레이아웃 파람을 설정
            val layout = ActionBar.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
            )

            //actionbar_custom.xml 파일에 디자인 된 레이아웃 가져오기
            val actionbarView: View = layoutInflater.inflate(R.layout.custom_actionbar_title, null)
            actionBar.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
            actionBar.setCustomView(actionbarView, layout)



            //커스팀 액션바 레이아웃에 있는 버튼에 이벤트 걸때는 아래처럼 사용
            val layoutBack = findViewById<View>(R.id.layout_back) as LinearLayout
            layoutBack.setOnClickListener { onBackPressed() }
            if (backTitle != null) {
                val backButtonTextView = actionbarView
                    .findViewById<View>(R.id.backButtonText) as TextView
                backButtonTextView.text = backTitle
            }
            if (title != null) {
                val titletv = actionbarView.findViewById<View>(R.id.title) as TextView
                titletv.text = title
            }
        }
    }

    fun initActionBar(title: String?, resid: Int) {
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(resources.getDrawable(R.drawable.action_bar))
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(resid)
            actionBar.setTitle(null)
            actionBar.setDisplayShowTitleEnabled(true)
            actionBar.setDisplayShowHomeEnabled(false)
            val textView = TextView(this)
            textView.text = title
            textView.setTextColor(Color.WHITE)
            textView.setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                resources.getDimension(R.dimen.title_size)
            )
            actionBar.setCustomView(
                textView, ActionBar.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.CENTER
                )
            )
            actionBar.setDisplayShowCustomEnabled(true)
        }
    }


    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == R.id.home) {
            onBackPressed()
        }
        return true
    }
}

