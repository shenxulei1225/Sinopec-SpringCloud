package cn.iocoder.yudao.module.twin.api.dto;

import cn.iocoder.yudao.module.scene.platform.api.dto.ActorInstanceSimpleRespDTO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class TwinMappingRespDTO {

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
    private ActorInstanceSimpleRespDTO actorInstance;
}
