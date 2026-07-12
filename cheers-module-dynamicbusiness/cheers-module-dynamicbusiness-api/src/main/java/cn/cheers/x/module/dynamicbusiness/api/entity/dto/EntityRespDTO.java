package cn.cheers.x.module.dynamicbusiness.api.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 通用实体读响应 DTO（跨模块 RPC）。
 *
 * <p>与运行时 {@code EntityRespVO} 同信封：固定列与扩展列均为 fieldCode → 值的 Map，
 * 不按具体业务（facility / equipment 等）拆强类型字段。</p>
 */
@Schema(description = "RPC 服务 - 通用实体响应 DTO")
@Data
public class EntityRespDTO {

    @Schema(description = "实体 ID")
    private Long id;

    @Schema(description = "排序序号")
    private Integer sort;

    @Schema(description = "固定列（BaseField），key 为 fieldCode")
    private Map<String, Object> baseFields;

    @Schema(description = "扩展列（CustomField），key 为 fieldCode")
    private Map<String, Object> customFields;

    @Schema(description = "模型名称（查询增强，只读）")
    private String modelName;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}
