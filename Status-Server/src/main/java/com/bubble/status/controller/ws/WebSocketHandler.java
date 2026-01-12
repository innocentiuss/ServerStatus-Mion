package com.bubble.status.controller.ws;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
@Component
@ServerEndpoint(value = "/connect")
public class WebSocketHandler {

    // 使用线程安全的 Set 存储所有连接的会话
    private static final CopyOnWriteArraySet<Session> sessions = new CopyOnWriteArraySet<>();

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
        log.info("New connection: {}", session.getId());
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
        log.info("Connection closed: {}", session.getId());
    }

    @OnError
    public void onError(Session session, Throwable error) {
        sessions.remove(session);
        log.error("WebSocket error", error);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        log.debug("Received message from client: {}", message);
    }

    public static void sendToAllClients(String message) {
        for (Session session : sessions) {
            if (session.isOpen()) {
                // 使用 getAsyncRemote() 非阻塞发送
                session.getAsyncRemote().sendText(message);
            }
        }
    }

    public static boolean noActiveSession() {
        return sessions.isEmpty();
    }
}
