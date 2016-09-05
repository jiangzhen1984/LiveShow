LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE    := libssl-prebuilt
LOCAL_SRC_FILES := ../../../libopenssl/armeabi/libssl.a
include $(PREBUILT_STATIC_LIBRARY)


include $(CLEAR_VARS)
LOCAL_MODULE := libcrypto-prebuilt
LOCAL_SRC_FILES := ../../../libopenssl/armeabi/libcrypto.a
LOCAL_LDLIBS := -lz 
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE    := librtmp
LOCAL_SRC_FILES := ../../../librtmp/amf.c  ../../../librtmp/hashswf.c  ../../../librtmp/log.c  ../../../librtmp/parseurl.c  ../../../librtmp/rtmp.c
LOCAL_STATIC_LIBRARIES := libssl-prebuilt
LOCAL_STATIC_LIBRARIES += libcrypto-prebuilt
LOCAL_SHARED_LIBRARIES := libz
LOCAL_LDLIBS := -lz 


LOCAL_C_INCLUDES := \
    $(LOCAL_PATH)/../../../include/

include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)

LOCAL_MODULE := rtmpclient
LOCAL_SRC_FILES := rtmpclient.c

LOCAL_C_INCLUDES := $(LOCAL_PATH)/../../../librtmp/

LOCAL_SHARED_LIBRARIES := rtmp

include $(BUILD_SHARED_LIBRARY)

