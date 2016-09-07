package com.cmedia;

import android.content.Context;
import android.media.MediaCodec;
import android.net.Uri;
import android.view.Surface;

import com.V2.jni.util.V2Log;
import com.cmedia.rtmp.RtmpDataSource;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.MediaCodecSelector;
import com.google.android.exoplayer.MediaCodecVideoTrackRenderer;
import com.google.android.exoplayer.extractor.ExtractorSampleSource;
import com.google.android.exoplayer.upstream.Allocator;
import com.google.android.exoplayer.upstream.DataSource;
import com.google.android.exoplayer.upstream.DefaultAllocator;

import java.lang.ref.WeakReference;

/**
 * Created by 28851274 on 9/7/16.
 */
public class CMediaPlayerImpl implements  CMediaPlayer {

    private static final int BUFFER_SEGMENT_SIZE = 64 * 1024;
    private static final int BUFFER_SEGMENT_COUNT = 256;


    private ExoPlayer player;
    private WeakReference<Context> wr;
    private Surface surface;
    private MediaCodecVideoTrackRenderer videoRenderer;
    private MediaCodecAudioTrackRenderer audioRenderer;


    public CMediaPlayerImpl(Context ctx) {
        wr = new WeakReference<Context>(ctx);
        player = ExoPlayer.Factory.newInstance(2);
    }

    @Override
    public void play(Uri uri, int type) {
        play(uri, type, true, true);
    }

    @Override
    public void play(Uri uri, int type, boolean videoRender, boolean audioRender) {
        //TODO should check render flag
        switch (type) {
            case URI_TYPE_VIDEO_RTMP:
                Allocator allocator = new DefaultAllocator(BUFFER_SEGMENT_SIZE);
                DataSource dataSource = new RtmpDataSource(uri);
                ExtractorSampleSource sampleSource = new ExtractorSampleSource(
                        uri, dataSource, allocator, BUFFER_SEGMENT_COUNT * BUFFER_SEGMENT_SIZE);
                videoRenderer = new MediaCodecVideoTrackRenderer(
                        wr.get(), sampleSource, MediaCodecSelector.DEFAULT, MediaCodec.VIDEO_SCALING_MODE_SCALE_TO_FIT);
                audioRenderer = new MediaCodecAudioTrackRenderer(
                        sampleSource, MediaCodecSelector.DEFAULT);
                player.prepare(videoRenderer,  audioRenderer);
                pushSurface(false);
                player.setPlayWhenReady(true);
                break;
            case URI_TYPE_ADUDIO_AAC:
                break;
            default:
                throw new RuntimeException(" Not support type" +type + " yet");
        }


    }

    @Override
    public boolean pause() {
        return false;
    }

    @Override
    public boolean resume() {
        return false;
    }

    @Override
    public boolean stop() {
        player.stop();
        return false;
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
        pushSurface(true);
    }


    private void pushSurface(boolean sync) {
        if (surface == null) {
            V2Log.e(" surface is null");
            return;
        }
        if (sync) {
            player.blockingSendMessage(videoRenderer, MediaCodecVideoTrackRenderer.MSG_SET_SURFACE, surface);
        } else {
            player.sendMessage(videoRenderer, MediaCodecVideoTrackRenderer.MSG_SET_SURFACE, surface);
        }
    }
}
