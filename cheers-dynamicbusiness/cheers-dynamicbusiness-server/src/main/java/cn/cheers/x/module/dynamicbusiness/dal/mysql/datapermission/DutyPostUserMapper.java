package cn.cheers.x.module.dynamicbusiness.dal.mysql.datapermission;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datapermission.DutyPostUserDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/** 职务上的人员。只读写本表，不读写任职。 */
@Mapper
public interface DutyPostUserMapper extends BaseMapperX<DutyPostUserDO> {

    default List<DutyPostUserDO> selectListByDutyPostId(Long dutyPostId) {
        return selectList(new LambdaQueryWrapperX<DutyPostUserDO>()
                .eq(DutyPostUserDO::getDutyPostId, dutyPostId)
                .orderByAsc(DutyPostUserDO::getId));
    }

    default List<DutyPostUserDO> selectListByUserId(Long userId) {
        return selectList(new LambdaQueryWrapperX<DutyPostUserDO>()
                .eq(DutyPostUserDO::getUserId, userId));
    }

    default DutyPostUserDO selectByDutyAndUser(Long dutyPostId, Long userId) {
        return selectOne(new LambdaQueryWrapperX<DutyPostUserDO>()
                .eq(DutyPostUserDO::getDutyPostId, dutyPostId)
                .eq(DutyPostUserDO::getUserId, userId));
    }
}
