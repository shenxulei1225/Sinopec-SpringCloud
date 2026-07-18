package cn.iocoder.yudao.module.system.api.permission.dto;

import cn.iocoder.yudao.module.system.enums.permission.DataScopeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;

@Schema(description = "RPC 服务 - 角色响应 DTO")
@Data
public class RoleRespDTO implements Serializable {

    @Schema(description = "角色编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "角色名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "管理员")
    private String name;

    @Schema(description = "角色标识", requiredMode = Schema.RequiredMode.REQUIRED, example = "admin")
    private String code;

    @Schema(description = "角色排序", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer sort;

    @Schema(description = "角色状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer status;

    @Schema(description = "角色类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer type;

    @Schema(description = "备注", example = "系统管理员")
    private String remark;

    @Schema(description = "数据范围", example = "1")
    private Integer dataScope;

    @Schema(description = "数据范围(指定部门数组)")
    private Set<Long> dataScopeDeptIds;

}