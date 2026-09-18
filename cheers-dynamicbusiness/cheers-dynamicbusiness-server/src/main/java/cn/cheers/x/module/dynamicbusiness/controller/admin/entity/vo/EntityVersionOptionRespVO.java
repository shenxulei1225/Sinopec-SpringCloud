package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.Instant;

/**
 * 通用实体版本下拉项。
 */
@Data
public class EntityVersionOptionRespVO {

    @Schema(description = "实体 id")
    private Long entityId;

    @Schema(description = "版本号（version_no）")
    private Integer versionNo;

    @Schema(description = "发布状态（PUBLISHED / UNPUBLISHED）")
    private String publishStatus;

    @Schema(description = "更新时间")
    private Instant updateTime;
}
