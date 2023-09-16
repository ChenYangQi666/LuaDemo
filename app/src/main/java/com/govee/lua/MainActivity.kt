package com.govee.lua

import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.govee.pad.base.lua.LuaManager
import java.lang.StringBuilder

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity_TAG"
    private lateinit var btnJsonToData: Button
    private lateinit var btnDataToJson: Button
    private lateinit var tvResult1: TextView
    private lateinit var tvResult2: TextView
    private val filePath = "lua_test.lua"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnJsonToData = findViewById(R.id.btnJsonToData)
        btnDataToJson = findViewById(R.id.btnDataToJson)
        tvResult1 = findViewById(R.id.tvResult1)
        tvResult2 = findViewById(R.id.tvResult2)

        btnJsonToData.setOnClickListener {
            jsonToData()
        }
        btnDataToJson.setOnClickListener {
            dataToJson()
        }
    }

    /**
     * json转16进制hex字符串
     */
    private fun jsonToData() {
        val jsonCmd = "{\"cmd_type\":\"control\",\"power\":\"on\",\"brightness\":\"88\"}"
        val result = LuaManager(this).encodeJsonToBytes(filePath, jsonCmd)
        if (!TextUtils.isEmpty(result)) {
            val sb = StringBuilder()
            sb.append("input：\n")
                .append(jsonCmd)
                .append("\n\n")
                .append("output：\n")
                .append(result)

            tvResult1.text = sb.toString()
        }
    }

    /**
     * 16进制hex转json
     */
    private fun dataToJson() {
        val hexCmd = "010158"
        val result = LuaManager(this).decodeBytesToJson(filePath, hexCmd)
        if (!TextUtils.isEmpty(result)) {
            val sb = StringBuilder()
            sb.append("input：\n")
                .append(hexCmd)
                .append("\n\n")
                .append("output：\n")
                .append(result)
            tvResult2.text = sb.toString()
        }
    }
}