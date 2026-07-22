package cn.cheers.x.twin.dal.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import cn.cheers.x.framework.mybatis.core.type.JsonbMapTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@TableName(value = "twin_mapping_history", schema = "twin", autoResultMap = true)
public class TwinMappingHistoryDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long mappingId;

    private String operationType;

    private Integer mappingType;

    private Integer relationMode;

    private Long facilityId;

    private Long entityId;

    private String entityTypeCode;

    private Long actorInstanceId;

    private Long sceneId;

    private String sceneCode;

    private String bindSource;

    private String operationRemark;

    private Long operatorId;

    private String operatorName;

    private LocalDateTime occurredAt;

    @TableField(typeHandler = JsonbMapTypeHandler.class)
    private Map<String, Object> snapshotJson;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private String creator;

    private String updater;

    private Boolean deleted;
}
