package com.xhhold.media.ext

import android.system.Os
import android.system.OsConstants
import java.io.FileDescriptor

fun FileDescriptor.size(): Long {
    var size = -1L
    try {
        val st = Os.fstat(this)
        size = if (OsConstants.S_ISREG(st.st_mode) || OsConstants.S_ISLNK(st.st_mode)) {
            st.st_size
        } else {
            -1
        }
    } catch (e: Exception) {
    }
    return size
}