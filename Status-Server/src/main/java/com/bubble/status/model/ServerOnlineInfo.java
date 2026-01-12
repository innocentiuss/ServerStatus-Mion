package com.bubble.status.model;

import lombok.Data;
@Data
public class ServerOnlineInfo extends ServerConfigInfo {
    private String host;
    private int connectedPort = -1;
    private String connectedIP = "";
    private boolean isOnline = false;
}
