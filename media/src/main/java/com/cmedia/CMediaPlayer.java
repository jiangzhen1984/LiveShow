package com.cmedia;

import android.net.Uri;
import android.view.Surface;

/**
 * Created by 28851274 on 9/7/16.
 */
public interface CMediaPlayer {


    public static final int URI_TYPE_VIDEO_HLS = 1;
    public static final int URI_TYPE_VIDEO_RTMP = 2;
    public static final int URI_TYPE_ADUDIO_AAC = 100;
    public static final int URI_TYPE_ADUDIO_AMR = 101;


    public void play(Uri uri, int type);


    public void play(Uri uri, int type, boolean videoRender, boolean audioRender);


    public boolean pause();


    public boolean resume();


    public boolean stop();


    public boolean seek(int seconds);


    public void release();



    public boolean enableVideoRender(boolean flag);


    public boolean enableAudioRender(boolean flag);


    public void setVideoRenderSurface(Surface surface);
}
