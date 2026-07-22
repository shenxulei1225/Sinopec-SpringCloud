package cn.iocoder.yudao.module.emergency.service.organization;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.emergency.controller.admin.organization.vo.abrole.ABRoleCreateReqVO;
import cn.iocoder.yudao.module.emergency.controller.admin.organization.vo.abrole.ABRolePageReqVO;
import cn.iocoder.yudao.module.emergency.controller.admin.organization.vo.abrole.ABRoleRespVO;
import cn.iocoder.yudao.module.emergency.controller.admin.organization.vo.abrole.ABRoleUpdateReqVO;

import java.util.List;

/**
 * 应急A/B角管理 Service 接口
 *
 * @author 芋道源码
 */
public interface EmergencyABRoleService {

    /**
     * 创建A/B角
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createABRole(ABRoleCreateReqVO createReqVO);

    /**
     * 更新A/B角
     *
     * @param updateReqVO 更新信息
     */
    void updateABRole(ABRoleUpdateReqVO updateReqVO);

    /**
     * 删除A/B角
     *
     * @param id 编号
     */
    void deleteABRole(Long id);

    /**
     * 切换A/B角
     *
     * @param roleCode 角色编号
     * @param reason 切换原因
     */
    void switchABRole(String roleCode, String reason);

    /**
     * 获得A/B角
     *
     * @param id 编号
     * @return A/B角
     */
    ABRoleRespVO getABRole(Long id);

    /**
     * 获得A/B角分页
     *
     * @param pageReqVO 分页查询
     * @return A/B角分页
     */
    PageResult<ABRoleRespVO> getABRolePage(ABRolePageReqVO pageReqVO);

    /**
     * 根据部门ID获取A/B角列表
     *
     * @param departmentId 部门ID
     * @return A/B角列表
     */
    List<ABRoleRespVO> getABRolesByDepartmentId(Long departmentId);
}
