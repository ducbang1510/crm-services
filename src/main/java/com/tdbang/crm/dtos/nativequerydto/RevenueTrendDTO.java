/*
 * Copyright © 2026 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.dtos.nativequerydto;

import java.math.BigDecimal;

public interface RevenueTrendDTO {

    String getPeriod();

    Long getOrderCount();

    BigDecimal getRevenue();
}
