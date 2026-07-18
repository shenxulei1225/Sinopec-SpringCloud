package cn.cheers.x.scene.platform.dal.dataobject.component;

import cn.cheers.x.framework.mybatis.core.dataobject.BaseDO;
import cn.cheers.x.scene.platform.dal.dataobject.JsonStringTypeHandler;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName(value = "widget_component", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class WidgetComponentDO extends BaseDO {

    @TableId
    private Long id;

    private String componentCode;

    private String displayName = "Widget";

    private String widgetCode;

    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String uiConfigJson = "{}";

    private Boolean drawAtDesiredSize = Boolean.TRUE;

    private Boolean receiveHardwareInput = Boolean.FALSE;

    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String metadataJson = "{}";
}
