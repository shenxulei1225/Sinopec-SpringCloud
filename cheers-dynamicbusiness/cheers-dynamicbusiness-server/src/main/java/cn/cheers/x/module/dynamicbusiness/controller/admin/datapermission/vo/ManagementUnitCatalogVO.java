package cn.cheers.x.module.dynamicbusiness.controller.admin.datapermission.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/** 「数据资源管理」页读写可管目录用的对象。 */
public final class ManagementUnitCatalogVO {

    private ManagementUnitCatalogVO() {
    }

    @Schema(description = "一个管理单元类型和它能管的数据目录")
    @Data
    public static class TypeItem {

        @Schema(description = "管理单元类型：管理单元目录里的型号编号", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "请选择管理单元类型")
        private Long modelId;

        @Schema(description = "型号名称，读取时补齐，保存时不用传")
        private String modelName;

        @Schema(description = "能管的数据目录注册编码")
        @NotNull(message = "能管的目录不能为空，没有就传空列表")
        private List<String> registryCodes;
    }

    @Schema(description = "可管目录的当前登记")
    @Data
    public static class Resp {

        @Schema(description = "管理单元目录：已登记的管理单元类型所在的目录；还没登记时为空")
        private String managementUnitRegistryCode;

        private List<TypeItem> types;
    }

    @Schema(description = "整份保存已登记的管理单元类型")
    @Data
    public static class SaveReq {

        @Schema(description = "登记的管理单元类型；空列表表示撤掉全部登记")
        @NotNull(message = "登记不能为空，没有就传空列表")
        private List<@Valid TypeItem> types;
    }

    @Schema(description = "已添加为管理单元的数据目录")
    @Data
    public static class RegistryResp {

        @NotNull
        private List<String> registryCodes;
    }

    @Schema(description = "整份保存已添加的管理单元目录")
    @Data
    public static class RegistrySaveReq {

        @Schema(description = "数据目录注册编码，顺序即标签顺序；空列表表示全部撤下")
        @NotNull(message = "目录列表不能为空，没有就传空列表")
        private List<String> registryCodes;
    }

    @Schema(description = "按管理点保存可管目录")
    @Data
    public static class PointSaveReq {

        @NotNull(message = "缺少管理单元目录")
        private String managementUnitRegistryCode;

        @NotNull(message = "请选择管理点")
        private Long managementUnitId;

        @NotNull(message = "能管的目录不能为空，没有就传空列表")
        private List<String> registryCodes;
    }
}
