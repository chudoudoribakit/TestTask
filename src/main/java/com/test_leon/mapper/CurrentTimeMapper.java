package com.test_leon.mapper;

import com.test_leon.dto.CurrentTimeDto;
import com.test_leon.entity.CurrentTime;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CurrentTimeMapper {

    CurrentTimeDto toDto(CurrentTime entity);

    List<CurrentTimeDto> toDtoList(List<CurrentTime> entities);
}
