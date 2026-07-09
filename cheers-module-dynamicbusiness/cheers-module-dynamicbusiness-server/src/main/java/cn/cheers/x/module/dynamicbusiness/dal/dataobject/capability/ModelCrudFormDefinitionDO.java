package cn.cheers.x.module.dynamicbusiness.dal.dataobject.capability;

import cn.cheers.x.module.dynamicbusiness.framework.mybatis.JsonbStringTypeHandler;
import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.ibatis.type.JdbcType;

/**
 * 模型 CRUD 表单定义（model_crud_form_definition）数据对象。
 *
 * <p>职责：</p>
 * <ul>
 *   <li>保存某业务类型下、某模型的 CRUD 表单字段定义；</li>
 *   <li>用于写端（create/update/delete）表单结构渲染；</li>
 *   <li>与 business_capability 解耦，避免能力全集单表体积无限膨胀。</li>
 * </ul>
 *
 * <p>索引语义：</p>
 * <ul>
 *   <li>唯一键是 (entityTypeCode, modelId, tenantId)；</li>
 *   <li>按模型增量更新，不影响同业务下其他模型。</li>
 * </ul>
 */
@TableName(value = "model_crud_form_definition", autoResultMap = true)
@KeySequence("model_crud_form_definition_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelCrudFormDefinitionDO extends TenantBaseDO {

    /** 自增主键。仅数据库内部使用。 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 业务类型编码（entityTypeCode）。
     *
     * <p>表单定义的业务域边界，用于与能力全集/投影一致定位。</p>
     */
    @TableField("entity_type_code")
    private String entityTypeCode;

    /**
     * 模型编号（modelId）。
     *
     * <p>同一 entityTypeCode 下的细粒度模型索引。</p>
     */
    @TableField("model_id")
    private Long modelId;

    /**
     * CRUD 表单字段定义 JSON（crud_form_fields）。
     *
     * <p>使用 JSONB 持久化，字段结构由服务层统一生成。</p>
     */
    @TableField(value = "crud_form_fields", jdbcType = JdbcType.OTHER, typeHandler = JsonbStringTypeHandler.class)
    private String crudFormFields;

    /**
     * 定义版本号。
     *
     * <p>建议与同批 business_capability 重建版本一致，便于联调与问题追踪。</p>
     */
    @TableField("version")
    private Long version;
}
