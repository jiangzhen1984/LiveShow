LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

include $(LOCAL_PATH)/../../../librtmp/Android.mk

include $(CLEAR_VARS)


LOCAL_MODULE := rtmpclient
LOCAL_C_SOURCE := rtmpclient.c

include $(BUILD_SHARED_LIBRARY)
