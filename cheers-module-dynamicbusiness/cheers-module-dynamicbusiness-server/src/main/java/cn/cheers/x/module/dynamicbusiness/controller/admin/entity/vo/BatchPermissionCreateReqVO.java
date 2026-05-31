package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 批量权限创建请求 VO
 */
public class BatchPermissionCreateReqVO {

    /**
     * 批量访问权限创建请求
     */
    @Schema(description = "批量创建实体访问权限请求")
    @Data
    public static class Access {
        @Schema(description = "角色ID列表", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotEmpty(message = "角色ID列表不能为空")
        private List<Long> roleIds;

        @Schema(description = "实体ID列表", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotEmpty(message = "实体ID列表不能为空")
        private List<Long> entityIds;

        @Schema(description = "是否可查看", example = "true")
        private Boolean canView = true;
    }

    /**
     * 批量操作权限创建请求
     */
    @Schema(description = "批量创建实体操作权限请求")
    @Data
    public static class Operation {
        @Schema(description = "角色ID列表", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotEmpty(message = "角色ID列表不能为空")
        private List<Long> roleIds;

        @Schema(description = "模型ID（模型级别权限时必填）")
        private Long modelId;

        @Schema(description = "实体ID列表（实体级别权限时必填）")
        private List<Long> entityIds;

        @Schema(description = "是否可创建", example = "false")
        private Boolean canCreate = false;

        @Schema(description = "是否可更新", example = "false")
        private Boolean canUpdate = false;

        @Schema(description = "是否可删除", example = "false")
        private Boolean canDelete = false;
    }

    /**
     * 批量字段权限创建请求
     */
    @Schema(description = "批量创建字段级权限请求")
    @Data
    public static class Field {
        @Schema(description = "角色ID列表", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotEmpty(message = "角色ID列表不能为空")
        private List<Long> roleIds;

        @Schema(description = "模型ID", requiredMode = Schema.RequiredMode.REQUIRED)
        private Long modelId;

        @Schema(description = "字段ID列表", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotEmpty(message = "字段ID列表不能为空")
        private List<Long> fieldIds;

        @Schema(description = "是否可查看", example = "true")
        private Boolean canView = true;

        @Schema(description = "是否可编辑", example = "false")
        private Boolean canEdit = false;
    }

    /**
     * 批量分类权限创建请求
     */
    @Schema(description = "批量创建分类级权限请求")
    @Data
    public static class Category {
        @Schema(description = "角色ID列表", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotEmpty(message = "角色ID列表不能为空")
        private List<Long> roleIds;

        @Schema(description = "分类ID列表", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotEmpty(message = "分类ID列表不能为空")
        private List<Long> categoryIds;

        @Schema(description = "是否可查看", example = "true")
        private Boolean canView = true;

        @Schema(description = "是否可管理", example = "false")
        private Boolean canManage = false;
    }
}
