package com.cmedia;

import android.content.Context;
import android.media.AudioManager;
import android.net.Uri;
import android.view.Surface;

import java.io.IOException;
import java.lang.ref.WeakReference;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Created by 28851274 on 9/7/16.
 */
public class CMediaPlayerImpl implements  CMediaPlayer {

    private WeakReference<Context> wr;
    private IjkMediaPlayer player;
    private AudioManager am;
    private Surface surface;

    static {
        IjkMediaPlayer.loadLibrariesOnce(null);
    }

    public CMediaPlayerImpl(Context ctx) {
        wr = new WeakReference<Context>(ctx);
        am = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
        am.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        player = new IjkMediaPlayer();
        player.native_setLogLevel(IjkMediaPlayer.IJK_LOG_DEBUG);
        player.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 0);
        player.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 0);
        player.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", IjkMediaPlayer.SDL_FCC_RV32);
        player.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 1);

        player.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48);
    }

    @Override
    public void play(Uri uri, int type) {
        play(uri, type, true, true);
    }

    @Override
    public void play(Uri uri, int type, boolean videoRender, boolean audioRender)  {
        //TODO should check render flag
        switch (type) {
            case URI_TYPE_VIDEO_RTMP:
                try {
                    player.setDataSource(wr.get(), uri, null);
                } catch (IOException e) {
                    throw new RuntimeException(" uri failed "+ uri);
                }
                //FIXME revert
               // player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                player.setSurface(surface);
                player.prepareAsync();
                player.start();
                break;
            case URI_TYPE_ADUDIO_AAC:
                break;
            default:
                throw new RuntimeException(" Not support type" +type + " yet");
        }


    }

    @Override
    public boolean pause() {
        player.pause();
        return true;
    }

    @Override
    public boolean resume() {
        return false;
    }

    @Override
    public boolean stop() {
        player.stop();
        return true;
    }

    @Override
    public boolean seek(int seconds) {
        return false;
    }

    @Override
    public void release() {
        player.stop();
        player.release();
    }

    @Override
    public boolean enableVideoRender(boolean flag) {
        return false;
    }

    @Override
    public boolean enableAudioRender(boolean flag) {
        return false;
    }

    @Override
    public void setVideoRenderSurface(Surface surface) {
        this.surface = surface;
    }



}
