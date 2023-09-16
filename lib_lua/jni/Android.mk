LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE := _lua_sdk

LOCAL_LDFLAGS := -Wl,--build-id
LOCAL_LDLIBS := -llog

LOCAL_SRC_FILES := \
        LuaState.c \
        bit.c \
        fpconv.c \
        lapi.c \
        lauxlib.c \
        lbaselib.c \
        lcode.c \
        ldblib.c \
        ldebug.c \
        ldo.c \
        ldump.c \
        lfunc.c \
        lgc.c \
        linit.c \
        liolib.c \
        llex.c \
        lmathlib.c \
        lmem.c \
        loadlib.c \
        lobject.c \
        lopcodes.c \
        loslib.c \
        lparser.c \
        lstate.c \
        lstring.c \
        lstrlib.c \
        ltable.c \
        ltablib.c \
        ltm.c \
        lua_cjson.c \
        lua_extensions.c \
        lundump.c \
        lvm.c \
        lzio.c \
        print.c \
        strbuf.c \


include $(BUILD_SHARED_LIBRARY)
