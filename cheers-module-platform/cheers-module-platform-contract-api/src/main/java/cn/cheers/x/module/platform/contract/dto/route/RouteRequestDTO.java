package cn.cheers.x.module.platform.contract.dto.route;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 路径请求（Route Request）— 路径引擎输入。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteRequestDTO {

    private String contractVersion;
    private String topologyRef;
    private List<String> stopIds;
    private String strategy;
    private String businessTypeCode;
}
