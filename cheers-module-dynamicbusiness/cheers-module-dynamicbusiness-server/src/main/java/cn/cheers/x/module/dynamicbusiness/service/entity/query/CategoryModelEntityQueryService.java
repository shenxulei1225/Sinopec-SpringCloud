package cn.cheers.x.module.dynamicbusiness.service.entity.query;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRespVO;

import java.util.List;

/**
 * 分类-模型-实体关联聚合查询服务
 *
 * <p>负责处理“从分类出发，聚合其下模型，再汇总模型下实体”的查询逻辑。
 * 该类属于关联查询聚合，不应放在核心 EntityService 中。</p>
 */
public interface CategoryModelEntityQueryService {

    /**
     * 根据分类查询指定业务类型下的实体树（聚合分类下的多个模型）。
     *
     * @param categoryId 分类ID
     * @param entityTypeCode 业务类型编码（用于筛选模型，并路由实体表）
     * @return 实体树（根节点列表）
     */
    List<EntityRespVO> getEntityTreeByCategory(Long categoryId, String entityTypeCode);

    /**
     * 根据分类分页查询指定业务类型下的实体列表（Pattern B 快路径）。
     *
     * <p>流程：分类(含子分类) -> 模型 -> 实体分页。</p>
     */
    PageResult<EntityRespVO> pageEntityByCategory(Long categoryId, String entityTypeCode,
                                                   String keyword, Integer pageNo, Integer pageSize);
}

