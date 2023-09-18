### 一、so库编译方式
使用ndk编译armeabi的方式：{NDK解压目录}/android-ndk-r16b/ndk-build

### 二、Mac安装Lua开发环境
- curl -R -O http://www.lua.org/ftp/lua-5.3.0.tar.gz
- tar zxf lua-5.3.0.tar.gz
- cd lua-5.3.0
- make macosx test
- sudo make install
- 输入lua -v查看是否安装成功

### 二、安装cJson
使用Lua处理json，需要用到cJson
- wget http://www.kyne.com.au/~mark/software/download/lua-cjson-2.1.0.tar.gz 下载安装包
- tar zxvf lua-cjson-2.1.0.tar.gz
- cd lua-cjson-2.1.0/
- 修改配置文件，修改lua版本，默认版本为5.1
  注释掉 CJSON_LDFLAGS = -shared
  设置CJSON_LDFLAGS = -bundle -undefined dynamic_lookup
  保存配置文件然后退出
- make
- sudo make install
- 检查 /usr/local/lib/lua/5.3文件夹下是否存在cjson.so文件，如果存在则代表安装成功

### 三、安装BitOp
使用Lua 5.1处理位运算，需要用到BitOp
- http://bitop.luajit.org/install.html