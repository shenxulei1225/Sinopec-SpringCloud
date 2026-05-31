package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.query;

import cn.cheers.x.module.dynamicbusiness.service.entity.query.enums.SortDirection;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;

/**
 * 排序条件 VO
 * 
 * <p>封装排序条件，用于 REST API 请求。</p>
 * 
 * @author 扩展字段查询服务
 */
@Schema(description = "管理后台 - 排序条件")
public class QuerySortVO {

    /**
     * 字段编码（必填）
     * 
     * <p>对应 FieldDefinition 中的 code。
     * 系统会验证字段是否存在且可查询。</p>
     */
    @Schema(description = "字段编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "power")
    @NotBlank(message = "排序字段编码不能为空")
    private String fieldCode;

    /**
     * 排序方向
     * 
     * <p>默认为升序（ASC）。可选值：ASC、DESC。</p>
     */
    @Schema(description = "排序方向", example = "ASC")
    private SortDirection direction = SortDirection.ASC;

    // ==================== 构造方法 ====================

    public QuerySortVO() {
    }

    public QuerySortVO(String fieldCode, SortDirection direction) {
        this.fieldCode = fieldCode;
        this.direction = direction;
    }

    // ==================== Getter/Setter ====================

    public String getFieldCode() {
        return fieldCode;
    }

    public void setFieldCode(String fieldCode) {
        this.fieldCode = fieldCode;
    }

    public SortDirection getDirection() {
        return direction;
    }

    public void setDirection(SortDirection direction) {
        this.direction = direction;
    }

    // ==================== 便捷构造方法 ====================

    /**
     * 创建升序排序条件
     */
    public static QuerySortVO asc(String fieldCode) {
        return new QuerySortVO(fieldCode, SortDirection.ASC);
    }

    /**
     * 创建降序排序条件
     */
    public static QuerySortVO desc(String fieldCode) {
        return new QuerySortVO(fieldCode, SortDirection.DESC);
    }
}
