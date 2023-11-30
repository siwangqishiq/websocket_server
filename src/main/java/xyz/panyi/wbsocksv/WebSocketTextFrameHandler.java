package xyz.panyi.wbsocksv;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import xyz.panyi.wbsocksv.util.LogUtil;

public class WebSocketTextFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    public static final String TAG = "WebSocketTextFrameHandler";


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        LogUtil.log(TAG , "channelActive " + ctx.channel().remoteAddress());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame textFrame) throws Exception {
        final String content = textFrame.text();
        LogUtil.log(TAG , ctx.channel().remoteAddress() + " : " + content);
        sendTextMsg(ctx.channel(),"Hello I am Server");
    }

    private void sendTextMsg(Channel channel , String msg){
        channel.writeAndFlush(new TextWebSocketFrame(msg));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        LogUtil.log(TAG , "channelInactive " + ctx.channel().remoteAddress());
    }
}
