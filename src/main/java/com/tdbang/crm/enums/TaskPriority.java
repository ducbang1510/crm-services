/*
 * Copyright © 2026 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.enums;

import lombok.Getter;

@Getter
public enum TaskPriority {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High");

    private final String name;

    TaskPriority(String name) {
        this.name = name;
    }

    public static TaskPriority fromName(String name) {
        for (TaskPriority taskPriority : values()) {
            if (taskPriority.getName().equalsIgnoreCase(name)) {
                return taskPriority;
            }
        }
        throw new IllegalArgumentException("No enum constant with code: " + name);
    }
}
