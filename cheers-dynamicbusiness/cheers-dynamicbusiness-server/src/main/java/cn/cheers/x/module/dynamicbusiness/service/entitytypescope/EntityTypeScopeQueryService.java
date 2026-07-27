package cn.cheers.x.module.dynamicbusiness.service.entitytypescope;

import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * 划分数据（SCOPE）查询：实体 id 列表。
 */
@Validated
public interface EntityTypeScopeQueryService {

    List<Long> listEntityIds(@NotBlank String entityTypeCode);
}
