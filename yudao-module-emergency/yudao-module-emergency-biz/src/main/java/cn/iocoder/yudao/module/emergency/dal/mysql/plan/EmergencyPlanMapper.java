package cn.iocoder.yudao.module.emergency.dal.mysql.plan;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.framework.common.pojo.PageParam;
import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.emergency.controller.admin.plan.vo.EmergencyPlanPageReqVO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.plan.EmergencyPlanDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EmergencyPlanMapper extends BaseMapperX<EmergencyPlanDO> {

    default PageResult<EmergencyPlanDO> selectPage(PageParam pageReqVO) {
        return selectPage(pageReqVO, new LambdaQueryWrapperX<EmergencyPlanDO>()
                .orderByDesc(EmergencyPlanDO::getId));
    }

    default PageResult<EmergencyPlanDO> selectPage(EmergencyPlanPageReqVO pageReqVO) {
        LambdaQueryWrapperX<EmergencyPlanDO> queryWrapper = new LambdaQueryWrapperX<EmergencyPlanDO>()
                .likeIfPresent(EmergencyPlanDO::getPlanNo, pageReqVO.getPlanNo())
                .likeIfPresent(EmergencyPlanDO::getPlanName, pageReqVO.getPlanName())
                .eqIfPresent(EmergencyPlanDO::getPlanType, pageReqVO.getPlanType())
                .eqIfPresent(EmergencyPlanDO::getStatus, pageReqVO.getStatus())
                .orderByDesc(EmergencyPlanDO::getId);
        
        // 处理 planGroupId 筛选（支持 null 查询未分组预案）
        if (pageReqVO.getPlanGroupId() != null) {
            if (pageReqVO.getPlanGroupId() == -1L) {
                // -1 表示查询未分组预案（plan_group_id IS NULL）
                queryWrapper.isNull(EmergencyPlanDO::getPlanGroupId);
            } else {
                queryWrapper.eq(EmergencyPlanDO::getPlanGroupId, pageReqVO.getPlanGroupId());
            }
        }
        
        return selectPage(pageReqVO, queryWrapper);
    }

    /**
     * 根据响应级别推荐应急预案
     * 使用PostgreSQL的JSONB查询功能：plan_levels @> '["I"]'::jsonb
     * 排除plan_levels为NULL或空数组的预案
     *
     * @param responseLevel JSON数组字符串，如 '["I"]'（由Service层构建）
     * @param planType 预案类型（可选）
     * @return 推荐的预案列表
     */
    List<EmergencyPlanDO> selectRecommendedPlans(@Param("responseLevel") String responseLevel,
                                                   @Param("planType") Integer planType);
}


