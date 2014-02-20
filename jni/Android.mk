APP_PLATFORM :=android-<minSdkVersion>

LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)
  
LOCAL_MODULE    := CBlurImage
  
LOCAL_SRC_FILES := CBlurImage.c

LOCAL_LDLIBS := -llog

LOCAL_LDLIBS += -ljnigraphics
  
include $(BUILD_SHARED_LIBRARY) 