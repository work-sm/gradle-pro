package com.sam.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

@Configuration
@EnableScheduling
//开启stomp协议来传输基于代理message broker的消息
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    // 添加这个Endpoint，这样在网页中就可以通过websocket连接上服务,也就是我们配置websocket的服务地址,并且可以指定是否使用socketjs
    @Override
    public void registerStompEndpoints(StompEndpointRegistry stompEndpointRegistry) {
        //注册一个Stomp的节点，客户端就可以通过这个端点来进行连接
        stompEndpointRegistry.addEndpoint("/socket")
                //拦截器（握手）
                .setHandshakeHandler(new DefaultHandshakeHandler())
                //WebSocket握手请求的拦截器. 检查握手请求和响应, 对WebSocketHandler传递属性
                .addInterceptors(new DefaultHandshakeInterceptor())
                .setAllowedOrigins("*")
                //指定使用SockJS协议
                .withSockJS();
    }

    // 配置发送与接收的消息参数，可以指定消息字节大小，缓存大小，发送超时时间
    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration.addDecoratorFactory(new AuthWebSocketHandlerDecoratorFactory());
    }

    // 配置消息代理，哪种路径的消息会进行代理处理
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        //服务器广播消息,客户端订阅信息的路径前缀
        registry.enableSimpleBroker("/topic", "/user");
        //服务端接收消息,客户端发送消息的路径前缀
        registry.setApplicationDestinationPrefixes("/app");
        //用户级通信，点对点，默认是/user/
        registry.setUserDestinationPrefix("/user");
    }

    // 设置输入消息通道的线程数，默认线程为1，可以自己自定义线程数，最大线程数，线程存活时间
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.taskExecutor().corePoolSize(10)
                .maxPoolSize(20)
                .keepAliveSeconds(20);
    }

    // 设置输出消息通道的线程数，默认线程为1，可以自己自定义线程数，最大线程数，线程存活时间
    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        registration.taskExecutor().corePoolSize(20)
                .maxPoolSize(30)
                .keepAliveSeconds(20);
    }

}
