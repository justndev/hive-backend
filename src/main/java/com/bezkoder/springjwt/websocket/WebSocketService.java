package com.bezkoder.springjwt.websocket;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
@Service
public class WebSocketService {


    @Autowired
    private UserSessionRegistry userSessionRegistry;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;



    public <T> void sendSpecific(String userId, String update) throws Exception {
        String sessionId = this.userSessionRegistry.getUserSessions().get(userId);
        SocketResponse<T> response = new SocketResponse<>();
        response.setUpdate(update);

        if (sessionId != null) {
            messagingTemplate.convertAndSend("/queue/specific-user-" + sessionId, response);
        }
    }
}
