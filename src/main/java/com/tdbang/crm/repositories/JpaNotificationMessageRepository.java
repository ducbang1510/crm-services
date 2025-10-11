package com.tdbang.crm.repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tdbang.crm.dtos.NotificationMessageDTO;
import com.tdbang.crm.entities.NotificationMessage;

@Repository
public interface JpaNotificationMessageRepository extends JpaRepository<NotificationMessage, Long> {
    @Query("SELECT nm FROM NotificationMessage nm WHERE nm.pk = :pk")
    NotificationMessage findByPk(Long pk);

    @Query("SELECT new com.tdbang.crm.dtos.NotificationMessageDTO(nm.pk, nm.senderUserFk, sender.name,"
            + " nm.recipientUserFk, recipient.name,"
            + " nm.type, nm.message, nm.notificationObjectFk, nm.unread, nm.createdOn)"
            + " FROM NotificationMessage nm"
            + " JOIN User sender ON nm.senderUserFk = sender.pk"
            + " JOIN User recipient ON nm.recipientUserFk = recipient.pk"
            + " WHERE nm.recipientUserFk = :userFk"
            + " ORDER BY nm.createdOn DESC")
    List<NotificationMessageDTO> retrieveNotificationMessagesByUserFk(Long userFk, Pageable pageable);

    @Query("SELECT COUNT(nm.pk) FROM NotificationMessage nm WHERE nm.recipientUserFk = :userFk AND nm.unread = TRUE")
    Long countUnreadNotifications(Long userFk);
}
