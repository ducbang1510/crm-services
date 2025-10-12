package com.tdbang.crm.services;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.tdbang.crm.repositories.NotificationMessageRepository;

@Log4j2
@Getter
@Service
@RequiredArgsConstructor
public class SocketEventService {

    private final SocketIOServer socketIOServer;
    private final NotificationMessageRepository notificationMessageRepository;

    private static final String UNREAD_COUNT_EVENT = "UNREAD_COUNT";

    public void joinRoom(SocketIOClient client, String room) {
        client.joinRoom(room);
        log.info("Client {} joined room: {}", client.getSessionId(), room);
    }

    public void leaveRoom(SocketIOClient client, String room) {
        client.leaveRoom(room);
        log.info("Client {} leaved room: {}", client.getSessionId(), room);
    }

    public void sendToRoom(String room, String eventName, Object data) {
        socketIOServer.getRoomOperations(room).sendEvent(eventName, data);
        log.info("Sent {} to room: {}", eventName, room);
    }

    public void broadcastExcept(String eventName, Object data, UUID senderId) {
        socketIOServer.getBroadcastOperations().getClients().stream()
                .filter(client -> !client.getSessionId().equals(senderId))
                .forEach(client -> client.sendEvent(eventName, data));
    }

    public Set<String> getAllRooms() {
        Set<String> allRooms = new HashSet<>();
        for (SocketIOClient i : socketIOServer.getAllClients()) {
            allRooms.addAll(i.getAllRooms());
        }
        return allRooms;
    }

    public Collection<SocketIOClient> getClientInRooms(String room) {
        return socketIOServer.getRoomOperations(room).getClients();
    }

    public void sendNotifications(List<Long> receivers) {
        try {
            if (!CollectionUtils.isEmpty(receivers)) {
                for (Long receiver : receivers) {
                    String room = "user_" + receiver;
                    Long unreadCount = notificationMessageRepository.countUnreadNotifications(receiver);
                    sendToRoom(room, UNREAD_COUNT_EVENT, unreadCount);
                }
            }
        } catch (Exception e) {
            log.error("Error when send notification: {}", e.getMessage());
        }
    }
}
