package com.tdbang.crm.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.tdbang.crm.dtos.ResponseDTO;
import com.tdbang.crm.dtos.nativequerydto.SalesOrderQueryDTO;
import com.tdbang.crm.repositories.SalesOrderRepository;
import com.tdbang.crm.utils.AppConstants;

@Service
public class SalesOrderService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SalesOrderService.class);
    private static final String FETCHING_LIST_OF_SALES_ORDER_SUCCESS = "Fetching list of sales order successfully!";
    @Autowired
    private SalesOrderRepository salesOrderRepository;

    public ResponseDTO getListOfOrder(Integer pageNumber, Integer pageSize) {
        ResponseDTO result = new ResponseDTO(1, FETCHING_LIST_OF_SALES_ORDER_SUCCESS);
        if (pageNumber != null && pageSize != null) {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<SalesOrderQueryDTO> salesOrderQueryDTOPagePage = salesOrderRepository.getSalesOrderPageable(pageable);
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put(AppConstants.SALES_ORDER_LIST, salesOrderQueryDTOPagePage.getContent());
            resultMap.put(AppConstants.TOTAL_RECORD, salesOrderQueryDTOPagePage.getTotalElements());
            result.setData(resultMap);
        } else {
            List<SalesOrderQueryDTO> salesOrderQueryDTOs = salesOrderRepository.getAllSalesOrder();
            result.setData(salesOrderQueryDTOs);
        }

        return result;
    }
}
