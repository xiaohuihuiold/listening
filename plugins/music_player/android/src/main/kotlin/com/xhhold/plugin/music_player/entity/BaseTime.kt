package com.xhhold.plugin.music_player.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import kotlinx.parcelize.Parcelize

@Parcelize
data class BaseTime(
    @ColumnInfo(name = "add_time")
    var addTime: Long,
    @ColumnInfo(name = "update_time")
    var updateTime: Long,
) : Parcelable