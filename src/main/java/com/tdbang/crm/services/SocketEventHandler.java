package com.tdbang.crm.services;

import com.corundumstudio.socketio.SocketIOServer;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SocketEventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(SocketEventHandler.class);

    private final SocketEventService socketEventService;

    @PostConstruct
    public void setupSocketListeners() {
        SocketIOServer socketIOServer = socketEventService.getSocketIOServer();

        socketIOServer.addConnectListener(client -> {
            String token = client.getHandshakeData().getSingleUrlParam("token");
            Long empFk = getEmpFkFromToken(token);
            client.joinRoom("user_" + empFk);
            LOGGER.info("Client connected: {}", empFk);
        });

        socketIOServer.addDisconnectListener(client -> {
            String empFk = client.getHandshakeData().getSingleUrlParam("emp");
            LOGGER.info("Client disconnected: {}", empFk);
        });

        socketIOServer.addEventListener("join_room", String.class, (client, room, ackSender) -> {
            socketEventService.joinRoom(client, room);
            ackSender.sendAckData("Joined room: {}", room);
        });

        socketIOServer.addEventListener("leave_room", String.class, (client, room, ackSender) -> {
            socketEventService.leaveRoom(client, room);
            ackSender.sendAckData("Left room: {}", room);
        });
    }

    private Long getEmpFkFromToken(String token) {
        // TODO: Verify token -> get username -> Get user pk
        return 0L;
    }
}