package cn.cheers.x.facility.management.controller.admin.vo.site;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 站场类型响应 VO
 */
@Schema(description = "管理后台 - 站场类型响应 VO")
@Data
public class SiteTypeRespVO {

    @Schema(description = "类型ID", example = "1")
    private Long id;

    @Schema(description = "类型编码", example = "GAS_STATION")
    private String typeCode;

    @Schema(description = "类型名称", example = "加油站")
    private String typeName;

    @Schema(description = "类型描述", example = "用于加油服务的站场")
    private String description;

    @Schema(description = "排序号", example = "1")
    private Integer sortNo;

    @Schema(description = "状态：0-正常，1-停用", example = "0")
    private Integer status;

    @Schema(description = "备注", example = "这是加油站的描述")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}
