package com.govee.lua

import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.govee.pad.base.lua.LuaManager

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity_TAG"
    private lateinit var btnJsonToData: Button
    private lateinit var btnDataToJson: Button
    private lateinit var tvResult1: TextView
    private lateinit var tvResult2: TextView

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

    private fun jsonToData() {
        val result = LuaManager(this)
            .encodeJsonToBytes("test.lua", "66666666")
        if (!TextUtils.isEmpty(result)) {
            tvResult1.text = result
        }
    }

    private fun dataToJson() {
        val result = LuaManager(this)
            .decodeBytesToJson("test.lua", "88888888")
        if (!TextUtils.isEmpty(result)) {
            tvResult2.text = result
        }
    }
}