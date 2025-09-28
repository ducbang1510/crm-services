package com.tdbang.crm.enums;

public enum LeadSource {
    EXISTING_CUSTOMER("Existing Customer"),
    PARTNER("Partner"),
    CONFERENCE("Conference"),
    WEBSITE("Website"),
    WORD_OF_MOUTH("Word of mouth"),
    OTHER("Other");

    LeadSource(String name) {
        this.name = name;
    }

    private String name;

    public String getName() {
        return name;
    }
}
