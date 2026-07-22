package cn.iocoder.yudao.module.emergency.dal.dataobject.plan;

import cn.iocoder.yudao.module.emergency.framework.mybatis.PostgreSQLJsonbTypeHandler;
import cn.iocoder.yudao.module.emergency.dal.dataobject.EmergencyBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;


/**
 * 应急预案附件 DO
 *
 * @author 芋道源码
 */
@TableName(value = "emergency_plan_attachment", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyPlanAttachmentDO extends EmergencyBaseDO {

    /**
     * 编号
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    /**
     * 关联预案ID
     */
    private Long planId;
    /**
     * 附件名称
     */
    private String name;
    /**
     * 附件类型
     *
     * 枚举 {@link cn.iocoder.yudao.module.emergency.enums.dict.EmergencyDictTypeConstants#EMERGENCY_PLAN_ATTACHMENT_TYPE}
     */
    private String type;
    /**
     * 附件内容 (JSONB)
     * 支持 List 或 Map 结构
     */
    @TableField(typeHandler = PostgreSQLJsonbTypeHandler.class)
    private Object content;

}







