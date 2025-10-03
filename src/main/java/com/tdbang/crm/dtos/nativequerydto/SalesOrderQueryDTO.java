package com.tdbang.crm.dtos.nativequerydto;

import java.math.BigDecimal;
import java.util.Date;

/**
 * SalesOrderQueryDTO for Native Query.
 */
public interface SalesOrderQueryDTO {
    Long getPk();

    String getSubject();

    String getContactName();

    Integer getStatus();

    BigDecimal getTotal();

    String getNameUserAssignedTo();

    Long getUserFkAssignedTo();

    String getCreatorName();

    Long getCreatorFk();

    String getDescription();

    Date getCreatedOn();

    Date getUpdatedOn();
}
