package cn.cheers.x.module.dynamicbusiness.service.category.relation;

import cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo.CategoryCategoryAssociationRespVO;

import java.util.List;

/**
 * 分类—分类跨种类关联。
 *
 * <p>边界：只维护关联与 ID 序列；同种类层级须用 {@code parent_id}，不得写入本表。</p>
 */
public interface CategoryCategoryRelationService {

    CategoryCategoryAssociationRespVO associate(Long hostCategoryId, Long memberCategoryId,
                                                String hostCategoryTypeCode, String memberCategoryTypeCode);

    /**
     * 解除宿主与成员的关联。
     *
     * @param includeDescendantMembers true：成员含子树（自身 + 子孙上与该宿主的边一并解除），
     *                                 与浏览「点上级含下级」对称；false：仅精确成员 id
     */
    CategoryCategoryAssociationRespVO disassociate(Long hostCategoryId, Long memberCategoryId,
                                                   String hostCategoryTypeCode, String memberCategoryTypeCode,
                                                   boolean includeDescendantMembers);

    boolean existsRelation(Long hostCategoryId, Long memberCategoryId,
                           String hostCategoryTypeCode, String memberCategoryTypeCode);

    CategoryCategoryAssociationRespVO batchAssociateMembersToHost(Long hostCategoryId, List<Long> memberCategoryIds,
                                                                  String hostCategoryTypeCode,
                                                                  String memberCategoryTypeCode);

    /**
     * 批量排除成员：对每个要排除的成员根，去掉其自身/下级挂靠，并对覆盖它的祖先行做
     * 「拆分重挂」（删祖先、补挂除排除支以外的兄弟），使排除节点在含子树展示下不再出现。
     */
    CategoryCategoryAssociationRespVO batchDisassociateMembersFromHost(Long hostCategoryId,
                                                                       List<Long> memberCategoryIds,
                                                                       String hostCategoryTypeCode,
                                                                       String memberCategoryTypeCode);

    /**
     * 某宿主下挂靠的成员分类 ID（按 sort、id；成员 id 去重保序）。
     *
     * @param includeDescendantHosts true：宿主含子树（自身 + 全部子孙宿主上的挂靠一并汇总），
     *                               与点分类查型号/实体「含下级」一致；false：仅精确匹配该宿主 id
     */
    List<Long> listMemberCategoryIdsByHost(Long hostCategoryId, String hostCategoryTypeCode,
                                           String memberCategoryTypeCode,
                                           boolean includeDescendantHosts);

    /**
     * 某成员被哪些宿主挂靠的宿主分类 ID。
     *
     * @param hostCategoryTypeCode 可选；非空时只返回该种类宿主
     */
    List<Long> listHostCategoryIdsByMember(Long memberCategoryId, String memberCategoryTypeCode,
                                           String hostCategoryTypeCode);
}
