package cn.iocoder.yudao.module.emergency.dal.mysql.command;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.emergency.dal.dataobject.command.EmergencyCommandTemplateDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 应急指令模板 Mapper
 */
@Mapper
public interface EmergencyCommandTemplateMapper extends BaseMapperX<EmergencyCommandTemplateDO> {

    default PageResult<EmergencyCommandTemplateDO> selectPage(String name, String category, String stage,
                                                             Boolean isEnabled, Boolean isSystem, Integer pageNo, Integer pageSize) {
        cn.cheers.x.framework.common.pojo.PageParam pageParam = new cn.cheers.x.framework.common.pojo.PageParam();
        pageParam.setPageNo(pageNo);
        pageParam.setPageSize(pageSize);
        return selectPage(pageParam,
                new LambdaQueryWrapperX<EmergencyCommandTemplateDO>()
                .likeIfPresent(EmergencyCommandTemplateDO::getName, name)
                .eqIfPresent(EmergencyCommandTemplateDO::getCategory, category)
                .eqIfPresent(EmergencyCommandTemplateDO::getStage, stage)
                .eqIfPresent(EmergencyCommandTemplateDO::getIsEnabled, isEnabled)
                .eqIfPresent(EmergencyCommandTemplateDO::getIsSystem, isSystem)
                .orderByDesc(EmergencyCommandTemplateDO::getUsageCount)
                .orderByDesc(EmergencyCommandTemplateDO::getCreateTime));
    }
}
