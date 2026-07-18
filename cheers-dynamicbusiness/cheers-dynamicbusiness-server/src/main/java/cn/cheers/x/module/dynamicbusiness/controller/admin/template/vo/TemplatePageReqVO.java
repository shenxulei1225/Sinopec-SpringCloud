package cn.cheers.x.module.dynamicbusiness.controller.admin.template.vo;

import cn.cheers.x.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字段模板分页查询请求 VO
 * 
 * @author yudao
 */
@Schema(description = "管理后台 - 字段模板分页查询请求")
@Data
@EqualsAndHashCode(callSuper = true)
public class TemplatePageReqVO extends PageParam {

    @Schema(description = "关键词（模糊匹配模板名称和描述）", example = "设备")
    private String keyword;

    @Schema(description = "业务类型编码", example = "equipment")
    private String entityTypeCode;

    @Schema(description = "模板状态（1-启用，0-禁用）", example = "1")
    private Integer status;
}
