/*
 * Create by jhong on 2022. 10. 20.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.ui.main.board

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.sorizava.asrplayer.data.item.BoardItem
import com.sorizava.asrplayer.ui.base.BaseFragment
import com.sorizava.asrplayer.ui.main.NewInputToolbarIntegration
import kotlinx.android.synthetic.main.fragment_main_search_notice.*
import kotlinx.android.synthetic.main.fragment_main_search_notice.browserToolbar
import kotlinx.android.synthetic.main.fragment_main_search_notice.searchViewContainer
import kotlinx.android.synthetic.main.fragment_main_search_notice.urlInputBackgroundView
import kotlinx.android.synthetic.main.fragment_main_search_notice.urlInputContainerView
import mozilla.components.browser.domains.autocomplete.CustomDomainsProvider
import mozilla.components.browser.domains.autocomplete.ShippedDomainsProvider
import mozilla.components.browser.state.action.ContentAction
import mozilla.components.browser.state.state.SessionState
import mozilla.components.browser.state.state.TabSessionState
import mozilla.components.support.base.feature.ViewBoundFeatureWrapper
import mozilla.components.support.utils.ThreadUtils
import org.mozilla.focus.R
import org.mozilla.focus.activity.MainActivity
import org.mozilla.focus.databinding.FragmentMainSearchNoticeBinding
import org.mozilla.focus.ext.requireComponents
import org.mozilla.focus.locale.LocaleAwareAppCompatActivity
import org.mozilla.focus.searchsuggestions.SearchSuggestionsViewModel
import org.mozilla.focus.searchsuggestions.ui.SearchSuggestionsFragment
import org.mozilla.focus.state.AppAction
import org.mozilla.focus.state.Screen
import org.mozilla.focus.telemetry.TelemetryWrapper
import org.mozilla.focus.utils.*


class MainUIFragment : BaseFragment<FragmentMainSearchNoticeBinding>(
    FragmentMainSearchNoticeBinding::inflate),

    View.OnClickListener{

    private var selectedTabId: Int = 0

    private lateinit var boardPagerAdapter: TabBoardPagerAdapter

    private val toolbarIntegration = ViewBoundFeatureWrapper<NewInputToolbarIntegration>()

    private val shippedDomainsProvider = ShippedDomainsProvider()
    private val customDomainsProvider = CustomDomainsProvider()

    var tab: TabSessionState? = null
        private set

    private val isOverlay: Boolean
        get() = tab != null

    @Volatile
    private var isAnimating: Boolean = false

    companion object {
        fun newInstance() = MainUIFragment()

        private const val ARGUMENT_ANIMATION = "animation"

        private const val ANIMATION_BROWSER_SCREEN = "browser_screen"

        private const val ANIMATION_DURATION = 200

        private lateinit var searchSuggestionsViewModel: SearchSuggestionsViewModel
    }

    override fun initView() {
        browserToolbar.private = true
        toolbarIntegration.set(
            NewInputToolbarIntegration(
                browserToolbar,
                fragment = this@MainUIFragment,
                shippedDomainsProvider = shippedDomainsProvider,
                customDomainsProvider = customDomainsProvider
            ),
            owner = this,
            view = browserToolbar
        )

        if (urlInputContainerView != null) {
            OneShotOnPreDrawListener(urlInputContainerView) {
                animateFirstDraw()
                true
            }
        }

        browserToolbar.edit.typeface

        setupTabUI()
        setupUI()
    }

    private fun setupUI() {

        searchSuggestionsViewModel = ViewModelProvider(this).get(
            SearchSuggestionsViewModel::class.java)

        childFragmentManager.beginTransaction()
            .replace(searchViewContainer.id, SearchSuggestionsFragment.create())
            .commit()

        searchSuggestionsViewModel.selectedSearchSuggestion.observe(
            viewLifecycleOwner,
            Observer {
                val isSuggestion = searchSuggestionsViewModel.searchQuery.value != it
                it?.let {
                    if (searchSuggestionsViewModel.alwaysSearch) {
                        onSearch(it, false, true)
                    } else {
                        onSearch(it, isSuggestion)
                    }
                    searchSuggestionsViewModel.clearSearchSuggestion()
                }
            }
        )

        searchSuggestionsViewModel.autocompleteSuggestion.observe(viewLifecycleOwner) { text ->
            if (text != null) {
                searchSuggestionsViewModel.clearAutocompleteSuggestion()
                browserToolbar.setSearchTerms(text)
            }
        }

        binding.btnMore.setOnClickListener {
            (activity as LocaleAwareAppCompatActivity).openNotice(selectedTabId)
        }
    }

    private fun setupTabUI() {
        val list = listOf(
            BoardItem(0, getString(R.string.board_menu_notice)),
            BoardItem(1,  getString(R.string.board_menu_faq)),
            BoardItem(2, getString(R.string.board_menu_event)),
        )
        boardPagerAdapter = TabBoardPagerAdapter(childFragmentManager, viewLifecycleOwner.lifecycle)
        boardPagerAdapter.setItems(list)
        viewpager_board.adapter = boardPagerAdapter
        viewpager_board.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                selectedTabId = position
            }
        })
        TabLayoutMediator(tab_board, viewpager_board) { tab, position ->
            tab.text = list[position].name
        }.attach()
    }

    override fun initViewModelObserver() {
    }

    private fun animateFirstDraw() {
        if (ANIMATION_BROWSER_SCREEN == arguments?.getString(ARGUMENT_ANIMATION)) {
            playVisibilityAnimation(false)
        }
    }

    private fun onSearch(query: String, isSuggestion: Boolean = false, alwaysSearch: Boolean = false) {
        if (alwaysSearch) {
            val url = SearchUtils.createSearchUrl(context, query)
            openUrl(url, query)
        } else {
            val searchTerms = if (UrlUtils.isUrl(query)) null else query
            val searchUrl = if (searchTerms != null) {
                SearchUtils.createSearchUrl(context, searchTerms)
            } else {
                UrlUtils.normalize(query)
            }

            openUrl(searchUrl, searchTerms)
        }

        TelemetryWrapper.searchSelectEvent(isSuggestion)
    }

    private fun openUrl(url: String, searchTerms: String?) {
        when (url) {
            "focus:about" -> {
                requireComponents.appStore.dispatch(
                    AppAction.OpenSettings(Screen.Settings.Page.About)
                )
                return
            }
        }

        if (!searchTerms.isNullOrEmpty()) {
            tab?.let {
                requireComponents.store.dispatch(ContentAction.UpdateSearchTermsAction(it.id, searchTerms))
            }
        }

        val tab = tab
        if (tab != null) {
            requireComponents.sessionUseCases.loadUrl(url, tab.id)

            requireComponents.appStore.dispatch(AppAction.FinishEdit(tab.id))
        } else {
            val tabId = requireComponents.tabsUseCases.addTab(
                url,
                source = SessionState.Source.Internal.UserEntered,
                selectTab = true,
                private = true
            )

            if (!searchTerms.isNullOrEmpty()) {
                requireComponents.store.dispatch(ContentAction.UpdateSearchTermsAction(tabId, searchTerms))
            }
        }
    }

    internal fun onStartEditing() {
        if (tab != null) {
            searchViewContainer?.isVisible = true
        }
    }

    internal fun onCancelEditing() {
        handleDismiss()
    }

    private fun handleDismiss() {
        if (isOverlay) {
            animateAndDismiss()
        } else {
            // This is a bit hacky, but emulates what the legacy toolbar did before we replaced
            // it with `browser-toolbar`. First we clear the text and then we invoke the "text
            // changed" callback manually for this change.
            browserToolbar.edit.updateUrl("", false)
            onTextChange("")
        }
    }

    private fun animateAndDismiss() {
        ThreadUtils.assertOnUiThread()

        if (isAnimating) {
            // We are already animating some state change. Ignore all other requests.
            return
        }
        dismiss()
    }

    @Suppress("ComplexMethod")
    private fun playVisibilityAnimation(reverse: Boolean) {
        if (isAnimating) {
            // We are already animating, let's ignore another request.
            return
        }

        isAnimating = true

        val xyOffset = (
                if (isOverlay)
                    (urlInputContainerView?.layoutParams as FrameLayout.LayoutParams).bottomMargin
                else
                    0
                ).toFloat()

        if (urlInputBackgroundView != null) {
            val width = urlInputBackgroundView.width.toFloat()
            val height = urlInputBackgroundView.height.toFloat()

            val widthScale = if (isOverlay)
                (width + 2 * xyOffset) / width
            else
                1f

            val heightScale = if (isOverlay)
                (height + 2 * xyOffset) / height
            else
                1f

            if (!reverse) {
                urlInputBackgroundView?.pivotX = 0f
                urlInputBackgroundView?.pivotY = 0f
                urlInputBackgroundView?.scaleX = widthScale
                urlInputBackgroundView?.scaleY = heightScale
                urlInputBackgroundView?.translationX = -xyOffset
                urlInputBackgroundView?.translationY = -xyOffset
            }

            // Let the URL input use the full width/height and then shrink to the actual size
            urlInputBackgroundView.animate()
                .setDuration(ANIMATION_DURATION.toLong())
                .scaleX(if (reverse) widthScale else 1f)
                .scaleY(if (reverse) heightScale else 1f)
                .alpha((if (reverse && isOverlay) 0 else 1).toFloat())
                .translationX(if (reverse) -xyOffset else 0f)
                .translationY(if (reverse) -xyOffset else 0f)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        if (reverse) {
                            if (isOverlay) {
                                dismiss()
                            }
                        }

                        isAnimating = false
                    }
                })
        }
    }

    private fun dismiss() {
        requireComponents.appStore.dispatch(AppAction.FinishEdit(tab!!.id))
    }

    internal fun onTextChange(text: String) {
        searchSuggestionsViewModel.setSearchQuery(text)

        if (text.trim { it <= ' ' }.isEmpty()) {
            searchViewContainer?.visibility = View.GONE

            if (!isOverlay) {
                playVisibilityAnimation(true)
            }
        } else {
            searchViewContainer?.visibility = View.VISIBLE
        }
    }

    internal fun onCommit(input: String) {
        if (input.trim { it <= ' ' }.isNotEmpty()) {
            ViewUtils.hideKeyboard(browserToolbar)
            if (handleL10NTrigger(input)) return
            val (isUrl, url, searchTerms) = normalizeUrlAndSearchTerms(input)
            openUrl(url, searchTerms)
            TelemetryWrapper.urlBarEvent(isUrl)
            (activity as MainActivity).callEndAndStartTime(url)
        }
    }
    // This isn't that complex, and is only used for l10n screenshots.
    @Suppress("ComplexMethod")
    private fun handleL10NTrigger(input: String): Boolean {
        if (!AppConstants.isDevBuild) return false

        var triggerHandled = true

        when (input) {

            "l10n:tip:1" -> Log.d("","")//home_tips.scrollToPosition(TIP_ONE_CAROUSEL_POSITION)
            "l10n:tip:2" -> Log.d("","") // home_tips.scrollToPosition(TIP_TWO_CAROUSEL_POSITION)
            "l10n:tip:3" -> Log.d("","") //home_tips.scrollToPosition(TIP_THREE_CAROUSEL_POSITION)
            "l10n:tip:4" -> Log.d("","") //home_tips.scrollToPosition(TIP_FOUR_CAROUSEL_POSITION)
            "l10n:tip:5" -> Log.d("","") //home_tips.scrollToPosition(TIP_FIVE_CAROUSEL_POSITION)
            else -> triggerHandled = false
        }

        if (triggerHandled) {
            browserToolbar.displayMode()
            browserToolbar.editMode()
        }
        return triggerHandled
    }

    private fun normalizeUrlAndSearchTerms(input: String): Triple<Boolean, String, String?> {
        val isUrl = UrlUtils.isUrl(input)

        val url = if (isUrl)
            UrlUtils.normalize(input)
        else
            SearchUtils.createSearchUrl(context, input)

        val searchTerms = if (isUrl)
            null
        else
            input.trim { it <= ' ' }
        return Triple(isUrl, url, searchTerms)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.dismissView -> handleDismiss()
        }
    }
}