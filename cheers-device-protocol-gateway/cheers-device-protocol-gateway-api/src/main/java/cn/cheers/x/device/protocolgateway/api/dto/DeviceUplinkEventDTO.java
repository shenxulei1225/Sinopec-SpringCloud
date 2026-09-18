package cn.cheers.x.device.protocolgateway.api.dto;

import cn.cheers.x.device.protocolgateway.api.datacollection.CollectionSample;
import cn.cheers.x.device.protocolgateway.api.identity.UplinkIdentity;

/**
 * 总线投递信封：里面只带采集样本。
 * <p>业务处理方法认 {@link CollectionSample}，不要再拆原始 JSON。
 * <p>禁止：通道为空默认巡检；把本对象当说明书核对器。
 */
public record DeviceUplinkEventDTO(CollectionSample sample) {

    public DeviceUplinkEventDTO {
        if (sample == null) {
            throw new IllegalArgumentException("上报事件缺少采集样本");
        }
    }

    public static DeviceUplinkEventDTO of(CollectionSample sample) {
        return new DeviceUplinkEventDTO(sample);
    }

    public String channelCode() {
        return sample.channelCode();
    }

    public String deviceId() {
        return sample.deviceId();
    }

    public String messageKind() {
        return sample.messageKind();
    }

    public String msgId() {
        return sample.msgId();
    }

    public UplinkIdentity identity() {
        return sample.identity();
    }
}
