// cspell:ignore spawnable
package cn.cheers.x.scene.platform.dal.dataobject.component;

import cn.cheers.x.framework.mybatis.core.dataobject.BaseDO;
import cn.cheers.x.scene.platform.dal.dataobject.JsonStringTypeHandler;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName(value = "component", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class ComponentDO extends BaseDO {

    @TableId
    private Long id;

    private String componentCode;

    private String componentName;

    private String displayName;

    private String componentClass;

    private String componentCategory;

    private String parentComponentCode;

    private String engineProfile;

    private Boolean abstractFlag;

    private Boolean editableFlag;

    private Boolean spawnableFlag;

    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String defaultJson;

    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String propertySchemaJson;

    private String lifecycleStatus;

    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String metadataJson;
}
