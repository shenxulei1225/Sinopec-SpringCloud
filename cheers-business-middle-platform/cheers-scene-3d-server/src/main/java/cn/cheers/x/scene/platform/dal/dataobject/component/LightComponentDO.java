package cn.cheers.x.scene.platform.dal.dataobject.component;

import cn.cheers.x.framework.mybatis.core.dataobject.BaseDO;
import cn.cheers.x.scene.platform.dal.dataobject.JsonStringTypeHandler;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName(value = "light_component", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class LightComponentDO extends BaseDO {

    @TableId
    private Long id;

    private String componentCode;

    private String displayName = "Light";

    private Float intensity = 5000.0F;

    private String lightColor = "#FFFFFF";

    private Boolean castShadows = Boolean.TRUE;

    private String mobility = "Movable";

    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String metadataJson = "{}";
}
