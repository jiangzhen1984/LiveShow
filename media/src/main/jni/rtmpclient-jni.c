

#include "rtmpclient.h"
#include <jni.h>
#include <stdlib.h>
#include <android/log.h>


#define LOG_N(P1)   __android_log_print(ANDROID_LOG_INFO, "JNIMsg", (P1));
#define LOG_1(P1, V1)   __android_log_print(ANDROID_LOG_INFO, "JNIMsg", (P1), (V1));
#define LOG_2(P1, V1, V2)   __android_log_print(ANDROID_LOG_INFO, "JNIMsg", (P1), (V1), (V2));





static jint jni_rtmp_client_init(JNIEnv * env, jobject thiz) {
   jint ret = RET_SUCCESS;

   ret = rtmp_client_initialize();
   LOG_1("====> initalize ret :%d", ret);

   return ret;
}

static jboolean jni_rtmp_client_release(JNIEnv * env, jobject thiz, jint cid) {
   int ret;
   ret = rtmp_client_release(cid);
   LOG_2(" relase rtmp client %d   ret: %d", cid, ret);
   return ret == RET_SUCCESS ? JNI_TRUE : JNI_FALSE;
}


static jboolean jni_setup_url(JNIEnv * env, jobject thiz, jint cid, jstring url, jboolean wFlag) {
     int ret = RET_SUCCESS;
     jboolean cp = JNI_TRUE;
     const char * czurl = (*env)->GetStringUTFChars(env, url, &cp);
     ret = rtmp_client_setup_url(cid, wFlag == JNI_TRUE? CLIENT_TYPE_WRITE : CLIENT_TYPE_READ, czurl);
     LOG_1(" setup url  ret: %d", ret);
     return ret == RET_SUCCESS ? JNI_TRUE : JNI_FALSE;
}


static jboolean jni_client_pause(JNIEnv * env, jobject cliz, jint cid) {
     int ret = RET_SUCCESS;
     ret = rtmp_client_pause(cid);
     LOG_1(" pause  ret: %d", ret);
     return ret == RET_SUCCESS ? JNI_TRUE : JNI_FALSE;
}


static jboolean jni_client_resume(JNIEnv * env, jobject thiz, jint cid) {
     int ret = RET_SUCCESS;
     ret = rtmp_client_resume(cid);
     LOG_1(" resume  ret: %d", ret);
     return ret == RET_SUCCESS ? JNI_TRUE : JNI_FALSE;
}


static int jin_client_read(JNIEnv * env, jobject thiz, jint cid, jbyteArray jbuf, int size) {

    int ret = RET_SUCCESS;
    int len = -1;
    if (jbuf == NULL) {
         return -1;
    }

    len = (*env)->GetArrayLength(env, jbuf);
    len = size > len ? len : size;
    char * buf = (char *)malloc(len);
    if (buf == NULL) {
         return -1;
    } 
    ret = rtmp_client_read(cid, buf, len);
   
    if (ret > 0) {
          (*env)->SetByteArrayRegion(env, jbuf, 0, len, buf);
    }

    free(buf);
    
    return ret;
}


static int jni_client_write(JNIEnv * env, jobject thiz, jint cid, jbyteArray jbuf, jint size) {
     int ret = RET_SUCCESS;
     char * buf = NULL;
     int len = -1;
    
     if (jbuf == NULL) {
          return -1;
     }

     len = (*env)->GetArrayLength(env, jbuf);
     len = size > len? len : size;
     buf = (char *) malloc(len);
 
     (*env)->GetByteArrayRegion(env, jbuf, 0, len, buf);
     ret = rtmp_client_write(cid, buf, len);

     free(buf);
     return ret;
}

