package cn.cheers.x.module.platformresource.controller.admin.component.vo;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 组件数据来源：配置器与持久化 data_source JSON 的统一结构。
 */
@Schema(description = "组件数据来源")
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ComponentDataSourceVO {

    @Schema(description = "业务分类：dynamic（动态业务）/ system（系统业务）/ category（分类体系）", example = "dynamic")
    private String businessCategory;

    @Schema(description = "实体类型编码（动态业务类型与系统模块编码统一）", example = "equipment")
    @JsonAlias("businessTypeCode")
    private String entityTypeCode;

    @Schema(description = "数据类型：model / entity；system 恒为 entity", example = "entity")
    private String dataKind;
}
