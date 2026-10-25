package cn.cheers.x.module.dynamicbusiness.service.organization;

import cn.cheers.x.module.dynamicbusiness.controller.admin.organization.vo.OrganizationDepartmentRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.organization.vo.OrganizationTreeNodeRespVO;

import java.util.List;

/**
 * 组织架构页左侧的组织树。
 * 负责：读运营区域分类和已有的区域—场站关联，一次返回拼好的树。
 *       按所属公司列出挂在这家公司或分公司上的部门。
 * 不负责：展开收起、新建部门、改场站的管线分类。
 * 禁止：关联缺失时替场站指定一个区域。禁止把区域编号当作所属场站去查部门。
 */
public interface OrganizationTreeQueryService {

    List<OrganizationTreeNodeRespVO> getTree();

    /** 所属公司是这一家、所属场站空着的部门。 */
    List<OrganizationDepartmentRespVO> listCompanyDepartments(Long companyEntityId);
}
