package com.sam.demo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory;

import java.util.Map;

@Slf4j
public class AuthWebSocketHandlerDecoratorFactory implements WebSocketHandlerDecoratorFactory {

    @Override
    public WebSocketHandler decorate(WebSocketHandler handler) {
        return new WebSocketHandlerDecorator(handler) {
            @Override
            public void afterConnectionEstablished(final WebSocketSession session) throws Exception {
                log.info("Connection Established");
                // 客户端与服务器端建立连接后，此处记录谁上线了
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
                Map<String, Object> attributes = session.getAttributes();
                attributes.forEach((key, val)-> {
                    log.info("websocket online: " + val + " session " + session.getId());
                });
                super.afterConnectionEstablished(session);
            }
        };
    }
}
