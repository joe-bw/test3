package org.mozilla.focus.fragment

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.provider.BaseColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Job
import mozilla.components.browser.state.selector.findTab
import mozilla.components.browser.state.state.SessionState
import mozilla.components.browser.state.state.TabSessionState
import org.mozilla.focus.FocusApplication
import org.mozilla.focus.R
import org.mozilla.focus.activity.EditBookmarkActivity
import org.mozilla.focus.bookmark.BookmarkAdapter
import org.mozilla.focus.bookmark.BookmarkProvider
import org.mozilla.focus.bookmark.BookmarkProvider.Columns.Companion.CONTENT_URI
import org.mozilla.focus.bookmark.BookmarkProvider.Companion.deleteBookmarkById
import org.mozilla.focus.databinding.FragmentAboutBinding
import org.mozilla.focus.databinding.FragmentBookmarksBinding
import org.mozilla.focus.ext.components
import org.mozilla.focus.ext.requireComponents
import org.mozilla.focus.searchsuggestions.SearchSuggestionsViewModel
import org.mozilla.focus.settings.BaseSettingsLikeFragment
import org.mozilla.focus.settings.BaseSettingsLikeRecycleViewFragment
import org.mozilla.focus.state.AppAction
import org.mozilla.geckoview.BuildConfig

class BookmarkListFragment : BaseSettingsLikeRecycleViewFragment(),
    BookmarkAdapter.BookmarkPanelListener,
    LoaderManager.LoaderCallbacks<Cursor>
{
    companion object {
        val FRAGMENT_TAG = "bookmark-list"

        val ARGUMENT_SESSION_UUID = "sesssion_uuid"

        fun create() = BookmarkListFragment()
    }

    var tab: TabSessionState? = null
        private set

    private var recyclerView: RecyclerView? = null
    private var emptyView: View? = null
    private var adapter: BookmarkAdapter? = null

    private var bookmarkURL: String = ""

    private val openBookmarkURL = {
        val tabId = requireContext().components.tabsUseCases.addTab(
            url = bookmarkURL,
            source = SessionState.Source.Internal.Menu,
            selectTab = true,
            private = true
        )
        requireContext().components.appStore.dispatch(AppAction.OpenTab(tabId))
    }

    fun newInstance(): BookmarksFragment {
        return BookmarksFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Get session from session manager if there's a session UUID in the fragment's arguments
        arguments?.getString(ARGUMENT_SESSION_UUID)?.let { id ->
            tab = requireComponents.store.state.findTab(id)
        }
    }

    override fun onResume() {
        super.onResume()
        updateTitle("BookmarkListFragment")

        LoaderManager.getInstance(this).initLoader(0, null, this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val v = inflater.inflate(R.layout.fragment_bookmarks, container, false)
        recyclerView = v.findViewById(R.id.recyclerview)
        emptyView = v.findViewById(R.id.empty_view_container)
        return v
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        return CursorLoader(
            requireContext(), CONTENT_URI,
            null, null, null, BaseColumns._ID + " DESC"
        )
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        adapter!!.swapCursor(data)
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        adapter!!.swapCursor(null)
    }

    @Suppress("DEPRECATION")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val layoutManager = LinearLayoutManager(activity)
        adapter = BookmarkAdapter(context, this)
        recyclerView!!.adapter = adapter
        recyclerView!!.layoutManager = layoutManager
    }

    fun tryLoadMore() {
        // Do nothing for now.
    }

    override fun onStatus(status: Int) {
        if (PanelFragment.VIEW_TYPE_EMPTY == status) {
            recyclerView!!.visibility = View.GONE
            emptyView!!.visibility = View.VISIBLE
        } else if (PanelFragment.VIEW_TYPE_NON_EMPTY == status) {
            recyclerView!!.visibility = View.VISIBLE
            emptyView!!.visibility = View.GONE
        } else {
            recyclerView!!.visibility = View.GONE
            emptyView!!.visibility = View.GONE
        }
    }

    override fun onItemClicked(url: String?) {
        val tabId = requireContext().components.tabsUseCases.addTab(
            url = bookmarkURL,
            source = SessionState.Source.Internal.Menu,
            selectTab = true,
            private = true
        )
        requireContext().components.appStore.dispatch(AppAction.OpenTab(tabId))
    }

    override fun onItemDeleted(id: Long?) {
        deleteBookmarkById(requireContext().contentResolver, id!!)
    }

    override fun onItemEdited(id: Long) {
        startActivity(
            Intent(requireContext(), EditBookmarkActivity::class.java).putExtra(
                "ITEM_UUID_KEY",
                id.toString()
            )
        )
    }
}
