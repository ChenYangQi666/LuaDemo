package com.govee.lua

import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.govee.pad.base.lua.LuaManager

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity_TAG"
    private lateinit var tvResult: TextView
    private lateinit var mLuaManager: LuaManager

    /**
     * 通过AES加密的lua文件
     */
    private val filePath = "encode_profile.lua"

    /**
     * 解密Lua文件的秘钥
     */
    private val decodeKey = "ABC123456789"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.btnJsonToData).setOnClickListener {
            jsonToData()
        }
        findViewById<Button>(R.id.btnDataToJson).setOnClickListener {
            dataToJson()
        }
        tvResult = findViewById(R.id.tvResult)

        initLuaManager()
    }

    private fun initLuaManager() {
        mLuaManager = LuaManager(this)
        // 设置解密秘钥
        mLuaManager.setDataKey(decodeKey)
    }

    /**
     * json转16进制hex字符串
     */
    private fun jsonToData() {
        val jsonCmd =
            "{\"power\":\"on\",\"cmd_type\":\"control\",\"brightness\":\"188\",\"time\":\"1694943200\"}"
        val beginTime = System.currentTimeMillis()
        // json转16进制指令
        val result = mLuaManager.encodeJsonToBytes(filePath, jsonCmd)

        if (!TextUtils.isEmpty(result)) {
            val sb = StringBuilder()
            sb.append("输入：\n")
                .append(Utils.formatJson(jsonCmd))
                .append("\n\n")
                .append("输出：\n")
                .append(result)
                .append("\n\n")
                .append("耗时：\n")
                .append("${System.currentTimeMillis() - beginTime}毫秒")

            tvResult.text = sb.toString()
        }
    }

    /**
     * 16进制hex转json
     */
    private fun dataToJson() {
        val hexCmd = "0101bce0c70665"
        val beginTime = System.currentTimeMillis()
        // 16进制指令转json
        val result = mLuaManager.decodeBytesToJson(filePath, hexCmd)

        if (!TextUtils.isEmpty(result)) {
            val sb = StringBuilder()
            sb.append("输入：\n")
                .append(hexCmd)
                .append("\n\n")
                .append("输出：\n")
                .append(Utils.formatJson(result))
                .append("\n\n")
                .append("耗时：\n")
                .append("${System.currentTimeMillis() - beginTime}毫秒")
            tvResult.text = sb.toString()
        }
    }
}