# ServerStatus-Bubble

以Java为服务端实现的一款前后端分离服务器探针、云监控

前端基于cokemine/Hotaru_theme项目修改, 使用Vue3、Typescript、Semantic UI

服务端基于SpringBoot、Netty(NIO方式)实现, 适合Netty初学者作为入门项目学习

客户端基于cppla/ServerStatus中Python客户端修改实现，无外部依赖

## 目录说明

+ Status-Client: Python客户端(需要Python3+版本)
+ Status-Server: Java服务端, 基于Maven构建
+ Status-Web: 前端展示页面, 使用npm工具构建

## 使用介绍

### 服务端安装

1 安装Java环境(如果已有请跳过这一步), 推荐jdk11

```
yum -y install java-11-openjdk.x86_64
```



## 相关项目

+ Hotaru_theme: [https://github.com/cokemine/hotaru_theme](https://github.com/cokemine/hotaru_theme)
+ ServerStatus: [https://github.com/cppla/ServerStatus](https://github.com/cppla/ServerStatus)
+ Netty: [https://github.com/netty/netty](https://github.com/netty/netty)