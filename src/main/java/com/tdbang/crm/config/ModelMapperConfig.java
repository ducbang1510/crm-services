/*
 * Copyright © 2025 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.config;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();

        // Optional: Strict matching (exact field names)
        mapper.getConfiguration()
            .setMatchingStrategy(MatchingStrategies.STRICT)
            .setSkipNullEnabled(true)
            .setFieldMatchingEnabled(true)
            .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE);

        Converter<String, Enum> stringToEnumConverter = new Converter<String, Enum>() {
            @Override
            public Enum convert(MappingContext<String, Enum> context) {
                if (context.getDestinationType().isEnum() && context.getSource() != null) {
                    return Enum.valueOf((Class<Enum>) context.getDestinationType(), context.getSource());
                }
                return null;
            }
        };

        mapper.addConverter(stringToEnumConverter);

        return mapper;
    }
}
