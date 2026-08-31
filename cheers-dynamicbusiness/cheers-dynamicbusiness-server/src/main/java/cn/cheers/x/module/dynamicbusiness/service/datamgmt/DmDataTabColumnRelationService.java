package cn.cheers.x.module.dynamicbusiness.service.datamgmt;

import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmDataTabColumnRelationRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmDataTabColumnRelationSaveReqVO;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 栏间关系声明（读 + 标准写删）。
 * <p>
 * ## 删边只有两种标准触发；另有「改名端点」不删边
 * <ol>
 *   <li>{@link #saveRelations}：用户点「保存关系」——请求体 = 最终全集，不在体内的边删除。</li>
 *   <li>{@link #removeRelationsTouchingIdentities}：布局保存时<strong>确实删掉了栏行</strong>
 *       ——只删两端碰到这些作废列身份的边。</li>
 * </ol>
 * 布局行 tabId 变更：{@link #renameColumnIdentities}（只改名 from/to，不删边）。
 * 加栏 / 改显示名 / 调序 / 配置隐藏一律不删边。
 */
public interface DmDataTabColumnRelationService {

    List<DmDataTabColumnRelationRespVO> listByLayoutId(Long layoutId);

    List<DmDataTabColumnRelationRespVO> listByEntityTypeCode(String entityTypeCode);

    /**
     * 触发 1：全量替换本页栏间关系。权威是请求体里的 relations 列表。
     */
    void saveRelations(DmDataTabColumnRelationSaveReqVO reqVO);

    /**
     * 触发 2：删栏连带清边。入参必须是本次作废的列身份（由被删布局行算出），禁止无参全表猜测。
     *
     * @param layoutId            工作台布局实例
     * @param removedIdentities   已删除布局行对应的列身份；空则 no-op
     */
    void removeRelationsTouchingIdentities(Long layoutId, Collection<String> removedIdentities);

    /**
     * 布局行 tabId 变更时：只改名边上的 from/to，不删边。
     * 身份跟着底座类型走（方案 A）；禁止当成「删栏」清边。
     *
     * @param layoutId 工作台布局实例
     * @param fromTo   旧身份 → 新身份；同串或空则跳过
     */
    void renameColumnIdentities(Long layoutId, Map<String, String> fromTo);
}
