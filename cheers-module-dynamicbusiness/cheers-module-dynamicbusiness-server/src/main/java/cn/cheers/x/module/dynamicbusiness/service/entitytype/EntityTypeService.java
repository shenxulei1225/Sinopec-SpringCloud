package cn.cheers.x.module.dynamicbusiness.service.businesstype;

import cn.cheers.x.module.dynamicbusiness.controller.admin.businesstype.vo.BusinessTypeCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.businesstype.vo.BusinessTypeRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.businesstype.vo.BusinessTypeSimpleVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.businesstype.vo.BusinessTypeUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelFieldAssignmentRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelRespVO;

import java.util.List;
import java.util.Map;

/**
 * 业务类型 Service 接口
 * 
 * @author yudao
 */
public interface BusinessTypeService {

    /**
     * 创建业务类型
     * 
     * @param reqVO 创建请求
     * @return 业务类型 ID
     */
    Long create(BusinessTypeCreateReqVO reqVO);

    /**
     * 更新业务类型
     * 
     * @param reqVO 更新请求
     */
    void update(BusinessTypeUpdateReqVO reqVO);

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
    BusinessTypeRespVO get(Long id);

    /**
     * 获取业务类型列表（平铺，含启用/停用）。
     *
     * @return 业务类型列表
     */
    List<BusinessTypeRespVO> listAcrossBusinessTypes();

    /**
     * 兼容旧命名：获取业务类型列表（平铺，含启用/停用）。
     *
     * @deprecated 请使用 {@link #listAcrossBusinessTypes()}
     */
    @Deprecated
    List<BusinessTypeRespVO> listAll();

    /**
     * 获取业务类型简单列表（用于下拉选择）
     *
     * @return 业务类型简单列表
     */
    List<BusinessTypeSimpleVO> listSimple();

    /**
     * 获取系统级业务类型列表
     *
     * @return 系统级业务类型列表
     */
    List<BusinessTypeRespVO> listSystemBusinessTypes();

    /**
     * 获取用户自定义业务类型列表
     *
     * @return 用户自定义业务类型列表
     */
    List<BusinessTypeRespVO> listUserBusinessTypes();

    /**
     * 获取业务类型树形结构
     * 
     * @return 业务类型树
     */
    List<BusinessTypeRespVO> listTree();

    /**
     * 检查业务类型是否存在
     * 
     * @param businessTypeCode 业务类型编码
     * @return 是否存在
     */
    boolean checkBusinessTypeExists(String businessTypeCode);

    /**
     * 根据编码获取业务类型详情
     *
     * @param code 业务类型编码
     * @return 业务类型详情
     */
    BusinessTypeRespVO getByCode(String code);

    /**
     * 获取指定业务的子业务树（导航用）
     *
     * 用于进入某业务管理页面时，展示该业务下属的业务入口树。
     *
     * @param businessTypeCode 根业务类型编码
     * @return 子业务树形结构
     */
    List<BusinessTypeRespVO> listChildrenTreeByCode(String businessTypeCode);

    /**
     * 获取某业务的配置子业务列表（独立业务，如巡检点、检查内容）
     *
     * 主业务通过编码关联若干配置/资源类子业务，本方法返回这些子业务的元信息。
     *
     * @param businessTypeCode 主业务类型编码
     * @return 配置子业务列表
     */
    List<BusinessTypeRespVO> listConfigChildrenByCode(String businessTypeCode);

    /**
     * 判断某业务类型是否采用专用存储
     *
     * @param businessTypeCode 业务类型编码
     * @return 是否专用存储
     */
    boolean isDedicatedStorage(String businessTypeCode);

    /**
     * 更新业务类型配置状态
     *
     * @param id 业务类型编号
     * @param status 配置状态
     */
    void updateStatus(Long id, String status);

    List<ModelRespVO> getModels(String businessTypeCode);

    List<ModelFieldAssignmentRespVO> getModelFields(Long modelId);

    void validateCustomFields(Long modelId, Map<String, Object> customFields);

    Map<String, Object> normalizeAndEncryptCustomFields(Map<String, Object> customFields, Long modelId);

    Map<String, Object> decryptCustomFields(Map<String, Object> customFields, Long modelId);

    Map<String, Object> getBusinessTypeStatistics(String businessTypeCode);

    boolean isSystemBusinessType(String businessTypeCode);
}

