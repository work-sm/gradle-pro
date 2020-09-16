package com.sam.demo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.util.Map;

@Slf4j
@Configuration
//开启stomp协议来传输基于代理message broker的消息
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    // 添加这个Endpoint
    @Override
    public void registerStompEndpoints(StompEndpointRegistry stompEndpointRegistry) {
        //注册一个Stomp的节点，客户端就可以通过这个端点来进行连接
        stompEndpointRegistry.addEndpoint("/socket")
                //拦截器（握手）
                .setHandshakeHandler(new DefaultHandshakeHandler())
                //WebSocket握手请求的拦截器. 检查握手请求和响应, 对WebSocketHandler传递属性
                .addInterceptors(handshakeInterceptor())
                .setAllowedOrigins("*")
                //指定使用SockJS协议
                .withSockJS();
    }

    // 配置发送与接收的消息参数，可以指定消息字节大小，缓存大小，发送超时时间
    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration.addDecoratorFactory(webSocketHandlerDecoratorFactory());
    }

    // 配置消息代理，哪种路径的消息会进行代理处理
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        //服务器广播消息,客户端订阅信息的路径前缀
        registry.enableSimpleBroker("/topic", "/user");
        //服务端接收消息,客户端发送消息的路径前缀
        //发送用http触发，不用ws
        registry.setApplicationDestinationPrefixes("/topic");
        //用户级通信，点对点，默认是/user/
        registry.setUserDestinationPrefix("/user");
    }

    // 设置输入消息通道的线程数，默认线程为1，可以自己自定义线程数，最大线程数，线程存活时间
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.taskExecutor()
                .corePoolSize(10)
                .maxPoolSize(20)
                .keepAliveSeconds(20);
    }

    // 设置输出消息通道的线程数，默认线程为1，可以自己自定义线程数，最大线程数，线程存活时间
    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        registration.taskExecutor()
                .corePoolSize(20)
                .maxPoolSize(30)
                .keepAliveSeconds(20);
    }

    private HandshakeInterceptor handshakeInterceptor(){
        return new HandshakeInterceptor() {
            // 握手之前执行, 继续握手返回true, 中断握手返回false.
            // 通过attributes参数设置WebSocketSession的属性
            @Override
            public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
                log.info("beforeHandshake");
                if (request instanceof ServletServerHttpRequest) {
                    attributes.put("userId", "123456");
                    attributes.put("userName", "假用户");
                    return true;
                } else {
                    return false;
                }
            }

            //握手之后执行
            @Override
            public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
                log.info("afterHandshake");
            }
        };
    }

    private WebSocketHandlerDecoratorFactory webSocketHandlerDecoratorFactory(){
        return new WebSocketHandlerDecoratorFactory() {
            @Override
            public WebSocketHandler decorate(WebSocketHandler handler) {
                return new WebSocketHandlerDecorator(handler) {
                    @Override
                    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                        log.info("Connection Established");
                        // 客户端与服务器端握手连接后，此处记录谁上线了
                        Map<String, Object> attributes = session.getAttributes();
                        attributes.forEach((key, val)-> {
                            log.info("websocket online: " + val + " session " + session.getId());
                        });
                        super.afterConnectionEstablished(session);
                    }

                    @Override
                    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
                        log.info("Connection Closed");
                        // 客户端与服务器端断开连接后，此处记录谁下线了
                        super.afterConnectionEstablished(session);
                    }
                };
            }
        };
    }

}
