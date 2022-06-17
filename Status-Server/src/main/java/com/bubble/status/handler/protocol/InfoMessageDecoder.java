package com.bubble.status.handler.protocol;

import com.bubble.status.model.Message;
import com.bubble.status.utils.CheckUtil;
import com.bubble.status.utils.ReadUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
public class InfoMessageDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        // 6字节魔数
        String magicNum = ReadUtil.readStringFromBuf(byteBuf, 6);
        if (CheckUtil.isNotSame(magicNum, "bubble")) {
            InetSocketAddress inetSocketAddress = (InetSocketAddress) channelHandlerContext.channel().remoteAddress();
            log.warn("接收到数据包错误, 断开连接, ip: {}", inetSocketAddress.getAddress().getHostAddress());
            channelHandlerContext.close();
            return;
        }

        // 1字节类型
        byte type = byteBuf.readByte();
        // 4字节长度
        int length = byteBuf.readInt();
        // 根据长度读取内容
        String content = ReadUtil.readStringFromBuf(byteBuf, length);
        Message message = new Message(type, content);
        list.add(message);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        log.info("检测到新连接建立, ip: {}, port: {}", inetSocketAddress.getAddress().getHostAddress(), inetSocketAddress.getPort());
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        log.info("检测到连接断开, ip: {}, port: {}", inetSocketAddress.getAddress().getHostAddress(), inetSocketAddress.getPort());
        super.channelUnregistered(ctx);
    }
}
