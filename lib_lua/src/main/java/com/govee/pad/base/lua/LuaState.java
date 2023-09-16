package com.govee.pad.base.lua;

import android.util.Log;

/**
 * @author：YangQi.Chen
 * @date：2023/9/8 10:35
 * @description：Lua状态机
 */
public class LuaState {
    private static final String TAG = "LuaState";
    public final static int LUA_GLOBALSINDEX = -10002;
    public final static int LUA_REGISTRYINDEX = -10000;
    public final static int LUA_TNONE = -1;
    public final static int LUA_TNIL = 0;
    public final static int LUA_TBOOLEAN = 1;
    public final static int LUA_TLIGHTUSERDATA = 2;
    public final static int LUA_TNUMBER = 3;
    public final static int LUA_TSTRING = 4;
    public final static int LUA_TTABLE = 5;
    public final static int LUA_TFUNCTION = 6;
    public final static int LUA_TUSERDATA = 7;
    public final static int LUA_TTHREAD = 8;
    private long luaStatePointer;

    static {
        System.loadLibrary("_lua_sdk");
    }

    public LuaState() {
    }

    /**
     * 初始化.
     *
     * @param luaFilePath LUA脚本文件路径
     * @return 初始化成功与否
     */
    public boolean initWithFile(String luaFilePath) {
        luaStatePointer = nativeLuaOpen();
        nativeOpenlibs(luaStatePointer);
        int result = nativeDofile(luaStatePointer, luaFilePath);
        return (result == 0);
    }

    /**
     * 使用字符串初始化.
     *
     * @param luaData LUA脚本内容
     * @return 初始化成功与否
     */
    public boolean initWithData(String luaData) {
        luaStatePointer = nativeLuaOpen();
        nativeOpenlibs(luaStatePointer);
        int result = nativeDoString(luaStatePointer, luaData);
        return (result == 0);
    }

    /**
     * 销毁
     */
    public void destroy() {
        nativeClose(luaStatePointer);
    }

    @Override
    protected void finalize() throws Throwable {
        if (luaStatePointer != 0) {
            destroy();
        }
        super.finalize();
    }

    /**
     * 获取lua栈顶元素的索引
     *
     * @return 返回索引
     */
    public int getTop() {
        return nativeGetTop(luaStatePointer);
    }

    /**
     * 获取索引index处的字符串
     *
     * @param index 索引
     * @return 返回值
     */
    public String tostring(int index) {
        return nativeTostring(luaStatePointer, index);
    }

    /**
     * 获取索引index出的浮点数
     *
     * @param index 索引
     * @return 返回浮点数
     */
    public double tonumber(int index) {
        return nativeTonumber(luaStatePointer, index);
    }

    /**
     * 查看index处的数据类型.
     * 返回值为如下：<br>
     * {@link LuaState#LUA_TNONE}<br>
     * {@link LuaState#LUA_TNIL}<br>
     * {@link LuaState#LUA_TBOOLEAN}<br>
     * {@link LuaState#LUA_TLIGHTUSERDATA}<br>
     * {@link LuaState#LUA_TNUMBER}<br>
     * {@link LuaState#LUA_TSTRING}<br>
     * {@link LuaState#LUA_TTABLE}<br>
     * {@link LuaState#LUA_TFUNCTION}<br>
     * {@link LuaState#LUA_TUSERDATA}<br>
     * {@link LuaState#LUA_TTHREAD}<br>
     *
     * @param index 索引
     * @return 类型
     */
    public int type(int index) {
        return nativeType(luaStatePointer, index);
    }

    /**
     * 获取type的类型名.
     *
     * @param type 必须是{@link LuaState#type} 的返回值
     * @return 返回类型名
     */
    public String typeName(int type) {
        return nativeTypename(luaStatePointer, type);
    }

    /**
     * 输出当前堆栈类容
     */
    public void dumpStack() {
        Log.e(TAG, getStack());
    }

    /**
     * 获取当前堆栈信息
     *
     * @return 返回所有数据变量的字符串。
     */
    public String getStack() {
        int n = getTop();
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= n; i++) {
            int t = type(i);
            sb.append(i).append(": ").append(typeName(t));
            if (t == LUA_TNUMBER)
                sb.append(" = ").append(tonumber(i));
            else if (t == LUA_TSTRING)
                sb.append(" = '").append(tostring(i)).append("'");
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * 把全局变量 name 里的值压入堆栈。
     *
     * @param name 变量名
     */
    public void getGlobal(String name) {
        nativeGetglobal(luaStatePointer, name);
    }

    /**
     * 把string字符串压栈。
     *
     * @param string 待压栈的字符串
     */
    public void pushString(String string) {
        nativePushString(luaStatePointer, string);
    }

    /**
     * 建议使用pcall.
     * 此方法如果遇到异常会直接退出程序，不方便捕获异常信息。
     *
     * @param argNum 函数调用时的参数个数。
     * @param resNum 函数调用返回值的个数。
     */
    public void call(int argNum, int resNum) {
        nativeCall(luaStatePointer, argNum, resNum);
    }

    /**
     * 调用lua中定义的函数.
     * 如果调用异常可以使用{@link LuaState#dumpStack()} 打印相关异常信息。
     *
     * @param argNum 函数调用时的参数个数。
     * @param resNum 函数调用返回值的个数。
     * @return 成功执行返回true，遇到异常返回false。
     */
    public boolean pcall(int argNum, int resNum) {
        int result = nativePcall(luaStatePointer, argNum, resNum);
        return result == 0;
    }

    /**
     * 从堆栈中弹出 n 个元素。
     *
     * @param n 弹出元素个数.
     */
    public void pop(int n) {
        nativePop(luaStatePointer, n);
    }

    //JNI本地方法
    public native void nativePop(long luaPointer, int n);

    public native void nativeCall(long luaPointer, int argNum, int resNum);

    public native int nativePcall(long luaPointer, int argNum, int resNum);

    public native void nativePushString(long luaPointer, String string);

    public native void nativeGetglobal(long luaPointer, String name);

    public native long nativeLuaOpen();

    public native void nativeOpenlibs(long luaPointer);

    public native int nativeDofile(long luaPointer, String luaFile);

    public native int nativeDoString(long luaPointer, String luaScript);

    public native void nativeClose(long luaPointer);

    public native int nativeGetTop(long luaPointer);

    public native String nativeTostring(long luaPointer, int index);

    public native double nativeTonumber(long luaPointer, int index);

    public native int nativeType(long luaPointer, int index);

    public native String nativeTypename(long luaPointer, int type);
}