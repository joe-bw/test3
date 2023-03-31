package kr.co.sorizava.asrplayer

import android.content.Context
import android.util.TypedValue

object UIUtils {

    /**
     * dp to px
     *
     * @param context
     * @return
     */
    fun dp2px(context: Context, dpVal: Float): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
            dpVal, context.resources.displayMetrics).toInt()
    }

    /**
     * sp to px
     *
     * @param context
     * @return
     */
    fun sp2px(context: Context, spVal: Float): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
            spVal, context.resources.displayMetrics).toInt()
    }

    /**
     * px to dp
     *
     * @param context
     * @param pxVal
     * @return
     */
    fun px2dp(context: Context, pxVal: Float): Float {
        val scale = context.resources.displayMetrics.density
        return pxVal / scale
    }

    /**
     * px to sp
     *
     * @param pxVal
     * @return
     */
    fun px2sp(context: Context, pxVal: Float): Float {
        return pxVal / context.resources.displayMetrics.scaledDensity
    }
}