/* -*- Mode: Java; c-basic-offset: 4; tab-width: 4; indent-tabs-mode: nil; -*-
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.mozilla.focus.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import android.util.AttributeSet
import android.view.View

import org.mozilla.focus.R
import org.mozilla.focus.fragment.BookmarksFragment
import org.mozilla.focus.locale.LocaleAwareAppCompatActivity

/**
 * A generic activity that supports showing additional information in a WebView. This is useful
 * for showing any web based content, including About/Help/Rights, and also SUMO pages.
 */
class BookmarksActivity : LocaleAwareAppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookmarks)

        supportFragmentManager.beginTransaction()
                .replace(R.id.bookmarksfragment, BookmarksFragment.newInstance())
                .commit()
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = getString(R.string.menu_bookmarks)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }
    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        return super.onCreateView(name, context, attrs)
    }

    override fun applyLocale() {
        // We don't care about this
    }
}