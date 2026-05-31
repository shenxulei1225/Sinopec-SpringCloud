package cn.cheers.x.module.dynamicbusiness.service.entity.query.dto;

import cn.cheers.x.module.dynamicbusiness.service.entity.query.enums.SortDirection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

/**
 * 字段排序条件
 *
 * 封装单个字段的排序条件，包含字段编码和排序方向
 *
 * @author 系统
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldSort {

    /**
     * 字段编码（必填）
     * 对应 FieldDefinition 中的 code
     */
    @NotBlank(message = "排序字段编码不能为空")
    private String fieldCode;

    /**
     * 排序方向
     * 默认为升序（ASC）
     */
    @Builder.Default
    private SortDirection direction = SortDirection.ASC;

    // ==================== 便捷构造方法 ====================

    /**
     * 创建升序排序条件
     *
     * @param fieldCode 字段编码
     * @return 排序条件
     */
    public static FieldSort asc(String fieldCode) {
        return FieldSort.builder()
                .fieldCode(fieldCode)
                .direction(SortDirection.ASC)
                .build();
    }

    /**
     * 创建降序排序条件
     *
     * @param fieldCode 字段编码
     * @return 排序条件
     */
    public static FieldSort desc(String fieldCode) {
        return FieldSort.builder()
                .fieldCode(fieldCode)
                .direction(SortDirection.DESC)
                .build();
    }
}
