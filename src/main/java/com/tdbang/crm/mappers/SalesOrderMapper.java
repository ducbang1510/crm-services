package com.tdbang.crm.mappers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tdbang.crm.dtos.SalesOrderDTO;
import com.tdbang.crm.dtos.nativequerydto.SalesOrderQueryDTO;
import com.tdbang.crm.entities.Contact;
import com.tdbang.crm.entities.SalesOrder;
import com.tdbang.crm.entities.User;
import com.tdbang.crm.enums.SalesOrderStatus;

@Component
public class SalesOrderMapper {

    @Autowired
    private ModelMapper modelMapper;

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
