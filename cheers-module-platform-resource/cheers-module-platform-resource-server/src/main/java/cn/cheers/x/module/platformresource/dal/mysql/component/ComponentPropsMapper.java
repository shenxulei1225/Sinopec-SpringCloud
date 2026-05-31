package cn.cheers.x.module.platformresource.dal.mysql.component;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.platformresource.dal.dataobject.component.ComponentPropsDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ComponentPropsMapper extends BaseMapperX<ComponentPropsDO> {

    default List<ComponentPropsDO> selectList(
            String componentCode, Boolean isTemplate, String dataSourceKey, Boolean onlyEnabled) {
        LambdaQueryWrapperX<ComponentPropsDO> wrapper = new LambdaQueryWrapperX<ComponentPropsDO>()
                .eqIfPresent(ComponentPropsDO::getComponentCode, componentCode)
                .eqIfPresent(ComponentPropsDO::getIsTemplate, isTemplate)
                .eqIfPresent(ComponentPropsDO::getDataSourceKey, dataSourceKey);
        if (!Boolean.FALSE.equals(onlyEnabled)) {
            wrapper.eq(ComponentPropsDO::getStatus, 1);
        }
        return selectList(wrapper
                .orderByAsc(ComponentPropsDO::getSort)
                .orderByAsc(ComponentPropsDO::getId));
    }

    default long selectCountByTemplateId(Long templateId) {
        return selectCount(new LambdaQueryWrapperX<ComponentPropsDO>()
                .eq(ComponentPropsDO::getIsTemplate, false)
                .eq(ComponentPropsDO::getTemplateId, templateId));
    }

    default ComponentPropsDO selectByIdNotDeleted(Long id) {
        return selectOne(new LambdaQueryWrapperX<ComponentPropsDO>()
                .eq(ComponentPropsDO::getId, id));
    }

    default ComponentPropsDO selectTemplateByDataSourceKey(String dataSourceKey, String componentCode) {
        return selectOne(new LambdaQueryWrapperX<ComponentPropsDO>()
                .eq(ComponentPropsDO::getIsTemplate, true)
                .eq(ComponentPropsDO::getDataSourceKey, dataSourceKey)
                .eq(ComponentPropsDO::getComponentCode, componentCode)
                .last("LIMIT 1"));
    }
}
