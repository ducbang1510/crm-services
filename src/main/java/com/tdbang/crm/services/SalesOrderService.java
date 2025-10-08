package com.tdbang.crm.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.tdbang.crm.dtos.DashboardDTO;
import com.tdbang.crm.dtos.ResponseDTO;
import com.tdbang.crm.dtos.SalesOrderDTO;
import com.tdbang.crm.dtos.nativequerydto.DashboardQueryDTO;
import com.tdbang.crm.dtos.nativequerydto.SalesOrderQueryDTO;
import com.tdbang.crm.entities.Contact;
import com.tdbang.crm.entities.SalesOrder;
import com.tdbang.crm.entities.User;
import com.tdbang.crm.enums.SalesOrderStatus;
import com.tdbang.crm.exceptions.GenericException;
import com.tdbang.crm.mappers.SalesOrderMapper;
import com.tdbang.crm.repositories.JpaContactRepository;
import com.tdbang.crm.repositories.JpaSalesOrderRepository;
import com.tdbang.crm.repositories.JpaUserRepository;
import com.tdbang.crm.utils.AppConstants;
import com.tdbang.crm.utils.MessageConstants;

@Log4j2
@Service
public class SalesOrderService {

    @Autowired
    private JpaSalesOrderRepository jpaSalesOrderRepository;

    @Autowired
    private JpaUserRepository jpaUserRepository;

    @Autowired
    private JpaContactRepository jpaContactRepository;

    @Autowired
    private SalesOrderMapper salesOrderMapper;

    public ResponseDTO getListOfOrder(Integer pageNumber, Integer pageSize, String subjectFilter) {
        ResponseDTO result = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.FETCHING_LIST_OF_SALES_ORDER_SUCCESS);
        if (pageNumber != null && pageSize != null) {
            Map<String, Object> resultMap = new HashMap<>();
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<SalesOrderQueryDTO> salesOrderQueryDTOPagePage = jpaSalesOrderRepository.getSalesOrderPageable(subjectFilter, pageable);
            resultMap.put(AppConstants.RECORD_LIST_KEY, salesOrderMapper.mappingToListSalesOrderDTO(salesOrderQueryDTOPagePage.getContent()));
            resultMap.put(AppConstants.TOTAL_RECORD_KEY, salesOrderQueryDTOPagePage.getTotalElements());
            result.setData(resultMap);
        } else {
            List<SalesOrderQueryDTO> salesOrderQueryDTOs = jpaSalesOrderRepository.getAllSalesOrder(subjectFilter);
            result.setData(salesOrderMapper.mappingToListSalesOrderDTO(salesOrderQueryDTOs));
        }

        return result;
    }

    public ResponseDTO getSalesOrderDetails(Long orderPk) {
        ResponseDTO result = new ResponseDTO();
        if (orderPk != null) {
            SalesOrderQueryDTO salesOrderQueryDTO = jpaSalesOrderRepository.getSalesOrderDetailsByPk(orderPk);
            result = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.FETCHING_SALES_ORDER_SUCCESS,
                    salesOrderMapper.mappingSalesOrderQueryDTOToSalesOrderDTO(salesOrderQueryDTO));
        }

        return result;
    }

    public ResponseDTO updateSalesOrderDetails(Long orderPk, Long creatorFk, SalesOrderDTO salesOrderDTO) {
        ResponseDTO result;
        SalesOrder updatedOrder = jpaSalesOrderRepository.findByPk(orderPk)
                .orElseThrow(() -> new GenericException(HttpStatus.NOT_FOUND, "SALES_ORDER_NOT_FOUND", "Sales order not found"));
        if (updatedOrder.getCreator().getPk().equals(creatorFk)) {
            User userAssignedTo = jpaUserRepository.getUsersByNames(salesOrderDTO.getAssignedTo()).get(0);
            Contact contact = jpaContactRepository.getContactsByContactName(salesOrderDTO.getContactName()).get(0);
            updatedOrder = salesOrderMapper.mappingSalesOrderDTOToEntity(salesOrderDTO, null, userAssignedTo, contact, false);
            jpaSalesOrderRepository.save(updatedOrder);
            result = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.UPDATING_SALES_ORDER_SUCCESS);
        } else {
            throw new GenericException(HttpStatus.METHOD_NOT_ALLOWED, "USER_NOT_THE_CREATOR", "User is not the creator");
        }
        return result;
    }

    public ResponseDTO createNewSalesOrder(SalesOrderDTO salesOrderDTO, Long creatorFk) {
        ResponseDTO result;
        User creatorUser = jpaUserRepository.findUserByPk(creatorFk);
        try {
            User userAssignedTo = jpaUserRepository.getUsersByNames(salesOrderDTO.getAssignedTo()).get(0);
            Contact contact = jpaContactRepository.getContactsByContactName(salesOrderDTO.getContactName()).get(0);
            SalesOrder saveSalesOrder = salesOrderMapper.mappingSalesOrderDTOToEntity(salesOrderDTO, creatorUser, userAssignedTo, contact, true);
            jpaSalesOrderRepository.save(saveSalesOrder);
            result = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.CREATING_NEW_SALES_ORDER_SUCCESS);
        } catch (Exception e) {
            throw new GenericException(HttpStatus.BAD_REQUEST, "CREATING_NEW_SALES_ORDER_ERROR", MessageConstants.CREATING_NEW_SALES_ORDER_ERROR);
        }
        return result;
    }

    public ResponseDTO deleteSalesOrderDetails(Long orderPk, Long creatorFk) {
        ResponseDTO result;
        SalesOrder deletedOrder = jpaSalesOrderRepository.findByPk(orderPk)
                .orElseThrow(() -> new GenericException(HttpStatus.NOT_FOUND, "SALES_ORDER_NOT_FOUND", "Sales order not found"));
        if (deletedOrder.getCreator().getPk().equals(creatorFk)) {
            jpaSalesOrderRepository.delete(deletedOrder);
            result = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.DELETING_SALES_ORDER_SUCCESS);
        } else {
            throw new GenericException(HttpStatus.METHOD_NOT_ALLOWED, "USER_NOT_THE_CREATOR", "User is not the creator");
        }
        return result;
    }

    public ResponseDTO retrieveOrderDashboardByStatus() {
        ResponseDTO result;
        List<DashboardQueryDTO> dashboardQueryDTOs = jpaSalesOrderRepository.countOrderGroupByStatus();
        List<DashboardDTO> dashboardDTOs = new ArrayList<>();
        for (DashboardQueryDTO i : dashboardQueryDTOs) {
            DashboardDTO dashboardDTO = new DashboardDTO();
            dashboardDTO.setId(SalesOrderStatus.values()[i.getId()].getName());
            dashboardDTO.setCount(i.getCount());
            dashboardDTOs.add(dashboardDTO);
        }
        result = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.COUNTING_NO_SALES_ORDERS_BY_STATUS_SUCCESS, dashboardDTOs);

        return result;
    }

    public ResponseDTO deleteSaleOrders(List<Long> orderPks, Long creatorFk) {
        ResponseDTO result;
        List<SalesOrder> deletedListOrders = jpaSalesOrderRepository.getSaleOrdersByOrderPks(orderPks);
        boolean hasOtherCreator = deletedListOrders.stream()
                .anyMatch(i -> !creatorFk.equals(i.getCreator().getPk()));
        if (!hasOtherCreator && orderPks.size() == deletedListOrders.size()) {
            jpaSalesOrderRepository.deleteAllById(deletedListOrders.stream().map(SalesOrder::getPk).toList());
            result = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.DELETING_LIST_OF_SALES_ORDERS_SUCCESS);
        } else {
            throw new GenericException(HttpStatus.METHOD_NOT_ALLOWED, "USER_NOT_THE_CREATOR", "User is not the creator");
        }
        return result;
    }
}
