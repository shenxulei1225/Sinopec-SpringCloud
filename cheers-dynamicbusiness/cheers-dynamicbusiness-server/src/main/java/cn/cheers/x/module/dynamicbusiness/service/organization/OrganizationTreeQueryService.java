package cn.cheers.x.module.dynamicbusiness.service.organization;

import cn.cheers.x.module.dynamicbusiness.controller.admin.organization.vo.OrganizationDepartmentRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.organization.vo.OrganizationTreeNodeRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.organization.vo.SolePlaceDepartmentsRespVO;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 组织架构页左侧的组织树，和中间一列的部门。
 * 负责：读组织机构目录的上下级，一次返回拼好的树。
 *       点中一个组织节点时，列出挂在这个节点上的部门，以及「所属组织」是这个节点的场站里的部门。
     * 不负责：展开收起、新建部门、改场站或部门的归属。不按登录人的数据范围裁树，部门和人员目录不声明归属。
     * 禁止：把场站放进组织树。场站部门只按场站上已填的「所属组织」列出，没填的站不替它挑节点。读时不补建任何列。
     * 场站目录只有一条启用记录时，组织列不显示，部门改按这一条的所属场站列出。
     */
public interface OrganizationTreeQueryService {

    List<OrganizationTreeNodeRespVO> getTree();

    /**
     * 点中组织节点后中间一列的部门：
     * - 直接挂在这个节点上的部门（所属组织是它），场站名为空。
     * - 「所属组织」是这个节点的场站里的部门，带场站名，页面显示成「站名 / 部门」。
     *   这是场站部门还没决定挂到哪一级之前的过渡显示，只读，不改部门的所属场站。
     */
    List<OrganizationDepartmentRespVO> listDepartments(Long organizationEntityId);

    /**
     * 场站目录恰好有一条启用记录时，返回这一条和挂在它上面的部门。
     * 零条或多条时返回 null，组织架构页继续显示组织列。
     * 不按所属组织过滤，也不补登记管理单元。
     */
    SolePlaceDepartmentsRespVO listSolePlaceDepartments();

    /**
     * 部门在列表里怎么写。
     * 部门在场站里：站名 / 部门。没有场站：只写部门名。
     * 只用于展示，不改部门自己的名称。
     */
    Map<Long, String> displayNames(Collection<Long> departmentEntityIds);
}
