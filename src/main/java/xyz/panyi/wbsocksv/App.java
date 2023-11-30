package xyz.panyi.wbsocksv;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import xyz.panyi.wbsocksv.util.LogUtil;

import java.net.InetSocketAddress;

public class App {
    public static final String TAG = "App";

    public static final int PORT = 1998;

    private final EventLoopGroup workerGroup = new NioEventLoopGroup();

    public static void main(String[] args){
        LogUtil.log(TAG , "开启服务...");
        new App().execute();
    }

    public void execute(){
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new LoggingHandler(LogLevel.INFO))  // 在BossGroup中增加一个日志处理器 日志级别为INFO
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new HttpServerCodec());
                            pipeline.addLast(new ChunkedWriteHandler());
                            pipeline.addLast(new HttpObjectAggregator(8192));
                            pipeline.addLast(new WebSocketServerProtocolHandler("/counter"));

                            // 自定义Handler, 处理业务逻辑
                            pipeline.addLast(new WebSocketTextFrameHandler());
                        }
                    });
            LogUtil.log(TAG , "netty server is starter......");
            ChannelFuture sync = serverBootstrap.bind(PORT).sync();
            sync.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}//end class
