package cn.iocoder.yudao.module.scene.platform.adapter.spi;

import lombok.Data;

@Data
public class ActorPreviewResult {

    private boolean success;

    private String actorCode;

    private String payloadJson;
}
