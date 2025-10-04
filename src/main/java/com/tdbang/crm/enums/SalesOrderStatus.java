package com.tdbang.crm.enums;

import lombok.Getter;

@Getter
public enum SalesOrderStatus {
    NONE("Created"),
    MR("Approved"),
    MRS("Delivered"),
    MS("Canceled");

    private final String name;

    SalesOrderStatus(String name) {
        this.name = name;
    }

    public static SalesOrderStatus fromName(String name) {
        for (SalesOrderStatus salesOrderStatus : values()) {
            if (salesOrderStatus.getName().equalsIgnoreCase(name)) {
                return salesOrderStatus;
            }
        }
        throw new IllegalArgumentException("No enum constant with code: " + name);
    }

}
