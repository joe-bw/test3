package kr.co.sorizava.asrplayer.helper

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.widget.TextView

object PlayerCaptionHelper {

    /**
     * Custom subtitleView font update
     *
     * @param context Context
     * @param subtitleView SubtitleView
     * @param fontPath font path
     */
    fun setSubtitleViewFont(
        context: Context,
        subtitleView: TextView?,
        fontPath: String?
    ) {
        if (subtitleView == null) {
            return
        }
        val typeface: Typeface = if (fontPath != null && fontPath != "") {
            Typeface.createFromAsset(context.assets, fontPath)
        } else {
            Typeface.defaultFromStyle(Typeface.BOLD)
        }
        subtitleView.typeface = typeface
    }

    /**
     * Get color with alpha
     *
     * @param color Color
     * @param ratio alpha ratio
     * @return Color
     */
    fun getColorWithAlpha(color: Int, ratio: Float): Int {
        var newColor: Int
        val alpha = Math.round(Color.alpha(color) * ratio)
        val r = Color.red(color)
        val g = Color.green(color)
        val b = Color.blue(color)
        newColor = Color.argb(alpha, r, g, b)
        return newColor
    }
}
