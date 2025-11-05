package com.cpb.fixengine.config;

import com.cpb.fixengine.codec.TcpMessageDecoder;
import com.cpb.fixengine.codec.TcpMessageEncoder;
import com.cpb.fixengine.handler.TcpServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class TcpServerConfig {

    @Value("${tcp.server.port}")
    private int port;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ChannelFuture serverChannelFuture;

    @Bean
    public ServerBootstrap tcpServerBootstrap(TcpServerHandler tcpServerHandler) {
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new TcpMessageDecoder());
                        ch.pipeline().addLast(new TcpMessageEncoder());
                        ch.pipeline().addLast(tcpServerHandler);
                    }
                });

        try {
            // 绑定端口，启动服务器（非阻塞）
            serverChannelFuture = bootstrap.bind(port).syncUninterruptibly();
            log.info("TCP服务器启动，监听端口: " + port);
        } catch (Exception e) {
            log.error("TCP服务器启动失败: " + e.getMessage());
            // 启动失败不影响应用启动
        }

        return bootstrap;
    }

    @PreDestroy
    public void stopServer() {
        if (serverChannelFuture != null) {
            try {
                serverChannelFuture.channel().close().sync();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        log.info("TCP服务器已关闭");
    }
}
    