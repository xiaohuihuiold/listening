package com.xhhold.plugin.music_player.util

import com.xhhold.plugin.music_player.entity.BaseTime

object CommonUtil {
    fun getTime(): BaseTime =
        BaseTime(System.currentTimeMillis(), System.currentTimeMillis())
}