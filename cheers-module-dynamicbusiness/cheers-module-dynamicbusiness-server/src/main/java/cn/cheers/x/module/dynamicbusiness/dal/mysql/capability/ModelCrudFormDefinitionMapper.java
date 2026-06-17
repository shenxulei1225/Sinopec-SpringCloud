package cn.cheers.x.module.dynamicbusiness.dal.mysql.capability;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.capability.ModelCrudFormDefinitionDO;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import org.apache.ibatis.annotations.Mapper;

/**
 * 模型 CRUD 表单定义 Mapper。
 *
 * <p>说明：</p>
 * <ul>
 *   <li>面向 model_crud_form_definition 表；</li>
 *   <li>按 (businessTypeCode, modelId) 读取模型级表单定义；</li>
 *   <li>用于写端表单字段渲染，不与 business_capability 混存。</li>
 * </ul>
 */
@Mapper
public interface ModelCrudFormDefinitionMapper extends BaseMapperX<ModelCrudFormDefinitionDO> {

    /**
     * 按业务类型编码 + 模型编号查询 CRUD 表单定义。
     *
     * @param businessTypeCode 业务类型编码
     * @param modelId          模型编号
     * @return 模型表单定义；不存在时返回 null
     */
    default ModelCrudFormDefinitionDO selectByBusinessTypeAndModel(String businessTypeCode, Long modelId) {
        return selectOne(new LambdaQueryWrapperX<ModelCrudFormDefinitionDO>()
                .eq(ModelCrudFormDefinitionDO::getBusinessTypeCode, businessTypeCode)
                .eq(ModelCrudFormDefinitionDO::getModelId, modelId));
    }
}
