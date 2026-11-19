package cn.cheers.x.module.dynamicbusiness.controller.admin.datapermission.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "一条任职")
@Data
public class PostAssignmentRespVO {

    private Long id;
    private Long userId;

    @Schema(description = "人员姓名，查询时补齐")
    private String userName;

    private Long dutyPostId;

    @Schema(description = "职务名称，查询时补齐")
    private String dutyPostName;

    @Schema(description = "管理单元：管理单元目录里的记录编号")
    private Long managementUnitId;

    @Schema(description = "管理单元名称，查询时补齐")
    private String managementUnitName;

    private LocalDate validFrom;
    private LocalDate validTo;

    @Schema(description = "这条任职能看的目录：职务绑定的角色有查看授权，并且单元类型登记了这本目录。只在人员详情里给")
    private List<CatalogName> visibleCatalogs;

    @Schema(description = "一本目录")
    @Data
    public static class CatalogName {
        private String registryCode;
        private String name;
    }
}
