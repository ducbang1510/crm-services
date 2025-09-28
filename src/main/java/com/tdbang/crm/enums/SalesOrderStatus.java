package com.tdbang.crm.enums;

public enum SalesOrderStatus {
    NONE("Created"),
    MR("Approved"),
    MRS("Delivered"),
    MS("Canceled");

    SalesOrderStatus(String name) {
        this.name = name;
    }

    private String name;

    public String getName() {
        return name;
    }
}
