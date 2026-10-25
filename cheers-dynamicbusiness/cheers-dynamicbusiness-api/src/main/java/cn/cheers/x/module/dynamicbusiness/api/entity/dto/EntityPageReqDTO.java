package cn.cheers.x.module.dynamicbusiness.api.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 跨模块实体分页条件。
 *
 * <p>负责把分页、关键词和基础字段等值条件交给实体表执行。</p>
 * <p>不负责分类、关系或业务状态推导。基础字段必须已配置为该底座的物理列。</p>
 */
@Schema(description = "RPC 服务 - 通用实体分页请求")
@Data
public class EntityPageReqDTO {

    @Schema(description = "底座类型编码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String entityTypeCode;

    @Schema(description = "型号 ID")
    private Long modelId;

    @Schema(description = "实体启停状态")
    private Integer status;

    @Schema(description = "名称关键词")
    private String keyword;

    @Schema(description = "页码，从 1 开始")
    private Integer pageNo = 1;

    @Schema(description = "每页条数")
    private Integer pageSize = 10;

    @Schema(description = "基础字段等值条件，键为字段编码")
    private Map<String, Object> baseFieldEquals = new LinkedHashMap<>();
}
