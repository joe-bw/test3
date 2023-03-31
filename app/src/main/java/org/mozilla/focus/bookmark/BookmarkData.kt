package org.mozilla.focus.bookmark

data class BookmarkData(val uid: Long, val url: String, val title: String) {
    var isSelected : Boolean = false
}