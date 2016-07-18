package com.v2tech.map;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * Created by 28851274 on 7/18/16.
 */
public class LocationNameQueryResponseListener {

    public LocationNameQueryResponseListener(Handler h, int what, Object obj) {
        refH = new WeakReference<Handler>(h);
        this.what = what;
        userObj = obj;
    }

    public Handler getHandler() {
        if (refH == null)
            return null;

        return (Handler) refH.get();
    }

    public int getWhat() {
        return what;
    }

    public Object getObject() {
        return userObj;
    }

    public void doNotification() {
        Handler target = this.refH.get();
        if (target != null) {
            Message m = Message.obtain();
            m.what = this.what;
            m.obj = userObj;
            target.sendMessage(m);

        }
    }

    public void doNotification(Object wrapObj) {
        Handler target = this.refH.get();
        if (target != null) {
            Message m = Message.obtain();
            m.what = this.what;
            m.obj = wrapObj;
            target.sendMessage(m);

        }
    }

    WeakReference<Handler> refH;
    int what;
    Object userObj;
}
