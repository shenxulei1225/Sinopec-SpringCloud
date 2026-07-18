package cn.iocoder.yudao.module.twin.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 场景映射概览")
@Data
public class TwinSceneMappingOverviewRespVO {

    private Long sceneId;
    private String sceneCode;
    private Integer totalMappings;
    private List<TwinMappingRespVO> mappings;
}
