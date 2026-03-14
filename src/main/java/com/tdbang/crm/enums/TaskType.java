/*
 * Copyright © 2026 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.enums;

import lombok.Getter;

@Getter
public enum TaskType {
    TODO("Todo"),
    CALL("Call"),
    MEETING("Meeting");

    private final String name;

    TaskType(String name) {
        this.name = name;
    }

    public static TaskType fromName(String name) {
        for (TaskType taskType : values()) {
            if (taskType.getName().equalsIgnoreCase(name)) {
                return taskType;
            }
        }
        throw new IllegalArgumentException("No enum constant with code: " + name);
    }
}
