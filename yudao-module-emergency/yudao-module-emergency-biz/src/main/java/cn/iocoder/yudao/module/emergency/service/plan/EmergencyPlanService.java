package cn.iocoder.yudao.module.emergency.service.plan;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.emergency.controller.admin.plan.vo.EmergencyPlanCreateReqVO;
import cn.iocoder.yudao.module.emergency.controller.admin.plan.vo.EmergencyPlanRespVO;
import cn.iocoder.yudao.module.emergency.controller.admin.plan.vo.EmergencyPlanUpdateReqVO;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 应急预案 Service 接口
 *
 * @author 芋道源码
 */
public interface EmergencyPlanService {

    /**
     * 创建应急预案
     * 包括步骤和附件
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createEmergencyPlan(@Valid EmergencyPlanCreateReqVO createReqVO);

    /**
     * 更新应急预案
     * 包括步骤和附件
     *
     * @param updateReqVO 更新信息
     */
    void updateEmergencyPlan(@Valid EmergencyPlanUpdateReqVO updateReqVO);

    /**
     * 删除应急预案
     *
     * @param id 编号
     */
    void deleteEmergencyPlan(Long id);

    /**
     * 获得应急预案
     *
     * @param id 编号
     * @return 应急预案
     */
    EmergencyPlanRespVO getEmergencyPlan(Long id);

    /**
     * 获得应急预案分页
     * 支持按分组筛选（planGroupId=null表示查询未分组预案）
     *
     * @param pageReqVO 分页查询（包含筛选条件）
     * @return 应急预案分页
     */
    PageResult<EmergencyPlanRespVO> getEmergencyPlanPage(cn.iocoder.yudao.module.emergency.controller.admin.plan.vo.EmergencyPlanPageReqVO pageReqVO);

    /**
     * 根据响应级别推荐应急预案
     * 从预案的plan_levels数组中查找包含该响应级别的预案
     * 排除plan_levels为NULL或空数组的预案（自动推荐场景）
     * 允许手动选择没有plan_levels的预案
     *
     * @param responseLevel 响应级别（I/II/III/IV/V）
     * @param planType 预案类型（可选，用于进一步筛选）
     * @return 推荐的预案列表
     */
    List<EmergencyPlanRespVO> recommendPlans(String responseLevel, Integer planType);

}

