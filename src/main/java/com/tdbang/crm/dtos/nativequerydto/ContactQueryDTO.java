package com.tdbang.crm.dtos.nativequerydto;

import java.util.Date;

/**
 * ContactQueryDTO for Native Query.
 */
public interface ContactQueryDTO {
    Long getPk();

    String getContactName();

    Integer getSalutation();

    String getMobilePhone();

    String getEmail();

    String getOrganization();

    Integer getLeadSrc();

    String getNameUserAssignedTo();

    Long getUserFkAssignedTo();

    String getCreatorName();

    Long getCreatorFk();

    String getAddress();

    String getDescription();

    Date getCreatedOn();

    Date getUpdatedOn();
}
