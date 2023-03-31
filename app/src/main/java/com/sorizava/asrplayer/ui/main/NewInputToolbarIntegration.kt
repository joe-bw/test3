/*
 * Create by jhong on 2022. 7. 28.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.ui.main

import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.sorizava.asrplayer.extension.textSizeByDensity
import com.sorizava.asrplayer.ui.main.board.MainUIFragment
import mozilla.components.browser.domains.autocomplete.CustomDomainsProvider
import mozilla.components.browser.domains.autocomplete.DomainAutocompleteResult
import mozilla.components.browser.domains.autocomplete.ShippedDomainsProvider
import mozilla.components.browser.toolbar.BrowserToolbar
import mozilla.components.concept.toolbar.Toolbar
import mozilla.components.support.base.feature.LifecycleAwareFeature
import org.mozilla.focus.R
import org.mozilla.focus.utils.Settings


class NewInputToolbarIntegration(
    toolbar: BrowserToolbar,
    fragment: MainUIFragment,
    shippedDomainsProvider: ShippedDomainsProvider,
    customDomainsProvider: CustomDomainsProvider
) : LifecycleAwareFeature {
    private val settings = Settings.getInstance(toolbar.context)

    private var useShippedDomainProvider: Boolean = false
    private var useCustomDomainProvider: Boolean = false

    init {
        with(toolbar.display) {
            indicators = emptyList()
            hint = fragment.getString(R.string.urlbar_hint)
            colors = toolbar.display.colors.copy(
                hint = ContextCompat.getColor(toolbar.context, R.color.primaryText),
                text = ContextCompat.getColor(toolbar.context, R.color.primaryText)
            )
        }
        toolbar.edit.hint = fragment.getString(R.string.urlbar_hint)
        toolbar.private = true
        toolbar.edit.colors = toolbar.edit.colors.copy(
            hint = ContextCompat.getColor(toolbar.context, R.color.primaryText),
            text = ContextCompat.getColor(toolbar.context, R.color.primaryText)
        )

        // 검색창 텍스트 사이즈 고정
        val dp = 15.textSizeByDensity()

        toolbar.display.textSize = dp
        toolbar.edit.textSize = dp

        toolbar.setOnEditListener(object : Toolbar.OnEditListener {
            override fun onStartEditing() {
                fragment.onStartEditing()
            }

            override fun onCancelEditing(): Boolean {
                fragment.onCancelEditing()
                return true
            }

            override fun onTextChanged(text: String) {
                fragment.onTextChange(text)
            }
        })

        toolbar.setOnUrlCommitListener { url ->
            fragment.onCommit(url)
            false
        }

        toolbar.setAutocompleteListener { text, delegate ->
            var result: DomainAutocompleteResult? = null
            if (useCustomDomainProvider) {
                result = customDomainsProvider.getAutocompleteSuggestion(text)
            }

            if (useShippedDomainProvider && result == null) {
                result = shippedDomainsProvider.getAutocompleteSuggestion(text)
            }

            if (result != null) {
                delegate.applyAutocompleteResult(
                    mozilla.components.concept.toolbar.AutocompleteResult(
                        result.input, result.text, result.url, result.source, result.totalItems
                    )
                )
            } else {
                delegate.noAutocompleteResult(text)
            }
        }

        // Use the same background for display/edit modes.
        val urlBackground = ResourcesCompat.getDrawable(
            fragment.resources,
            R.color.background_edit,
            fragment.context?.theme
        )

        toolbar.display.setUrlBackground(urlBackground)
        toolbar.edit.setUrlBackground(urlBackground)
    }

    override fun start() {
        useCustomDomainProvider = settings.shouldAutocompleteFromCustomDomainList()
        useShippedDomainProvider = settings.shouldAutocompleteFromShippedDomainList()
    }

    override fun stop() {
        // Do nothing
    }
}
