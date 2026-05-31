package cn.cheers.x.module.dynamicbusiness.service.relation;

import java.util.List;
import java.util.Map;

/**
 * 关联展示服务接口
 * 
 * 用于获取关联字段的展示值，支持：
 * - 使用关联字段配置的展示字段（displayFieldCode）
 * - 如果未配置，则使用实体的 name 字段或 ID 作为展示值
 * 
 * 需求：FR-BDA-040~042
 * 
 * @author yudao
 */
public interface RelationDisplayService {

    /**
     * 获取关联实体的展示值
     * 
     * 展示值获取规则：
     * 1. 如果指定了 displayFieldCode，使用该字段的值
     * 2. 如果未指定，返回实体的 name 字段值（如果存在）
     * 3. 如果都没有，返回实体 ID
     * 
     * @param targetBusinessType 目标业务类型编码
     * @param targetModelCode 目标 Model 编码
     * @param entityId 实体 ID
     * @param displayFieldCode 展示字段编码（可选，优先使用）
     * @return 展示值
     */
    String getDisplayValue(String targetBusinessType, String targetModelCode, 
                           Long entityId, String displayFieldCode);

    /**
     * 批量获取关联实体的展示值
     * 
     * @param targetBusinessType 目标业务类型编码
     * @param targetModelCode 目标 Model 编码
     * @param entityIds 实体 ID 列表
     * @param displayFieldCode 展示字段编码（可选，优先使用）
     * @return 实体 ID 到展示值的映射
     */
    Map<Long, String> getDisplayValues(String targetBusinessType, String targetModelCode,
                                        List<Long> entityIds, String displayFieldCode);

    /**
     * 获取关联实体的展示字段编码
     * 
     * 返回实际使用的展示字段编码：
     * 1. 如果指定了 displayFieldCode，返回该值
     * 2. 如果未指定，返回 null（后续会使用实体的 name 字段或 ID）
     * 
     * @param targetBusinessType 目标业务类型编码
     * @param targetModelCode 目标 Model 编码
     * @param displayFieldCode 展示字段编码（可选，优先使用）
     * @return 实际使用的展示字段编码
     */
    String getEffectiveDisplayFieldCode(String targetBusinessType, String targetModelCode,
                                         String displayFieldCode);

    /**
     * 获取关联实体的展示信息
     * 
     * 返回包含展示值和其他元信息的完整展示信息
     * 
     * @param targetBusinessType 目标业务类型编码
     * @param targetModelCode 目标 Model 编码
     * @param entityId 实体 ID
     * @param displayFieldCode 展示字段编码（可选，优先使用）
     * @return 展示信息
     */
    RelationDisplayInfo getDisplayInfo(String targetBusinessType, String targetModelCode,
                                        Long entityId, String displayFieldCode);

    /**
     * 关联展示信息
     */
    class RelationDisplayInfo {
        /** 实体 ID */
        private Long entityId;
        /** 展示值 */
        private String displayValue;
        /** 使用的展示字段编码 */
        private String displayFieldCode;
        /** 目标业务类型编码 */
        private String targetBusinessType;
        /** 目标 Model 编码 */
        private String targetModelCode;

        public Long getEntityId() {
            return entityId;
        }

        public void setEntityId(Long entityId) {
            this.entityId = entityId;
        }

        public String getDisplayValue() {
            return displayValue;
        }

        public void setDisplayValue(String displayValue) {
            this.displayValue = displayValue;
        }

        public String getDisplayFieldCode() {
            return displayFieldCode;
        }

        public void setDisplayFieldCode(String displayFieldCode) {
            this.displayFieldCode = displayFieldCode;
        }

        public String getTargetBusinessType() {
            return targetBusinessType;
        }

        public void setTargetBusinessType(String targetBusinessType) {
            this.targetBusinessType = targetBusinessType;
        }

        public String getTargetModelCode() {
            return targetModelCode;
        }

        public void setTargetModelCode(String targetModelCode) {
            this.targetModelCode = targetModelCode;
        }
    }
}
