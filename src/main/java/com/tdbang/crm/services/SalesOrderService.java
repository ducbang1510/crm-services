package com.tdbang.crm.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.tdbang.crm.repositories.ContactRepository;
import com.tdbang.crm.repositories.SalesOrderRepository;
import com.tdbang.crm.repositories.UserRepository;
import com.tdbang.crm.utils.AppConstants;
import com.tdbang.crm.utils.MessageConstants;

@Service
public class SalesOrderService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SalesOrderService.class);
    @Autowired
    private SalesOrderRepository salesOrderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ContactRepository contactRepository;

    public ResponseDTO getListOfOrder(Integer pageNumber, Integer pageSize, String subjectFilter) {
        ResponseDTO result = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.FETCHING_LIST_OF_SALES_ORDER_SUCCESS);
        if (pageNumber != null && pageSize != null) {
            Map<String, Object> resultMap = new HashMap<>();
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<SalesOrderQueryDTO> salesOrderQueryDTOPagePage = salesOrderRepository.getSalesOrderPageable(subjectFilter, pageable);
            resultMap.put(AppConstants.SALES_ORDER_LIST, mappingToListSalesOrderDTO(salesOrderQueryDTOPagePage.getContent()));
            resultMap.put(AppConstants.TOTAL_RECORD, salesOrderQueryDTOPagePage.getTotalElements());
            result.setData(resultMap);
        } else {
            List<SalesOrderQueryDTO> salesOrderQueryDTOs = salesOrderRepository.getAllSalesOrder(subjectFilter);
            result.setData(mappingToListSalesOrderDTO(salesOrderQueryDTOs));
        }

        return result;
    }

    public ResponseDTO getSalesOrderDetails(Long orderPk) {
        ResponseDTO result = new ResponseDTO();
        if (orderPk != null) {
            SalesOrderQueryDTO salesOrderQueryDTO = salesOrderRepository.getSalesOrderDetailsByPk(orderPk);
            result = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.FETCHING_SALES_ORDER_SUCCESS, mappingSalesOrderQueryDTOToSalesOrderDTO(salesOrderQueryDTO));
        }

        return result;
    }

    public ResponseDTO updateSalesOrderDetails(Long orderPk, Long creatorFk, SalesOrderDTO salesOrderDTO) {
        ResponseDTO result;
        SalesOrder updatedOrder = salesOrderRepository.findByPk(orderPk)
                .orElseThrow(() -> new GenericException(HttpStatus.NOT_FOUND, "SALES_ORDER_NOT_FOUND", "Sales order not found"));
        if (updatedOrder.getCreator().getPk().equals(creatorFk)) {
            updatedOrder = mappingSalesOrderDTOToEntity(salesOrderDTO, null, false);
            salesOrderRepository.save(updatedOrder);
            result = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.UPDATING_SALES_ORDER_SUCCESS);
        } else {
            throw new GenericException(HttpStatus.METHOD_NOT_ALLOWED, "USER_NOT_THE_CREATOR", "User is not the creator");
        }
        return result;
    }

    public ResponseDTO createNewSalesOrder(SalesOrderDTO salesOrderDTO, Long creatorFk) {
        ResponseDTO result;
        User creatorUser = userRepository.findUserByPk(creatorFk);
        try {
            SalesOrder saveSalesOrder = mappingSalesOrderDTOToEntity(salesOrderDTO, creatorUser, true);
            salesOrderRepository.save(saveSalesOrder);
            result = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.CREATING_NEW_SALES_ORDER_SUCCESS);
        } catch (Exception e) {
            throw new GenericException(HttpStatus.BAD_REQUEST, "CREATING_NEW_SALES_ORDER_ERROR", MessageConstants.CREATING_NEW_SALES_ORDER_ERROR);
        }
        return result;
    }

    public ResponseDTO deleteSalesOrderDetails(Long orderPk, Long creatorFk) {
        ResponseDTO result;
        SalesOrder deletedOrder = salesOrderRepository.findByPk(orderPk)
                .orElseThrow(() -> new GenericException(HttpStatus.NOT_FOUND, "SALES_ORDER_NOT_FOUND", "Sales order not found"));
        if (deletedOrder.getCreator().getPk().equals(creatorFk)) {
            salesOrderRepository.delete(deletedOrder);
            result = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.DELETING_SALES_ORDER_SUCCESS);
        } else {
            throw new GenericException(HttpStatus.METHOD_NOT_ALLOWED, "USER_NOT_THE_CREATOR", "User is not the creator");
        }
        return result;
    }

    public ResponseDTO retrieveOrderDashboardByStatus() {
        ResponseDTO result;
        List<DashboardQueryDTO> dashboardQueryDTOs = salesOrderRepository.countOrderGroupByStatus();
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
        List<SalesOrder> deletedListOrders = salesOrderRepository.getSaleOrdersByOrderPks(orderPks);
        boolean hasOtherCreator = deletedListOrders.stream()
                .anyMatch(i -> !creatorFk.equals(i.getCreator().getPk()));
        if (!hasOtherCreator && orderPks.size() == deletedListOrders.size()) {
            salesOrderRepository.deleteAllById(deletedListOrders.stream().map(SalesOrder::getPk).toList());
            result = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.DELETING_LIST_OF_SALES_ORDERS_SUCCESS);
        } else {
            throw new GenericException(HttpStatus.METHOD_NOT_ALLOWED, "USER_NOT_THE_CREATOR", "User is not the creator");
        }
        return result;
    }

    private List<SalesOrderDTO> mappingToListSalesOrderDTO(List<SalesOrderQueryDTO> salesOrderQueryDTOList) {
        List<SalesOrderDTO> salesOrderDTOList = new ArrayList<>();
        for (SalesOrderQueryDTO salesOrderQueryDTO : salesOrderQueryDTOList) {
            SalesOrderDTO salesOrderDTO = mappingSalesOrderQueryDTOToSalesOrderDTO(salesOrderQueryDTO);
            salesOrderDTOList.add(salesOrderDTO);
        }
        return salesOrderDTOList;
    }

    private SalesOrderDTO mappingSalesOrderQueryDTOToSalesOrderDTO(SalesOrderQueryDTO salesOrderQueryDTO) {
        SalesOrderDTO salesOrderDTO = new SalesOrderDTO();
        salesOrderDTO.setPk(salesOrderQueryDTO.getPk());
        salesOrderDTO.setSubject(salesOrderQueryDTO.getSubject());
        salesOrderDTO.setContactName(salesOrderQueryDTO.getContactName());
        salesOrderDTO.setStatus(SalesOrderStatus.values()[salesOrderQueryDTO.getStatus()].getName());
        salesOrderDTO.setTotal(salesOrderQueryDTO.getTotal());
        salesOrderDTO.setAssignedTo(salesOrderQueryDTO.getNameUserAssignedTo());
        salesOrderDTO.setCreator(salesOrderQueryDTO.getCreatorName());
        salesOrderDTO.setDescription(salesOrderQueryDTO.getDescription());
        salesOrderDTO.setCreatedTime(salesOrderQueryDTO.getCreatedOn());
        salesOrderDTO.setUpdatedTime(salesOrderQueryDTO.getUpdatedOn());
        return salesOrderDTO;
    }

    private SalesOrder mappingSalesOrderDTOToEntity(SalesOrderDTO salesOrderDTO, User creatorUser, boolean isCreateNew) {
        SalesOrder salesOrder = new SalesOrder();
        User userAssignedTo = userRepository.getUsersByNames(salesOrderDTO.getAssignedTo()).get(0);
        Contact contact = contactRepository.getContactsByContactName(salesOrderDTO.getContactName()).get(0);
        salesOrder.setPk(salesOrderDTO.getPk());
        salesOrder.setSubject(salesOrderDTO.getSubject());
        salesOrder.setContact(contact);
        salesOrder.setStatus(SalesOrderStatus.fromName(salesOrderDTO.getStatus()));
        salesOrder.setTotal(salesOrderDTO.getTotal());
        salesOrder.setAssignedTo(userAssignedTo);
        salesOrder.setDescription(salesOrderDTO.getDescription());
        if (isCreateNew) {
            if (creatorUser != null)
                salesOrder.setCreator(creatorUser);
        } else {
            salesOrder.setUpdatedOn(new Date());
        }
        return salesOrder;
    }
}
