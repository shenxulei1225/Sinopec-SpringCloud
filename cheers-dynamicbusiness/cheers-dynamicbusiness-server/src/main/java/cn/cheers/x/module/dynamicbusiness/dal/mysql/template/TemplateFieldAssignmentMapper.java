package cn.cheers.x.module.dynamicbusiness.dal.mysql.template;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.template.TemplateFieldAssignmentDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 模板字段分配 Mapper
 * 
 * @author yudao
 */
@Mapper
public interface TemplateFieldAssignmentMapper extends BaseMapperX<TemplateFieldAssignmentDO> {

    /**
     * 根据模板ID查询字段分配列表
     */
    default List<TemplateFieldAssignmentDO> selectByTemplateId(Long templateId) {
        return selectList(new LambdaQueryWrapperX<TemplateFieldAssignmentDO>()
                .eq(TemplateFieldAssignmentDO::getTemplateId, templateId)
                .orderByAsc(TemplateFieldAssignmentDO::getSortOrder));
    }

    /**
     * 根据模板ID和字段ID查询
     */
    default TemplateFieldAssignmentDO selectByTemplateIdAndFieldId(Long templateId, Long fieldId) {
        return selectOne(new LambdaQueryWrapperX<TemplateFieldAssignmentDO>()
                .eq(TemplateFieldAssignmentDO::getTemplateId, templateId)
                .eq(TemplateFieldAssignmentDO::getFieldId, fieldId));
    }

    /**
     * 根据模板ID删除所有字段分配
     */
    default int deleteByTemplateId(Long templateId) {
        return delete(new LambdaQueryWrapperX<TemplateFieldAssignmentDO>()
                .eq(TemplateFieldAssignmentDO::getTemplateId, templateId));
    }

    /**
     * 根据字段ID查询使用该字段的模板数量
     */
    default Long countByFieldId(Long fieldId) {
        return selectCount(new LambdaQueryWrapperX<TemplateFieldAssignmentDO>()
                .eq(TemplateFieldAssignmentDO::getFieldId, fieldId));
    }
}
