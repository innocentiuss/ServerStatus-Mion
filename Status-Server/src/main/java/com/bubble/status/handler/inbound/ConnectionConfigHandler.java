package com.bubble.status.handler.inbound;

import com.bubble.status.model.Status;
import com.bubble.status.structure.FastConcurrentMap;
import io.netty.channel.ChannelInboundHandlerAdapter;

// 为每一个channel绑定一个属性
public class ConnectionConfigHandler extends ChannelInboundHandlerAdapter {

    private int connectionIndex;

    public ConnectionConfigHandler() {
        super();
        connectionIndex = FastConcurrentMap.getNextIndex();
    }

    public int getConnectionIndex() {
        return connectionIndex;
    }

    public void putVal(Status status) {

    }
}
