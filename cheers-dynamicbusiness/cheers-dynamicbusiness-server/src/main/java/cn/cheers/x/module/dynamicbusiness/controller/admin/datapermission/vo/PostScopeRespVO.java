package cn.cheers.x.module.dynamicbusiness.controller.admin.datapermission.vo;

import cn.cheers.x.module.dynamicbusiness.service.datapermission.PostScopeQueryService.CatalogAccess;
import cn.cheers.x.module.dynamicbusiness.service.datapermission.PostScopeQueryService.PostScope;
import cn.cheers.x.module.dynamicbusiness.service.datapermission.PostScopeQueryService.ScopeCoverage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "能看的数据：某个组织里任某个职务的人能看到的范围、目录和操作")
@Data
public class PostScopeRespVO {

    private Long managementUnitId;
    @Schema(description = "组织名称")
    private String managementUnitName;
    @Schema(description = "组织类型（型号名称），例如作业区")
    private String unitTypeName;
    private Long dutyPostId;
    private String dutyPostName;

    @Schema(description = "这类组织能管的全部目录，改权限弹窗只列这些")
    private List<Catalog> manageableCatalogs;

    @Schema(description = "范围：第一项是组织本身和下级，其后是范围里的场站、管线等记录")
    private List<Coverage> coverage;

    @Schema(description = "按组织划分的目录，只看范围里的记录")
    private List<Catalog> scopedCatalogs;

    @Schema(description = "不按组织划分的目录，看全部记录")
    private List<Catalog> openCatalogs;

    @Data
    public static class Coverage {
        private String registryCode;
        private String catalogName;
        @Schema(description = "范围里启用的记录总数")
        private Integer total;
        @Schema(description = "前 100 条记录名称，按名称排")
        private List<String> names;
    }

    @Data
    public static class Catalog {
        private String registryCode;
        private String name;
        private String groupName;
        @Schema(description = "能做的操作：query 查看、create 新增、update 修改、delete 删除")
        private List<String> operations;
    }

    public static PostScopeRespVO from(PostScope scope) {
        PostScopeRespVO vo = new PostScopeRespVO();
        vo.setManagementUnitId(scope.managementUnitId());
        vo.setManagementUnitName(scope.managementUnitName());
        vo.setUnitTypeName(scope.unitTypeName());
        vo.setDutyPostId(scope.dutyPostId());
        vo.setDutyPostName(scope.dutyPostName());
        vo.setManageableCatalogs(scope.manageableCatalogs().stream().map(source -> {
            Catalog item = new Catalog();
            item.setRegistryCode(source.registryCode());
            item.setName(source.name());
            item.setGroupName(source.groupName());
            item.setOperations(List.of());
            return item;
        }).toList());
        vo.setCoverage(scope.coverage().stream().map(PostScopeRespVO::coverage).toList());
        vo.setScopedCatalogs(scope.scopedCatalogs().stream().map(PostScopeRespVO::catalog).toList());
        vo.setOpenCatalogs(scope.openCatalogs().stream().map(PostScopeRespVO::catalog).toList());
        return vo;
    }

    private static Coverage coverage(ScopeCoverage source) {
        Coverage item = new Coverage();
        item.setRegistryCode(source.registryCode());
        item.setCatalogName(source.catalogName());
        item.setTotal(source.total());
        item.setNames(source.records().stream().map(record -> record.name()).toList());
        return item;
    }

    private static Catalog catalog(CatalogAccess source) {
        Catalog item = new Catalog();
        item.setRegistryCode(source.registryCode());
        item.setName(source.name());
        item.setGroupName(source.groupName());
        item.setOperations(source.operations());
        return item;
    }
}
