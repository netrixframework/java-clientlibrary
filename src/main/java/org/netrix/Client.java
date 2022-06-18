package org.netrix;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import org.netrix.comm.*;
import org.netrix.timeouts.Timeout;
import org.netrix.timeouts.Timer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

public class Client extends Thread {
    NettyServer server;
    NetrixClient client;
    Timer timer;
    DirectiveExecutor executor;
    Config config;
    MessageHandler messageHandler;

    ChannelFuture serverFuture;

    public Client(Config c, DirectiveExecutor executor) {
        this.config = c;
        this.timer = new Timer();
        this.client = new NetrixClient(c);
        this.executor = executor;
        this.messageHandler = new MessageHandler(this.client);
        initServer();

        try {
            this.client.register();
        } catch (IOException ignored) {

        }
    }

    @Override
    public void run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast(new HttpRequestDecoder());
                        p.addLast(new HttpResponseEncoder());
                        p.addLast(server);
                    }
                }).childOption(ChannelOption.SO_KEEPALIVE, true);

            this.serverFuture = b.bind(config.clientServerAddr, config.clientServerPort).sync();
        } catch (Exception ignored) {

        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public void stopClient() {
        try {
            this.serverFuture.channel().closeFuture().sync();
        } catch (Exception ignored) {

        }
    }

    private void initServer() {
        NettyRouter router = new NettyRouter();

        Route messagesRoute = new Route("messages");
        messagesRoute.post(messageHandler);
        router.addRoute(messagesRoute);

        Route directiveRoute = new Route("directive");
        directiveRoute.post(new DirectiveHandler(this.executor));
        router.addRoute(directiveRoute);

        Route timeoutRoute = new Route("timeouts");
        timeoutRoute.post(new TimeoutHandler(timer, client));
        router.addRoute(timeoutRoute);

        this.server = new NettyServer(router);
    }

    public Vector<Message> getMessages() {
        return messageHandler.getMessages();
    }

    public void sendMessage(Message message) throws IOException {
        message.setFrom(config.replicaID);
        client.sendMessage(message);

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("message_id", message.getId());
        Event sendEvent = new Event("MessageSend", params);
        sendEvent.setReplicaID(config.replicaID);
        client.sendEvent(sendEvent);
    }

    public void setReady() throws IOException {
        this.client.setReady();
    }

    public void unsetReady() throws IOException {
        this.client.unsetReady();
    }

    public void sendEvent(String type, HashMap<String, String> params) throws IOException{
        Event event = new Event(type, params);
        event.setReplicaID(config.replicaID);
        client.sendEvent(event);
    }

    public void sendEvent(Event event) throws IOException {
        event.setReplicaID(config.replicaID);
        client.sendEvent(event);
    }

    public void startTimeout(Timeout timeout) {
        if(timer.addTimeout(timeout)) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("type", timeout.key());
            params.put("duration", timeout.getDuration().toString());
            Event event = new Event("TimeoutStart", params);
        }
    }
}
