package com.bubble.status.model;

import lombok.Data;

import java.util.List;

// 对应server_config文件的实体类
@Data
public class Configs {
    private Login loginInfo;
    private List<ConfigInfo> servers;
}
