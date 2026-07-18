package cn.cheers.x.scene.platform.dal.dataobject.actor;

import cn.cheers.x.framework.mybatis.core.dataobject.BaseDO;
import cn.cheers.x.scene.platform.dal.dataobject.JsonStringTypeHandler;
import cn.cheers.x.scene.platform.model.ComponentTree;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName(value = "actor", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class ActorDO extends BaseDO {

    @TableId
    private Long id;

    private String actorCode;

    private String actorName;

    private String actorClass;

    private String parentActorCode;

    private String actorCategory;

    private String engineProfile;

    private Boolean abstractFlag;

    private String lifecycleStatus;

    /**
     * Actor 默认组件树，对应 UE 的 SceneComponent 树。
     */
    @TableField(typeHandler = ComponentTreeTypeHandler.class)
    private ComponentTree componentTree;

    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String metadataJson;
}
