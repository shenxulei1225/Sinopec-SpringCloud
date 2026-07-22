package cn.iocoder.yudao.module.emergency.dal.dataobject.command;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import cn.iocoder.yudao.module.emergency.framework.mybatis.PostgreSQLJsonbTypeHandler;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.util.List;

/**
 * 应急指令模板
 */
@TableName("emergency_command_template")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyCommandTemplateDO extends TenantBaseDO {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 模板名称
     */
    private String name;

    /**
     * 模板分类
     */
    private String category;

    /**
     * 标题模板
     */
    private String titleTemplate;

    /**
     * 内容模板
     */
    private String contentTemplate;

    /**
     * 适用场景（JSON数组）
     */
    @TableField(typeHandler = PostgreSQLJsonbTypeHandler.class)
    private List<Integer> applicableScenarios;

    /**
     * 适用阶段
     */
    private String stage;

    /**
     * 默认优先级
     */
    private String priority;

    /**
     * 是否系统模板
     */
    private Boolean isSystem;

    /**
     * 是否启用
     */
    private Boolean isEnabled;

    /**
     * 使用次数
     */
    private Integer usageCount;

    /**
     * 关联自定义配置表单ID（可选，仅创建时设置，更新时不允许修改）
     * 用于从模板创建指令时自动继承表单配置
     * 当设置时必须验证表单配置ID的有效性（存在且未删除）
     */
    private Long formId;
}
