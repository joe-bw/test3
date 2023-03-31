package org.mozilla.focus.settings

import android.os.Bundle
import android.view.View
import androidx.annotation.IntDef
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceFragmentCompat
import org.mozilla.focus.R
import org.mozilla.focus.fragment.PanelFragment
import org.mozilla.focus.fragment.PanelFragment.ViewStatus
import org.mozilla.focus.fragment.PanelFragmentStatusListener
import org.mozilla.focus.utils.StatusBarUtils

/**
 * Similar behavior as [BaseSettingsFragment], but doesn't extend [PreferenceFragmentCompat] and is
 * a regular [Fragment] instead.
 */
abstract class BaseSettingsLikeRecycleViewFragment : Fragment(), PanelFragmentStatusListener {

    @IntDef(
        PanelFragment.VIEW_TYPE_EMPTY,
        PanelFragment.VIEW_TYPE_NON_EMPTY,
        PanelFragment.ON_OPENING
    )
    annotation class ViewStatus

    fun updateTitle(title: String) {
        (requireActivity() as AppCompatActivity).supportActionBar?.title = title
    }

    fun updateTitle(@StringRes titleResource: Int) {
        updateTitle(getString(titleResource))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val statusBarView = view.findViewById<View>(R.id.status_bar_background)
        StatusBarUtils.getStatusBarHeight(statusBarView) { statusBarHeight ->
            statusBarView.layoutParams.height = statusBarHeight
            statusBarView.setBackgroundColor(
                ContextCompat.getColor(
                    view.context,
                    R.color.statusBarBackground
                )
            )
        }

        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)

        val activity = requireActivity() as AppCompatActivity
        activity.setSupportActionBar(toolbar)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    abstract override fun onStatus(@PanelFragment.ViewStatus status: Int)
}
