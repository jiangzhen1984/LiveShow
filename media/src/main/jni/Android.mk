LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
include $(LOCAL_PATH)/../../../librtmp/Android.mk

include $(CLEAR_VARS)
LOCAL_PATH := $(call my-dir)
LOCAL_MODULE := rtmpclient
LOCAL_SRC_FILES := rtmpclient.c

LOCAL_C_INCLUDES := $(LOCAL_PATH)/../../../librtmp/

LOCAL_SHARED_LIBRARIES := librtmp

include $(BUILD_SHARED_LIBRARY)

