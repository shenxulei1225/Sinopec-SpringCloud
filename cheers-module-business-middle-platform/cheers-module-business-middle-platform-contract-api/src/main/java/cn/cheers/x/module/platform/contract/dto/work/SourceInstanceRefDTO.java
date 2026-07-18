package cn.cheers.x.module.platform.contract.dto.work;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * L1 实例引用 — Phase 3 映射解析输入。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SourceInstanceRefDTO {

    private String sourceInstanceId;
    /** L1 自定义字段快照；MVP 由调用方传入，后续可改为平台拉取实体 */
    private Map<String, Object> customFields;
}
