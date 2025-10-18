/*
 * Copyright © 2025 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.enums;

import lombok.Getter;

@Getter
public enum Salutation {
    NONE("None"),
    MR("Mr."),
    MRS("Mrs."),
    MS("Ms."),
    DR("Dr."),
    PROF("Prof.");

    private final String name;

    Salutation(String name) {
        this.name = name;
    }

    public static Salutation fromName(String name) {
        for (Salutation salutation : values()) {
            if (salutation.getName().equalsIgnoreCase(name)) {
                return salutation;
            }
        }
        throw new IllegalArgumentException("No enum constant with code: " + name);
    }

}
