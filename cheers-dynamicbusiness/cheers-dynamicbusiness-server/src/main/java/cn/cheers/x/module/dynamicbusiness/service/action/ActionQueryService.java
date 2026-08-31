package cn.cheers.x.module.dynamicbusiness.service.action;

import cn.cheers.x.module.dynamicbusiness.controller.admin.action.vo.ActionListItemRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.action.vo.ActionListReqVO;

import java.util.List;

/**
 * 动作库查询：按手段、按启用主体过滤。
 *
 * <p><b>负责</b>：列出动作定义供 SOP 编模板或按 owner 启用集收窄。</p>
 * <p><b>不负责</b>：启用行写入、SOP 树、绑定、检查业务。</p>
 * <p><b>禁止</b>：传了 owner 且启用为空时静默返回全库。</p>
 */
public interface ActionQueryService {

    /**
     * 列出动作。
     *
     * <ul>
     *   <li>未传 ownerKind+ownerId → 全库（可叠 executionMeans），供 SOP 模板编排选用</li>
     *   <li>传了 ownerKind+ownerId → 仅该 owner 启用的动作；启用为空 → 空列表</li>
     * </ul>
     */
    List<ActionListItemRespVO> listActions(ActionListReqVO reqVO);
}
