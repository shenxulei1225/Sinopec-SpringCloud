package cn.iocoder.yudao.module.emergency.service.guarantee;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.emergency.controller.admin.guarantee.vo.*;

/**
 * 应急保障 Service 接口
 */
public interface EmergencyGuaranteeService {

    /**
     * 创建应急保障
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createGuarantee(GuaranteeCreateReqVO createReqVO);

    /**
     * 更新应急保障
     *
     * @param updateReqVO 更新信息
     */
    void updateGuarantee(GuaranteeUpdateReqVO updateReqVO);

    /**
     * 删除应急保障
     *
     * @param id 编号
     */
    void deleteGuarantee(Long id);

    /**
     * 获得应急保障
     *
     * @param id 编号
     * @return 应急保障
     */
    GuaranteeRespVO getGuarantee(Long id);

    /**
     * 获得应急保障分页
     *
     * @param pageReqVO 分页查询
     * @return 应急保障分页
     */
    PageResult<GuaranteeRespVO> getGuaranteePage(GuaranteePageReqVO pageReqVO);

    /**
     * 添加保障资源
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long addGuaranteeResource(GuaranteeResourceCreateReqVO createReqVO);

    /**
     * 删除保障资源
     *
     * @param id 编号
     */
    void removeGuaranteeResource(Long id);
}



