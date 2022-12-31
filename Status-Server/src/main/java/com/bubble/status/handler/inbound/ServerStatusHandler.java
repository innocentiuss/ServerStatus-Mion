package com.bubble.status.handler.inbound;

import com.bubble.status.enums.MessageType;
import com.bubble.status.model.Message;
import com.bubble.status.service.StatusService;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

@Component
@ChannelHandler.Sharable
public class ServerStatusHandler extends SimpleChannelInboundHandler<Message> {

    @Autowired
    StatusService statusService;
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Message message) throws Exception {
        if (message != null) {
            int type = message.getType();
            if (type != MessageType.INFO.getType()) {
                channelHandlerContext.fireChannelRead(message);
                return;
            }
            InetSocketAddress inetSocketAddress = (InetSocketAddress) channelHandlerContext.channel().remoteAddress();
            statusService.updateStatus(inetSocketAddress.getAddress().getHostAddress(), inetSocketAddress.getPort(), message.getContent());
        }
    }
}
