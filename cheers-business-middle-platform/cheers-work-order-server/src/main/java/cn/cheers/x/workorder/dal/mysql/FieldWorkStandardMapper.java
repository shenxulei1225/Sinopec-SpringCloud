package cn.cheers.x.workorder.dal.mysql;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.workorder.controller.admin.vo.standard.FieldWorkStandardPageReqVO;
import cn.cheers.x.workorder.dal.dataobject.FieldWorkStandardDO;
import cn.cheers.x.workorder.enums.FieldWorkStandardStatusEnum;
import org.apache.ibatis.annotations.Mapper;

/**
 * 现场作业标准 Mapper
 *
 * @author 工单标准服务
 */
@Mapper
public interface FieldWorkStandardMapper extends BaseMapperX<FieldWorkStandardDO> {

    /**
     * 按编码与版本查询标准
     *
     * @param code      标准编码
     * @param versionNo 版本号
     * @return 现场作业标准
     */
    default FieldWorkStandardDO selectByCodeAndVersion(String code, Integer versionNo) {
        return selectOne(new LambdaQueryWrapperX<FieldWorkStandardDO>()
                .eq(FieldWorkStandardDO::getCode, code)
                .eq(FieldWorkStandardDO::getVersionNo, versionNo));
    }

    /**
     * 查询指定编码下的最大版本号；无记录时返回 null
     *
     * @param code 标准编码
     * @return 最大版本号，或 null
     */
    default Integer selectMaxVersionNoByCode(String code) {
        FieldWorkStandardDO latest = selectOne(new LambdaQueryWrapperX<FieldWorkStandardDO>()
                .eq(FieldWorkStandardDO::getCode, code)
                .orderByDesc(FieldWorkStandardDO::getVersionNo)
                .last("LIMIT 1"));
        return latest == null ? null : latest.getVersionNo();
    }

    /**
     * 查询指定编码下最新已发布版本；无记录时返回 null
     *
     * @param code 标准编码
     * @return 最新已发布标准，或 null
     */
    default FieldWorkStandardDO selectLatestPublishedByCode(String code) {
        return selectOne(new LambdaQueryWrapperX<FieldWorkStandardDO>()
                .eq(FieldWorkStandardDO::getCode, code)
                .eq(FieldWorkStandardDO::getStatus, FieldWorkStandardStatusEnum.PUBLISHED.getStatus())
                .orderByDesc(FieldWorkStandardDO::getVersionNo)
                .last("LIMIT 1"));
    }

    /**
     * 分页查询现场作业标准
     *
     * @param reqVO 查询条件
     * @return 分页结果
     */
    default PageResult<FieldWorkStandardDO> selectPage(FieldWorkStandardPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<FieldWorkStandardDO>()
                .eqIfPresent(FieldWorkStandardDO::getCode, reqVO.getCode())
                .likeIfPresent(FieldWorkStandardDO::getName, reqVO.getName())
                .eqIfPresent(FieldWorkStandardDO::getScope, reqVO.getScope())
                .eqIfPresent(FieldWorkStandardDO::getStatus, reqVO.getStatus())
                .orderByDesc(FieldWorkStandardDO::getId));
    }

}
