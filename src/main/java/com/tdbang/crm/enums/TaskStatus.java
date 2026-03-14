/*
 * Copyright © 2026 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.enums;

import lombok.Getter;

@Getter
public enum TaskStatus {
    OPEN("Open"),
    IN_PROGRESS("In Progress"),
    DONE("Done");

    private final String name;

    TaskStatus(String name) {
        this.name = name;
    }

    public static TaskStatus fromName(String name) {
        for (TaskStatus taskStatus : values()) {
            if (taskStatus.getName().equalsIgnoreCase(name)) {
                return taskStatus;
            }
        }
        throw new IllegalArgumentException("No enum constant with code: " + name);
    }
}
