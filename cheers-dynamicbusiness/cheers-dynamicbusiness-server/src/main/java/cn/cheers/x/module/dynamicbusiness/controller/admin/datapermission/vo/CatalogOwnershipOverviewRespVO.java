package cn.cheers.x.module.dynamicbusiness.controller.admin.datapermission.vo;

import cn.cheers.x.module.dynamicbusiness.service.datapermission.CatalogOwnershipQueryService.CatalogOwnershipView;
import cn.cheers.x.module.dynamicbusiness.service.datapermission.CatalogOwnershipQueryService.OwnershipDeclarationView;
import cn.cheers.x.module.dynamicbusiness.service.datapermission.CatalogOwnershipQueryService.OwnershipHop;
import cn.cheers.x.module.dynamicbusiness.service.datapermission.CatalogOwnershipQueryService.OwnershipOverview;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "数据资源管理「数据管理方式」标签：可选的管理方式，每本目录怎么管、整条链怎么走")
@Data
public class CatalogOwnershipOverviewRespVO {

    @Schema(description = "管理单元目录的注册编码；租户还没在数据资源管理里登记管理单元类型时为空")
    private String managementUnitRegistryCode;

    @Schema(description = "可选的管理方式，按表格列顺序")
    private List<Method> methods;

    @Schema(description = "全部数据目录，每本一行")
    private List<Catalog> catalogs;

    @Schema(description = "一种可选的管理方式，表格里的一列，页面显示为「按<name>管理」")
    @Data
    public static class Method {
        private String registryCode;
        private String name;
        @Schema(description = "能对应到单位。对应不到时整列不能勾")
        private Boolean reachesManagementUnit;
        @Schema(description = "有几本目录在用这种方式")
        private Integer usageCount;
    }

    @Schema(description = "一本目录的归属情况")
    @Data
    public static class Catalog {
        private String registryCode;
        private String name;
        @Schema(description = "数据目录的分组，空表示未分组")
        private String groupName;
        @Schema(description = "这本就是管理单元目录，是范围的起点")
        private Boolean managementUnitCatalog;
        @Schema(description = "逐跳能到达管理单元目录：按任职所在单元收窄")
        private Boolean reachesManagementUnit;
        private List<Declaration> declarations;
        @Schema(description = "这一行能勾的管理方式（目录注册编码）")
        private List<String> allowedMethods;
    }

    @Schema(description = "一条归属声明，以及它到管理单元目录的整条链")
    @Data
    public static class Declaration {
        private String fieldCode;
        private String fieldName;
        private String targetRegistryCode;
        private String targetName;
        private Boolean required;
        private Boolean multiValued;
        @Schema(description = "从这条声明开始、逐跳到管理单元目录的每一跳；到不了时为空")
        private List<Hop> path;
    }

    @Schema(description = "链上的一跳：用哪个字段，归到哪本目录")
    @Data
    public static class Hop {
        private String fieldCode;
        private String fieldName;
        private String targetRegistryCode;
        private String targetName;
    }

    public static CatalogOwnershipOverviewRespVO from(OwnershipOverview overview) {
        CatalogOwnershipOverviewRespVO resp = new CatalogOwnershipOverviewRespVO();
        resp.setManagementUnitRegistryCode(overview.managementUnitRegistryCode());
        resp.setMethods(overview.methods().stream().map(view -> {
            Method method = new Method();
            method.setRegistryCode(view.registryCode());
            method.setName(view.name());
            method.setReachesManagementUnit(view.reachesManagementUnit());
            method.setUsageCount(view.usageCount());
            return method;
        }).toList());
        resp.setCatalogs(overview.catalogs().stream().map(CatalogOwnershipOverviewRespVO::toCatalog).toList());
        return resp;
    }

    private static Catalog toCatalog(CatalogOwnershipView view) {
        Catalog catalog = new Catalog();
        catalog.setRegistryCode(view.registryCode());
        catalog.setName(view.name());
        catalog.setGroupName(view.groupName());
        catalog.setAllowedMethods(view.allowedMethods());
        catalog.setManagementUnitCatalog(view.managementUnitCatalog());
        catalog.setReachesManagementUnit(view.reachesManagementUnit());
        catalog.setDeclarations(view.declarations().stream().map(CatalogOwnershipOverviewRespVO::toDeclaration).toList());
        return catalog;
    }

    private static Declaration toDeclaration(OwnershipDeclarationView view) {
        Declaration declaration = new Declaration();
        declaration.setFieldCode(view.hop().fieldCode());
        declaration.setFieldName(view.hop().fieldName());
        declaration.setTargetRegistryCode(view.hop().targetRegistryCode());
        declaration.setTargetName(view.hop().targetName());
        declaration.setRequired(view.required());
        declaration.setMultiValued(view.multiValued());
        declaration.setPath(view.path().stream().map(CatalogOwnershipOverviewRespVO::toHop).toList());
        return declaration;
    }

    private static Hop toHop(OwnershipHop source) {
        Hop hop = new Hop();
        hop.setFieldCode(source.fieldCode());
        hop.setFieldName(source.fieldName());
        hop.setTargetRegistryCode(source.targetRegistryCode());
        hop.setTargetName(source.targetName());
        return hop;
    }
}
