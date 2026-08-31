package cn.cheers.x.module.dynamicbusiness.dal.mysql.action;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.action.ActionEnablementDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 动作启用表访问。
 *
 * <p><b>负责</b>：按 owner 查启用的动作 id。</p>
 * <p><b>禁止</b>：owner 无启用行时伪造「全库可用」。</p>
 */
@Mapper
public interface ActionEnablementMapper extends BaseMapperX<ActionEnablementDO> {

    default List<ActionEnablementDO> selectByOwner(String ownerKind, Long ownerId) {
        return selectList(new LambdaQueryWrapperX<ActionEnablementDO>()
                .eq(ActionEnablementDO::getOwnerKind, ownerKind)
                .eq(ActionEnablementDO::getOwnerId, ownerId)
                .orderByAsc(ActionEnablementDO::getId));
    }
}
