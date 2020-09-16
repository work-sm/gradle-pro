package com.sam.demo.webSocket;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@Api(value = "WsController")
@Slf4j
//@EnableScheduling
@RestController
public class WsController {

    @Autowired
    private SimpMessagingTemplate template;

    @PostMapping("/msg")
    public String msg(@RequestBody Message message) {
        String destination = "/topic";
        Integer type = message.getType();
        String msg = message.getMsg();
        Integer loop = message.getLoop();

        if (Objects.nonNull(type) && type == 1) {
            String target = message.getTarget();
            if (StringUtils.isNotEmpty(target)) {
                if (!target.startsWith("/") && !target.startsWith("\\")) {
                    target = "/" + target;
                }
            } else {
                target = "/def";
            }
            destination += target;
            log.info("{} {}", destination, msg);
            template.convertAndSend(destination, msg);
            return destination;
        } else if (Objects.nonNull(type) && type == 2) {
            String target = message.getTarget();
            String name = message.getName();
            if (StringUtils.isEmpty(name)) {
                name = "def";
            }
            if (StringUtils.isNotEmpty(target)) {
                if (!target.startsWith("/") && !target.startsWith("\\")) {
                    target = "/" + target;
                }
            }else{
                target = "/def";
            }
            log.info("{} {} {}", name, target, msg);
            template.convertAndSendToUser(name, target, msg);
            return "/user/"+name+target;
        } else {
            destination += "/all";
            log.info("{} {}", destination, msg);
            template.convertAndSend(destination, msg);
            return destination;
        }
    }

    // 模拟广播接口
    // @Scheduled(fixedRate = 1000)
    public void sendAllTopicMessage() {
        Message message = new Message();
        message.setMsg("all");
        this.template.convertAndSend("/topic/toAny", message);
    }

    // 模拟后端主动推送，部分用户
    // @Scheduled(fixedRate = 1000)
    public void sendQueueMessage() {
        Message message = new Message();
        message.setMsg("user");
        this.template.convertAndSendToUser("sam", "/topic/toUser", message);
    }

    //@MessageMapping("/welcome1")
    public void say(Message message) {
        log.info("{}", message);
        template.convertAndSend("/topic/toAny", message);
    }

    //@MessageMapping("/welcome2")
    //@SendTo("/topic/toAny")
    public Message greeting(Message message) {
        return message;
    }

}
