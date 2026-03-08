/*
 * Copyright © 2025 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.repositories;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tdbang.crm.dtos.nativequerydto.DashboardQueryDTO;
import com.tdbang.crm.dtos.nativequerydto.SalesOrderDailyAggregationDTO;
import com.tdbang.crm.dtos.nativequerydto.SalesOrderQueryDTO;
import com.tdbang.crm.dtos.nativequerydto.SalesOrderUserSummaryDTO;
import com.tdbang.crm.entities.SalesOrder;
import com.tdbang.crm.enums.SalesOrderStatus;

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

    @Query(value = SELECT_LIST_SALES_ORDER + SUBJECT_FILTER,
        countQuery = "SELECT COUNT(sc.pk) FROM sales_order sc JOIN contact c ON sc.contact_fk = c.pk"
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

    /**
     * Aggregates all sales orders created within (startOfDay, endOfDay).
     * Status ordinals: 0=CREATED, 1=APPROVED, 2=DELIVERED, 3=CANCELED.
     */
    @Query(value = "SELECT COUNT(so.pk) AS totalOrders,"
        + " COALESCE(SUM(so.total), 0) AS totalRevenue,"
        + " SUM(CASE WHEN so.status = 0 THEN 1 ELSE 0 END) AS ordersCreated,"
        + " SUM(CASE WHEN so.status = 1 THEN 1 ELSE 0 END) AS ordersApproved,"
        + " SUM(CASE WHEN so.status = 2 THEN 1 ELSE 0 END) AS ordersDelivered,"
        + " SUM(CASE WHEN so.status = 3 THEN 1 ELSE 0 END) AS ordersCanceled"
        + " FROM sales_order so"
        + " WHERE so.created_on >= :startOfDay AND so.created_on < :endOfDay", nativeQuery = true)
    SalesOrderDailyAggregationDTO aggregateDailyOrders(Date startOfDay, Date endOfDay);

    /**
     * Returns per-user order count and total revenue for orders created within (startOfDay, endOfDay)
     */
    @Query(value = "SELECT u.name AS assignedToName, COUNT(so.pk) AS orderCount,"
        + " COALESCE(SUM(so.total), 0) AS totalRevenue"
        + " FROM sales_order so"
        + " LEFT JOIN user u ON so.assigned_to = u.pk"
        + " WHERE so.created_on >= :startOfDay AND so.created_on < :endOfDay"
        + " GROUP BY so.assigned_to, u.name", nativeQuery = true)
    List<SalesOrderUserSummaryDTO> aggregateDailyOrdersByUser(Date startOfDay, Date endOfDay);

    /**
     * Finds sales orders in actionable statuses that have not been updated since
     * {@code cutoffDate} and have an assigned user with a valid email.
     * Used as a repository-level alternative to the batch item reader query.
     */
    @Query("SELECT s FROM SalesOrder s"
        + " WHERE s.status IN (:statuses)"
        + " AND s.updatedOn < :cutoffDate"
        + " AND s.assignedTo IS NOT NULL"
        + " AND s.assignedTo.email IS NOT NULL"
        + " ORDER BY s.pk ASC")
    List<SalesOrder> findOrdersNeedingFollowUp(List<SalesOrderStatus> statuses, Date cutoffDate);
}
