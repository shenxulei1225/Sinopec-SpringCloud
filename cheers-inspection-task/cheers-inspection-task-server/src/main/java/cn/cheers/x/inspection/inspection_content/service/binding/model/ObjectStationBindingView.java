package cn.cheers.x.inspection.inspection_content.service.binding.model;

import lombok.Data;

/**
 * 对象停靠点绑定读模型。
 */
@Data
public class ObjectStationBindingView {

    private Long objectId;

    private String stationNodeId;

    private Integer workMinutes;

    private Integer sortNo;
}
