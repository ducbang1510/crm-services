package com.tdbang.crm.mappers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tdbang.crm.dtos.SalesOrderDTO;
import com.tdbang.crm.dtos.nativequerydto.SalesOrderQueryDTO;
import com.tdbang.crm.entities.Contact;
import com.tdbang.crm.entities.SalesOrder;
import com.tdbang.crm.entities.User;
import com.tdbang.crm.enums.SalesOrderStatus;
import com.tdbang.crm.utils.AppConstants;

@Component
public class SalesOrderMapper {

    @Autowired
    private ModelMapper modelMapper;

    public List<SalesOrder> mapRecordList(Map<String, Object> resultMap) {
        List<SalesOrder> salesOrders = new ArrayList<>();

        // Extract the "recordList" value
        Object recordsObj = resultMap.get(AppConstants.RECORD_LIST_KEY);
        if (recordsObj instanceof List<?>) {
            List<?> recordList = (List<?>) recordsObj;

            for (Object obj : recordList) {
                if (obj instanceof Map) {
                    Map<String, Object> salesOrderMap = (Map<String, Object>) obj;

                    if (salesOrderMap.containsKey("contact") && salesOrderMap.get("contact") instanceof Map) {
                        Contact contact = modelMapper.map(salesOrderMap.get("contact"), Contact.class);
                        salesOrderMap.put("contact", contact);
                    }

                    // Convert nested maps for creator and assignedTo
                    if (salesOrderMap.containsKey("creator") && salesOrderMap.get("creator") instanceof Map) {
                        User creator = modelMapper.map(salesOrderMap.get("creator"), User.class);
                        salesOrderMap.put("creator", creator);
                    }

                    if (salesOrderMap.containsKey("assignedTo") && salesOrderMap.get("assignedTo") instanceof Map) {
                        User assignedTo = modelMapper.map(salesOrderMap.get("assignedTo"), User.class);
                        salesOrderMap.put("assignedTo", assignedTo);
                    }

                    // Map to SalesOrder
                    SalesOrder contact = modelMapper.map(salesOrderMap, SalesOrder.class);
                    salesOrders.add(contact);
                }
            }
        }

        return salesOrders;
    }

    public SalesOrderDTO mappingSalesOrderEntityToSalesOrderDTO(SalesOrder salesOrder) {
        SalesOrderDTO salesOrderDTO = new SalesOrderDTO();
        salesOrderDTO.setPk(salesOrder.getPk());
        salesOrderDTO.setSubject(salesOrder.getSubject());
        salesOrderDTO.setContactName(salesOrder.getContact().getContactName());
        salesOrderDTO.setStatus(salesOrder.getStatus().getName());
        salesOrderDTO.setTotal(salesOrder.getTotal());
        salesOrderDTO.setAssignedTo(salesOrder.getAssignedTo().getName());
        salesOrderDTO.setCreator(salesOrder.getCreator().getName());
        salesOrderDTO.setDescription(salesOrder.getDescription());
        salesOrderDTO.setCreatedTime(salesOrder.getCreatedOn());
        salesOrderDTO.setUpdatedTime(salesOrder.getUpdatedOn());
        return salesOrderDTO;
    }

    public List<SalesOrderDTO> mappingToListSalesOrderDTO(List<SalesOrderQueryDTO> salesOrderQueryDTOList) {
        List<SalesOrderDTO> salesOrderDTOList = new ArrayList<>();
        for (SalesOrderQueryDTO salesOrderQueryDTO : salesOrderQueryDTOList) {
            SalesOrderDTO salesOrderDTO = mappingSalesOrderQueryDTOToSalesOrderDTO(salesOrderQueryDTO);
            salesOrderDTOList.add(salesOrderDTO);
        }
        return salesOrderDTOList;
    }

    public SalesOrderDTO mappingSalesOrderQueryDTOToSalesOrderDTO(SalesOrderQueryDTO salesOrderQueryDTO) {
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

    public SalesOrder mappingSalesOrderDTOToEntity(SalesOrderDTO salesOrderDTO, User creatorUser, User userAssignedTo,
                                                   Contact contact, boolean isCreateNew) {
        SalesOrder salesOrder = new SalesOrder();
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
