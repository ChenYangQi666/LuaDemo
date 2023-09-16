package com.govee.pad.base.lua;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author：YangQi.Chen
 * @date：2023/9/8 10:42
 * @description：Lua管理器
 */
public class LuaManager {
    private static final String TAG = "LuaManager";
    private final Context mContext;
    /**
     * LUA脚本的JSON转换数据的方法
     */
    private static final String JSON_TO_DATA = "jsonToData";

    /**
     * LUA脚本的数据转换JSON的方法
     */
    private static final String DATA_TO_JSON = "dataToJson";

    /**
     * LUA脚本对象缓存
     */
    private final ConcurrentHashMap<String, LuaState> mLuaStateCacheMap = new ConcurrentHashMap<>();

    /**
     * 解密Lua字符流的密钥
     */
    private String mDecodeKey = "";

    public LuaManager(Context context) {
        mContext = context;
    }

    public void setDataKey(String dataKey) {
        mDecodeKey = dataKey;
    }

    /**
     * 将设备的JSON控制指令转换成设备二进制控制数据.
     *
     * @param filePath 设备类型
     * @param jsonStr  需要转换的JSON指令
     * @return 设备二进制控制数据
     */
    public synchronized String encodeJsonToBytes(String filePath, String jsonStr) {
        LuaState luaState = createLuaState(filePath, true);
        if (luaState != null) {
            luaState.getGlobal(JSON_TO_DATA);
            luaState.pushString(jsonStr);
            boolean success = luaState.pcall(1, 1);
            if (success) {
                String byteStr = luaState.tostring(-1);
                luaState.pop(1);
                return byteStr;
            } else {
                Log.e(TAG, "encode json to bytes failed: " + jsonStr);
                luaState.dumpStack();
            }
        } else {
            Log.e(TAG, "LuaState is null!");
        }
        return null;
    }

    /**
     * 将设备二进制控制数据转换成JSON控制指令.
     *
     * @param filePath 设备类型
     * @param jsonStr  二进制数据协议JSON
     * @return 请求结果JSON
     */
    public synchronized String decodeBytesToJson(String filePath, String jsonStr) {
        LuaState luaState = createLuaState(filePath, true);
        if (luaState != null) {
            luaState.getGlobal(DATA_TO_JSON);
            luaState.pushString(jsonStr);
            boolean success = luaState.pcall(1, 1);
            if (success) {
                String str = luaState.tostring(-1);
                luaState.pop(1);
                return str;
            } else {
                Log.e(TAG, "Decode byte to json failed: " + jsonStr);
                luaState.dumpStack();
            }
        }
        return null;
    }

    /**
     * 创建LUA状态机
     *
     * @param filePath:  assets目录下的lua文件路径
     * @param encrypted: 是否需要解密，如果是加密的lua文件，则需要解密
     * @return lua状态机LuaState
     */
    private synchronized LuaState createLuaState(String filePath, boolean encrypted) {
        LuaState luaState = mLuaStateCacheMap.get(filePath);
        if (luaState == null) {
            boolean initSuccess = false;
            String errMsg = null;
            luaState = new LuaState();
            if (encrypted) {
                String data = readLuaStringFromAssets(filePath);
                Log.i(TAG, "data=" + data);
                if (data.length() > 0) {
                    String decodeData = data;
                    if (mDecodeKey != null) {
                        decodeData = SecurityUtils.decodeAES128WithAppKey(data, mDecodeKey);
                    }
                    if (!TextUtils.isEmpty(decodeData)) {
                        if (luaState.initWithData(decodeData)) {
                            initSuccess = true;
                        } else {
                            errMsg = "Lua file init with data failed!";
                        }
                    } else {
                        errMsg = "Lua file decode failed!";
                    }
                } else {
                    errMsg = "Lua file is empty!";
                }
            } else {
                if (luaState.initWithFile(filePath)) {
                    initSuccess = true;
                } else {
                    errMsg = "Lua file init with file failed!";
                }
            }
            if (initSuccess) {
                mLuaStateCacheMap.put(filePath, luaState);
                return luaState;
            } else {
                Log.e(TAG, errMsg);
                return null;
            }
        }
        return luaState;
    }

    /**
     * 读取getAssets下的Lua文件，并转字符串
     *
     * @param fileName:lua文件目录
     */
    private String readLuaStringFromAssets(String fileName) {
        try {
            if (TextUtils.isEmpty(fileName) || mContext == null) {
                return "";
            }
            InputStream inputStream = mContext.getAssets().open(fileName);
            InputStreamReader inputReader = new InputStreamReader(inputStream);
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line;
            StringBuilder Result = new StringBuilder();
            while ((line = bufReader.readLine()) != null) {
                Result.append(line).append("\n");
            }
            bufReader.close();
            inputReader.close();
            inputStream.close();
            return Result.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
