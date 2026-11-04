package cn.cheers.x.module.dynamicbusiness.service.linkage;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 联动整体执行记录的数据访问。
 */
@Mapper
public interface LinkageExecutionMapper extends BaseMapperX<LinkageExecutionDO> {

    default LinkageExecutionDO selectByEventAndRule(String eventId, Long ruleId) {
        return selectOne(new LambdaQueryWrapperX<LinkageExecutionDO>()
                .eq(LinkageExecutionDO::getEventId, eventId)
                .eq(LinkageExecutionDO::getLinkageRuleId, ruleId)
                .eq(LinkageExecutionDO::getDeleted, false));
    }

    default List<LinkageExecutionDO> selectRecentBySource(String sourceType, long sourceId, int limit) {
        return selectList(new LambdaQueryWrapperX<LinkageExecutionDO>()
                .eq(LinkageExecutionDO::getSourceEntityTypeCode, sourceType)
                .eq(LinkageExecutionDO::getSourceEntityId, sourceId)
                .eq(LinkageExecutionDO::getDeleted, false)
                .orderByDesc(LinkageExecutionDO::getId)
                .last("LIMIT " + Math.max(1, limit)));
    }
}
