package com.sam.demo.webSocket;

import com.alibaba.fastjson.support.spring.messaging.MappingFastJsonMessageConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.messaging.simp.stomp.StompSession.Receiptable;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.jetty.JettyWebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import org.springframework.web.socket.sockjs.frame.Jackson2SockJsMessageCodec;

import javax.websocket.ClientEndpoint;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
@ClientEndpoint
public class Client {

    private static ThreadPoolTaskScheduler taskScheduler;
    private static String url = "ws://localhost:8080/socket";
    private static StompSession stompSession;// 定义全局变量，代表一个session
    private static WebSocketStompClient webSocketStompClient;

    public static void main(String[] args) throws Exception {
        String sendMsg = "{\"msg\": \"test\"}";

        Client myClient = new Client();

        StompSessionHandler handler = new SimpleStompSessionHandler();

        myClient.connect(handler);

        stompSession.subscribe("/topic/toAny", handler);

        Receiptable rec = stompSession.send("/app/welcome1", sendMsg);

        Thread.sleep(10000);
        stompSession.disconnect();
        taskScheduler.destroy();
        webSocketStompClient.stop();

        Thread.sleep(10000);
        myClient.connect(handler);
        stompSession.subscribe("/topic/toAny", handler);
    }

    public void connect(StompSessionHandler handler) throws ExecutionException, InterruptedException {
        if (stompSession == null || !stompSession.isConnected()) {
            log.info("当前处于断开状态,尝试连接");
            // 不要用 StandardWebSocketClient
            StandardWebSocketClient standardWebSocketClient = new StandardWebSocketClient();
            JettyWebSocketClient jettyWebSocketClient = new JettyWebSocketClient();
            Transport webSocketTransport = new WebSocketTransport(jettyWebSocketClient);
            List<Transport> transports = Collections.singletonList(webSocketTransport);
            SockJsClient sockJsClient = new SockJsClient(transports);
            //设置对应的解码器,也是默认解码器
            sockJsClient.setMessageCodec(new Jackson2SockJsMessageCodec());
            webSocketStompClient = new WebSocketStompClient(sockJsClient);
            //设置对应的解码器, MimeType 和 返回类型要对应
            webSocketStompClient.setMessageConverter(new MappingFastJsonMessageConverter());

            long receiptTimeLimit = TimeUnit.SECONDS.toMillis(15);
            webSocketStompClient.setReceiptTimeLimit(receiptTimeLimit);
            // 客户端心跳，服务端心跳 ms
            webSocketStompClient.setDefaultHeartbeat(new long[]{10000, 10000});
            taskScheduler = new ThreadPoolTaskScheduler();
            taskScheduler.afterPropertiesSet();
            webSocketStompClient.setTaskScheduler(taskScheduler);

            WebSocketHttpHeaders headers = new WebSocketHttpHeaders();//请求头
            StompHeaders stompHeaders = new StompHeaders();

            // 必须启动
            webSocketStompClient.start();
            ListenableFuture<StompSession> future = webSocketStompClient.connect(url, headers, stompHeaders, handler);
            stompSession = future.get();
            stompSession.setAutoReceipt(true);
        } else {
            log.info("当前处于连接状态");
        }
    }

}
