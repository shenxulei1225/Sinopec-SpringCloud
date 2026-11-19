package cn.cheers.x.module.dynamicbusiness.service.entity;

import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityExportExcelVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityExportReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityImportExcelVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityImportRespVO;

import java.util.List;

/**
 * 业务实体数据导入导出服务接口
 */
public interface EntityDataExportService {

    /**
     * 导入业务实体数据
     *
     * 这条导入只写名称、型号、状态和扩展字段，不写所属场站等归属列。
     * 所以按数据范围分的目录拒绝这条导入：导进去的记录谁都看不到。
     *
     * @param registryCode  本页数据目录注册编码
     * @param importList    导入数据列表
     * @param updateSupport 保留入参。名称可以重复，导入不按名称查找或覆盖已有记录
     * @return 导入结果
     */
    EntityImportRespVO importEntityList(String registryCode, List<EntityImportExcelVO> importList, Boolean updateSupport);

    /**
     * 导出业务实体数据
     *
     * @param reqVO 导出请求参数
     * @return 导出数据列表
     */
    List<EntityExportExcelVO> exportEntityList(EntityExportReqVO reqVO);

    /**
     * 获取导入模板数据
     *
     * @param modelId 模型ID（可选，用于生成带字段说明的模板）
     * @return 模板数据列表
     * @deprecated 使用 {@link #getImportTemplate(String, Long)} 代替，需要传递 entityTypeCode
     */
    @Deprecated
    List<EntityImportExcelVO> getImportTemplate(Long modelId);

    /**
     * 获取导入模板数据（指定业务类型）
     *
     * @param entityTypeCode 业务类型编码（必填）
     * @param modelId 模型ID（可选，用于生成带字段说明的模板）
     * @return 模板数据列表
     */
    List<EntityImportExcelVO> getImportTemplate(String entityTypeCode, Long modelId);

    /**
     * 导出为 CSV 格式
     *
     * @param reqVO 导出请求参数
     * @return CSV 格式字符串
     */
    String exportToCsv(EntityExportReqVO reqVO);

    /**
     * 导出为 JSON 格式
     *
     * @param reqVO 导出请求参数
     * @return JSON 格式字符串
     */
    String exportToJson(EntityExportReqVO reqVO);
}
