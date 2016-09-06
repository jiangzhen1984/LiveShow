package com.cmedia.rtmp;

/**
 * Created by 28851274 on 9/6/16.
 */
public class RtmpClient {

    static {
        System.loadLibrary("librtmpclient");
    }

    private int clientId;


    public RtmpClient() {
        clientId = nativeInit();
        checkClientId();
    }


    public boolean release() {
        if (clientId > 0) {
            nativeRelase(clientId);
        }
        clientId = -1;
        return true;
    }


    public boolean openURL(String url, boolean write) {
        checkClientId();
        return nativeOpenURL(clientId, url, write);
    }


    public int read(byte[] buf, int len) {
        checkClientId();
        return nativeRead(clientId, buf, len);
    }


    public int write(byte[] buf, int len) {
        checkClientId();
        return nativeWrite(clientId, buf, len);
    }

    private void checkClientId() {
        if (clientId < 0) {
            throw new RuntimeException("init rtmp client error: "+ clientId);
        }
    }

    private native int nativeInit();


    private native boolean nativeRelase(int clientId);


    private native boolean nativeOpenURL(int clientId, String rul, boolean wflag);


    private native boolean nativePause(int clientId);


    private native boolean nativeResume(int clientId);


    private native int nativeRead(int clientId, byte[] buf, int size);


    private native int nativeWrite(int clientId, byte[] buf, int size);

}
