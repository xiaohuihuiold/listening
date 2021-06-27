package com.xhhold.plugin.music_player.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update

interface BaseDao<T> {
    @Insert
    fun insert(vararg entities: T): LongArray

    @Insert
    fun insert(entities: List<T>): LongArray

    @Delete
    fun delete(vararg entities: T): Int

    @Delete
    fun delete(entities: List<T>): Int

    @Update
    fun update(vararg entities: T): Int

    @Update
    fun update(entities: List<T>): Int
}