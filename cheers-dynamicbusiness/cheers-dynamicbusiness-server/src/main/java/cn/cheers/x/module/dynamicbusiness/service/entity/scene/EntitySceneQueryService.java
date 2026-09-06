package cn.cheers.x.module.dynamicbusiness.service.entity.scene;

import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.CategoryIdGroupReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntitySceneQueryRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.FieldFilterReqVO;
import cn.cheers.x.module.dynamicbusiness.enums.entity.EntityQueryScene;

import java.util.List;

/**
 * 实体按场景查询编排。
 *
 * <p><b>管什么</b>：数据页/工作台按 scene 查实体列表、树、分类绑定实体、型号实体树；
 * 分类范围展开、候选 id 过滤排序、表内直分页与结果 LIGHT/FULL。</p>
 * <p><b>不管什么</b>：实体创建/更新/删除、关联块组装（associations）、批量改分类、排序保存。</p>
 * <p><b>禁止</b>：在读路径补写权威字段；为详情场景私自再实现一套 get；把 CRUD 写逻辑塞进本类。</p>
 */
public interface EntitySceneQueryService {

    EntitySceneQueryRespVO queryEntities(EntityQueryScene scene, String resultShape, String resultDetail,
            String categoryTypeCode, String entityTypeCode,
            List<Long> modelIds, String modelEntityTypeCode, List<Long> categoryIds,
            List<CategoryIdGroupReqVO> categoryIdGroups, String categoryViaRefPathCode,
            String categoryFilterMode,
            Long entityId, Long rootEntityId, String entitySourceEntityType,
            Integer pageNo, Integer pageSize, String keyword, String domain,
            List<FieldFilterReqVO> filters, String orderByColumn, Boolean isAsc,
            List<String> searchFieldCodes);

    EntityRespVO getCategoryLinkedEntity(Long categoryId, String entityTypeCode);

    List<EntityRespVO> getEntityTreeByModelId(String entityTypeCode, Long modelId);

    List<Long> listDistinctModelIdsByCategoryScope(String entityTypeCode,
                                                   List<Long> categoryIds,
                                                   List<CategoryIdGroupReqVO> categoryIdGroups,
                                                   String categoryTypeCode,
                                                   String domain,
                                                   Boolean includeDescendants);
}
