package com.tdbang.crm.enums;

import lombok.Getter;

@Getter
public enum NotificationType {
    CONTACT_ASSIGNED("Contact Assigned"),
    CONTACT_UPDATED("Contact Updated"),
    SALES_ORDER_ASSIGNED("Sales Order Assigned"),
    SALES_ORDER_UPDATED("Sales Order Updated");

    private final String name;

    NotificationType(String name) {
        this.name = name;
    }
}
