package com.v2tech;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.cmedia.rtmp.RtmpClient;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
        RtmpClient rc = new RtmpClient();
        boolean ret = rc.openURL("rtmp://live.hkstv.hk.lxdns.com/live/hks", false);
        Log.i("RTMP-TEST", "open result:"+ ret);

        rc.release();
    }






}