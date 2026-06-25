package cn.cheers.x.module.platformresource.dal.mysql.view;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.platformresource.dal.dataobject.view.ViewConfigDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ViewConfigMapper extends BaseMapperX<ViewConfigDO> {

    default List<ViewConfigDO> selectList(String viewType, Boolean isTemplate, Long categoryId, Boolean onlyEnabled) {
        LambdaQueryWrapperX<ViewConfigDO> wrapper = new LambdaQueryWrapperX<ViewConfigDO>()
                .eqIfPresent(ViewConfigDO::getViewType, viewType)
                .eqIfPresent(ViewConfigDO::getIsTemplate, isTemplate)
                .eqIfPresent(ViewConfigDO::getCategoryId, categoryId);
        if (!Boolean.FALSE.equals(onlyEnabled)) {
            wrapper.eq(ViewConfigDO::getStatus, 1);
        }
        return selectList(wrapper
                .orderByAsc(ViewConfigDO::getSort)
                .orderByAsc(ViewConfigDO::getId));
    }

    default ViewConfigDO selectByCode(String viewCode) {
        return selectOne(new LambdaQueryWrapperX<ViewConfigDO>()
                .eq(ViewConfigDO::getViewCode, viewCode)
                .eq(ViewConfigDO::getStatus, 1)
                .last("LIMIT 1"));
    }

    default long selectCountByTemplateId(Long templateId) {
        return selectCount(new LambdaQueryWrapperX<ViewConfigDO>()
                .eq(ViewConfigDO::getIsTemplate, false)
                .eq(ViewConfigDO::getTemplateId, templateId));
    }
}
