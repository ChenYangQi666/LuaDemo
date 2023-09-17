package com.govee.lua

import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.govee.pad.base.lua.AESCrypt
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

/**
 * @author：YangQi.Chen
 * @date：2023/9/17 18:30
 * @description：
 */
object Utils {

    fun formatJson(jsonString: String): String {
        val indentSize = 4 // 定义缩进的空格数
        val result = StringBuilder()
        var currentIndent = 0
        var inString = false

        for (char in jsonString) {
            when (char) {
                '{', '[' -> {
                    result.append(char)
                    result.append('\n')
                    currentIndent += indentSize
                    result.append(" ".repeat(currentIndent))
                }
                '}', ']' -> {
                    result.append('\n')
                    currentIndent -= indentSize
                    result.append(" ".repeat(currentIndent))
                    result.append(char)
                }
                ',' -> {
                    result.append(char)
                    if (!inString) {
                        result.append('\n')
                        result.append(" ".repeat(currentIndent))
                    }
                }
                '"' -> {
                    result.append(char)
                    inString = !inString
                }
                else -> {
                    result.append(char)
                }
            }
        }

        return result.toString()
    }


    /**
     * 加密Lua文件工具方法
     */
    fun testEncode(context: Context) {
        val str1 = AESCrypt.encrypt(readLuaStringFromAssets(context, "原始lua脚本.lua"), "ABC123456789")
        Log.e("AESCrypt", str1)
        val file = File(context.cacheDir, "encode_profile.lua")
        if (file.exists()) {
            file.delete()
        }
        file.createNewFile()
        val writer = BufferedWriter(FileWriter(file))
        writer.write(str1)
        writer.close()
        Log.e("AESCrypt", "文件写入完成=${file.absolutePath}")
        val str2 = AESCrypt.decrypt(str1, "ABC123456789")
        Log.e("AESCrypt", str2)
    }

    /*
     * 读取getAssets下的Lua文件，并转字符串
     *
     * @param fileName:lua文件目录
     */
    private fun readLuaStringFromAssets(context: Context, fileName: String): String? {
        return try {
            if (TextUtils.isEmpty(fileName)) {
                return ""
            }
            val inputStream: InputStream = context.assets.open(fileName)
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
    }
}