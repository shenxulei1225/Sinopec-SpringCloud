package cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 业务类型关联 DO(用户操作层)
 * 
 * 业务含义:存储 EntityType 之间的关联关系。
 * 
 * 两层关联架构:
 * 1. 第一层(用户操作层):EntityType 级别关联,存储在此表
 * 2. 第二层(系统执行层):Model 级别关联,存储在 dynamic_model_relation 表
 * 
 * 关联展开机制:
 * - 当用户建立 EntityType 关联时,系统自动展开为 Model 级别的关联
 * - 源 EntityType 下的所有 Model 都与目标 EntityType 下的所有 Model 建立关联
 * - 系统自动为源 Model 创建关联字段(ENTITY_REF 类型)
 * 
 * 示例:
 * - 用户操作:在"任务管理"中勾选关联"生产计划"
 * - 系统行为:
 *   1. 保存 EntityType 关联到此表
 *   2. 为"任务管理"下的所有 Model 创建到"生产计划"下所有 Model 的关联
 *   3. 自动创建关联字段(如 plan_id)
 * 
 * @author yudao
 */
@TableName("dynamic_entity_type_relation")
@KeySequence("dynamic_entity_type_relation_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntityTypeRelationDO extends TenantBaseDO {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 源业务类型编码
     * 
     * 建立关联的业务类型
     */
    private String sourceEntityTypeCode;

    /**
     * 目标业务类型编码
     * 
     * 被关联的业务类型
     */
    private String targetEntityTypeCode;



}
