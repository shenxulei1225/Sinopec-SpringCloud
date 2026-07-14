package cn.cheers.x.module.platform.contract.dto.route;

import cn.cheers.x.module.platform.contract.enums.NetworkKind;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 路径请求中的单段机动剖面（leg）— 多段联程时按段指定剖面与可选网络种类。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteLegDTO {

    private String mobilityProfileId;
    private NetworkKind networkKind;
}
