LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)  
LOCAL_LDLIBS := -llog  
LOCAL_MODULE    := tinylib  
LOCAL_SRC_FILES := symlink.c getownerid.c chownerid.c
include $(BUILD_SHARED_LIBRARY)  
