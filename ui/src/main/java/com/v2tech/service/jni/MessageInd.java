package com.v2tech.service.jni;

/**
 * Created by 28851274 on 7/18/16.
 */
public class MessageInd extends JNIIndication  {

    public long uid;

    public long lid;

    public String content;

    public MessageInd(Result res) {
        super(res);
    }



}
