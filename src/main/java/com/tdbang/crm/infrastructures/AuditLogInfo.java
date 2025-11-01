/*
 * Copyright © 2025 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.infrastructures;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuditLogInfo implements Serializable {
    private String username;
    private String requestUrl;
    private String action;
    private String details;
    private String description;
}
