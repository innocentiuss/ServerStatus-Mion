package com.bubble.status.runner;

import com.bubble.status.handler.inbound.ServerStatusHandler;
import com.bubble.status.handler.protocol.InfoMessageDecoder;
import com.bubble.status.handler.inbound.AuthHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NettyBootstrapRunner implements ApplicationRunner, ApplicationListener<ContextClosedEvent>, ApplicationContextAware {

    @Value("${netty.server.port}")
    private int port;

    private ApplicationContext applicationContext;

    private Channel serverChannel;
    @Autowired
    private AuthHandler authHandler;
    @Autowired
    private ServerStatusHandler serverStatusHandler;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup);
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                            nioSocketChannel.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024, 7, 4, 0, 0));
                            nioSocketChannel.pipeline().addLast(new InfoMessageDecoder());
                            nioSocketChannel.pipeline().addLast(authHandler);
                            nioSocketChannel.pipeline().addLast(serverStatusHandler);
                        }
                    });
            Channel channel = serverBootstrap.bind(this.port).sync().channel();
            this.serverChannel = channel;
            log.info("netty 服务启动, port={}", this.port);
            channel.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        if (this.serverChannel != null) {
            try {
                this.serverChannel.close().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        log.info("Netty server 正在停止");
    }
}
