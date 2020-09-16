package com.sam.demo.webSocket;

import com.alibaba.fastjson.support.spring.messaging.MappingFastJsonMessageConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSession.Receiptable;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.jetty.JettyWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import org.springframework.web.socket.sockjs.frame.Jackson2SockJsMessageCodec;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;

@Slf4j
public class Client {

    private String url;
    private ThreadPoolTaskScheduler taskScheduler;
    private WebSocketStompClient webSocketStompClient;
    private StompSession stompSession;
    private StompSessionHandler handler;

    public Client(String url, StompSessionHandler handler) {
        this.url = url;
        this.handler = handler;
        // 不要用 StandardWebSocketClient
        // StandardWebSocketClient standardWebSocketClient = new StandardWebSocketClient();
        JettyWebSocketClient jettyWebSocketClient = new JettyWebSocketClient();
        Transport webSocketTransport = new WebSocketTransport(jettyWebSocketClient);
        List<Transport> transports = Collections.singletonList(webSocketTransport);
        SockJsClient sockJsClient = new SockJsClient(transports);
        //设置对应的解码器,也是默认解码器
        sockJsClient.setMessageCodec(new Jackson2SockJsMessageCodec());
        webSocketStompClient = new WebSocketStompClient(sockJsClient);
        taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.afterPropertiesSet();
        webSocketStompClient.setTaskScheduler(taskScheduler);
        webSocketStompClient.setAutoStartup(true);
    }

    public void setMessageConverter(MessageConverter messageConverter) {
        //设置对应的解码器, MimeType 和 返回类型要对应
        webSocketStompClient.setMessageConverter(messageConverter);
    }

    public void setReceiptTimeLimit(long receiptTimeLimit) {
        // 接收时间限制 ms
        webSocketStompClient.setReceiptTimeLimit(receiptTimeLimit);
    }

    public void setDefaultHeartbeat(long[] heartbeat) {
        // 两位数组，客户端，服务的心跳 ms
        webSocketStompClient.setDefaultHeartbeat(heartbeat);
    }

    public void setInboundMessageSizeLimit(int inboundMessageSizeLimit) {
        // 入栈数据缓冲区
        webSocketStompClient.setInboundMessageSizeLimit(inboundMessageSizeLimit);
    }

    public static void main(String[] args) throws Exception {
        String sendMsg = "{\"msg\": \"test\"}";

        SimpleStompSessionHandler handler = new SimpleStompSessionHandler();
        Client client = new Client("ws://localhost:8080/socket", handler);
        client.setReceiptTimeLimit(15000);
        client.setDefaultHeartbeat(new long[]{10000, 10000});
        client.setInboundMessageSizeLimit(64 * 1024);
        client.setMessageConverter(new MappingFastJsonMessageConverter());

        client.connect();

        // 订阅相关
        StompSession.Subscription subscribe = client.subscribe("/user/sam/any", handler);
        BlockingQueue<Object> blockingQueue = handler.getBlockingQueue();
        Object poll = blockingQueue.take();
        System.out.println(poll);

        // 发送回调相关
//        Receiptable rec = client.send("/app/welcome1", sendMsg);
//        rec.addReceiptTask(() -> System.out.println("发送成功"));

        subscribe.unsubscribe();
        client.disconnect();
    }

    public void connect() throws Exception {
        if (!webSocketStompClient.isRunning()) {
            // 必须启动
            webSocketStompClient.start();
        }
        if (stompSession == null || !stompSession.isConnected()) {
            log.info("当前处于断开状态,尝试连接");
            WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
            StompHeaders stompHeaders = new StompHeaders();
            ListenableFuture<StompSession> future = webSocketStompClient.connect(url, headers, stompHeaders, handler);
            stompSession = future.get();
            stompSession.setAutoReceipt(true);
        } else {
            log.info("当前处于连接状态");
        }
    }

    public void disconnect() {
        if (stompSession != null && stompSession.isConnected()) {
            stompSession.disconnect();
        }
        taskScheduler.destroy();
        if (webSocketStompClient.isRunning()) {
            webSocketStompClient.stop();
        }
    }

    public StompSession.Subscription subscribe(String destination, StompFrameHandler handler){
        return stompSession.subscribe(destination, handler);
    }

    public Receiptable send(String destination, Object payload){
        return stompSession.send(destination, payload);
    }

}
