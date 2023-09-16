#include <jni.h>
#include <stdio.h>
#include <string.h>
#include <android/log.h>
#include <malloc.h>
#include "lua.h"
#include "lualib.h"
#include "lauxlib.h"
#include "lua_extensions.h"
#include "lua.h"
#include "com_govee_pad_base_lua_LuaState.h"

#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, "LuaState", __VA_ARGS__)

/*
 * Class:     com_govee_pad_base_lua_LuaState
 * Method:    nativeLuaOpen
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_com_govee_pad_base_lua_LuaState_nativeLuaOpen
        (JNIEnv *env, jobject obj){
    lua_State *state=lua_open();
    jlong lp=(jlong)state;
    return lp;
}

/*
 * Class:     com_govee_pad_base_lua_LuaState
 * Method:    nativeOpenlibs
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_govee_pad_base_lua_LuaState_nativeOpenlibs
(JNIEnv *env, jobject obj, jlong lp){
    lua_State *L=(lua_State *)lp;
    luaL_openlibs( L );
    luaopen_lua_extensions(L);
}

/*
 * Class:     com_govee_pad_base_lua_LuaState
 * Method:    nativeDofile
 * Signature: (JLjava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_com_govee_pad_base_lua_LuaState_nativeDofile
        (JNIEnv *env, jobject obj, jlong lp, jstring luaFile){
    lua_State *L=(lua_State *)lp;
    char * file = ( *env)->GetStringUTFChars( env , luaFile, NULL );
    LOGD("---> start dofile.");
    int ret=luaL_dofile(L,file);
    if(ret==0){
        LOGD("---> dofile success.");
    }else{
        LOGD("---> dofile failed. error:%s",lua_tostring(L,-1));
    }
    (*env)->ReleaseStringUTFChars(env, luaFile , file);
    return ret;
}

/*
  * Class:     com_govee_pad_base_lua_LuaState
  * Method:    nativeDoString
  * Signature: (JLjava/lang/String;)I
  */
 JNIEXPORT jint JNICALL Java_com_govee_pad_base_lua_LuaState_nativeDoString
         (JNIEnv *env, jobject obj, jlong lp, jstring luaScript){
         lua_State *L=(lua_State *)lp;
         char *script = ( *env)->GetStringUTFChars( env , luaScript, NULL );
         LOGD("---> start dostring.");
         int ret=luaL_dostring(L,script);
         if(ret==0){
             LOGD("---> dostring success.");
         }else{
             LOGD("---> dostring failed. error:%s",lua_tostring(L,-1));
         }
         (*env)->ReleaseStringUTFChars(env, luaScript , script);
         return ret;
 }

/*
 * Class:     com_govee_pad_base_lua_LuaState
 * Method:    nativeClose
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_govee_pad_base_lua_LuaState_nativeClose
        (JNIEnv *env, jobject obj, jlong lp){
    lua_State *L=(lua_State *)lp;
    lua_close( L );
}

/*
 * Class:     com_govee_pad_base_lua_LuaState
 * Method:    nativeGetTop
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_com_govee_pad_base_lua_LuaState_nativeGetTop
        (JNIEnv *env, jobject obj,jlong lp){
    lua_State *L=(lua_State *)lp;
    return ( jint ) lua_gettop( L );
}

/*
 * Class:     com_govee_pad_base_lua_LuaState
 * Method:    nativeTostring
 * Signature: (JI)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_govee_pad_base_lua_LuaState_nativeTostring
        (JNIEnv *env, jobject obj,jlong lp, jint index){
    lua_State *L=(lua_State *)lp;
    char * str = lua_tostring(L,index);
    return ( *env )->NewStringUTF( env , str );
}

/*
 * Class:     com_govee_pad_base_lua_LuaState
 * Method:    nativeTonumber
 * Signature: (JI)D
 */
JNIEXPORT jdouble JNICALL Java_com_govee_pad_base_lua_LuaState_nativeTonumber
        (JNIEnv *env, jobject obj,jlong lp, jint index){
    lua_State *L=(lua_State *)lp;
    return (jdouble)lua_tonumber(L,(int)index);
}

/*
 * Class:     com_govee_pad_base_lua_LuaState
 * Method:    nativeType
 * Signature: (JI)I
 */
JNIEXPORT jint JNICALL Java_com_govee_pad_base_lua_LuaState_nativeType
        (JNIEnv *env, jobject obj, jlong lp, jint index){

    lua_State *L=(lua_State *)lp;
    return ( jint ) lua_type( L , (int) index );
}

/*
 * Class:     com_govee_pad_base_lua_LuaState
 * Method:    nativeTypename
 * Signature: (JI)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_govee_pad_base_lua_LuaState_nativeTypename
        (JNIEnv *env, jobject obj, jlong lp, jint type){
    lua_State *L=(lua_State *)lp;
    char * name = lua_typename( L , type );
    return ( *env )->NewStringUTF( env , name );
}

/*
 * Class:     com_govee_pad_base_lua_LuaState
 * Method:    nativeGetglobal
 * Signature: (JLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_govee_pad_base_lua_LuaState_nativeGetglobal
        (JNIEnv *env, jobject obj, jlong lp, jstring name){
    lua_State *L=(lua_State *)lp;
    char * str = ( *env)->GetStringUTFChars(env , name, NULL );
    lua_getglobal(L,str);
    ( *env )->ReleaseStringUTFChars(env , name , str);
}

/*
 * Class:     com_govee_pad_base_lua_LuaState
 * Method:    nativePushString
 * Signature: (JLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_govee_pad_base_lua_LuaState_nativePushString
        (JNIEnv *env, jobject obj, jlong lp, jstring jstr){
    lua_State *L=(lua_State *)lp;
    char * str = ( *env)->GetStringUTFChars(env , jstr, NULL );
    lua_pushstring( L , str );
    ( *env )->ReleaseStringUTFChars(env , jstr , str);
}

/*
 * Class:     com_govee_pad_base_lua_LuaState
 * Method:    nativeCall
 * Signature: (JII)V
 */
JNIEXPORT void JNICALL Java_com_govee_pad_base_lua_LuaState_nativeCall
        (JNIEnv *env, jobject obj, jlong lp, jint argNum, jint resNum){
    lua_State *L=(lua_State *)lp;
    lua_call( L , argNum , resNum);
}

/*
 * Class:     com_govee_pad_base_lua_LuaState
 * Method:    nativePcall
 * Signature: (JII)I
 */
JNIEXPORT jint JNICALL Java_com_govee_pad_base_lua_LuaState_nativePcall
    (JNIEnv *env, jobject obj, jlong lp, jint argNum, jint resNum){
    lua_State *L=(lua_State *)lp;
    int res=lua_pcall( L , argNum , resNum, 0);
    if(res!=0){
        LOGD("Invock lua_pcall encounter error:%s",lua_tostring(L,-1));
    }
    return res;
}

/*
 * Class:     com_govee_pad_base_lua_LuaState
 * Method:    nativePop
 * Signature: (JI)V
 */
JNIEXPORT void JNICALL Java_com_govee_pad_base_lua_LuaState_nativePop
        (JNIEnv *env, jobject obj, jlong lp, jint n){
    lua_State *L=(lua_State *)lp;
    lua_pop( L , ( int ) n );
}