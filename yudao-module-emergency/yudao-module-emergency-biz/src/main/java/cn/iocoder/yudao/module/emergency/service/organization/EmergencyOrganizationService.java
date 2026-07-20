package cn.iocoder.yudao.module.emergency.service.organization;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.emergency.controller.admin.organization.vo.OrganizationCreateReqVO;
import cn.iocoder.yudao.module.emergency.controller.admin.organization.vo.OrganizationUpdateReqVO;
import cn.iocoder.yudao.module.emergency.controller.admin.organization.vo.OrganizationRespVO;
import cn.iocoder.yudao.module.emergency.controller.admin.organization.vo.OrganizationPageReqVO;
import cn.iocoder.yudao.module.emergency.controller.admin.organization.vo.OrganizationMemberCreateReqVO;
import cn.iocoder.yudao.module.emergency.controller.admin.organization.vo.OrganizationMemberRespVO;

import java.util.List;

/**
 * 应急组织 Service 接口
 */
public interface EmergencyOrganizationService {

    /**
     * 创建应急组织
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createOrganization(OrganizationCreateReqVO createReqVO);

    /**
     * 更新应急组织
     *
     * @param updateReqVO 更新信息
     */
    void updateOrganization(OrganizationUpdateReqVO updateReqVO);

    /**
     * 删除应急组织
     *
     * @param id 编号
     */
    void deleteOrganization(Long id);

    /**
     * 获得应急组织
     *
     * @param id 编号
     * @return 应急组织
     */
    OrganizationRespVO getOrganization(Long id);

    /**
     * 获得应急组织分页
     *
     * @param pageReqVO 分页查询
     * @return 应急组织分页
     */
    PageResult<OrganizationRespVO> getOrganizationPage(OrganizationPageReqVO pageReqVO);

    /**
     * 获得应急组织列表
     *
     * @param orgType 组织类型
     * @return 应急组织列表
     */
    List<OrganizationRespVO> getOrganizationList(String orgType);

    /**
     * 添加组织成员
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long addOrganizationMember(OrganizationMemberCreateReqVO createReqVO);

    /**
     * 删除组织成员
     *
     * @param id 编号
     */
    void removeOrganizationMember(Long id);

    /**
     * 获得组织成员列表
     *
     * @param orgId 组织ID
     * @return 组织成员列表
     */
    List<OrganizationMemberRespVO> getOrganizationMemberList(Long orgId);
}
