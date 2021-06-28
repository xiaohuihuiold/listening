package com.xhhold.plugin.music_player

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import androidx.media.session.MediaButtonReceiver
import com.xhhold.plugin.music_player.ext.toBitmap

class MediaNotificationManager(private val service: MusicService) {

    private val notificationManager =
        service.getSystemService(NotificationManager::class.java).apply {
            cancelAll()
        }

    private val playAction: NotificationCompat.Action
    private val pauseAction: NotificationCompat.Action
    private val skipPreviousAction: NotificationCompat.Action
    private val skipNextAction: NotificationCompat.Action
    private val favoriteAction: NotificationCompat.Action
    private val unFavoriteAction: NotificationCompat.Action

    companion object {
        const val TAG = "MediaNotificationManager"
        const val ID = 0x01
        const val CHANNEL_ID = "com.xhhold.plugin.music_player.channel"
    }

    init {
        playAction =
            createAction(R.drawable.ic_play, R.string.play, PlaybackStateCompat.ACTION_PLAY)
        pauseAction =
            createAction(R.drawable.ic_pause, R.string.pause, PlaybackStateCompat.ACTION_PAUSE)
        skipPreviousAction =
            createAction(
                R.drawable.ic_skip_previous,
                R.string.skip_previous,
                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
            )
        skipNextAction =
            createAction(
                R.drawable.ic_skip_next,
                R.string.skip_next,
                PlaybackStateCompat.ACTION_SKIP_TO_NEXT
            )
        favoriteAction =
            createAction(
                R.drawable.ic_favorite_border,
                R.string.favorite,
                PlaybackStateCompat.ACTION_SET_RATING
            )
        unFavoriteAction =
            createAction(
                R.drawable.ic_favorite,
                R.string.unfavorite,
                PlaybackStateCompat.ACTION_SET_RATING
            )
    }

    fun start(
        description: MediaDescriptionCompat,
        state: PlaybackStateCompat,
        token: MediaSessionCompat.Token
    ) {
        service.startForeground(ID, getNotification(description, state, token).build())
    }

    fun update(
        description: MediaDescriptionCompat,
        state: PlaybackStateCompat,
        token: MediaSessionCompat.Token
    ) {
        val notification = getNotification(description, state, token)
        notificationManager.notify(ID, notification.build())
    }

    fun stop(remove: Boolean) {
        service.stopForeground(remove)
    }

    private fun getNotification(
        description: MediaDescriptionCompat,
        state: PlaybackStateCompat,
        token: MediaSessionCompat.Token
    ): NotificationCompat.Builder {
        if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "MediaSession",
                NotificationManager.IMPORTANCE_MIN
            ).apply {
                this.description = "MediaSession"
            }
            notificationManager.createNotificationChannel(channel)
        }
        return NotificationCompat.Builder(service, CHANNEL_ID).apply {
            setColorized(true)
            setContentTitle(description.title)
            setContentText(description.subtitle)
            setSmallIcon(R.drawable.ic_music)
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            setDeleteIntent(createIntent(PlaybackStateCompat.ACTION_STOP))
            setLargeIcon(
                description.iconUri?.toBitmap(
                    service.contentResolver,
                    (description.title ?: description.subtitle ?: "").toString()
                )
            )
            addAction(skipPreviousAction)
            addAction(if (state.state != PlaybackStateCompat.STATE_PLAYING) playAction else pauseAction)
            addAction(skipNextAction)
            setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(token)
                    .setShowActionsInCompactView(0, 1, 2)
                    .setShowCancelButton(true)
                    .setCancelButtonIntent(createIntent(PlaybackStateCompat.ACTION_STOP))
            )
        }
    }

    private fun createIntent(action: Long): PendingIntent =
        MediaButtonReceiver.buildMediaButtonPendingIntent(service, action)

    private fun createAction(icon: Int, title: Int, action: Long): NotificationCompat.Action =
        NotificationCompat.Action(
            icon,
            service.getString(title),
            MediaButtonReceiver.buildMediaButtonPendingIntent(service, action)
        )
}