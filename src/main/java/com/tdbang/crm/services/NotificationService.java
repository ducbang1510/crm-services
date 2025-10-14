package com.tdbang.crm.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.tdbang.crm.dtos.NotificationMessageDTO;
import com.tdbang.crm.entities.NotificationMessage;
import com.tdbang.crm.enums.NotificationType;
import com.tdbang.crm.repositories.NotificationMessageRepository;
import com.tdbang.crm.utils.AppConstants;

@Log4j2
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationMessageRepository notificationMessageRepository;
    private final SocketEventService socketEventService;

    public Map<String, Object> retrieveNotifications(Long userPk, int pageNumber, int pageSize) {
        Map<String, Object> resultMap = new HashMap<>();
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<NotificationMessageDTO> resultPage = notificationMessageRepository.retrieveNotificationMessagesByUserFk(userPk, pageable);
        List<NotificationMessageDTO> result = resultPage.getContent();

        List<Long> notificationPks = result.stream().map(NotificationMessageDTO::getPk).toList();
        List<NotificationMessage> notificationMessages = notificationMessageRepository.findAllById(notificationPks);
        notificationMessages.forEach(r -> r.setUnread(false));
        notificationMessageRepository.saveAll(notificationMessages);

        resultMap.put(AppConstants.RECORD_LIST_KEY, result);
        resultMap.put(AppConstants.TOTAL_RECORD_KEY, resultPage.getTotalElements());
        return resultMap;
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
                notificationMessageRepository.saveAll(notificationMessages);
            } catch (Exception e) {
                log.error("Error when create notifications: {}", e.getMessage());
            }
        }
    }
}
