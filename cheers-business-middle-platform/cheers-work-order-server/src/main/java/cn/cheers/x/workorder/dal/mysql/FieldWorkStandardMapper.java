package cn.cheers.x.workorder.dal.mysql;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.workorder.dal.dataobject.FieldWorkStandardDO;
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

}
