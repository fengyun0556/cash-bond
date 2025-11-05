package com.cpb.fixengine.handler;

import com.cpb.fixengine.service.BBGService;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ChannelHandler.Sharable
@Slf4j
public class TcpServerHandler extends ChannelInboundHandlerAdapter {

    // 保存客户端连接，实际项目中可以用ConcurrentHashMap管理多个连接
    private volatile ChannelHandlerContext clientCtx;
    @Autowired
    private BBGService bbgService;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("新的客户端连接: " + ctx.channel().remoteAddress());
        this.clientCtx = ctx;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        String message = (String) msg;
        log.info("Fix Engine收到BBG消息: " + message);

        // 这里可以根据业务逻辑调用其他服务处理消息
        String[] values = message.split("\\|");
        for (String value : values) {
            if (value.startsWith("18=")) {
                String[] strings = value.split("=");
                if ("0".equals(strings[1])) {
                    bbgService.handleBBGAck(values);
                } else if ("1".equals(strings[1])) {
                    bbgService.handleBBGExecution(values);
                }
                break;
            }
        }
//        ctx.writeAndFlush("0=1");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("服务端发生异常: " + cause.getMessage());
        ctx.close();
    }

    // 向客户端发送消息的方法
    public void sendMessage(String message) {
        if (clientCtx != null && clientCtx.channel().isActive()) {
            clientCtx.writeAndFlush(message);
        } else {
            log.error("客户端未连接，无法发送消息");
        }
    }
}
    