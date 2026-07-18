package cn.cheers.x.module.dynamicbusiness.dal.dataobject.relation;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * Model 关联声明 DO
 * 
 * 业务含义：声明 Model 可以关联哪些业务类型。
 * 用于控制在字段管理中添加关联字段时的选择范围。
 * 
 * @author yudao
 */
@TableName("dynamic_model_relation_declaration")
@KeySequence("dynamic_model_relation_declaration_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelRelationDeclarationDO extends TenantBaseDO {

    /**
     * 声明ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * Model ID
     * 
     * 关联到 dynamic_model 表
     */
    private Long modelId;

    /**
     * 模型编码（迁移幂等键，对应 {@link cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO#getCode()}）
     */
    private String modelCode;

    /**
     * 可关联的业务类型编码
     * 
     * 声明该 Model 可以关联哪个业务类型
     */
    private String targetEntityType;
}
