package com.tdbang.crm.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tdbang.crm.dtos.nativequerydto.DashboardQueryDTO;
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

    public static final String SUBJECT_FILTER = " WHERE :subject IS NULL OR sc.subject LIKE %:subject%";

    @Query(value = SELECT_LIST_SALES_ORDER + SUBJECT_FILTER
            , countQuery = "SELECT COUNT(sc.pk) FROM sales_order sc JOIN contact c ON sc.contact_fk = c.pk"
            + SUBJECT_FILTER, nativeQuery = true)
    Page<SalesOrderQueryDTO> getSalesOrderPageable(String subject, Pageable pageable);

    @Query(value = SELECT_LIST_SALES_ORDER + SUBJECT_FILTER, nativeQuery = true)
    List<SalesOrderQueryDTO> getAllSalesOrder(String subject);

    @Query(value = SELECT_LIST_SALES_ORDER + " WHERE sc.pk = :orderPk", nativeQuery = true)
    SalesOrderQueryDTO getSalesOrderDetailsByPk(Long orderPk);

    Optional<SalesOrder> findByPk(Long pk);

    @Query(value = "SELECT sc.status AS id, COUNT(c.pk) AS count"
            + " FROM sales_order sc JOIN contact c ON sc.contact_fk = c.pk GROUP BY sc.status", nativeQuery = true)
    List<DashboardQueryDTO> countOrderGroupByStatus();

    @Query(value = "SELECT sc FROM SalesOrder sc WHERE sc.pk IN (:pks)")
    List<SalesOrder> getSaleOrdersByOrderPks(List<Long> pks);
}
