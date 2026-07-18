package cn.cheers.x.scene.platform.controller.admin.component.vo;

import cn.cheers.x.scene.platform.dal.dataobject.component.TimelineComponentDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - Timeline 组件保存请求 VO")
@Data
public class TimelineComponentSaveReqVO {

    private String componentCode;

    private String displayName;

    private String timelineConfigJson;

    private Boolean autoPlay;

    private Boolean loop;

    private String metadataJson;

    public TimelineComponentDO toDO() {
        TimelineComponentDO item = new TimelineComponentDO();
        item.setComponentCode(componentCode);
        item.setDisplayName(displayName);
        item.setTimelineConfigJson(timelineConfigJson);
        item.setAutoPlay(autoPlay);
        item.setLoop(loop);
        item.setMetadataJson(metadataJson);
        return item;
    }
}
