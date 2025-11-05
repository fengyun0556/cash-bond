package com.bbg.handler;

import com.bbg.service.BBGAckService;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@ChannelHandler.Sharable
@Component
@Slf4j
public class TcpClientHandler extends ChannelInboundHandlerAdapter {

//    private volatile ChannelHandlerContext serverCtx;
    @Autowired
    private BBGAckService bbgAckService;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("已连接到Fix Engine服务器");
//        this.serverCtx = ctx;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            log.info("收到客户端[{}]消息: {}", ctx.channel().remoteAddress(), msg.toString());
//            ctx.writeAndFlush("ack");
//            log.info("发送响应给客户端[{}]", ctx.channel().remoteAddress());

            // 处理业务逻辑
            bbgAckService.sendAckMessage(msg.toString());
        } catch (Exception e) {
            log.error("处理消息出错", e);
            // 发送错误响应
//            ctx.writeAndFlush("0=0");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("客户端发生异常: " + cause.getMessage());
        ctx.close();
    }

    // 向服务器发送消息的方法
    /*public void sendMessage(String message) {
        if (serverCtx != null && serverCtx.channel().isActive()) {
            serverCtx.writeAndFlush(message);
        } else {
            log.error("未连接到服务器，无法发送消息");
        }
    }*/

}
    