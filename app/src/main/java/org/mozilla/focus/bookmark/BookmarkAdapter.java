package org.mozilla.focus.bookmark;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import org.mozilla.focus.R;
import org.mozilla.focus.fragment.PanelFragment;
import org.mozilla.focus.fragment.PanelFragmentStatusListener;
import org.mozilla.focus.site.SiteItemViewHolder;

public class BookmarkAdapter extends RecyclerView.Adapter<SiteItemViewHolder> {
    private final Context mContext;
    private Cursor mCursor;
    private BookmarkPanelListener mListener;

    private int mIdColumnIndex;
    private int mTitleColumnIndex;
    private int mUrlColumnIndex;
    private int mColorColumnIndex;

    public BookmarkAdapter(Context context, BookmarkPanelListener listener) {
        this.mContext = context;
        this.mListener = listener;
        setHasStableIds(true);
    }

    public void swapCursor(Cursor cursor) {

        if (cursor == mCursor) {
            return;
        }
        if (mCursor != null) {
            mCursor.close();
        }

        mCursor = cursor;

        if (mCursor != null && mCursor.getCount() == 0) {
            mListener.onStatus(PanelFragment.VIEW_TYPE_EMPTY);
        }
        else {
            mListener.onStatus(PanelFragment.VIEW_TYPE_NON_EMPTY);
        }

        if (mCursor != null) {
            mIdColumnIndex = cursor.getColumnIndexOrThrow(BookmarkProvider.Columns._ID);
            mTitleColumnIndex = cursor.getColumnIndexOrThrow(BookmarkProvider.Columns.TITLE);
            mUrlColumnIndex = cursor.getColumnIndexOrThrow(BookmarkProvider.Columns.URL);
            mColorColumnIndex = cursor.getColumnIndexOrThrow(BookmarkProvider.Columns.COLOR);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SiteItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_website, parent, false);
        return new SiteItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SiteItemViewHolder holder, int position) {

        if (!mCursor.moveToPosition(position)) {
            return;
        }
        long id = mCursor.getLong(mIdColumnIndex);
        String title = mCursor.getString(mTitleColumnIndex);
        String url = mCursor.getString(mUrlColumnIndex);
        int color = mCursor.getInt(mColorColumnIndex);

        holder.rootView.setTag(id);
        holder.textMain.setText(title);
        holder.textSecondary.setText(url);
        holder.rootView.setOnClickListener(v -> {
            mListener.onItemClicked(url);
        });
        final PopupMenu popupMenu = new PopupMenu(holder.btnMore.getContext(), holder.btnMore);
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            if (menuItem.getItemId() == R.id.remove) {
                mListener.onItemDeleted(id);
            }
            if (menuItem.getItemId() == R.id.edit) {
                mListener.onItemEdited(id);
            }
            return false;
        });
        popupMenu.inflate(R.menu.menu_bookmarks);
        holder.btnMore.setOnClickListener(v -> {
            popupMenu.show();
        });
    }

    @Override
    public int getItemCount() {
        return mCursor != null ? mCursor.getCount() : 0;
    }

    @Override
    public long getItemId(int position) {
        return mCursor.moveToPosition(position) ? mCursor.getLong(mIdColumnIndex) : -1;
    }

    public interface BookmarkPanelListener extends PanelFragmentStatusListener {
        void onItemClicked(String url);

        void onItemDeleted(Long uid);

        void onItemEdited(Long uid);
    }
}