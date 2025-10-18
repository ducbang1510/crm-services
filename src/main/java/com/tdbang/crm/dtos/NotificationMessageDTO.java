/*
 * Copyright © 2025 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.dtos;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.tdbang.crm.enums.NotificationType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessageDTO {
    private Long pk;
    private Long senderUserFk;
    private String senderName;
    private Long recipientUserFk;
    private String recipientName;
    private NotificationType type;
    private String message;
    private Long notificationObjectFk;
    private Boolean unread;
    private Date createdOn;
}
