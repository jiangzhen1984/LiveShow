LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := librtmp
LOCAL_SRC_FILES := amf.c  hashswf.c  log.c  parseurl.c  rtmp.c
LOCAL_SRC_FILES := $(LOCAL_PATH)/../libopenssl/$(TARGET_ARCH_ABI)/libssl.a


LOCAL_C_INCLUDES := \
    $(LOCAL_PATH)/../include/openssl 

include $(BUILD_SHARED_LIBRARY)

