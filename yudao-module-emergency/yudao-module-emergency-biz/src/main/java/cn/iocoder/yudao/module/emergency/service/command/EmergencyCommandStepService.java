package cn.iocoder.yudao.module.emergency.service.command;

import cn.iocoder.yudao.module.emergency.controller.admin.command.vo.CommandStepCreateReqVO;
import cn.iocoder.yudao.module.emergency.controller.admin.command.vo.CommandStepRespVO;
import cn.iocoder.yudao.module.emergency.controller.admin.command.vo.CommandStepUpdateReqVO;
import cn.iocoder.yudao.module.emergency.controller.admin.command.vo.CommandStepCompleteReqVO;

import java.util.List;

/**
 * 指令步骤 Service 接口
 */
public interface EmergencyCommandStepService {

    /**
     * 创建指令步骤
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createCommandStep(CommandStepCreateReqVO createReqVO);

    /**
     * 更新指令步骤
     *
     * @param updateReqVO 更新信息
     */
    void updateCommandStep(CommandStepUpdateReqVO updateReqVO);

    /**
     * 删除指令步骤
     *
     * @param id 编号
     */
    void deleteCommandStep(Long id);

    /**
     * 获得指令步骤
     *
     * @param id 编号
     * @return 指令步骤
     */
    CommandStepRespVO getCommandStep(Long id);

    /**
     * 获得指令步骤列表
     *
     * @param commandId 指令ID
     * @return 指令步骤列表
     */
    List<CommandStepRespVO> getCommandStepList(Long commandId);

    /**
     * 启动指令步骤
     *
     * @param id 步骤ID
     */
    void startCommandStep(Long id);

    /**
     * 完成指令步骤
     *
     * @param id 步骤ID
     * @param completeReqVO 完成信息
     */
    void completeCommandStep(Long id, CommandStepCompleteReqVO completeReqVO);

    /**
     * 取消指令步骤
     *
     * @param id 步骤ID
     * @param reason 取消原因
     */
    void cancelCommandStep(Long id, String reason);

    /**
     * 标记超时
     *
     * @param id 步骤ID
     * @param timeoutReason 超时原因
     * @param handlingMeasures 处理措施
     */
    void markTimeout(Long id, String timeoutReason, String handlingMeasures);
}

