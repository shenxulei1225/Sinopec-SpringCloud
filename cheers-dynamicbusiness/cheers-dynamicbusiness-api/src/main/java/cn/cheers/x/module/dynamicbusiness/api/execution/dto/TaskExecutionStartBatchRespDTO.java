package cn.cheers.x.module.dynamicbusiness.api.execution.dto;

import lombok.Data;

import java.util.List;

/**
 * 批量写下的待执行账。顺序与请求里的计划点一致。
 */
@Data
public class TaskExecutionStartBatchRespDTO {

    private List<RecordRef> records;

    @Data
    public static class RecordRef {
        private String pendingRef;
        private Long executionRecordId;
    }
}
