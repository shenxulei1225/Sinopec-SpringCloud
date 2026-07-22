package cn.iocoder.yudao.module.emergency.dal.dataobject.guarantee;

import cn.iocoder.yudao.module.emergency.dal.dataobject.EmergencyBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 应急保障
 */
@TableName("emergency_guarantee")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyGuaranteeDO extends EmergencyBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 保障编号（唯一标识）
     */
    private String guaranteeCode;

    /**
     * 保障名称
     */
    private String guaranteeName;

    /**
     * 保障类型：personnel/material/technology/transportation/communication/medical等
     */
    private String guaranteeType;

    /**
     * 保障描述
     */
    private String description;

    /**
     * 保障状态：available/in_use/reserved/maintenance
     */
    private String status;

    /**
     * 联系人
     */
    private String contactPerson;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 保障位置
     */
    private String location;

    /**
     * 保障能力/容量
     */
    private String capacity;

    /**
     * 是否启用
     */
    private Boolean isEnabled;
}



