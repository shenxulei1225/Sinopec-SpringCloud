package cn.iocoder.yudao.module.emergency.dal.dataobject.organization;

import cn.iocoder.yudao.module.emergency.dal.dataobject.EmergencyBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 应急组织
 *
 * 按照数据模型.md中的定义实现
 */
@TableName("emergency_organization")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyOrganizationDO extends EmergencyBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 组织编号（唯一标识）
     */
    private String orgCode;

    /**
     * 组织名称
     */
    private String orgName;

    /**
     * 组织类型：leadership_group/office/work_group/expert_group/sub_unit
     */
    private String orgType;

    /**
     * 上级组织ID（构建组织树）
     */
    private Long parentId;

    /**
     * 组织职责描述
     */
    private String description;

    /**
     * 是否动态组织（现场工作组、专家组为动态组织）
     */
    private Boolean isDynamic;

    /**
     * 是否启用
     */
    private Boolean isEnabled;
}
