package cn.iocoder.yudao.module.emergency.dal.dataobject.contact;

import cn.iocoder.yudao.module.emergency.dal.dataobject.EmergencyBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 应急联络通讯录
 */
@TableName("emergency_contact")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyContactDO extends EmergencyBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 联系人编号（唯一标识）
     */
    private String contactCode;

    /**
     * 联系人姓名
     */
    private String contactName;

    /**
     * 联系人类型：internal/external
     */
    private String contactType;

    /**
     * 所属组织/机构
     */
    private String organization;

    /**
     * 部门
     */
    private String department;

    /**
     * 职位
     */
    private String position;

    /**
     * 电话
     */
    private String phone;

    /**
     * 手机
     */
    private String mobile;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 地址
     */
    private String address;

    /**
     * 应急级别：primary/secondary
     */
    private String emergencyLevel;

    /**
     * 是否启用
     */
    private Boolean isEnabled;

    /**
     * 备注
     */
    private String remark;
}



