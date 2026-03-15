/*
 * Copyright © 2026 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.dtos.nativequerydto;

import java.math.BigDecimal;

public interface TopUserDTO {

    String getUserName();

    Long getOrderCount();

    BigDecimal getTotalRevenue();
}
