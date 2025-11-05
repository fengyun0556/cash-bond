package com.cpb.fixengine.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class TcpMessageDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        // 简单的基于换行符的解码器，实际项目中可以根据协议定制
        if (in.readableBytes() > 0) {
            String message = in.toString(StandardCharsets.UTF_8);
            in.clear();
            out.add(message);
        }
    }
}
    