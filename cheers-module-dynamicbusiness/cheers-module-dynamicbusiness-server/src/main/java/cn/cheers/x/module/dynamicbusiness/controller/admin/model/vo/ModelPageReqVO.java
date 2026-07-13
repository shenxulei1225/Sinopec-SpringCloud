package cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 业务模型分页查询请求 VO
 * 
 * @author yudao
 */
@Schema(description = "管理后台 - 业务模型分页查询请求")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ModelPageReqVO extends PageParam {

    @Schema(description = "业务类型编码", example = "equipment")
    private String entityTypeCode;

    @Schema(description = "是否包含子业务类型（true 时按业务树汇总查询）", example = "true")
    private Boolean includeChildren;

    @Schema(description = "关键词（模型名称、描述）", example = "灭火器")
    private String keyword;

    @Schema(description = "模型状态（1-启用，0-禁用）", example = "1")
    private Integer status;

    @Schema(description = "业务域 Scope（可选；SCOPED 入口下列表/创建时过滤）", example = "巡检")
    private String dataScope;
}

