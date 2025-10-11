package com.tdbang.crm.entities;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.tdbang.crm.enums.NotificationType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notification_message")
public class NotificationMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pk;

    @Column(name = "sender_user_fk", nullable = false)
    private Long senderUserFk;

    @Column(name = "recipient_user_fk")
    private Long recipientUserFk;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "type", nullable = false)
    private NotificationType type;

    @Column(name = "message")
    private String message;

    @Column(name = "notification_object_fk")
    private Long notificationObjectFk;

    @Column(name = "unread", nullable = false)
    private Boolean unread = true;

    @Column(name = "created_on")
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date createdOn;

    @Column(name = "updated_on")
    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private Date updatedOn;

    public NotificationMessage(Long senderUserFk, Long recipientUserFk, String message) {
        this.senderUserFk = senderUserFk;
        this.recipientUserFk = recipientUserFk;
        this.message = message;
    }
}
