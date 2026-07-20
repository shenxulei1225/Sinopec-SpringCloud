package cn.iocoder.yudao.module.emergency.controller.admin.response.vo;

import cn.cheers.x.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 时间轴分页查询请求")
@Data
@EqualsAndHashCode(callSuper = true)
public class TimelinePageReqVO extends PageParam {

    @Schema(description = "排序方式：asc-正序（按创建时间升序），desc-倒序（按创建时间降序），默认为desc", example = "desc")
    private String orderBy = "desc";
}

















