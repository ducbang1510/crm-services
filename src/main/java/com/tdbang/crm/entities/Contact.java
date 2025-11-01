/*
 * Copyright © 2025 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.entities;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
import org.hibernate.envers.Audited;

import com.tdbang.crm.enums.LeadSource;
import com.tdbang.crm.enums.Salutation;

@Getter
@Setter
@Audited
@Entity
@Table(name = "contact")
public class Contact {
    @Id
    @Column(name = "pk")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pk;

    @Column(name = "contact_name", nullable = false)
    private String contactName;

    @Column(name = "salutation", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private Salutation salutation;

    @Column(name = "mobile_phone", nullable = false)
    private String mobilePhone;

    @Column(name = "email")
    private String email;

    @Column(name = "organization", nullable = false)
    private String organization;

    @Column(name = "dob")
    private Date dob;

    @Column(name = "lead_src", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private LeadSource leadSrc;

    @ManyToOne
    @JoinColumn(name = "assigned_to")
    private User assignedTo;

    @ManyToOne
    @JoinColumn(name = "creator")
    private User creator;

    @Column(name = "address")
    private String address;

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
