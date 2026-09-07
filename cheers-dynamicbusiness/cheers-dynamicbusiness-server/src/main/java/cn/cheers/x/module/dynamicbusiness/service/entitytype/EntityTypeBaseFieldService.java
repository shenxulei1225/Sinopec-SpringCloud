package cn.cheers.x.module.dynamicbusiness.service.entitytype;

import cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo.EntityTypeBaseFieldBatchSaveReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo.EntityTypeBaseFieldRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo.EntityTypeBaseFieldSaveReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo.EntityTypePlatformFieldRespVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeBaseFieldDO;

import java.util.List;

/**
 * 业务类型固定列字段 Service 接口
 * 
 * 提供业务类型固定列字段的管理功能：
 * - 固定列字段自动继承到该业务类型下的所有 Model
 * - 用户创建 Model 时无需手动添加这些字段
 * - 固定列字段存储在专用表的物理列中，支持索引和约束
 * 
 * @author yudao
 */
public interface EntityTypeBaseFieldService {

    /**
     * 创建固定列字段
     * 
     * @param reqVO 创建请求
     * @return 字段ID
     */
    Long createBaseField(EntityTypeBaseFieldSaveReqVO reqVO);

    /**
     * 更新固定列字段
     * 
     * @param reqVO 更新请求
     */
    void updateBaseField(EntityTypeBaseFieldSaveReqVO reqVO);

    /**
     * 删除固定列字段
     * 
     * @param id 字段ID
     */
    void deleteBaseField(Long id);

    /**
     * 获取固定列字段
     * 
     * @param id 字段ID
     * @return 字段信息
     */
    EntityTypeBaseFieldDO getBaseField(Long id);

    /**
     * 获取固定列字段响应 VO
     * 
     * @param id 字段ID
     * @return 字段响应 VO
     */
    EntityTypeBaseFieldRespVO getBaseFieldRespVO(Long id);

    /**
     * 根据业务类型编码获取所有启用的固定列字段
     * 
     * @param entityTypeCode 业务类型编码
     * @return 固定列字段列表（按排序顺序排列）
     */
    List<EntityTypeBaseFieldRespVO> listByEntityTypeCode(String entityTypeCode);

    /**
     * 根据业务类型编码获取所有固定列字段（包括禁用的）
     * 
     * @param entityTypeCode 业务类型编码
     * @return 固定列字段列表（按排序顺序排列）
     */
    List<EntityTypeBaseFieldRespVO> listAllByEntityTypeCode(String entityTypeCode);

    /**
     * 根据业务类型编码获取固定列字段 DO 列表
     * 
     * @param entityTypeCode 业务类型编码
     * @return 固定列字段 DO 列表
     */
    List<EntityTypeBaseFieldDO> getBaseFieldsByEntityTypeCode(String entityTypeCode);

    /**
     * 根据业务类型编码和字段编码获取固定列字段
     * 
     * @param entityTypeCode 业务类型编码
     * @param fieldCode 字段编码
     * @return 固定列字段
     */
    EntityTypeBaseFieldDO getBaseFieldByCode(String entityTypeCode, String fieldCode);

    /**
     * 检查字段编码是否存在
     * 
     * @param entityTypeCode 业务类型编码
     * @param fieldCode 字段编码
     * @return 是否存在
     */
    boolean existsFieldCode(String entityTypeCode, String fieldCode);

    /**
     * 更新字段状态
     * 
     * @param id 字段ID
     * @param status 状态（1=启用，0=禁用）
     */
    void updateBaseFieldStatus(Long id, Integer status);

    /**
     * 统计业务类型下的固定列字段数量
     * 
     * @param entityTypeCode 业务类型编码
     * @return 字段数量
     */
    Long countByEntityTypeCode(String entityTypeCode);

    /**
     * 获取固定列字段编码列表
     * 
     * @param entityTypeCode 业务类型编码
     * @return 字段编码列表
     */
    List<String> getFieldCodes(String entityTypeCode);

    /**
     * 验证固定列字段值
     * 
     * @param entityTypeCode 业务类型编码
     * @param fieldCode 字段编码
     * @param value 字段值
     * @return 验证结果，null 表示验证通过，否则返回错误信息
     */
    String validateFieldValue(String entityTypeCode, String fieldCode, Object value);

    /**
     * 实体通用列（名称、状态等）在该业务类型下的展示信息。
     */
    List<EntityTypePlatformFieldRespVO> listPlatformFields(String entityTypeCode);

    /**
     * 更新实体通用列在该业务类型下的显示别名。
     */
    void updatePlatformFieldLabel(String entityTypeCode, String fieldCode, String label);

    /**
     * 按模型字段信息删除基础字段：优先删注册记录，无注册记录时仍从全部模型移除。
     */
    void deleteBaseFieldByAssignment(String entityTypeCode, Long libraryFieldId, String fieldCode);

    /**
     * 批量保存基础字段配置：一次事务提交，结束时统一刷新能力投影。
     */
    void saveBaseFieldBatch(EntityTypeBaseFieldBatchSaveReqVO reqVO);
}
