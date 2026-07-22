package cn.cheers.x.twin.api.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import cn.cheers.x.framework.mybatis.core.type.JsonbMapTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Twin 映射主记录。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "twin_mapping", schema = "twin", autoResultMap = true)
public class TwinMappingDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String mappingCode;

    /** 1 = Facility ↔ ActorInstance；2 = EquipmentEntity ↔ ActorInstance */
    private Integer mappingType;

    /** 1 = 1:1 主绑定 */
    private Integer relationMode;

    /** 站场/设施作用域；type=1 时即绑定的设施；type=2 时为站场 scope */
    private Long facilityId;

    /**
     * 动态业务实体 id（mappingType=2 时为设备）。
     * type=1 可空。
     */
    private Long entityId;

    /** 动态业务实体类型编码，如 equipment；type=2 使用 */
    private String entityTypeCode;

    private Long actorInstanceId;

    private Long sceneId;

    private String sceneCode;

    private String bindSource;

    /** 1=有效 0=失效 */
    private Integer status;

    private Integer version;

    private String remark;

    @TableField(typeHandler = JsonbMapTypeHandler.class)
    private Map<String, Object> extJson;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private String creator;

    private String updater;

    private Boolean deleted;
}
