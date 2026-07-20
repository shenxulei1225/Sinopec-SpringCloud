package cn.iocoder.yudao.module.emergency.dal.dataobject.guarantee;

import cn.iocoder.yudao.module.emergency.dal.dataobject.EmergencyBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 保障资源关联
 */
@TableName("emergency_guarantee_resource")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyGuaranteeResourceDO extends EmergencyBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 保障ID
     */
    private Long guaranteeId;

    /**
     * 资源ID（关联resource_pool表）
     */
    private Long resourceId;

    /**
     * 资源类型
     */
    private String resourceType;

    /**
     * 数量
     */
    private Integer quantity;
}



