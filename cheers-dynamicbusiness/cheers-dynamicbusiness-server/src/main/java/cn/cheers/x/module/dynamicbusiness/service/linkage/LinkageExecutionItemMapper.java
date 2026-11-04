package cn.cheers.x.module.dynamicbusiness.service.linkage;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 联动方式每一对执行项的数据访问。
 */
@Mapper
public interface LinkageExecutionItemMapper extends BaseMapperX<LinkageExecutionItemDO> {

    default LinkageExecutionItemDO selectByActionExecutionId(Long actionExecutionId) {
        return selectOne(new LambdaQueryWrapperX<LinkageExecutionItemDO>()
                .eq(LinkageExecutionItemDO::getActionExecutionId, actionExecutionId)
                .eq(LinkageExecutionItemDO::getDeleted, false));
    }

    default List<LinkageExecutionItemDO> selectByLinkageExecutionId(Long linkageExecutionId) {
        return selectList(new LambdaQueryWrapperX<LinkageExecutionItemDO>()
                .eq(LinkageExecutionItemDO::getLinkageExecutionId, linkageExecutionId)
                .eq(LinkageExecutionItemDO::getDeleted, false)
                .orderByAsc(LinkageExecutionItemDO::getId));
    }
}
