package cn.iocoder.yudao.module.twin.controller.admin.vo;

import cn.iocoder.yudao.module.facility.management.api.dto.FacilityRespDTO;
import cn.iocoder.yudao.module.scene.platform.api.dto.ActorInstanceSimpleRespDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Schema(description = "管理后台 - Twin 映射响应")
@Data
public class TwinMappingRespVO {

    private Long id;
    private String mappingCode;
    private Integer mappingType;
    private Integer relationMode;
    private Long facilityId;
    private Long actorInstanceId;
    private Long sceneId;
    private String sceneCode;
    private String bindSource;
    private Integer status;
    private Integer version;
    private String remark;
    private Map<String, Object> extJson;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private FacilityRespDTO facility;
    private ActorInstanceSimpleRespDTO actorInstance;
}
