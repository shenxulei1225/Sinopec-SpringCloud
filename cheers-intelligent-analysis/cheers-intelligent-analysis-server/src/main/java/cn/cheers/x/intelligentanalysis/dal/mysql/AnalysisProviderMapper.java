package cn.cheers.x.intelligentanalysis.dal.mysql;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.intelligentanalysis.dal.dataobject.AnalysisProviderDO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 智能分析服务接入表。
 */
@Mapper
public interface AnalysisProviderMapper extends BaseMapperX<AnalysisProviderDO> {

    default List<AnalysisProviderDO> selectAllOrdered() {
        return selectList(new LambdaQueryWrapper<AnalysisProviderDO>()
                .orderByAsc(AnalysisProviderDO::getName)
                .orderByAsc(AnalysisProviderDO::getId));
    }
}
