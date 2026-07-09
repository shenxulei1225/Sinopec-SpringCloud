package cn.cheers.x.module.dynamicbusiness.controller.admin.relation.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 关联字段库 分页请求 VO
 */
@Schema(description = "管理后台 - 关联字段库分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class RelationFieldLibraryPageReqVO extends PageParam {

    @Schema(description = "关联业务类型编码", example = "personnel")
    private String refEntityType;

    @Schema(description = "关键字（搜索字段名称、编码、说明）", example = "负责人")
    private String keyword;
}
