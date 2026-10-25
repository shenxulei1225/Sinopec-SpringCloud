package cn.cheers.x.module.dynamicbusiness.api.directory.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 部门目录的强类型只读数据。
 *
 * <p>权威来源是动态业务 {@code department} 实体。禁止调用方解析通用实体字段袋。</p>
 */
@Data
@Schema(description = "RPC 服务 - 部门目录数据")
public class DepartmentDTO {

    @Schema(description = "部门实体 ID")
    private Long id;

    @Schema(description = "部门编码")
    private String code;

    @Schema(description = "部门名称")
    private String name;

    @Schema(description = "上级部门 ID")
    private Long parentId;

    @Schema(description = "状态。1 启用，0 停用")
    private Integer status;

    @Schema(description = "负责人用户目录 ID")
    private Long leaderUserId;
}
