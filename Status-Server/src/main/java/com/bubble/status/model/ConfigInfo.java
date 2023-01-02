package com.bubble.status.model;

import lombok.Data;

@Data
public class ConfigInfo {
    private boolean disabled = false;
    private String username;
    private String password;
    private String name;
    private String type;
    private String location;
    private String region;
}
