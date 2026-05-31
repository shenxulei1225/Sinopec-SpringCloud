package cn.cheers.x.module.dynamicbusiness.service.relation;

/**
 * 关联数据修复服务接口
 * 
 * 用于修复历史数据中缺失的关联信息，包括：
 * - 回填 ModelFieldAssignment 中缺失的 modelRelationId
 * - 回填 ModelFieldAssignment 中缺失的 targetBusinessType、targetModelCode
 * 
 * 需求：5.1, 5.2, 5.3
 * 
 * @author yudao
 */
public interface RelationDataRepairService {

    /**
     * 修复关联字段数据
     * 
     * 执行以下修复操作：
     * 1. 查找所有 ENTITY_REF 类型字段的 ModelFieldAssignment 记录
     * 2. 对于缺少 modelRelationId 的记录，尝试通过字段编码匹配 ModelRelation
     * 3. 对于缺少 targetBusinessType/targetModelCode 的记录，从 RelationFieldLibrary 或 ModelRelation 回填
     * 
     * @return 修复结果统计
     */
    RepairResult repairRelationFieldData();

    /**
     * 修复指定模型的关联字段数据
     * 
     * @param modelId 模型ID
     * @return 修复结果统计
     */
    RepairResult repairRelationFieldDataByModelId(Long modelId);

    /**
     * 修复结果统计
     */
    class RepairResult {
        /** 成功修复的记录数 */
        private int successCount;
        /** 修复失败的记录数 */
        private int failedCount;
        /** 跳过的记录数（数据已完整或无法匹配） */
        private int skippedCount;
        /** 总处理记录数 */
        private int totalCount;
        /** 详细信息 */
        private String details;

        public RepairResult() {
            this.successCount = 0;
            this.failedCount = 0;
            this.skippedCount = 0;
            this.totalCount = 0;
        }

        public void incrementSuccess() {
            this.successCount++;
            this.totalCount++;
        }

        public void incrementFailed() {
            this.failedCount++;
            this.totalCount++;
        }

        public void incrementSkipped() {
            this.skippedCount++;
            this.totalCount++;
        }

        public int getSuccessCount() {
            return successCount;
        }

        public void setSuccessCount(int successCount) {
            this.successCount = successCount;
        }

        public int getFailedCount() {
            return failedCount;
        }

        public void setFailedCount(int failedCount) {
            this.failedCount = failedCount;
        }

        public int getSkippedCount() {
            return skippedCount;
        }

        public void setSkippedCount(int skippedCount) {
            this.skippedCount = skippedCount;
        }

        public int getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(int totalCount) {
            this.totalCount = totalCount;
        }

        public String getDetails() {
            return details;
        }

        public void setDetails(String details) {
            this.details = details;
        }

        @Override
        public String toString() {
            return String.format("RepairResult{total=%d, success=%d, failed=%d, skipped=%d}",
                    totalCount, successCount, failedCount, skippedCount);
        }
    }
}
