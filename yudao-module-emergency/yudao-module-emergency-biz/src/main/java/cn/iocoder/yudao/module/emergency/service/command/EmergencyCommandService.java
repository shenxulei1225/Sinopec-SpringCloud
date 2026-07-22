package cn.iocoder.yudao.module.emergency.service.command;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.emergency.controller.admin.command.vo.*;
import cn.iocoder.yudao.module.emergency.dal.dataobject.command.EmergencyCommandDO;

import jakarta.validation.Valid;

/**
 * 应急指令 Service 接口
 */
public interface EmergencyCommandService {

    /**
     * 创建指令
     *
     * @param createReqVO 创建指令请求VO
     * @return 指令编号
     */
    Long createCommand(@Valid EmergencyCommandCreateReqVO createReqVO);

    /**
     * 更新指令
     *
     * @param updateReqVO 更新指令请求VO
     */
    void updateCommand(@Valid EmergencyCommandUpdateReqVO updateReqVO);

    /**
     * 删除指令
     *
     * @param id 指令ID
     */
    void deleteCommand(Long id);

    /**
     * 获得指令
     *
     * @param id 指令ID
     * @return 指令
     */
    EmergencyCommandDO getCommand(Long id);

    /**
     * 获得指令分页
     *
     * @param pageReqVO 分页查询请求VO
     * @return 指令分页
     */
    PageResult<EmergencyCommandDO> getCommandPage(@Valid EmergencyCommandPageReqVO pageReqVO);

    /**
     * 更新指令状态
     *
     * @param id 指令ID
     * @param status 新状态
     */
    void updateCommandStatus(Long id, String status);

    /**
     * 终止指令
     *
     * @param id 指令ID
     * @param reason 终止原因
     */
    void terminateCommand(Long id, String reason);
}
