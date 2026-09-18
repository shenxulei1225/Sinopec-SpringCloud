package cn.cheers.x.module.dynamicbusiness.api.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

/**
 * 跨模块写实体：按型号编码落到对应底座表。
 * <p>不负责：列表查数、型号定义。
 */
@Schema(description = "RPC 服务 - 通用实体写入")
@Data
public class EntityWriteReqDTO {

    @Schema(description = "更新时的实体 id")
    private Long id;

    @Schema(description = "底座类型编码", example = "task")
    private String entityTypeCode;

    @Schema(description = "型号编码", example = "patrol_task")
    private String modelCode;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "业务字段，key 为字段编码")
    private Map<String, Object> fields;
}
