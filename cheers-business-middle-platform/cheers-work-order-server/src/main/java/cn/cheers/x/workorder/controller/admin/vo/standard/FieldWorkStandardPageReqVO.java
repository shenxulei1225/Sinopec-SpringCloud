package cn.cheers.x.workorder.controller.admin.vo.standard;

import cn.cheers.x.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 管理后台 - 现场作业标准分页查询 Request VO
 */
@Schema(description = "管理后台 - 现场作业标准分页查询 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class FieldWorkStandardPageReqVO extends PageParam {

    @Schema(description = "标准编码", example = "pump-monthly")
    private String code;

    @Schema(description = "标准名称", example = "离心泵")
    private String name;

    @Schema(description = "业务域范围", example = "inspection")
    private String scope;

    @Schema(description = "状态：0=草稿，1=已发布", example = "0")
    private Integer status;

}
