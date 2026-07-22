package cn.iocoder.yudao.module.emergency.service.plan;

import cn.iocoder.yudao.module.emergency.controller.admin.plan.vo.PlanStepCopyReqVO;
import cn.iocoder.yudao.module.emergency.controller.admin.plan.vo.PlanStepCreateReqVO;
import cn.iocoder.yudao.module.emergency.controller.admin.plan.vo.PlanStepRespVO;
import cn.iocoder.yudao.module.emergency.controller.admin.plan.vo.PlanStepUpdateReqVO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.plan.EmergencyPlanStepDO;

import java.util.List;

/**
 * 预案步骤 Service 接口
 *
 * @author 芋道源码
 */
public interface PlanStepService {

    /**
     * 创建预案步骤
     * 支持创建根步骤或子步骤
     *
     * @param createReqVO 创建信息
     * @return 步骤ID
     */
    Long createStep(PlanStepCreateReqVO createReqVO);

    /**
     * 查询预案步骤树
     * 按plan_id和plan_level查询，返回树形结构
     *
     * @param planId 预案ID
     * @param planLevel 预案级别（可选，如果为null则查询所有级别）
     * @return 步骤树形列表
     */
    List<PlanStepRespVO> getStepTree(Long planId, String planLevel);

    /**
     * 获得预案步骤
     *
     * @param id 步骤ID
     * @return 步骤
     */
    EmergencyPlanStepDO getStep(Long id);

    /**
     * 更新预案步骤
     * 支持更新步骤属性、调整顺序、移动位置
     *
     * @param updateReqVO 更新信息
     */
    void updateStep(PlanStepUpdateReqVO updateReqVO);

    /**
     * 移动步骤到新的父节点
     *
     * @param id 步骤ID
     * @param targetParentId 目标父节点ID（null表示移动到根节点）
     */
    void moveStep(Long id, Long targetParentId);

    /**
     * 删除预案步骤
     * 支持级联删除或提升子步骤
     *
     * @param id 步骤ID
     * @param cascade 是否级联删除子步骤。true=级联删除，false=提升子步骤到父级
     */
    void deleteStep(Long id, boolean cascade);

    /**
     * 复制预案步骤（包括子树）
     * 支持复制到同一预案的其他位置或不同预案
     * 支持调整计划启动时间（相对于新父节点）
     *
     * @param copyReqVO 复制信息
     * @return 新步骤ID（根步骤的ID）
     */
    Long copyStep(PlanStepCopyReqVO copyReqVO);

    /**
     * 检测并修复数据完整性问题
     * 查找所有父步骤不存在的步骤，并将其parent_id设为null
     *
     * @return 修复的步骤数量
     */
    int fixOrphanedSteps();

    /**
     * 获取指定预案的活跃步骤ID列表
     * 用于前端刷新步骤选择器，避免引用已删除的步骤
     *
     * @param planId 预案ID
     * @return 活跃步骤ID列表
     */
    List<Long> getActiveStepIds(Long planId);

    /**
     * 清理测试数据 - 删除所有已删除的步骤记录
     * 注意：此方法仅用于清理测试过程中的垃圾数据，生产环境请谨慎使用
     *
     * @return 删除的步骤数量
     */
    int cleanupDeletedSteps();
}
















