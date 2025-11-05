package com.cpb.fixengine.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.StandardCharsets;

public class TcpMessageEncoder extends MessageToByteEncoder<String> {

    @Override
    protected void encode(ChannelHandlerContext ctx, String msg, ByteBuf out) {
        // 简单的字符串编码器，实际项目中可以根据协议定制
        out.writeBytes(msg.getBytes(StandardCharsets.UTF_8));
    }
}
    