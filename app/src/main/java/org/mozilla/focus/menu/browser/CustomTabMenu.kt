/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.mozilla.focus.menu.browser

import android.content.Context
import android.graphics.Typeface
import android.widget.Toast
import mozilla.components.browser.menu.WebExtensionBrowserMenuBuilder
import mozilla.components.browser.menu.item.*
import mozilla.components.browser.state.selector.findCustomTab
import mozilla.components.browser.state.state.CustomTabSessionState
import mozilla.components.browser.state.store.BrowserStore
import mozilla.components.feature.webcompat.reporter.WebCompatReporterFeature
import org.mozilla.focus.R
import org.mozilla.focus.bookmark.BookmarkProvider
import org.mozilla.focus.extension.showToast
import org.mozilla.focus.menu.ToolbarMenu
import org.mozilla.focus.theme.resolveAttribute
import org.mozilla.focus.utils.ToastMessage
import org.mozilla.focus.utils.UrlUtils

class CustomTabMenu(
    private val context: Context,
    private val store: BrowserStore,
    private val currentTabId: String,
    private val onItemTapped: (ToolbarMenu.Item) -> Unit = {}
) : ToolbarMenu {

    private val selectedSession: CustomTabSessionState?
        get() = store.state.findCustomTab(currentTabId)

    override val menuBuilder by lazy {
        WebExtensionBrowserMenuBuilder(
            items = menuItems,
            store = store,
            style = WebExtensionBrowserMenuBuilder.Style(

                addonsManagerMenuItemDrawableRes = R.drawable.ic_setting
            )
        )
    }

    override val menuToolbar by lazy {
        val back = BrowserMenuItemToolbar.TwoStateButton(
            primaryImageResource = R.drawable.mozac_ic_back,
            primaryContentDescription = context.getString(R.string.content_description_back),
            primaryImageTintResource = context.theme.resolveAttribute(R.color.black),
            isInPrimaryState = {
                selectedSession?.content?.canGoBack ?: false
            },
            secondaryImageTintResource = context.theme.resolveAttribute(R.attr.disabled),
            disableInSecondaryState = true,
            longClickListener = { onItemTapped.invoke(ToolbarMenu.Item.Back) }
        ) {
            onItemTapped.invoke(ToolbarMenu.Item.Back)
        }

        val forward = BrowserMenuItemToolbar.TwoStateButton(
            primaryImageResource = R.drawable.mozac_ic_forward,
            primaryContentDescription = context.getString(R.string.content_description_forward),
            primaryImageTintResource = context.theme.resolveAttribute(R.color.black),
            isInPrimaryState = {
                selectedSession?.content?.canGoForward ?: true
            },
            secondaryImageTintResource = context.theme.resolveAttribute(R.attr.disabled),
            disableInSecondaryState = true,
            longClickListener = { onItemTapped.invoke(ToolbarMenu.Item.Forward) }
        ) {
            onItemTapped.invoke(ToolbarMenu.Item.Forward)
        }

        val bookmark = BrowserMenuItemToolbar.TwoStateButton(
            primaryImageResource = R.drawable.ic_bookmark_outline,
            primaryContentDescription = context.getString(R.string.content_description_bookmark_add),
            primaryImageTintResource = context.theme.resolveAttribute(R.color.black),
            isInPrimaryState = {
                !BookmarkProvider.isBookmarkedUrl(context.contentResolver, selectedSession?.content?.url as String)
            },
            secondaryImageResource = R.drawable.ic_bookmark_filled,
            secondaryContentDescription = context.getString(R.string.content_description_bookmark_remove),
            secondaryImageTintResource = context.theme.resolveAttribute(R.attr.primaryText),
            disableInSecondaryState = false,
            longClickListener = {  }
        ) {
            val currentUrl = selectedSession?.content?.url as String
            val currentTitle = selectedSession?.content?.title as String

            val isCurrentUrlBookmarked = BookmarkProvider.isBookmarkedUrl(context.contentResolver, currentUrl)

            if (isCurrentUrlBookmarked) {
                BookmarkProvider.deleteBookmarkByUrl(context.contentResolver, currentUrl)
                context.showToast(ToastMessage(R.string.bookmark_removed, duration = Toast.LENGTH_LONG))
            } else {
                if (!currentUrl.isNullOrEmpty()) {
                    val title = currentTitle.takeUnless { it.isNullOrEmpty() }
                        ?: UrlUtils.stripCommonSubdomains(UrlUtils.stripHttp(currentUrl))

                    BookmarkProvider.addOrUpdateItem(context.contentResolver, title, currentUrl, 0);
                    context.showToast(ToastMessage(R.string.bookmark_saved, duration = Toast.LENGTH_LONG))
                }
            }
        }

        val refresh = BrowserMenuItemToolbar.TwoStateButton(
            primaryImageResource = R.drawable.mozac_ic_refresh,
            primaryContentDescription = context.getString(R.string.content_description_reload),
            primaryImageTintResource = context.theme.resolveAttribute(R.color.black),
            isInPrimaryState = {
                selectedSession?.content?.loading == false
            },
            secondaryImageResource = R.drawable.mozac_ic_stop,
            secondaryContentDescription = context.getString(R.string.content_description_stop),
            secondaryImageTintResource = context.theme.resolveAttribute(R.attr.primaryText),
            disableInSecondaryState = false,
            longClickListener = { onItemTapped.invoke(ToolbarMenu.Item.Reload) }
        ) {
            if (selectedSession?.content?.loading == true) {
                onItemTapped.invoke(ToolbarMenu.Item.Stop)
            } else {
                onItemTapped.invoke(ToolbarMenu.Item.Reload)
            }
        }
        BrowserMenuItemToolbar(listOf(back, forward, bookmark, refresh))
    }

    private val menuItems by lazy {
        val findInPage = BrowserMenuImageText(
            label = context.getString(R.string.find_in_page),
            imageResource = R.drawable.mozac_ic_search
        ) {
            onItemTapped.invoke(ToolbarMenu.Item.FindInPage)
        }

        val desktopMode = BrowserMenuImageSwitch(
            imageResource = R.drawable.mozac_ic_device_desktop,
            label = context.getString(R.string.preference_performance_request_desktop_site2),
            initialState = {
                selectedSession?.content?.desktopMode ?: true
            }
        ) { checked ->
            onItemTapped.invoke(ToolbarMenu.Item.RequestDesktop(checked))
        }

        val reportSiteIssue = WebExtensionPlaceholderMenuItem(
            id = WebCompatReporterFeature.WEBCOMPAT_REPORTER_EXTENSION_ID,
            iconTintColorResource = context.theme.resolveAttribute(R.attr.primaryText)
        )

        // add: sorizava bookmark
        val openBookmarks = BrowserMenuImageText(
            label = context.getString(R.string.menu_bookmarks),
            imageResource = R.drawable.ic_bookmark_outline
        ) {
            onItemTapped.invoke(ToolbarMenu.Item.Bookmarks)
        }

        val addToHomescreen = BrowserMenuImageText(
            label = context.getString(R.string.menu_add_to_home_screen),
            imageResource = R.drawable.mozac_ic_add_to_home_screen
        ) {
            onItemTapped.invoke(ToolbarMenu.Item.AddToHomeScreen)
        }

        val appName = context.getString(R.string.app_name)
        val openInFocus = SimpleBrowserMenuItem(
            label = context.getString(R.string.menu_open_with_default_browser2, appName)
        ) {
            onItemTapped.invoke(ToolbarMenu.Item.OpenInBrowser)
        }

        val openInApp = SimpleBrowserMenuItem(
            label = context.getString(R.string.menu_open_with_a_browser2)
        ) {
            onItemTapped.invoke(ToolbarMenu.Item.OpenInApp)
        }

        val poweredBy = BrowserMenuCategory(
            label = context.getString(R.string.menu_custom_tab_branding, context.getString(R.string.app_name)),
            textSize = CAPTION_TEXT_SIZE,
            textColorResource = context.theme.resolveAttribute(R.attr.primaryText),
            textStyle = Typeface.NORMAL
        )

        listOfNotNull(
            menuToolbar,
            BrowserMenuDivider(),
            findInPage,
            desktopMode,
            reportSiteIssue,
            BrowserMenuDivider(),
            openBookmarks,
            addToHomescreen,
            openInFocus,
            openInApp,
            poweredBy
        )
    }

    companion object {
        private const val CAPTION_TEXT_SIZE = 12f
    }
}
