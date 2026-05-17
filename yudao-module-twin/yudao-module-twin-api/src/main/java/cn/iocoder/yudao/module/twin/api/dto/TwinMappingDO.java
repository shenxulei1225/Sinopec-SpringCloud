package cn.iocoder.yudao.module.twin.api.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
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

    /** 1 = Facility ↔ ActorInstance */
    private Integer mappingType;

    /** 1 = 1:1 主绑定 */
    private Integer relationMode;

    private Long facilityId;

    private Long actorInstanceId;

    private Long sceneId;

    private String sceneCode;

    private String bindSource;

    /** 1=有效 0=失效 */
    private Integer status;

    private Integer version;

    private String remark;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> extJson;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private String creator;

    private String updater;

    private Boolean deleted;
}
