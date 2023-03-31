package org.mozilla.focus.bookmark

import android.content.*
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import android.provider.BaseColumns
import org.mozilla.focus.utils.requireContextExt
import android.R.id

class BookmarkProvider : ContentProvider() {
    companion object {
        private const val MATCH_ALL = 0
        private const val MATCH_ID = 1
        private val sURIMatcher = UriMatcher(UriMatcher.NO_MATCH)

        fun addOrUpdateItem(resolver: ContentResolver, title: String?, url: String,
                            color: Int) {
            var existingId: Long = -1
            val cursor = resolver.query(Columns.CONTENT_URI, arrayOf(BaseColumns._ID),
                Columns.URL + "=?", arrayOf(url), null)
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    existingId = cursor.getLong(0)
                }
                cursor.close()
            }
            val values = ContentValues()
            values.put(Columns.TITLE, title)
            values.put(Columns.COLOR, color)
            if (existingId >= 0) {
                resolver.update(ContentUris.withAppendedId(Columns.CONTENT_URI, existingId),
                    values, null, null)
            } else {
                values.put(Columns.URL, url)
                resolver.insert(Columns.CONTENT_URI, values)
            }
        }

        fun updateItem(resolver: ContentResolver, id: Long, title: String?, url: String?) {
            val values = ContentValues()
            values.put(Columns.TITLE, title)
            values.put(Columns.URL, url)
            resolver.update(ContentUris.withAppendedId(Columns.CONTENT_URI, id), values, null, null)
        }

        fun isBookmarkedUrl(resolver: ContentResolver, url: String): Boolean {
            var existingId: Long = -1
            val cursor = resolver.query(Columns.CONTENT_URI, arrayOf(BaseColumns._ID),
                Columns.URL + "=?", arrayOf(url), null)
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    existingId = cursor.getLong(0)
                }
                cursor.close()
            }
            if (existingId >= 0) {
                return true
            }

            return false
        }

        fun getBookmarkById(resolver: ContentResolver, id: Long): BookmarkData {

            val cursor = resolver.query(Columns.CONTENT_URI, null,
                BaseColumns._ID + "=?", arrayOf(id.toString()), null)
            if (cursor != null) {
                if (cursor.moveToFirst()) {

                    val idColumnIndex: Int = cursor.getColumnIndexOrThrow(BaseColumns._ID)
                    val titleColumnIndex: Int = cursor.getColumnIndexOrThrow(Columns.TITLE)
                    val urlColumnIndex: Int = cursor.getColumnIndexOrThrow(Columns.URL)

                    val uid: Long = cursor.getLong(idColumnIndex)
                    val title: String = cursor.getString(titleColumnIndex)
                    val url: String = cursor.getString(urlColumnIndex)

                    cursor.close()
                    return BookmarkData(uid, url, title)
                }
                cursor.close()
            }
            return BookmarkData(0, "", "")
        }

        fun deleteBookmarkById(resolver: ContentResolver, id: Long) {
            val uri = ContentUris.withAppendedId(Columns.CONTENT_URI, id)
            resolver.delete(uri, null, null)
        }

        fun deleteBookmarkByUrl(resolver: ContentResolver, url: String) {
            var existingId: Long = -1
            val cursor = resolver.query(Columns.CONTENT_URI, arrayOf(BaseColumns._ID),
                Columns.URL + "=?", arrayOf(url), null)
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    existingId = cursor.getLong(0)
                }
                cursor.close()
            }
            if (existingId >= 0) {
                val uri = ContentUris.withAppendedId(Columns.CONTENT_URI, existingId)
                resolver.delete(uri, null, null)
            }
        }

        init {
            sURIMatcher.addURI(Columns.AUTHORITY, "bookmark", MATCH_ALL)
            sURIMatcher.addURI(Columns.AUTHORITY, "bookmark/#", MATCH_ID)
        }
    }

    private lateinit var mDbHelper: BookmarkDbHelper
    override fun onCreate(): Boolean {
        mDbHelper = BookmarkDbHelper(context)
        return true
    }

    override fun query(uri: Uri, projection: Array<String>?,
                       selection: String?, selectionArgs: Array<String>?,
                       sortOrder: String?): Cursor? {
        val qb = SQLiteQueryBuilder()
        val match = sURIMatcher.match(uri)
        qb.tables = BookmarkDbHelper.DB_TABLE_BOOKMARKS
        when (match) {
            MATCH_ALL -> {
            }
            MATCH_ID -> qb.appendWhere(BaseColumns._ID + " = " + uri.lastPathSegment)
            else -> return null
        }
        val db = mDbHelper.readableDatabase
        val ret = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder)
        ret.setNotificationUri(requireContextExt().contentResolver, uri)
        return ret
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        if (sURIMatcher.match(uri) != MATCH_ALL) {
            return null
        }
        val db = mDbHelper.writableDatabase
        val rowID = db.insert(BookmarkDbHelper.DB_TABLE_BOOKMARKS, null, values)
        if (rowID <= 0) {
            return null
        }
        requireContextExt().contentResolver.notifyChange(Columns.CONTENT_URI, null)
        return ContentUris.withAppendedId(Columns.CONTENT_URI, rowID)
    }

    override fun update(uri: Uri, values: ContentValues?,
                        selection: String?, selectionArgs: Array<String>?): Int {
        val count: Int
        val match = sURIMatcher.match(uri)
        val db = mDbHelper.writableDatabase
        count = when (match) {
            MATCH_ALL -> db.update(BookmarkDbHelper.DB_TABLE_BOOKMARKS,
                values, selection, selectionArgs)
            MATCH_ID -> {
                if (selection != null || selectionArgs != null) {
                    throw UnsupportedOperationException(
                        "Cannot update URI $uri with a where clause")
                }
                db.update(BookmarkDbHelper.DB_TABLE_BOOKMARKS,
                    values, BaseColumns._ID + " = ?", arrayOf(uri.lastPathSegment))
            }
            else -> throw UnsupportedOperationException("Cannot update that URI: $uri")
        }
        if (count > 0) {
            requireContextExt().contentResolver.notifyChange(Columns.CONTENT_URI, null)
        }
        return count
    }

    override fun delete(uri: Uri, selection: String?,
                        selectionArgs: Array<String>?): Int {
        var localSelection = selection
        var localSelectionArgs = selectionArgs
        val match = sURIMatcher.match(uri)
        val db = mDbHelper.writableDatabase
        when (match) {
            MATCH_ALL -> {
            }
            MATCH_ID -> {
                if (localSelection != null || localSelectionArgs != null) {
                    throw UnsupportedOperationException(
                        "Cannot delete URI $uri with a where clause")
                }
                localSelection = BaseColumns._ID + " = ?"
                uri.lastPathSegment?.let {
                    localSelectionArgs = arrayOf(it)
                }
            }
            else -> throw UnsupportedOperationException("Cannot delete the URI $uri")
        }
        val count = db.delete(BookmarkDbHelper.DB_TABLE_BOOKMARKS,
            localSelection, localSelectionArgs)
        if (count > 0) {
            requireContextExt().contentResolver.notifyChange(Columns.CONTENT_URI, null)
        }
        return count
    }

    interface Columns : BaseColumns {
        companion object {
            const val AUTHORITY = "org.mozilla.sorizava.focus.bookmark"
            val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/bookmark")
            const val TITLE = "title"
            const val URL = "url"
            const val COLOR = "color"
        }
    }

    private class BookmarkDbHelper constructor(context: Context?) :
        SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL("CREATE TABLE " + DB_TABLE_BOOKMARKS + " (" +
                    BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    Columns.TITLE + " TEXT, " +
                    Columns.URL + " TEXT, " +
                    Columns.COLOR + " INTEGER)")
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            if (oldVersion < 2) {
                // Recreate table with auto incrementing id column.
                db.execSQL("CREATE TABLE " + DB_TABLE_BOOKMARKS + "_new (" +
                        BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        Columns.TITLE + " TEXT, " +
                        Columns.URL + " TEXT, " +
                        Columns.COLOR + " INTEGER)")
                db.execSQL("INSERT INTO " + DB_TABLE_BOOKMARKS + "_new("
                        + Columns.TITLE + ", " + Columns.URL + ", " + Columns.COLOR
                        + ") SELECT " + Columns.TITLE + ", " + Columns.URL + ", " + Columns.COLOR
                        + " FROM " + DB_TABLE_BOOKMARKS)
                db.execSQL("DROP TABLE $DB_TABLE_BOOKMARKS")
                db.execSQL("ALTER TABLE " + DB_TABLE_BOOKMARKS
                        + "_new RENAME TO " + DB_TABLE_BOOKMARKS)
            }
        }

        companion object {
            private const val DB_VERSION = 1
            private const val DB_NAME = "BookmarkDatabase"
            const val DB_TABLE_BOOKMARKS = "bookmarks"
        }
    }
}