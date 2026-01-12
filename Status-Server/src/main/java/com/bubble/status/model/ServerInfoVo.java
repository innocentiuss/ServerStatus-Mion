package com.bubble.status.model;

import lombok.Data;

@Data
public class ServerInfoVo extends Status{
    private String name;
    private String type;
    private String location;
    private boolean isOnline;
    private boolean lost = false;
    private String region;
}
