# ServerStatus-Bubble

以Java为服务端实现的一款前后端分离服务器探针、云监控

前端基于cokemine/Hotaru_theme项目修改, 使用Websocket替代HTTP实现通信, 使用Vue3、Typescript、Semantic UI

服务端基于Netty、非阻塞IO方式实现

客户端基于cppla/ServerStatus中Python客户端修改实现，无外部依赖

## 目录说明

+ Status-Client: Python客户端(需要Python3+版本)
+ Status-Server: Java服务端, 基于Maven构建
+ Status-Web: 前端展示页面, 使用npm工具构建

## 使用

### 服务端

1 安装Java环境(如果已有请跳过这一步), 推荐JDK11

```
yum -y install java-11-openjdk.x86_64
```

2 前往[release](https://github.com/innocentiuss/ServerStatus-Bubble/releases)下载最新版本后解压进入

```
unzip xxx.zip && cd xxx
```

3 配置服务器连接信息server_config.json

4 运行服务端, 例如

```
java -jar server-status-x.x.x.jar
```

无报错后即可后台运行

```
nohup java -jar server-status-x.x.x.jar &
```

5 Nginx或者其他Web服务器创建好网站后, 将Web下所有内容拷贝到网站根目录中, 例如

```
sudo cp -r web/* /home/wwwroot/yourwebsite
```

即可正常运行

##### 服务端可选配置

自定义配置文件名(需要在jar包同目录下)

```
java -jar server-status-x.x.x.jar --server.config=xxx.json
```

Java Tomcat Web服务器端口，需要和上面反向代理规则一致(默认8080端口)

```
java -jar ... --server.port=xxxx
```

Netty服务器端口, 需要和客户端连接配置设置一致(默认48084端口)

```
java -jar ... --netty.server.port=xxx
```

所有配置可配合使用

### 客户端

下载客户端后, 修改连接信息(user, password, server_ip)后使用Python3运行即可

```shell
wget https://raw.githubusercontent.com/innocentiuss/ServerStatus-Bubble/main/Status-Client/status_client.py
vim status_client.py
python3 status_client.py
```

连接成功后退出, 再配合nohup实现后台运行即可

## 相关项目

+ Hotaru_theme: [https://github.com/cokemine/hotaru_theme](https://github.com/cokemine/hotaru_theme)
+ ServerStatus: [https://github.com/cppla/ServerStatus](https://github.com/cppla/ServerStatus)
+ Netty: [https://github.com/netty/netty](https://github.com/netty/netty)
