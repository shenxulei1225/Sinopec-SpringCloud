package cn.iocoder.yudao.module.emergency.service.command;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.emergency.controller.admin.command.vo.EmergencyCommandTemplateCreateReqVO;
import cn.iocoder.yudao.module.emergency.controller.admin.command.vo.EmergencyCommandTemplatePageReqVO;
import cn.iocoder.yudao.module.emergency.controller.admin.command.vo.EmergencyCommandTemplateUpdateReqVO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.command.EmergencyCommandTemplateDO;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 应急指令模板 Service 接口
 */
public interface EmergencyCommandTemplateService {

    /**
     * 创建指令模板
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createCommandTemplate(@Valid EmergencyCommandTemplateCreateReqVO createReqVO);

    /**
     * 更新指令模板
     *
     * @param updateReqVO 更新信息
     */
    void updateCommandTemplate(@Valid EmergencyCommandTemplateUpdateReqVO updateReqVO);

    /**
     * 删除指令模板
     *
     * @param id 编号
     */
    void deleteCommandTemplate(Long id);

    /**
     * 获得指令模板
     *
     * @param id 编号
     * @return 指令模板
     */
    EmergencyCommandTemplateDO getCommandTemplate(Long id);

    /**
     * 获得指令模板列表
     *
     * @param ids 编号
     * @return 指令模板列表
     */
    List<EmergencyCommandTemplateDO> getCommandTemplateList(List<Long> ids);

    /**
     * 获得指令模板分页
     *
     * @param pageReqVO 分页查询
     * @return 指令模板分页
     */
    PageResult<EmergencyCommandTemplateDO> getCommandTemplatePage(EmergencyCommandTemplatePageReqVO pageReqVO);

    /**
     * 获得指定分类的指令模板列表
     *
     * @param category 分类
     * @param stage 阶段
     * @return 指令模板列表
     */
    List<EmergencyCommandTemplateDO> getCommandTemplateListByCategory(String category, String stage);

    /**
     * 启用/禁用指令模板
     *
     * @param id 编号
     * @param enabled 是否启用
     */
    void enableCommandTemplate(Long id, Boolean enabled);

    /**
     * 增加模板使用次数
     *
     * @param id 编号
     */
    void increaseUsageCount(Long id);
}
