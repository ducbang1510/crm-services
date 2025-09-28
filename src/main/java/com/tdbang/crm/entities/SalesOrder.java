package com.tdbang.crm.entities;

import java.math.BigDecimal;
import java.util.Date;

import com.tdbang.crm.enums.SalesOrderStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Getter
@Setter
@Entity
@Table(name = "sales_order")
public class SalesOrder {
    @Id
    @Column(name = "pk")
    private Long pk;

    @Column(name = "subject", nullable = false)
    private String subject;

    @ManyToOne
    @JoinColumn(name = "contact_fk")
    private Contact contact;

    @Column(name = "status", nullable = false)
    @Enumerated
    private SalesOrderStatus status;

    @Column(name = "total", nullable = false)
    private BigDecimal total;

    @ManyToOne
    @JoinColumn(name = "assigned_to")
    private User assignedTo;

    @ManyToOne
    @JoinColumn(name = "creator")
    private User creator;

    @Column(name = "description")
    private String description;

    @Column(name = "created_on")
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date createdOn;

    @Column(name = "updated_on")
    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private Date updatedOn;
}
