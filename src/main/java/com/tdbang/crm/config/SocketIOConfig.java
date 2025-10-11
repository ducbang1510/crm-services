package com.tdbang.crm.config;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.Transport;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.tdbang.crm.authentication.JwtTokenService;

@Log4j2
@Configuration
@RequiredArgsConstructor
public class SocketIOConfig {
    @Value("${socketio.ui.host}")
    private String uiHost;

    @Value("${socketio.port}")
    private Integer port;

    @Value("${socketio.ping.interval:60000}")
    private Integer pingInterval;

    @Value("${socketio.ping.timeout:120000}")
    private Integer pingTimeout;

    private final JwtTokenService jwtTokenService;

    @Bean
    public SocketIOServer socketIOServer() {
        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        config.setTransports(Transport.WEBSOCKET);
        config.setPort(port);
        config.setOrigin(uiHost);
        config.setAuthorizationListener(handshakeData -> {
            log.info("Start to verify token for socket io");
            String tokenString = handshakeData.getSingleUrlParam("token");
            if (tokenString != null && !tokenString.isEmpty()) {
                try {
                    return jwtTokenService.verifyTokenAndGetUserPk(tokenString) != null;
                } catch (Exception e) {
                    return false;
                }
            }
            return false;
        });
        config.setPingInterval(pingInterval);
        config.setPingTimeout(pingTimeout);

        return new SocketIOServer(config);
    }
}