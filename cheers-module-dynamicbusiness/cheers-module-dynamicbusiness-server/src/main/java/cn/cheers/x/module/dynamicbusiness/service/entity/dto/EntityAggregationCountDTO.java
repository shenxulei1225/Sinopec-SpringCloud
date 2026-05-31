package cn.cheers.x.module.dynamicbusiness.service.entity.dto;

import lombok.Data;

/**
 * Entity 聚合统计通用返回 DTO
 */
@Data
public class EntityAggregationCountDTO<T> {

    private T key;

    private Long cnt;
}
