package com.cmedia.rtmp;

import android.net.Uri;

import com.V2.jni.util.V2Log;
import com.google.android.exoplayer.C;
import com.google.android.exoplayer.upstream.DataSpec;
import com.google.android.exoplayer.upstream.UriDataSource;

import java.io.IOException;

/**
 * Created by 28851274 on 9/7/16.
 */
public class RtmpDataSource implements UriDataSource  {


    private Uri uri;
    private RtmpClient rtmpClient;

    public RtmpDataSource(Uri uri) {
        this.uri = uri;
        rtmpClient = new RtmpClient();
    }

    @Override
    public String getUri() {
        return uri.toString();
    }

    @Override
    public long open(DataSpec dataSpec) throws IOException {
        boolean ret = rtmpClient.openURL(dataSpec.uri.toString(), false);
        V2Log.i(" rtmp set url result: "+ ret+"  ==> "+ uri.toString());
        return C.LENGTH_UNBOUNDED;
    }

    @Override
    public void close() throws IOException {
        rtmpClient.release();
    }

    @Override
    public int read(byte[] buffer, int offset, int readLength) throws IOException {
        byte[] buf = new byte[readLength];
        int len = rtmpClient.read(buf, readLength);
        if (len > 0) {
            System.arraycopy(buf, 0, buffer, offset, len);
        }
        return len;
    }
}
