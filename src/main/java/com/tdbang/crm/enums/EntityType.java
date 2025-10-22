/*
 * Copyright © 2025 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.enums;

import lombok.Getter;

/**
 * Enum of Entities have file storage
 */
@Getter
public enum EntityType {
    CONTACT("Contact"),
    SALES_ORDER("Sales_order");

    private final String name;

    EntityType(String name) {
        this.name = name;
    }

    public static EntityType fromName(String name) {
        for (EntityType entityType : values()) {
            if (entityType.getName().equalsIgnoreCase(name)) {
                return entityType;
            }
        }
        throw new IllegalArgumentException("No enum constant with code: " + name);
    }
}
