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
    private lateinit var mLuaManager: LuaManager

    /**
     * 通过AES加密的lua文件
     */
    private val filePath = "encode_lua_file.text"

    /**
     * 解密Lua文件的秘钥
     */
    private val decodeKey = "cyq123456"

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
        initLuaManager()
    }

    private fun initLuaManager() {
        mLuaManager = LuaManager(this)
        mLuaManager.setDataKey("cyq123456")
    }

    /**
     * json转16进制hex字符串
     */
    private fun jsonToData() {
        val jsonCmd = "{\"cmd_type\":\"control\",\"power\":\"on\",\"brightness\":\"88\"}"
        // json转16进制指令
        val result = mLuaManager.encodeJsonToBytes(filePath, jsonCmd)

        if (!TextUtils.isEmpty(result)) {
            val sb = StringBuilder()
            sb.append("输入：\n")
                .append(jsonCmd)
                .append("\n\n")
                .append("输出：\n")
                .append(result)

            tvResult1.text = sb.toString()
        }
    }

    /**
     * 16进制hex转json
     */
    private fun dataToJson() {
        val hexCmd = "010158"
        // 16进制指令转json
        val result = mLuaManager.decodeBytesToJson(filePath, hexCmd)

        if (!TextUtils.isEmpty(result)) {
            val sb = StringBuilder()
            sb.append("输入：\n")
                .append(hexCmd)
                .append("\n\n")
                .append("输出：\n")
                .append(result)
            tvResult2.text = sb.toString()
        }
    }

    /*private fun testEncode() {
        val str1 = AESCrypt.encrypt(readLuaStringFromAssets(filePath), "cyq123456")
        Log.e("AESCrypt", str1)
        val file = File(cacheDir, "encode_test.lua")
        if (file.exists()) {
            file.delete()
        }
        file.createNewFile()
        val writer = BufferedWriter(FileWriter(file))
        writer.write(str1)
        writer.close()
        Log.e("AESCrypt", "文件写入完成=${file.absolutePath}")

        val str2 = AESCrypt.decrypt(str1, "cyq123456")
        Log.e("AESCrypt", str2)
    }

    */
    /**
     * 读取getAssets下的Lua文件，并转字符串
     *
     * @param fileName:lua文件目录
     *//*
    private fun readLuaStringFromAssets(fileName: String): String? {
        return try {
            if (TextUtils.isEmpty(fileName)) {
                return ""
            }
            val inputStream: InputStream = assets.open(fileName)
            val inputReader = InputStreamReader(inputStream)
            val bufReader = BufferedReader(inputReader)
            var line: String?
            val Result = StringBuilder()
            while (bufReader.readLine().also { line = it } != null) {
                Result.append(line).append("\n")
            }
            bufReader.close()
            inputReader.close()
            inputStream.close()
            Result.toString()
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }*/
}