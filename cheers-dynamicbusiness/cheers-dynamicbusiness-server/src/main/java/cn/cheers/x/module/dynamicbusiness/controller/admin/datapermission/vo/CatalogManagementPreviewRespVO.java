package cn.cheers.x.module.dynamicbusiness.controller.admin.datapermission.vo;

import cn.cheers.x.module.dynamicbusiness.service.datapermission.CatalogManagementQueryService.FieldPlan;
import cn.cheers.x.module.dynamicbusiness.service.datapermission.CatalogManagementQueryService.PlanPreview;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "保存管理方式前的提示：一种管理方式会用哪个字段、要不要新加、有多少条记录还没填")
@Data
public class CatalogManagementPreviewRespVO {

    @Schema(description = "按哪本目录管理")
    private String targetRegistryCode;

    @Schema(description = "那本目录的名称")
    private String targetName;

    @Schema(description = "USE_EXISTING 用目录上已有字段；ATTACH_LIBRARY 挂字段库字段；CREATE_LIBRARY 新建字段再挂；CHOOSE 几个字段要用户挑")
    private String action;

    @Schema(description = "管理用的字段编码；CHOOSE 时为空")
    private String fieldCode;

    @Schema(description = "管理用的字段名称；CHOOSE 时为空")
    private String fieldName;

    @Schema(description = "CHOOSE 时让用户挑的字段")
    private List<Candidate> candidates;

    @Schema(description = "保存后有多少条记录还没填这个字段，这些记录谁都看不到，直到补上")
    private Long recordsWithoutValue;

    @Data
    public static class Candidate {
        private String fieldCode;
        private String fieldName;
    }

    public static CatalogManagementPreviewRespVO from(PlanPreview preview) {
        FieldPlan plan = preview.plan();
        CatalogManagementPreviewRespVO vo = new CatalogManagementPreviewRespVO();
        vo.setTargetRegistryCode(plan.targetRegistryCode());
        vo.setTargetName(plan.targetName());
        vo.setAction(plan.action().name());
        vo.setFieldCode(plan.fieldCode());
        vo.setFieldName(plan.fieldName());
        vo.setCandidates(plan.candidates().stream().map(ref -> {
            Candidate candidate = new Candidate();
            candidate.setFieldCode(ref.fieldCode());
            candidate.setFieldName(ref.fieldName());
            return candidate;
        }).toList());
        vo.setRecordsWithoutValue(preview.recordsWithoutValue());
        return vo;
    }
}
