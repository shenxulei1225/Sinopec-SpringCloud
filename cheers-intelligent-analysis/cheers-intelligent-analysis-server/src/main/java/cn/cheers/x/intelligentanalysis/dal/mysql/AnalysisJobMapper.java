package cn.cheers.x.intelligentanalysis.dal.mysql;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.intelligentanalysis.dal.dataobject.AnalysisJobDO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 智能分析任务账。
 */
@Mapper
public interface AnalysisJobMapper extends BaseMapperX<AnalysisJobDO> {

    default AnalysisJobDO selectByJobNo(String jobNo) {
        return selectOne(new LambdaQueryWrapper<AnalysisJobDO>()
                .eq(AnalysisJobDO::getJobNo, jobNo));
    }

    default List<AnalysisJobDO> selectRecent(int limit) {
        return selectList(new LambdaQueryWrapper<AnalysisJobDO>()
                .orderByDesc(AnalysisJobDO::getCreateTime)
                .last("LIMIT " + Math.max(1, Math.min(limit, 500))));
    }
}
