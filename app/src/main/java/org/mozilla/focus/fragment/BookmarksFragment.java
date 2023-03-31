/* -*- Mode: Java; c-basic-offset: 4; tab-width: 4; indent-tabs-mode: nil; -*-
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.focus.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.mozilla.focus.FocusApplication;
import org.mozilla.focus.R;
import org.mozilla.focus.activity.EditBookmarkActivity;
import org.mozilla.focus.activity.EditBookmarkActivityKt;
import org.mozilla.focus.bookmark.BookmarkAdapter;
import org.mozilla.focus.bookmark.BookmarkProvider;

import javax.annotation.Nonnull;

public class BookmarksFragment extends PanelFragment implements BookmarkAdapter.BookmarkPanelListener, LoaderManager.LoaderCallbacks<Cursor> {

    private RecyclerView recyclerView;
    private View emptyView;
    private BookmarkAdapter adapter;

    public static BookmarksFragment newInstance() {
        return new BookmarksFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        //  ExtensionKt.appComponent(this).inject(this);
        super.onCreate(savedInstanceState);

        LoaderManager.getInstance(this).initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), BookmarkProvider.Columns.Companion.getCONTENT_URI(),
                null, null, null, BaseColumns._ID + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    @Override
    public View onCreateView(@Nonnull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bookmarks, container, false);
        recyclerView = v.findViewById(R.id.recyclerview);
        emptyView = v.findViewById(R.id.empty_view_container);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        adapter = new BookmarkAdapter(getContext(), this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void tryLoadMore() {
        // Do nothing for now.
    }

    @Override
    public void onStatus(int status) {
        if (VIEW_TYPE_EMPTY == status) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else if (VIEW_TYPE_NON_EMPTY == status) {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemClicked(String url) {
        FocusApplication application = (FocusApplication) getActivity().getApplicationContext();
        application.goUrl(url);
        getActivity().finish();
    }

    @Override
    public void onItemDeleted(Long id) {
        BookmarkProvider.Companion.deleteBookmarkById(getContext().getContentResolver(), id);
    }

    @Override
    public void onItemEdited(Long id) {
        startActivity(new Intent(getContext(), EditBookmarkActivity.class).putExtra(EditBookmarkActivityKt.ITEM_UUID_KEY, id.toString()));
    }
}
