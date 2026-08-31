package cn.cheers.x.module.dynamicbusiness.controller.admin.sop.vo;

import cn.cheers.x.framework.common.pojo.PageParam;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.CategoryIdGroupReqVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Schema(description = "管理后台 - SOP 模板库分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SopTemplatePageReqVO extends PageParam {

    @Schema(description = "SOP 分类体系编码；默认 sop", example = "sop")
    private String categoryTypeCode;

    @Schema(description = "分类 ID 列表（含子树展开，与 ENTITIES_BY_CATEGORY 一致）", example = "[1, 2]")
    private List<Long> categoryIds;

    @Schema(description = "多独立栏分类求交组；语义与 query-by-scene 一致")
    private List<CategoryIdGroupReqVO> categoryIdGroups;

    @Schema(description = "搜索关键词", example = "泄漏")
    private String keyword;

    @Schema(description = "结果详情（FULL/LIGHT）；列表默认 LIGHT", example = "LIGHT")
    private String resultDetail = "LIGHT";

    @Schema(description = "关键词搜索字段编码；空则默认 name", example = "[\"name\",\"code\"]")
    private List<String> searchFieldCodes;

    @Schema(description = "排序列；默认 name", example = "name")
    private String orderByColumn;

    @Schema(description = "是否升序；默认 true", example = "true")
    private Boolean isAsc;

}
