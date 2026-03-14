/*
 * Copyright © 2026 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.enums;

import lombok.Getter;

@Getter
public enum NoteType {
    CALL("Call"),
    MEETING("Meeting"),
    EMAIL("Email"),
    NOTE("Note");

    private final String name;

    NoteType(String name) {
        this.name = name;
    }

    public static NoteType fromName(String name) {
        for (NoteType noteType : values()) {
            if (noteType.getName().equalsIgnoreCase(name)) {
                return noteType;
            }
        }
        throw new IllegalArgumentException("No enum constant with code: " + name);
    }

}
