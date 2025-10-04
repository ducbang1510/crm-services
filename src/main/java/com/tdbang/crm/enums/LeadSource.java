package com.tdbang.crm.enums;

import lombok.Getter;

@Getter
public enum LeadSource {
    EXISTING_CUSTOMER("Existing Customer"),
    PARTNER("Partner"),
    CONFERENCE("Conference"),
    WEBSITE("Website"),
    WORD_OF_MOUTH("Word of mouth"),
    OTHER("Other");

    private final String name;

    LeadSource(String name) {
        this.name = name;
    }

    public static LeadSource fromName(String name) {
        for (LeadSource leadSource : values()) {
            if (leadSource.getName().equalsIgnoreCase(name)) {
                return leadSource;
            }
        }
        throw new IllegalArgumentException("No enum constant with code: " + name);
    }

}
