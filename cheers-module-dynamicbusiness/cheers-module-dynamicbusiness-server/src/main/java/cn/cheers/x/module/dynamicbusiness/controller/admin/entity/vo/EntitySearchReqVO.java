package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 管理后台 - 实体搜索请求 VO
 * 
 * 支持全文搜索、高级过滤、多字段排序功能
 */
@Schema(description = "管理后台 - 实体搜索请求 VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class EntitySearchReqVO extends PageParam {

    // ==================== 基础过滤条件 ====================

    @Schema(description = "业务类型编码（必填）", requiredMode = Schema.RequiredMode.REQUIRED, example = "equipment")
    @jakarta.validation.constraints.NotBlank(message = "业务类型编码不能为空")
    private String businessTypeCode;

    @Schema(description = "模型ID", example = "1")
    private Long modelId;

    @Schema(description = "状态（0-禁用，1-启用）", example = "1")
    private Integer status;

    // ==================== 全文搜索 ====================

    @Schema(description = "全文搜索关键词（搜索实体名称和自定义字段）", example = "设备")
    private String keyword;

    @Schema(description = "是否搜索自定义字段（默认true）", example = "true")
    private Boolean searchCustomFields = true;

    // ==================== 高级过滤条件 ====================

    @Schema(description = "分类ID列表（支持多分类过滤）", example = "[1, 2, 3]")
    private List<Long> categoryIds;

    @Schema(description = "创建时间范围-开始", example = "2024-01-01T00:00:00")
    private LocalDateTime createTimeStart;

    @Schema(description = "创建时间范围-结束", example = "2024-12-31T23:59:59")
    private LocalDateTime createTimeEnd;

    @Schema(description = "更新时间范围-开始", example = "2024-01-01T00:00:00")
    private LocalDateTime updateTimeStart;

    @Schema(description = "更新时间范围-结束", example = "2024-12-31T23:59:59")
    private LocalDateTime updateTimeEnd;

    @Schema(description = "自定义字段过滤条件（键为字段ID，值为过滤条件）", 
            example = "{\"1\": {\"operator\": \"eq\", \"value\": \"test\"}, \"2\": {\"operator\": \"gt\", \"value\": 100}}")
    private transient Map<String, FieldFilter> customFieldFilters;

    // ==================== 排序条件 ====================

    @Schema(description = "排序字段列表", example = "[{\"field\": \"createTime\", \"order\": \"desc\"}, {\"field\": \"name\", \"order\": \"asc\"}]")
    private transient List<SortField> sortFields;


    /**
     * 字段过滤条件
     */
    @Schema(description = "字段过滤条件")
    @Data
    public static class FieldFilter {
        
        @Schema(description = "操作符：eq(等于), ne(不等于), gt(大于), gte(大于等于), lt(小于), lte(小于等于), like(模糊匹配), in(包含), between(范围)", 
                example = "eq")
        private String operator;

        @Schema(description = "过滤值（单值或数组，between操作符需要数组[min, max]）", example = "test")
        private Object value;
    }

    /**
     * 排序字段
     */
    @Schema(description = "排序字段")
    @Data
    public static class SortField {
        
        @Schema(description = "排序字段名（支持：id, name, status, createTime, updateTime，或自定义字段ID）", 
                example = "createTime")
        private String field;

        @Schema(description = "排序方向：asc(升序), desc(降序)", example = "desc")
        private String order = "desc";
    }
}
