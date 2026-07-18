package cn.cheers.x.module.dynamicbusiness.dal.dataobject.model;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 业务模型/品类 DO
 * 
 * 业务含义：定义"一类东西长什么样"，是字段规则和结构的承载者。
 * 例如「9kg 泡沫灭火器 A 型号」是一个 Model，该 Model 可能挂在「消防设备 / 灭火器」和「应急物资」两个分类下。
 * 
 * 层级关系：
 * <pre>
 * EntityType (业务类型)
 *     └── Model (模型/品类)
 *             └── Entity (实体/实例)
 * </pre>
 * 
 * 示例数据结构：
 * <pre>
 * 业务类型: task (任务管理)
 *     ├── Model: daily-task (日常任务)
 *     ├── Model: weekly-task (周任务)
 *     └── Model: maintenance-task (维护任务)
 * 
 * 业务类型: plan (计划管理)
 *     ├── Model: production-plan (生产计划)
 *     └── Model: maintenance-plan (维护计划)
 * 
 * 业务类型: equipment (设备管理)
 *     ├── Model: fire-extinguisher-9kg (9kg灭火器)
 *     └── Model: smoke-detector (烟雾探测器)
 * </pre>
 * 
 * 字段说明：
 * - code: Model 自身的唯一编码，如 daily-task、fire-extinguisher-9kg
 * - entityTypeCode: Model 所属的业务类型编码，如 task、plan、equipment
 *
 * 关于“基础字段/固定列字段（BASE）”：
 * - BASE 字段的“定义”不存放在 dynamic_model 表中，而是由业务类型固定列字段配置统一定义，并在模型维度自动继承展示
 * - 模型“拥有哪些字段/规则”由模型字段分配承载（dynamic_model_field_assignment 等），BASE 字段在返回时会标记为不可编辑/删除（仅限制字段定义，不限制实体实例的字段值编辑）
 * 
 * @author yudao
 */
@TableName("dynamic_model")
@KeySequence("dynamic_model_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelDO extends TenantBaseDO {

    /**
     * 模型ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 模型编码（全局唯一，系统自动生成）
     */
    private String code;

    /**
     * 模型名称（如"9kg 泡沫灭火器 A 型号"）
     * 同一租户内必须唯一
     */
    private String name;

    /**
     * 业务类型编码（用于区分不同业务领域）
     */
    private String entityTypeCode;

    /**
     * 业务域 Scope（与 SCOPED 数据类型入口对齐；NATIVE 全量入口可为空）
     */
    private String dataScope;

    /**
     * 模型描述
     */
    private String description;

    /**
     * 模型状态（1-启用，0-禁用）
     */
    private Integer status;

    /**
     * 模型在业务类型下的显示顺序（值越小越靠前）
     */
    private Integer sort;

    /**
     * 字段分组配置（JSON格式）
     * 
     * 格式：
     * {
     *   "groups": [
     *     {
     *       "id": "group-1",
     *       "name": "基础字段",
     *       "color": "#409eff",
     *       "sort": 1
     *     }
     *   ]
     * }
     * 
     * 分组信息作为模型配置的一部分存储，便于整体管理和迁移。
     */
    private String fieldGroupsConfig;

    
}




































