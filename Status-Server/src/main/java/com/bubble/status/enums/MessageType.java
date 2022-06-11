package com.bubble.status.enums;

public enum MessageType {
    AUTH(0), INFO(1);
    int type;

    MessageType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
