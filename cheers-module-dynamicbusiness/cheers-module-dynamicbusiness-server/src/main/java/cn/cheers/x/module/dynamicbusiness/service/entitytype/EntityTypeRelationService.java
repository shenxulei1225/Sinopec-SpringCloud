package cn.cheers.x.module.dynamicbusiness.service.businesstype;

import cn.cheers.x.module.dynamicbusiness.controller.admin.businesstype.vo.BusinessTypeRelationCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.businesstype.vo.BusinessTypeRelationRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.businesstype.vo.RelatableBusinessTypeRespVO;

import java.util.List;

/**
 * BusinessType 关联服务接口
 * 
 * 业务含义：管理 BusinessType 之间的关联关系（双向门禁许可关系）。
 * 
 * 关联机制：
 * 1. 业务关联作为“门禁”：必须先建立业务关联，模型才允许添加引用字段。
 * 2. 不自动展开：建立关联后，不会自动在所有模型中创建字段，避免字段爆炸。
 * 3. 实体可见性：由模型管理按需添加字段后，实体表单才可见。
 * 
 * @author yudao
 */
public interface BusinessTypeRelationService {

    /**
     * 创建 BusinessType 关联（门禁许可）
     * 
     * @param reqVO 创建请求
     * @return 关联 ID
     */
    Long createRelation(BusinessTypeRelationCreateReqVO reqVO);

    /**
     * 删除 BusinessType 关联
     * 
     * @param id 关联 ID
     */
    void deleteRelation(Long id);

    /**
     * 获取 BusinessType 关联详情
     * 
     * @param id 关联 ID
     * @return 关联详情
     */
    BusinessTypeRelationRespVO getRelation(Long id);

    /**
     * 获取源 BusinessType 的所有关联列表
     * 
     * @param sourceBusinessTypeCode 源业务类型编码
     * @return 关联列表
     */
    List<BusinessTypeRelationRespVO> getRelationsBySourceCode(String sourceBusinessTypeCode);

    /**
     * 获取目标 BusinessType 的所有关联列表
     * 
     * @param targetBusinessTypeCode 目标业务类型编码
     * @return 关联列表
     */
    List<BusinessTypeRelationRespVO> getRelationsByTargetCode(String targetBusinessTypeCode);

    /**
     * 获取所有 BusinessType 关联列表
     * 
     * @return 所有关联列表
     */
    List<BusinessTypeRelationRespVO> getAllRelations();

    /**
     * 检查 BusinessType 关联是否存在
     * 
     * @param sourceBusinessTypeCode 源业务类型编码
     * @param targetBusinessTypeCode 目标业务类型编码
     * @return 是否存在
     */
    boolean existsRelation(String sourceBusinessTypeCode, String targetBusinessTypeCode);

    /**
     * 获取可关联的目标业务类型列表
     * 
     * 支持通过编码或名称查询当前业务类型，并返回尚未建立关联的候选目标列表。
     * 
     * @param currentBusinessTypeCode 当前业务类型编码
     * @param currentBusinessTypeName 当前业务类型名称
     * @return 可关联的目标列表（RelatableBusinessTypeRespVO 列表）
     */
    List<RelatableBusinessTypeRespVO> getAvailableTargets(String currentBusinessTypeCode, String currentBusinessTypeName);

}
