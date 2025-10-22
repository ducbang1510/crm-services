/*
 * Copyright © 2025 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.enums;

import lombok.Getter;

/**
 * Enum of Collections for file storage
 */
@Getter
public enum CollectionType {
    CONTRACT("Contract"),
    INVOICE("Invoice");

    private final String name;

    CollectionType(String name) {
        this.name = name;
    }

    public static CollectionType fromName(String name) {
        for (CollectionType collectionType : values()) {
            if (collectionType.getName().equalsIgnoreCase(name)) {
                return collectionType;
            }
        }
        throw new IllegalArgumentException("No enum constant with code: " + name);
    }
}
