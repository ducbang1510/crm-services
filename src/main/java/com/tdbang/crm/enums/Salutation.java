package com.tdbang.crm.enums;

public enum Salutation {
    NONE("None"),
    MR("Mr."),
    MRS("Mrs."),
    MS("Ms."),
    DR("Dr."),
    PROF("Prof.");

    Salutation(String name) {
        this.name = name;
    }

    private String name;

    public String getName() {
        return name;
    }
}
