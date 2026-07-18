package cn.iocoder.yudao.module.scene.platform.dal.dataobject.scene;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 设施与场景绑定 DO
 */
@TableName(value = "facility_scene_binding", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class FacilitySceneBindingDO extends TenantBaseDO {

    @TableId
    private Long id;

    /** 设施编号（动态业务 ent_facility.id） */
    private Long facilityId;

    /** 场景编码 */
    private String sceneCode;
}
