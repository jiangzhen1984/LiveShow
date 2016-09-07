package com.cmedia;

/**
 * Created by 28851274 on 9/7/16.
 */
public interface CMediaPlayerStateListener {


    public void onError(Throwable e);


    public void onPlay();


    public void onPause();


    public void onResume();


    public void onFinish();
}
