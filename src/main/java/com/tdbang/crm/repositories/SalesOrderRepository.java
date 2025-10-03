package com.tdbang.crm.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tdbang.crm.dtos.nativequerydto.SalesOrderQueryDTO;
import com.tdbang.crm.entities.SalesOrder;

@Repository
public interface SalesOrderRepository extends JpaRepository<SalesOrder, Long> {
    public static final String SELECT_LIST_SALES_ORDER = "SELECT sc.pk AS pk, sc.subject AS subject, c.contact_name AS contactName,"
            + " sc.status AS status,sc.total AS total,"
            + " assignTo.name AS nameUserAssignedTo, assignTo.pk AS userFkAssignedTo,"
            + " creator.name AS creatorName, creator.pk AS creatorFk,"
            + " sc.description AS description, sc.created_on AS createdOn, sc.updated_on AS updatedOn"
            + " FROM sales_order sc"
            + " JOIN contact c ON sc.contact_fk = c.pk"
            + " LEFT JOIN user assignTo ON sc.assigned_to = assignTo.pk"
            + " LEFT JOIN user creator ON sc.creator = creator.pk";

    @Query(value = SELECT_LIST_SALES_ORDER
            , countQuery = "SELECT COUNT(sc.pk) FROM sales_order sc JOIN contact c ON sc.contact_fk = c.pk", nativeQuery = true)
    Page<SalesOrderQueryDTO> getSalesOrderPageable(Pageable pageable);

    @Query(value = SELECT_LIST_SALES_ORDER, nativeQuery = true)
    List<SalesOrderQueryDTO> getAllSalesOrder();
}
