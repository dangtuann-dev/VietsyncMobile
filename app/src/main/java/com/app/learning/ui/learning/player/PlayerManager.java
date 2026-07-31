package com.app.learning.ui.learning.player;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.OptIn;
import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackParameters;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import com.app.learning.data.local.AppDatabase;
import com.app.learning.data.local.VideoPositionDao;
import com.app.learning.data.local.VideoPositionEntity;
import com.app.learning.utils.AppExecutors;

public class PlayerManager {

    private static volatile PlayerManager instance;
    private ExoPlayer player;
    private Context applicationContext;
    private VideoPositionDao videoPositionDao;

    private String currentLessonId;

    private PlayerManager(Context context) {
        this.applicationContext = context.getApplicationContext();
        this.videoPositionDao = AppDatabase.getInstance(applicationContext).videoPositionDao();
        initPlayer();
    }

    public static PlayerManager getInstance(Context context) {
        if (instance == null) {
            synchronized (PlayerManager.class) {
                if (instance == null) {
                    instance = new PlayerManager(context);
                }
            }
        }
        return instance;
    }

    private void initPlayer() {
        if (player == null) {
            androidx.media3.datasource.DefaultHttpDataSource.Factory httpDataSourceFactory =
                    new androidx.media3.datasource.DefaultHttpDataSource.Factory()
                            .setUserAgent("VietsyncMobile/1.0")
                            .setAllowCrossProtocolRedirects(true)
                            .setConnectTimeoutMs(15000)
                            .setReadTimeoutMs(15000);

            androidx.media3.datasource.DefaultDataSource.Factory dataSourceFactory =
                    new androidx.media3.datasource.DefaultDataSource.Factory(applicationContext, httpDataSourceFactory);

            androidx.media3.exoplayer.source.DefaultMediaSourceFactory mediaSourceFactory =
                    new androidx.media3.exoplayer.source.DefaultMediaSourceFactory(dataSourceFactory);

            player = new ExoPlayer.Builder(applicationContext)
                    .setMediaSourceFactory(mediaSourceFactory)
                    .build();

            player.setPlayWhenReady(true);

            player.addListener(new Player.Listener() {
                @Override
                public void onPlayerError(androidx.media3.common.PlaybackException error) {
                    android.util.Log.e("PlayerManager", "Playback Error: " + error.getMessage(), error);
                    String fallbackUri = "android.resource://" + applicationContext.getPackageName() + "/" + com.example.vietsyncmobile.R.raw.sample_lesson;
                    if (player != null) {
                        try {
                            player.setMediaItem(MediaItem.fromUri(Uri.parse(fallbackUri)));
                            player.prepare();
                            player.play();
                        } catch (Exception ignored) {}
                    }
                }
            });
        }
    }

    public ExoPlayer getPlayer() {
        if (player == null) initPlayer();
        return player;
    }

    public void attachPlayerView(PlayerView playerView) {
        if (playerView != null) {
            if (player == null) initPlayer();
            playerView.setPlayer(player);
        }
    }

    public void setMediaUrl(String lessonId, String mediaUrl) {
        this.currentLessonId = lessonId;
        if (player == null) initPlayer();

        String rawUriStr = "android.resource://" + applicationContext.getPackageName() + "/" + com.example.vietsyncmobile.R.raw.sample_lesson;
        String activeUrl = (mediaUrl != null && !mediaUrl.isEmpty())
                ? mediaUrl
                : rawUriStr;

        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(activeUrl));
        player.setMediaItem(mediaItem);
        player.prepare();
        player.play();

        // Restore position from Room DB
        AppExecutors.getInstance().diskIO().execute(() -> {
            VideoPositionEntity entity = videoPositionDao.getPositionSync(lessonId);
            if (entity != null && entity.getPositionMs() > 0) {
                AppExecutors.getInstance().mainThread().execute(() -> {
                    if (player != null) player.seekTo(entity.getPositionMs());
                });
            }
        });
    }

    public void play() {
        if (player != null) player.play();
    }

    public void pause() {
        if (player != null) player.pause();
    }

    public void seekTo(long positionMs) {
        if (player != null) player.seekTo(positionMs);
    }

    public void setPlaybackSpeed(float speed) {
        if (player != null) {
            PlaybackParameters parameters = new PlaybackParameters(speed);
            player.setPlaybackParameters(parameters);
        }
    }

    public long getCurrentPosition() {
        return player != null ? player.getCurrentPosition() : 0;
    }

    public long getDuration() {
        return player != null ? player.getDuration() : 0;
    }

    public void saveCurrentPosition() {
        if (player != null && currentLessonId != null) {
            long pos = player.getCurrentPosition();
            long dur = player.getDuration();
            if (pos > 0) {
                AppExecutors.getInstance().diskIO().execute(() -> {
                    videoPositionDao.savePosition(new VideoPositionEntity(currentLessonId, pos, dur, System.currentTimeMillis()));
                });
            }
        }
    }

    public void releasePlayer() {
        if (player != null) {
            saveCurrentPosition();
            player.release();
            player = null;
        }
    }
}
