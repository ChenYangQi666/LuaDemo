---
--- Generated by EmmyLua(https://github.com/EmmyLua)
--- Created by YangQi.Chen.
--- DateTime: 2023/9/17 03:02
---

-- 必须要引入的库
local JSON = require "cjson"

-- 定义协议属性Key
local KEY_CMD_TYPE = "cmd_type"
local KEY_POWER = "power"
local KEY_BRIGHTNESS = "brightness"

-- 定义协议属性Value
local cmdTypeValue = 0
local powerValue = 0
local brightnessValue = 0

-- 将json字符串转换为LUA中的table
local function decodeJsonToTable(cmd)
    local tb
    if JSON == nil then
        JSON = require "cjson"
    end
    tb = JSON.decode(cmd)
    return tb
end

-- 将LUA中的table转换为json字符串
local function encodeTableToJson(luaTable)
    local jsonStr

    if JSON == nil then
        JSON = require "cjson"
    end

    jsonStr = JSON.encode(luaTable)

    return jsonStr
end

--table 转 string
function table2string(cmd)
    local ret = ""
    local i
    for i = 1, #cmd do
        ret = ret .. string.char(cmd[i])
    end
    return ret
end

-- 将字符串转成十六进制字符串输出
local function string2hexstring(str)
    local ret = ""
    for i = 1, #str do
        ret = ret .. string.format("%02x", str:byte(i))
    end
    return ret
end

-- 将String转int
local function string2Int(data)
    if (not data) then
        data = tonumber("0")
    end
    data = tonumber(data)
    if (data == nil) then
        data = 0
    end
    return data
end

-- 将int转String
local function int2String(data)
    if (not data) then
        data = tostring(0)
    end
    data = tostring(data)
    if (data == nil) then
        data = "0"
    end
    return data
end

-- 打印 table 表
function print_lua_table(lua_table, indent)
    indent = indent or 0
    for k, v in pairs(lua_table) do
        if type(k) == "string" then
            k = string.format("%q", k)
        end
        local szSuffix = ""
        if type(v) == "table" then
            szSuffix = "{"
        end
        local szPrefix = string.rep("    ", indent)
        formatting = szPrefix .. "[" .. k .. "]" .. " = " .. szSuffix
        if type(v) == "table" then
            print(formatting)
            print_lua_table(v, indent + 1)
            print(szPrefix .. "},")
        else
            local szValue = ""
            if type(v) == "string" then
                szValue = string.format("%q", v)
            else
                szValue = tostring(v)
            end
            print(formatting .. szValue .. ",")
        end
    end
end

-- 根据传入的json修改全局变量值
local function updateGlobalPropertyValueByJson(luaTable)
    -- 指令类型
    if luaTable[KEY_CMD_TYPE] == "control" then
        cmdTypeValue = 0x01
    elseif luaTable[KEY_CMD_TYPE] == "query" then
        cmdTypeValue = 0x02
    end
    -- 开关的
    if luaTable[KEY_POWER] == "on" then
        powerValue = 0x01
    elseif luaTable[KEY_POWER] == "off" then
        powerValue = 0x00
    end
    -- 亮度
    if luaTable[KEY_BRIGHTNESS] ~= nil then
        brightnessValue = string2Int(luaTable[KEY_BRIGHTNESS])
    end
end

-- 根据传入的byte[]修改全局变量值
local function updateGlobalPropertyValueByByte(messageBytes)
    cmdTypeValue = messageBytes[1]
    powerValue = messageBytes[2]
    brightnessValue = messageBytes[3]
end

-- 将属性值转换为最终table
local function assembleJsonByGlobalProperty()
    local jsonTable = {}
    -- 指令类型
    if cmdTypeValue == 0x01 then
        jsonTable[KEY_CMD_TYPE] = "control"
    elseif cmdTypeValue == 0x02 then
        jsonTable[KEY_CMD_TYPE] = "query"
    end
    -- 开关灯
    if powerValue == 0x01 then
        jsonTable[KEY_POWER] = "on"
    elseif powerValue == 0x00 then
        jsonTable[KEY_POWER] = "off"
    end
    -- 亮度
    if brightnessValue ~= nil then
        jsonTable[KEY_BRIGHTNESS] = int2String(brightnessValue)
    end
    return jsonTable
end

-- 接口方法，json转二进制，可传入原状态，此方法不能使用local修饰
function jsonToData(jsonCmdStr)
    if (#jsonCmdStr == 0) then
        return nil
    end
    local msgBytes
    local json = decodeJsonToTable(jsonCmdStr)
    -- step1：更新属性
    updateGlobalPropertyValueByJson(json)
    -- step2：
    local bodyBytes = {}
    bodyBytes[1] = cmdTypeValue
    bodyBytes[2] = powerValue
    bodyBytes[3] = brightnessValue

    --table 转换成 string 之后返回
    local ret = table2string(bodyBytes)
    ret = string2hexstring(ret)
    return ret
end

-- 接口方法，二进制转json，此方法不能使用local修饰
function dataToJson(jsonStr)
    if (not jsonStr) then
        return nil
    end
    local jsonTable = assembleJsonByGlobalProperty(jsonStr)
    local ref = encodeTableToJson(jsonTable)
    return ref
end




local jsonCmd = "{\"cmd_type\":\"control\",\"power\":\"on\",\"brightness\":\"88\"}"
local hexResult = jsonToData(jsonCmd)
print("jsonToData()--->>>>>input=" .. jsonCmd)
print("jsonToData()--->>>>>output=" .. hexResult)

local jsonResult = dataToJson(hexResult)
print("dataToJson()--->>>>>input=" .. hexResult)
print("jsonToData()--->>>>>output=" .. jsonResult)







