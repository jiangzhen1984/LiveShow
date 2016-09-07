

#include "rtmpclient.h"
#include <jni.h>
#include <stdlib.h>
#include <android/log.h>


#define LOG_N(P1)   __android_log_print(ANDROID_LOG_INFO, "JNIMsg", (P1));
#define LOG_1(P1, V1)   __android_log_print(ANDROID_LOG_INFO, "JNIMsg", (P1), (V1));
#define LOG_2(P1, V1, V2)   __android_log_print(ANDROID_LOG_INFO, "JNIMsg", (P1), (V1), (V2));





static jint jni_client_init(JNIEnv * env, jobject thiz) {
   jint ret = RET_SUCCESS;

   ret = rtmp_client_initialize();
   LOG_1("====> initalize ret :%d", ret);

   return ret;
}

static jboolean jni_client_release(JNIEnv * env, jobject thiz, jint cid) {
   int ret;
   ret = rtmp_client_release(cid);
   LOG_2(" relase rtmp client %d   ret: %d", cid, ret);
   return ret == RET_SUCCESS ? JNI_TRUE : JNI_FALSE;
}


static jboolean jni_setup_url(JNIEnv * env, jobject thiz, jint cid, jstring url, jboolean wFlag) {
     int ret = RET_SUCCESS;
     jboolean cp = JNI_TRUE;
     const char * czurl = (*env)->GetStringUTFChars(env, url, &cp);
     LOG_1(" setup url  : %s", czurl);
     ret = rtmp_client_setup_url(cid, wFlag == JNI_TRUE? CLIENT_TYPE_WRITE : CLIENT_TYPE_READ, czurl);
     LOG_1(" setup url  ret: %d", ret);
      (*env)->ReleaseStringUTFChars(env, url, czurl);
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


static int jni_client_read(JNIEnv * env, jobject thiz, jint cid, jbyteArray jbuf, jint offset, jint size) {

    int len = -1;
    int readLen = -1;
    if (jbuf == NULL) {
         return -1;
    }

    len = (*env)->GetArrayLength(env, jbuf);
    len = size > len ? len : size;
    char * buf = (char *)malloc(len * sizeof(jbyte));
    if (buf == NULL) {
         return -1;
    } 
    readLen = rtmp_client_read(cid, buf, len);
   
    if (readLen > 0) {
          (*env)->SetByteArrayRegion(env, jbuf, offset, readLen, buf);
    }

    free(buf);
    
    return readLen;
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
     buf = (char *) malloc(len * sizeof(jbyte));
 
     (*env)->GetByteArrayRegion(env, jbuf, 0, len, buf);
     ret = rtmp_client_write(cid, buf, len);

     free(buf);
     return ret;
}



static JNINativeMethod methods[] = {
     { "nativeInit", "()I", (void *)jni_client_init },
     { "nativeRelase", "(I)Z", (void *)jni_client_release },
     { "nativeOpenURL", "(ILjava/lang/String;Z)Z", (void *)jni_setup_url },
     { "nativePause", "(I)Z", (void *)jni_client_pause },
     { "nativeResume", "(I)Z", (void *)jni_client_resume },
     { "nativeRead", "(I[BII)I", (void *)jni_client_read },
     { "nativeWrite", "(I[BI)I", (void *)jni_client_write },
    
};


typedef union {
    JNIEnv* env;
    void* venv;
} UnionJNIEnvToVoid;

jint JNI_OnLoad(JavaVM* vm, void * reserved) {
    UnionJNIEnvToVoid uenv;
    uenv.venv = NULL;
    jint result = -1;
    JNIEnv* env = NULL;
    jclass clazz;
    const char * clazzname = "com/cmedia/rtmp/RtmpClient";
    
    LOG_N("JNI_OnLoad");
    if ((*vm)->GetEnv(vm, &uenv.venv, JNI_VERSION_1_4) != JNI_OK) {
        LOG_N("ERROR: GetEnv failed");
        goto bail;
    }
    env = uenv.env;

    clazz = (*env)->FindClass(env, clazzname);
    if (clazz == NULL) {
        LOG_1("ERROR: registerNatives failed class not found %s", clazzname);
        goto bail;
    }

    
    if ((*env)->RegisterNatives(env, clazz,
                 methods, sizeof(methods) / sizeof(methods[0])) < 0 ) {
        LOG_N("ERROR: registerNatives failed");
        goto bail;
    }
    
    result = JNI_VERSION_1_4;
    
bail:
    return result;
}
