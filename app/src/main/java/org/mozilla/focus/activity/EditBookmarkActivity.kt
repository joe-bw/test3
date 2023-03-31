package org.mozilla.focus.activity

import android.content.Intent
import android.os.Bundle
import android.text.*
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.OnFocusChangeListener
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_edit_bookmark.*
import org.mozilla.focus.R
import org.mozilla.focus.bookmark.BookmarkData
import org.mozilla.focus.bookmark.BookmarkProvider
import org.mozilla.focus.locale.LocaleAwareAppCompatActivity


private const val SAVE_ACTION_ID = 1
const val ITEM_UUID_KEY = "ITEM_UUID_KEY"

class EditBookmarkActivity : LocaleAwareAppCompatActivity() {

    private val itemId: String by lazy { intentData(ITEM_UUID_KEY) }

    fun intentData(key: String): String {
        return intent.getStringExtra(key) ?: "intent is null"
    }
    private lateinit var bookmark: BookmarkData

    private val editTextName: EditText by lazy { findViewById<EditText>(R.id.bookmark_name) }
    private val editTextLocation: EditText by lazy { findViewById<EditText>(R.id.bookmark_location) }
    private val labelName: TextView by lazy { findViewById<TextView>(R.id.bookmark_name_label) }
    private val labelLocation: TextView by lazy { findViewById<TextView>(R.id.bookmark_location_label) }
    private val originalName: String by lazy { bookmark.title }
    private val originalLocation: String by lazy { bookmark.url }
    private lateinit var menuItemSave: MenuItem
    private var nameChanged: Boolean = false
    private var locationChanged: Boolean = false
    private var locationEmpty: Boolean = false
    private val buttonClearName: ImageButton by lazy { findViewById<ImageButton>(R.id.bookmark_name_clear) }
    private val buttonClearLocation: ImageButton by lazy { findViewById<ImageButton>(R.id.bookmark_location_clear) }
    private val nameWatcher: TextWatcher = object : TextWatcher {
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun afterTextChanged(s: Editable?) {
            if (::bookmark.isInitialized) {
                nameChanged = s.toString() != originalName
                setupMenuItemSave()
            }
        }
    }
    private val locationWatcher: TextWatcher = object : TextWatcher {
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun afterTextChanged(s: Editable?) {
            if (::bookmark.isInitialized) {
                locationChanged = s.toString() != originalLocation
                locationEmpty = TextUtils.isEmpty(s)
                setupMenuItemSave()
            }
        }
    }

    private val focusChangeListener: OnFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
        when (v.id) {
            R.id.bookmark_location -> {
                labelLocation.isActivated = hasFocus
            }
            R.id.bookmark_name -> {
                labelName.isActivated = hasFocus
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_edit_bookmark)
        setSupportActionBar(toolbar)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = getString(R.string.title_activity_edit_bookmark);
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        editTextName.addTextChangedListener(nameWatcher)
        editTextLocation.addTextChangedListener(locationWatcher)
        editTextName.onFocusChangeListener = focusChangeListener
        editTextLocation.onFocusChangeListener = focusChangeListener
        buttonClearName.setOnClickListener {
            editTextName.text.clear()
        }
        buttonClearLocation.setOnClickListener {
            editTextLocation.text.clear()
        }

        bookmark = BookmarkProvider.getBookmarkById(this.contentResolver, itemId.toLong());

        editTextName.setText(bookmark.title)
        editTextLocation.setText(bookmark.url)
    }

    override fun onDestroy() {
        editTextName.removeTextChangedListener(nameWatcher)
        editTextLocation.removeTextChangedListener(locationWatcher)
        super.onDestroy()
    }

    override fun applyLocale() {
    }

    private fun isSaveValid(): Boolean {
        return !locationEmpty && (nameChanged || locationChanged)
    }

    fun setupMenuItemSave() {
        if (::menuItemSave.isInitialized) {
            menuItemSave.isEnabled = isSaveValid()
        }
    }

    private fun setToolbarActionTextColor(menu: Menu, color: Int) {
        val tb = findViewById<Toolbar>(R.id.toolbar)
        tb?.let { toolbar ->
            toolbar.post {
                val view = findViewById<View>(SAVE_ACTION_ID)
                if (view is TextView) {
                    view.setTextColor(ContextCompat.getColor(this, color))
                } else {
                    val mi = menu.findItem(SAVE_ACTION_ID)
                    mi?.let {
                        val newTitle: Spannable = SpannableString(it.title.toString())
                        val newColor = ContextCompat.getColor(this, color)

                        newTitle.setSpan(ForegroundColorSpan(newColor),
                                0, newTitle.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        it.title = newTitle
                    }
                }
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuItemSave = menu.add(Menu.NONE, SAVE_ACTION_ID, Menu.NONE, R.string.bookmark_edit_save)

        menuItemSave.setShowAsAction(android.view.MenuItem.SHOW_AS_ACTION_ALWAYS)

        setupMenuItemSave()

        setToolbarActionTextColor(menu, R.color.black)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            SAVE_ACTION_ID -> {
                BookmarkProvider.updateItem(this.contentResolver, bookmark.uid, editTextName.text.toString(), editTextLocation.text.toString())
                Toast.makeText(this, R.string.bookmark_edit_success, Toast.LENGTH_LONG).show()
                finish()
            }
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}
