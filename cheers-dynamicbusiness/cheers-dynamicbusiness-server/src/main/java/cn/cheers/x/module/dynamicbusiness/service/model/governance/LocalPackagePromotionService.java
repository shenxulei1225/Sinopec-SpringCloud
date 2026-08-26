package cn.cheers.x.module.dynamicbusiness.service.model.governance;

import java.util.Map;

/**
 * 本地型号包晋升服务。
 *
 * <p>这是型号及其本地字段晋升为公司主数据的唯一写入口。调用方只能提交整包，
 * 不得绕过本服务单独改型号治理状态。</p>
 */
public interface LocalPackagePromotionService {

    /**
     * 原子晋升一个本地型号包。
     *
     * @param modelId 本地型号编号
     * @param fieldMergeMap 本地字段编码到既有公司字段编码的映射；未映射字段直接晋升
     */
    void promoteLocalPackage(Long modelId, Map<String, String> fieldMergeMap);
}
