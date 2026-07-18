package cn.iocoder.yudao.module.scene.platform.dal.dataobject.component;

import cn.cheers.x.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.JsonStringTypeHandler;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName(value = "timeline_component", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class TimelineComponentDO extends BaseDO {

    @TableId
    private Long id;

    private String componentCode;

    private String displayName = "Timeline";

    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String timelineConfigJson = "{}";

    private Boolean autoPlay = Boolean.FALSE;

    private Boolean loop = Boolean.FALSE;

    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String metadataJson = "{}";
}
