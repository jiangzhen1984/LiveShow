LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE    := libssl-prebuilt
LOCAL_SRC_FILES := ../../libopenssl/armeabi/libssl.a
include $(PREBUILT_STATIC_LIBRARY)


include $(CLEAR_VARS)
LOCAL_MODULE := libcrypto-prebuilt
LOCAL_SRC_FILES := ../../libopenssl/armeabi/libcrypto.a
LOCAL_LDLIBS := -lz
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE    := librtmp
LOCAL_SRC_FILES := ../amf.c  ../hashswf.c  ../log.c  ../parseurl.c  ../rtmp.c
LOCAL_STATIC_LIBRARIES := libssl-prebuilt
LOCAL_STATIC_LIBRARIES += libcrypto-prebuilt
LOCAL_LDLIBS := -lz
LOCAL_LDLIBS := -llog


LOCAL_C_INCLUDES := \
    $(LOCAL_PATH)/../../include/

include $(BUILD_SHARED_LIBRARY)
