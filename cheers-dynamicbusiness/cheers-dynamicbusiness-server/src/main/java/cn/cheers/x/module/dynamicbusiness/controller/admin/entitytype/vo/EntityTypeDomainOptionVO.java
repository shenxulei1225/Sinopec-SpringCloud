package cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 子数据类型业务域下拉选项（型号 CRUD 等）。
 */
@Schema(description = "业务域选项（来自 DOMAIN 子数据类型）")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntityTypeDomainOptionVO {

    @Schema(description = "业务域标识（写入型号/实体的 domain）", example = "巡检")
    private String value;

    @Schema(description = "展示名（子数据类型名称）", example = "巡检任务")
    private String label;

    @Schema(description = "子数据类型注册编码", example = "task_patrol")
    private String registryCode;
}
