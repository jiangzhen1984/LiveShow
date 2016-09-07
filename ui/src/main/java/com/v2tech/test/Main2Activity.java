package com.v2tech.test;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.media.MediaCodec;
import android.net.Uri;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import com.V2.jni.util.V2Log;
import com.cmedia.rtmp.RtmpDataSource;
import com.google.android.exoplayer.ExoPlaybackException;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.ExoPlayer.Listener;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.MediaCodecSelector;
import com.google.android.exoplayer.MediaCodecVideoTrackRenderer;
import com.google.android.exoplayer.extractor.ExtractorSampleSource;
import com.google.android.exoplayer.upstream.Allocator;
import com.google.android.exoplayer.upstream.DataSource;
import com.google.android.exoplayer.upstream.DefaultAllocator;
import com.v2tech.R;

public class Main2Activity extends Activity {

    private static final int BUFFER_SEGMENT_SIZE = 64 * 1024;
    private static final int BUFFER_SEGMENT_COUNT = 256;


    private MediaCodecVideoTrackRenderer videoRenderer;
    private MediaCodecAudioTrackRenderer audioRenderer;


    private SurfaceView sv;
    ExoPlayer player;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        sv = (SurfaceView) findViewById(R.id.surfaceView);
        sv.getHolder().setFormat(PixelFormat.TRANSPARENT);
        sv.getHolder().addCallback(new Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {



                Uri uri =  Uri.parse("rtmp://live.hkstv.hk.lxdns.com/live/hks");
                Allocator allocator = new DefaultAllocator(BUFFER_SEGMENT_SIZE);
                DataSource dataSource = new RtmpDataSource(uri);
                ExtractorSampleSource sampleSource = new ExtractorSampleSource(
                        uri, dataSource, allocator, BUFFER_SEGMENT_COUNT * BUFFER_SEGMENT_SIZE);
                videoRenderer = new MediaCodecVideoTrackRenderer(
                        Main2Activity.this, sampleSource, MediaCodecSelector.DEFAULT, MediaCodec.VIDEO_SCALING_MODE_SCALE_TO_FIT);
                audioRenderer = new MediaCodecAudioTrackRenderer(
                        sampleSource, MediaCodecSelector.DEFAULT);

                player = ExoPlayer.Factory.newInstance(2);
                player.addListener(new Listener() {
                    @Override
                    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                        V2Log.i("onPlayerStateChanged===>" + playbackState);
                    }

                    @Override
                    public void onPlayWhenReadyCommitted() {

                    }

                    @Override
                    public void onPlayerError(ExoPlaybackException error) {
                        V2Log.i("onPlayerStateChanged===>" + error);
                    }
                });
                player.prepare(videoRenderer,  audioRenderer);

                player.sendMessage(videoRenderer, MediaCodecVideoTrackRenderer.MSG_SET_SURFACE, holder.getSurface());

                player.setPlayWhenReady(true);


            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.release();
    }
}
