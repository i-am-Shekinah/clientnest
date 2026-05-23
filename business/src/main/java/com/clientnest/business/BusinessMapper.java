package com.clientnest.business;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BusinessMapper {
    BusinessMapper INSTANCE = Mappers.getMapper(BusinessMapper.class);
    BusinessDto toDto(Business business);
}
