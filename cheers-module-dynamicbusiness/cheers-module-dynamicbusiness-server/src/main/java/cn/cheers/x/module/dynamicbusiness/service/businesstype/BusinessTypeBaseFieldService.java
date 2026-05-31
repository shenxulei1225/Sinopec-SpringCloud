package cn.cheers.x.module.dynamicbusiness.service.businesstype;

import cn.cheers.x.module.dynamicbusiness.controller.admin.businesstype.vo.BusinessTypeBaseFieldRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.businesstype.vo.BusinessTypeBaseFieldSaveReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.businesstype.BusinessTypeBaseFieldDO;

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
public interface BusinessTypeBaseFieldService {

    /**
     * 创建固定列字段
     * 
     * @param reqVO 创建请求
     * @return 字段ID
     */
    Long createBaseField(BusinessTypeBaseFieldSaveReqVO reqVO);

    /**
     * 更新固定列字段
     * 
     * @param reqVO 更新请求
     */
    void updateBaseField(BusinessTypeBaseFieldSaveReqVO reqVO);

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
    BusinessTypeBaseFieldDO getBaseField(Long id);

    /**
     * 获取固定列字段响应 VO
     * 
     * @param id 字段ID
     * @return 字段响应 VO
     */
    BusinessTypeBaseFieldRespVO getBaseFieldRespVO(Long id);

    /**
     * 根据业务类型编码获取所有启用的固定列字段
     * 
     * @param businessTypeCode 业务类型编码
     * @return 固定列字段列表（按排序顺序排列）
     */
    List<BusinessTypeBaseFieldRespVO> listByBusinessTypeCode(String businessTypeCode);

    /**
     * 根据业务类型编码获取所有固定列字段（包括禁用的）
     * 
     * @param businessTypeCode 业务类型编码
     * @return 固定列字段列表（按排序顺序排列）
     */
    List<BusinessTypeBaseFieldRespVO> listAllByBusinessTypeCode(String businessTypeCode);

    /**
     * 根据业务类型编码获取固定列字段 DO 列表
     * 
     * @param businessTypeCode 业务类型编码
     * @return 固定列字段 DO 列表
     */
    List<BusinessTypeBaseFieldDO> getBaseFieldsByBusinessTypeCode(String businessTypeCode);

    /**
     * 根据业务类型编码和字段编码获取固定列字段
     * 
     * @param businessTypeCode 业务类型编码
     * @param fieldCode 字段编码
     * @return 固定列字段
     */
    BusinessTypeBaseFieldDO getBaseFieldByCode(String businessTypeCode, String fieldCode);

    /**
     * 检查字段编码是否存在
     * 
     * @param businessTypeCode 业务类型编码
     * @param fieldCode 字段编码
     * @return 是否存在
     */
    boolean existsFieldCode(String businessTypeCode, String fieldCode);

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
     * @param businessTypeCode 业务类型编码
     * @return 字段数量
     */
    Long countByBusinessTypeCode(String businessTypeCode);

    /**
     * 获取固定列字段编码列表
     * 
     * @param businessTypeCode 业务类型编码
     * @return 字段编码列表
     */
    List<String> getFieldCodes(String businessTypeCode);

    /**
     * 验证固定列字段值
     * 
     * @param businessTypeCode 业务类型编码
     * @param fieldCode 字段编码
     * @param value 字段值
     * @return 验证结果，null 表示验证通过，否则返回错误信息
     */
    String validateFieldValue(String businessTypeCode, String fieldCode, Object value);
}
