package cn.cheers.x.module.dynamicbusiness.service.organization;

import cn.cheers.x.module.dynamicbusiness.controller.admin.organization.vo.OrganizationDepartmentRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.organization.vo.OrganizationTreeNodeRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.organization.vo.SolePlaceDepartmentsRespVO;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 组织架构页左侧的组织树，和中间一列的部门。
 * 负责：读组织架构这本数据目录的上下级，一次返回拼好的树。
 *       点中一个组织节点时，列出挂在这个节点上的部门，以及「所属组织」是这个节点的场站里的部门。
 * 不负责：管理单元登记、展开收起、新建部门、改场站或部门的归属。不按登录人的数据范围裁树。
 * 禁止：用管理单元登记决定这棵树读哪本目录。禁止把场站放进组织树。读时不补建任何列。
 * 场站目录只有一条启用记录时，组织列不显示，部门改按这一条的所属场站列出。
 */
public interface OrganizationTreeQueryService {

    /** 组织架构页左侧。固定读组织架构目录，不看管理单元登记。 */
    List<OrganizationTreeNodeRespVO> getTree();

    /**
     * 把一本有上下级的目录拼成树。
     * 组织架构页不调用这方法。管理单元页传入自己登记的目录编码。
     * 目录编码为空时返回空树。不负责判断这本目录是不是管理单元目录。
     */
    List<OrganizationTreeNodeRespVO> listParentChild(String registryCode);

    /**
     * 点中组织节点后中间一列的部门：
     * - 直接挂在这个节点上的部门（所属组织是它），场站名为空。
     * - 「所属组织」是这个节点的场站里的部门：返回 facilityId/facilityName，前端按站分组行展示，部门名仍是短名。
     */
    List<OrganizationDepartmentRespVO> listDepartments(Long organizationEntityId);

    /**
     * 场站目录恰好有一条启用记录时，返回这一条和挂在它上面的部门。
     * 零条或多条时返回 null，组织架构页继续显示组织列。
     * 不按所属组织过滤，也不补登记管理单元。
     */
    SolePlaceDepartmentsRespVO listSolePlaceDepartments();

    /**
     * 部门在引用展示里写什么：只返回部门短名，不叠场站前缀。
     */
    Map<Long, String> displayNames(Collection<Long> departmentEntityIds);
}
