package com.bubble.status.handler.inbound;

import com.bubble.status.enums.MessageType;
import com.bubble.status.model.Message;
import com.bubble.status.service.AuthService;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

@Slf4j
@Component
@ChannelHandler.Sharable
public class AuthHandler extends SimpleChannelInboundHandler<Message> {

    @Autowired
    AuthService authService;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Message message) throws Exception {
        if (message != null) {
            int type = message.getType();
            if (type != MessageType.AUTH.getType()) {
                channelHandlerContext.fireChannelRead(message);
                return;
            }

            InetSocketAddress inetSocketAddress = (InetSocketAddress) channelHandlerContext.channel().remoteAddress();
            boolean success = authService.doAuth(message.getContent(), inetSocketAddress.getAddress().getHostAddress(), inetSocketAddress.getPort());
            if (!success) channelHandlerContext.writeAndFlush("Auth failed. Please check your settings!");
            // message 不再使用 引用计数减一 由simple来自己完成
        }
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        authService.disConnect(inetSocketAddress.getAddress().getHostAddress(), inetSocketAddress.getPort());
        super.channelUnregistered(ctx);
    }
}
