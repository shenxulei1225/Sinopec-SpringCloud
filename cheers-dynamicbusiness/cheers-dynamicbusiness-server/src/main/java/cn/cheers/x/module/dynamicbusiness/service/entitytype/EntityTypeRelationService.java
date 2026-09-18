package cn.cheers.x.module.dynamicbusiness.service.entitytype;

import cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo.EntityTypeRelationCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo.EntityTypeRelationRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo.RelatableEntityTypeRespVO;

import java.util.List;

/**
 * 旧「业务类型关联」表的维护接口。
 *
 * <p>不再作为保存引用的门禁。运行时保存只认型号已分配引用字段声明的目标类型。</p>
 *
 * @author yudao
 */
public interface EntityTypeRelationService {

    /**
     * 创建 EntityType 关联（门禁许可）
     * 
     * @param reqVO 创建请求
     * @return 关联 ID
     */
    Long createRelation(EntityTypeRelationCreateReqVO reqVO);

    /**
     * 删除 EntityType 关联
     * 
     * @param id 关联 ID
     */
    void deleteRelation(Long id);

    /**
     * 获取 EntityType 关联详情
     * 
     * @param id 关联 ID
     * @return 关联详情
     */
    EntityTypeRelationRespVO getRelation(Long id);

    /**
     * 获取源 EntityType 的所有关联列表
     * 
     * @param sourceEntityTypeCode 源业务类型编码
     * @return 关联列表
     */
    List<EntityTypeRelationRespVO> getRelationsBySourceCode(String sourceEntityTypeCode);

    /**
     * 获取目标 EntityType 的所有关联列表
     * 
     * @param targetEntityTypeCode 目标业务类型编码
     * @return 关联列表
     */
    List<EntityTypeRelationRespVO> getRelationsByTargetCode(String targetEntityTypeCode);

    /**
     * 获取所有 EntityType 关联列表
     * 
     * @return 所有关联列表
     */
    List<EntityTypeRelationRespVO> getAllRelations();

    /**
     * 检查 EntityType 关联是否存在
     * 
     * @param sourceEntityTypeCode 源业务类型编码
     * @param targetEntityTypeCode 目标业务类型编码
     * @return 是否存在
     */
    boolean existsRelation(String sourceEntityTypeCode, String targetEntityTypeCode);

    /**
     * 获取可关联的目标业务类型列表
     * 
     * 支持通过编码或名称查询当前业务类型，并返回尚未建立关联的候选目标列表。
     * 
     * @param currentEntityTypeCode 当前业务类型编码
     * @param currentEntityTypeName 当前业务类型名称
     * @return 可关联的目标列表（RelatableEntityTypeRespVO 列表）
     */
    List<RelatableEntityTypeRespVO> getAvailableTargets(String currentEntityTypeCode, String currentEntityTypeName);

}
