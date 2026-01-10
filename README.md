# ServerStatus-Mion

以Java为服务端实现的一款前后端分离服务器探针、云监控

前端基于cokemine/Hotaru_theme项目拓展, 使用Websocket替代HTTP实现通信, 使用Vue3、Typescript、Semantic UI, 新增后台管理页面, 可实现节点热添加/删除/编辑

服务端基于Netty、非阻塞式IO方式实现，性能更高效

客户端（采集端）基于cppla/ServerStatus中Python客户端修改实现，无外部依赖 

工作机制为：客户端运行在Linux系统上运行收集数据，并push到服务端，服务端整理后展示在前端 

##### Demo图片展示:

![主界面1](https://s1.ax1x.com/2023/01/07/pSVJGff.png)

![主界面2](https://s1.ax1x.com/2023/01/07/pSVJUXQ.png)

![后台](https://s1.ax1x.com/2023/01/07/pSV0olt.png)

## 项目目录说明

+ Status-Client: Python客户端(需要Python3+版本)
+ Status-Server: Java服务端, 基于Maven构建
+ Status-Web: 前端展示页面, 使用npm工具构建

## 使用

### 项目服务端部署
##### 后端部署
1 安装Java环境(如果已有请跳过这一步), 推荐JDK11

```
yum -y install java-11-openjdk.x86_64
```

2 前往[release](https://github.com/innocentiuss/ServerStatus-Mion/releases)下载最新版本后解压进入

```
unzip xxx.zip && cd xxx
```

3 配置服务器连接信息server_config.json

4 运行服务端, 例如(会占用8080端口)

```
java -jar server-status-x.x.x.jar
```

无报错后即可后台运行

```
nohup java -jar server-status-x.x.x.jar &
```
##### 前端部署
5 Nginx或者其他Web服务器创建好网站后, 将下载下来的zip包中Web文件夹下所有内容拷贝到网站根目录中, 例如

```
sudo cp -r web/* /home/wwwroot/yourwebsite
```

6 添加伪静态设置, 这里以Nginx为例(其实包括了一个反向代理, 为了解决跨域问题)

```
location / {
  try_files $uri $uri/ /index.html;
}
location /api/ {
  proxy_pass http://localhost:8080/api/;
}
```

即可正常运行~

+ 后台管理页面为:http://yourwebsite/admin

##### 服务端可选配置

自定义配置文件名(需要在jar包同目录下)

```
java -jar server-status-x.x.x.jar --server.config=xxx.json
```

Netty服务器端口, 需要和客户端连接配置设置一致(默认48084端口)

```
java -jar ... --netty.server.port=xxx
```

### 客户端安装部署

客户端仅一个文件，下载客户端后, 编辑里面的连接信息(user, password, server_ip)后使用Python3运行即可

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
