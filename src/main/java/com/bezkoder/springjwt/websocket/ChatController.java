package com.bezkoder.springjwt.websocket;

import com.bezkoder.springjwt.websocket.ChatMessage;
import com.sun.security.auth.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class ChatController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;


    @Autowired
    private UserSessionRegistry userSessionRegistry;

    @MessageMapping("/chat.sendMessage")
    public void sendMessageToAll(@Payload ChatMessage chatMessage) {
        messagingTemplate.convertAndSend("/topic/public", chatMessage);
    }

    @MessageMapping("/chat.sendPrivateMessage")
    public void sendSpecific(@Payload ChatMessage msg, Principal user, @Header("simpSessionId") String sessionId) throws Exception {
        String session = this.userSessionRegistry.getUserSessions().get(msg.getRecipient());

        if (session !=null) {
            messagingTemplate.convertAndSend("/queue/specific-user-" + session, msg);
        }
    }

    @MessageMapping("/chat.addUser")
    public void addUser(@Payload String username, Principal user, @Header("simpSessionId") String sessionId) {
        ChatMessage msg = new ChatMessage();
        msg.setContent("Update");
        msg.setSender("System");

        this.userSessionRegistry.getUserSessions().put(username, sessionId);
        messagingTemplate.convertAndSend("/queue/specific-user-" + sessionId, msg);

    }
}