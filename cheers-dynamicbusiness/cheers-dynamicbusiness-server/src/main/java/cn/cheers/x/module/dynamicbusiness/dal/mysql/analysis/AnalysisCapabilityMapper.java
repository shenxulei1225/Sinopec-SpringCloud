package cn.cheers.x.module.dynamicbusiness.dal.mysql.analysis;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.analysis.AnalysisCapabilityDO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 平台智能分析能力目录。
 */
@Mapper
public interface AnalysisCapabilityMapper extends BaseMapperX<AnalysisCapabilityDO> {

    default List<AnalysisCapabilityDO> selectAllOrdered() {
        return selectList(new LambdaQueryWrapper<AnalysisCapabilityDO>()
                .orderByAsc(AnalysisCapabilityDO::getName)
                .orderByAsc(AnalysisCapabilityDO::getId));
    }

    default List<AnalysisCapabilityDO> selectEnabled() {
        return selectList(new LambdaQueryWrapper<AnalysisCapabilityDO>()
                .eq(AnalysisCapabilityDO::getEnabled, true)
                .orderByAsc(AnalysisCapabilityDO::getName)
                .orderByAsc(AnalysisCapabilityDO::getId));
    }
}
