package com.sam.demo.webSocket;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "WsController")
@Slf4j
@RestController("/app")
public class WsController {

    @Autowired
    private SimpMessagingTemplate template;

    // 模拟后端主动推送，所有用户
    @Scheduled(fixedRate = 1000)
    public void sendTopicMessage() {
        Message message = new Message();
        message.setMsg("all");
        this.template.convertAndSend("/topic/toAny", message);
    }

    // 模拟后端主动推送，部分用户
    @Scheduled(fixedRate = 1000)
    public void sendQueueMessage() {
        Message message = new Message();
        message.setMsg("user");
        this.template.convertAndSendToUser("sam", "/topic/toUser", message);
    }

    @MessageMapping("/welcome1")
    public void say(Message message) {
        log.info("{}", message);
        template.convertAndSend("/topic/toAny", message);
    }

    @MessageMapping("/welcome2")
    @SendTo("/topic/toAny")
    public Message greeting(Message message) {
        return message;
    }

}
