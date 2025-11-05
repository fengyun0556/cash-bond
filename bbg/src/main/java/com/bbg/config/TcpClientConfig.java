package com.bbg.config;

import com.bbg.codec.TcpMessageDecoder;
import com.bbg.codec.TcpMessageEncoder;
import com.bbg.handler.TcpClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Configuration
@Slf4j
public class TcpClientConfig {

    @Value("${tcp.client.host}")
    private String host;
    @Value("${tcp.client.port}")
    private int port;
    @Value("${tcp.client.local-port}") // 新增：本地绑定端口，0表示随机
    private int localPort;
    @Value("${tcp.client.reconnect-delay:5}") // 重连延迟（秒）
    private int reconnectDelay;

    private EventLoopGroup group;
    private Bootstrap bootstrap;
    private Channel channel;
    private final ScheduledExecutorService reconnectExecutor = Executors.newSingleThreadScheduledExecutor();

    @Bean
    public Bootstrap tcpClientBootstrap(TcpClientHandler tcpClientHandler) {
        group = new NioEventLoopGroup();

        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.SO_REUSEADDR, true) // 重要：允许地址重用
                .localAddress(localPort) // 重要：指定本地绑定端口
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new TcpMessageDecoder());
                        ch.pipeline().addLast(new TcpMessageEncoder());
                        ch.pipeline().addLast(tcpClientHandler);
                    }
                });

        // 连接服务器，失败后自动重试
        connectWithRetry();

        return bootstrap;
    }

    private void connectWithRetry() {
        try {
            ChannelFuture future = bootstrap.connect(host, port);
            
            future.addListener(f -> {
                if (f.isSuccess()) {
                    channel = future.channel();
                    log.info("成功连接到TCP服务器: " + host + ":" + port);
                    
                    // 注册断线重连监听
                    channel.closeFuture().addListener(closeFuture -> {
                        log.info("与服务器的连接已断开，将在" + reconnectDelay + "秒后尝试重连");
                        reconnectExecutor.schedule(this::connectWithRetry, reconnectDelay, TimeUnit.SECONDS);
                    });
                } else {
                    log.error("连接服务器失败，将在" + reconnectDelay + "秒后重试: " + f.cause().getMessage());
                    reconnectExecutor.schedule(this::connectWithRetry, reconnectDelay, TimeUnit.SECONDS);
                }
            });
        } catch (Exception e) {
            log.error("连接发生错误，将在" + reconnectDelay + "秒后重试: " + e.getMessage());
            reconnectExecutor.schedule(this::connectWithRetry, reconnectDelay, TimeUnit.SECONDS);
        }
    }

    public Channel getChannel() {
        return channel;
    }

    @PreDestroy
    public void stopClient() {
        reconnectExecutor.shutdown();
        
        if (channel != null && channel.isActive()) {
            channel.close().syncUninterruptibly();
        }
        
        if (group != null) {
            group.shutdownGracefully();
        }
        
        log.info("TCP客户端已关闭");
    }
}
    