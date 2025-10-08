package com.tdbang.crm.services;

import com.corundumstudio.socketio.SocketIOServer;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class SocketEventHandler {

    private final SocketEventService socketEventService;

    @PostConstruct
    public void setupSocketListeners() {
        SocketIOServer socketIOServer = socketEventService.getSocketIOServer();

        socketIOServer.addConnectListener(client -> {
            String token = client.getHandshakeData().getSingleUrlParam("token");
            Long empFk = getEmpFkFromToken(token);
            client.joinRoom("user_" + empFk);
            log.info("Client connected: {}", empFk);
        });

        socketIOServer.addDisconnectListener(client -> {
            String empFk = client.getHandshakeData().getSingleUrlParam("emp");
            log.info("Client disconnected: {}", empFk);
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