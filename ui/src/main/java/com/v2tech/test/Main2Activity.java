package com.v2tech.test;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import com.cmedia.CMediaPlayer;
import com.cmedia.CMediaPlayerImpl;
import com.v2tech.R;

public class Main2Activity extends Activity {

    private static final int BUFFER_SEGMENT_SIZE = 64 * 1024;
    private static final int BUFFER_SEGMENT_COUNT = 256;


    private SurfaceView sv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        sv = (SurfaceView) findViewById(R.id.surfaceView);
//        sv.getHolder().setFormat(PixelFormat.TRANSPARENT);
//        sv.getHolder().addCallback(new Callback() {
//            @Override
//            public void surfaceCreated(SurfaceHolder holder) {
//
//
//
//                Uri uri =  Uri.parse("rtmp://live.hkstv.hk.lxdns.com/live/hks");
//                Allocator allocator = new DefaultAllocator(BUFFER_SEGMENT_SIZE);
//                DataSource dataSource = new RtmpDataSource(uri);
//                ExtractorSampleSource sampleSource = new ExtractorSampleSource(
//                        uri, dataSource, allocator, BUFFER_SEGMENT_COUNT * BUFFER_SEGMENT_SIZE);
//                videoRenderer = new MediaCodecVideoTrackRenderer(
//                        Main2Activity.this, sampleSource, MediaCodecSelector.DEFAULT, MediaCodec.VIDEO_SCALING_MODE_SCALE_TO_FIT);
//                audioRenderer = new MediaCodecAudioTrackRenderer(
//                        sampleSource, MediaCodecSelector.DEFAULT);
//
//                player = ExoPlayer.Factory.newInstance(2);
//                player.addListener(new Listener() {
//                    @Override
//                    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
//                        V2Log.i("onPlayerStateChanged===>" + playbackState);
//                    }
//
//                    @Override
//                    public void onPlayWhenReadyCommitted() {
//
//                    }
//
//                    @Override
//                    public void onPlayerError(ExoPlaybackException error) {
//                        V2Log.i("onPlayerStateChanged===>" + error);
//                    }
//                });
//                player.prepare(videoRenderer,  audioRenderer);
//
//                player.sendMessage(videoRenderer, MediaCodecVideoTrackRenderer.MSG_SET_SURFACE, holder.getSurface());
//
//                player.setPlayWhenReady(true);
//
//
//            }
//
//            @Override
//            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//
//            }
//
//            @Override
//            public void surfaceDestroyed(SurfaceHolder holder) {
//
//            }
//        });

//
//        IjkMediaPlayer.loadLibrariesOnce(null);
//        IjkMediaPlayer.native_profileBegin("libijkplayer.so");
//        final Uri uri = Uri.parse("rtmp://live.hkstv.hk.lxdns.com/live/hks");
//        sv = (SurfaceView) findViewById(R.id.surfaceView);
//        sv.getHolder().addCallback(new Callback() {
//            @Override
//            public void surfaceCreated(SurfaceHolder holder) {
//                IjkMediaPlayer ijkMediaPlayer = new IjkMediaPlayer();
//                ijkMediaPlayer.native_setLogLevel(IjkMediaPlayer.IJK_LOG_DEBUG);
//                ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 0);
//                ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 0);
//                ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", IjkMediaPlayer.SDL_FCC_RV32);
//                ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 1);
//
//                ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48);
//                ijkMediaPlayer.setSurface(holder.getSurface());
//
//                AudioManager am = (AudioManager) Main2Activity.this.getSystemService(Context.AUDIO_SERVICE);
//                am.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
//                try {
//                    ijkMediaPlayer.setDataSource(Main2Activity.this, uri, null);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//              //  ijkMediaPlayer.setDisplay(holder);
//                ijkMediaPlayer.setScreenOnWhilePlaying(true);
//                ijkMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//                ijkMediaPlayer.prepareAsync();
//                ijkMediaPlayer.start();
//            }
//
//            @Override
//            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//
//            }
//
//            @Override
//            public void surfaceDestroyed(SurfaceHolder holder) {
//
//            }
//        });


        sv.getHolder().addCallback(new Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                CMediaPlayer player = new CMediaPlayerImpl(Main2Activity.this);
                player.setVideoRenderSurface(holder.getSurface());
                Uri uri = Uri.parse("rtmp://live.hkstv.hk.lxdns.com/live/hks");
                player.play(uri, CMediaPlayer.URI_TYPE_VIDEO_RTMP);
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
        // player.release();
    }
}
