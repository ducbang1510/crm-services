package com.tdbang.crm.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.tdbang.crm.dtos.NotificationMessageDTO;
import com.tdbang.crm.entities.NotificationMessage;
import com.tdbang.crm.enums.NotificationType;
import com.tdbang.crm.repositories.JpaNotificationMessageRepository;

@Log4j2
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final JpaNotificationMessageRepository jpaNotificationMessageRepository;
    private final SocketEventService socketEventService;

    public List<NotificationMessageDTO> retrieveNotifications(Long userPk, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        List<NotificationMessageDTO> result = jpaNotificationMessageRepository.retrieveNotificationMessagesByUserFk(userPk, pageable);

        List<Long> notificationPks = result.stream().map(NotificationMessageDTO::getPk).toList();
        List<NotificationMessage> notificationMessages = jpaNotificationMessageRepository.findAllById(notificationPks);
        notificationMessages.forEach(r -> r.setUnread(false));
        jpaNotificationMessageRepository.saveAll(notificationMessages);

        return result;
    }

    public void createNotifications(Long sender, List<Long> recipients, NotificationType type, Long notificationObjectFk) {
        if (!recipients.isEmpty()) {
            try {
                List<NotificationMessage> notificationMessages = new ArrayList<>();
                for (Long recipient : recipients) {
                    NotificationMessage notificationMessage = new NotificationMessage();
                    notificationMessage.setSenderUserFk(sender);
                    notificationMessage.setRecipientUserFk(recipient);
                    notificationMessage.setType(type);
                    notificationMessage.setNotificationObjectFk(notificationObjectFk);
                    notificationMessage.setUnread(true);
                    notificationMessage.setCreatedOn(new Date());
                    notificationMessages.add(notificationMessage);
                }
                jpaNotificationMessageRepository.saveAll(notificationMessages);
            } catch (Exception e) {
                log.error("Error when create notifications: {}", e.getMessage());
            }
        }
    }
}
