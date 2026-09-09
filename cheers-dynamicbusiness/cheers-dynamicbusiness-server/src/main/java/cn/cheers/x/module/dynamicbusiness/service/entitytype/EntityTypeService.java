package cn.cheers.x.module.dynamicbusiness.service.entitytype;

import cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo.EntityTypeCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo.EntityTypeDomainOptionVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo.EntityTypeRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo.EntityTypeSimpleVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo.EntityTypeUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelFieldAssignmentRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelRespVO;

import java.util.List;
import java.util.Map;

/**
 * 业务类型 Service 接口
 * 
 * @author yudao
 */
public interface EntityTypeService {

    /**
     * 创建业务类型
     * 
     * @param reqVO 创建请求
     * @return 业务类型 ID
     */
    Long create(EntityTypeCreateReqVO reqVO);

    /**
     * 更新业务类型
     * 
     * @param reqVO 更新请求
     */
    void update(EntityTypeUpdateReqVO reqVO);

    /**
     * 删除业务类型
     * 
     * @param id 业务类型 ID
     */
    void delete(Long id);

    /**
     * 获取业务类型详情
     * 
     * @param id 业务类型 ID
     * @return 业务类型详情
     */
    EntityTypeRespVO get(Long id);

    /**
     * 获取业务类型列表（平铺，含启用/停用）。
     *
     * @return 业务类型列表
     */
    List<EntityTypeRespVO> listAcrossEntityTypes();

    /**
     * 兼容旧命名：获取业务类型列表（平铺，含启用/停用）。
     *
     * @deprecated 请使用 {@link #listAcrossEntityTypes()}
     */
    @Deprecated
    List<EntityTypeRespVO> listAll();

    /**
     * 获取业务类型简单列表（用于下拉选择）
     *
     * @return 业务类型简单列表
     */
    List<EntityTypeSimpleVO> listSimple();

    /**
     * 列出某存储类型下子数据类型（DOMAIN）的业务域选项，供型号 CRUD 下拉。
     *
     * @param baseEntityTypeCode 存储类型编码（如 task、equipment）
     * @return value=domain，label=子数据类型名称
     */
    List<EntityTypeDomainOptionVO> listDomainOptions(String baseEntityTypeCode);

    /**
     * 业务域是否属于该存储类型下已登记的 DOMAIN 入口（空域视为合法）。
     */
    boolean isRegisteredDomain(String baseEntityTypeCode, String domain);

    /**
     * 获取系统级业务类型列表
     *
     * @return 系统级业务类型列表
     */
    List<EntityTypeRespVO> listSystemEntityTypes();

    /**
     * 获取用户自定义业务类型列表
     *
     * @return 用户自定义业务类型列表
     */
    List<EntityTypeRespVO> listUserEntityTypes();

    /**
     * 获取业务类型树形结构
     * 
     * @return 业务类型树
     */
    List<EntityTypeRespVO> listTree();

    /**
     * 检查业务类型是否存在
     * 
     * @param entityTypeCode 业务类型编码
     * @return 是否存在
     */
    boolean checkEntityTypeExists(String entityTypeCode);

    /**
     * 确保目录已挂载「模型管理」页签独立布局，返回 modelLayoutId。
     * 划分（SCOPE）无模型管理页，调用方勿用。
     */
    Long ensureModelLayout(String entityTypeCode);

    /**
     * 根据编码获取业务类型详情
     *
     * @param code 业务类型编码
     * @return 业务类型详情
     */
    EntityTypeRespVO getByCode(String code);

    /**
     * 获取指定业务的子业务树（导航用）
     *
     * 用于进入某业务管理页面时，展示该业务下属的业务入口树。
     *
     * @param entityTypeCode 根业务类型编码
     * @return 子业务树形结构
     */
    List<EntityTypeRespVO> listChildrenTreeByCode(String entityTypeCode);

    /**
     * 获取某业务的配置子业务列表（独立业务，如巡检点、检查内容）
     *
     * 主业务通过编码关联若干配置/资源类子业务，本方法返回这些子业务的元信息。
     *
     * @param entityTypeCode 主业务类型编码
     * @return 配置子业务列表
     */
    List<EntityTypeRespVO> listConfigChildrenByCode(String entityTypeCode);

    /**
     * 判断某业务类型是否采用专用存储
     *
     * @param entityTypeCode 业务类型编码
     * @return 是否专用存储
     */
    boolean isDedicatedStorage(String entityTypeCode);

    /**
     * 更新业务类型配置状态
     *
     * @param id 业务类型编号
     * @param status 配置状态
     */
    void updateStatus(Long id, String status);

    List<ModelRespVO> getModels(String entityTypeCode);

    List<ModelFieldAssignmentRespVO> getModelFields(Long modelId);

    void validateCustomFields(Long modelId, Map<String, Object> customFields);

    Map<String, Object> normalizeAndEncryptCustomFields(Map<String, Object> customFields, Long modelId);

    Map<String, Object> decryptCustomFields(Map<String, Object> customFields, Long modelId);

    Map<String, Object> getEntityTypeStatistics(String entityTypeCode);

    boolean isSystemEntityType(String entityTypeCode);
}

