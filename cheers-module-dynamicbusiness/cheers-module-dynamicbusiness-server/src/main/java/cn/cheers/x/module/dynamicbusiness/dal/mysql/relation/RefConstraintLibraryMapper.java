package cn.cheers.x.module.dynamicbusiness.dal.mysql.relation;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.controller.admin.relation.vo.RefConstraintLibraryPageReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.relation.RefConstraintLibraryDO;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Ref 约束器库 Mapper
 */
@Mapper
public interface RefConstraintLibraryMapper extends BaseMapperX<RefConstraintLibraryDO> {

    default List<RefConstraintLibraryDO> selectByBusinessType(String businessTypeCode, String refTargetType) {
        return selectList(new LambdaQueryWrapperX<RefConstraintLibraryDO>()
                .eq(RefConstraintLibraryDO::getBusinessTypeCode, businessTypeCode)
                .eqIfPresent(RefConstraintLibraryDO::getRefTargetType, refTargetType)
                .eq(RefConstraintLibraryDO::getStatus, CommonStatusEnum.ENABLE.getStatus())
                .orderByAsc(RefConstraintLibraryDO::getSort)
                .orderByAsc(RefConstraintLibraryDO::getConstraintName));
    }

    default boolean existsEnabled(String businessTypeCode, String refTargetType, String constraintType) {
        return selectCount(new LambdaQueryWrapperX<RefConstraintLibraryDO>()
                .eq(RefConstraintLibraryDO::getBusinessTypeCode, businessTypeCode)
                .eqIfPresent(RefConstraintLibraryDO::getRefTargetType, refTargetType)
                .eq(RefConstraintLibraryDO::getConstraintType, constraintType)
                .eq(RefConstraintLibraryDO::getStatus, CommonStatusEnum.ENABLE.getStatus())) > 0;
    }

    default PageResult<RefConstraintLibraryDO> selectPage(RefConstraintLibraryPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<RefConstraintLibraryDO>()
                .eqIfPresent(RefConstraintLibraryDO::getBusinessTypeCode, reqVO.getBusinessTypeCode())
                .eqIfPresent(RefConstraintLibraryDO::getRefTargetType, reqVO.getRefTargetType())
                .eqIfPresent(RefConstraintLibraryDO::getConstraintType, reqVO.getConstraintType())
                .eqIfPresent(RefConstraintLibraryDO::getStatus, reqVO.getStatus())
                .and(StringUtils.isNotBlank(reqVO.getConstraintName()), q -> q
                        .like(RefConstraintLibraryDO::getConstraintName, reqVO.getConstraintName()))
                .orderByAsc(RefConstraintLibraryDO::getSort)
                .orderByAsc(RefConstraintLibraryDO::getConstraintName));
    }

    default List<RefConstraintLibraryDO> selectAll() {
        return selectList(new LambdaQueryWrapperX<RefConstraintLibraryDO>()
                .orderByAsc(RefConstraintLibraryDO::getSort)
                .orderByAsc(RefConstraintLibraryDO::getConstraintName));
    }
}
