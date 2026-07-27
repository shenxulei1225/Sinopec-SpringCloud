package cn.cheers.x.module.dynamicbusiness.service.entitytypescope;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * 划分数据（SCOPE）命令：批量加入 / 移出。
 */
@Validated
public interface EntityTypeScopeService {

    void join(@NotBlank String entityTypeCode, @NotEmpty List<Long> entityIds);

    void leave(@NotBlank String entityTypeCode, @NotEmpty List<Long> entityIds);
}
