package com.sam.demo.webSocket;

import com.google.common.util.concurrent.SettableFuture;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import java.lang.reflect.Type;
import java.util.concurrent.*;

@Slf4j
public class SimpleStompSessionHandler extends StompSessionHandlerAdapter {

    @Getter
    private BlockingQueue<Object> blockingQueue = new SynchronousQueue<>();
    private SettableFuture settableFuture = SettableFuture.create();

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return String.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, @Nullable Object payload) {
        log.info("{}", payload);
        blockingQueue.add(payload);
    }

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        log.info("Connected");
    }

    @Override
    public void handleException(StompSession session, @Nullable StompCommand command,
                                StompHeaders headers, byte[] payload, Throwable exception) {
        log.error("Exception", exception);
        blockingQueue.add(exception);
    }

    @Override
    public void handleTransportError(StompSession session, Throwable exception) {
        log.error("Error", exception);
        blockingQueue.add(exception);
    }

}
