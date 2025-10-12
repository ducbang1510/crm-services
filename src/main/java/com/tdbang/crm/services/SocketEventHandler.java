package com.tdbang.crm.services;

import com.corundumstudio.socketio.SocketIOServer;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import com.tdbang.crm.authentication.JwtTokenService;
import com.tdbang.crm.repositories.UserRepository;

@Log4j2
@Component
@RequiredArgsConstructor
public class SocketEventHandler {
    private final UserRepository userRepository;

    private final SocketEventService socketEventService;

    private final JwtTokenService jwtTokenService;

    @PostConstruct
    public void setupSocketListeners() {
        SocketIOServer socketIOServer = socketEventService.getSocketIOServer();

        socketIOServer.addConnectListener(client -> {
            String token = client.getHandshakeData().getSingleUrlParam("token");
            Long userFk = getUserFkFromToken(token);
            client.joinRoom("user_" + userFk);
            log.info("Client connected: {}", userFk);
        });

        socketIOServer.addDisconnectListener(client -> {
            String userFk = client.getHandshakeData().getSingleUrlParam("user");
            log.info("Client disconnected: {}", userFk);
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

    private Long getUserFkFromToken(String token) {
        return jwtTokenService.verifyTokenAndGetUserPk(token);
    }
}