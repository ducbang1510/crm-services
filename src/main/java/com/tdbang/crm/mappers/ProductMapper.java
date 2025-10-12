package com.tdbang.crm.mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tdbang.crm.entities.Product;
import com.tdbang.crm.utils.AppConstants;

@Component
public class ProductMapper {

    @Autowired
    private ModelMapper modelMapper;

    public List<Product> mapRecordList(Map<String, Object> resultMap) {
        List<Product> products = new ArrayList<>();

        // Extract the "recordList" value
        Object recordsObj = resultMap.get(AppConstants.RECORD_LIST_KEY);
        if (recordsObj instanceof List<?>) {
            List<?> recordList = (List<?>) recordsObj;

            for (Object obj : recordList) {
                if (obj instanceof Map) {
                    Map<String, Object> productMap = (Map<String, Object>) obj;

                    // Map to Product
                    Product product = modelMapper.map(productMap, Product.class);
                    products.add(product);
                }
            }
        }

        return products;
    }
}
