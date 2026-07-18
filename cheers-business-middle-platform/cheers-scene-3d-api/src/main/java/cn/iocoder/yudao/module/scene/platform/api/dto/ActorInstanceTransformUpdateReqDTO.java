package cn.iocoder.yudao.module.scene.platform.api.dto;

import cn.iocoder.yudao.module.scene.platform.model.Rotator;
import cn.iocoder.yudao.module.scene.platform.model.Transform;
import cn.iocoder.yudao.module.scene.platform.model.Vector3;
import lombok.Data;

@Data
public class ActorInstanceTransformUpdateReqDTO {

    private Double positionX;
    private Double positionY;
    private Double positionZ;

    private Double rotationX;
    private Double rotationY;
    private Double rotationZ;

    private Double scaleX;
    private Double scaleY;
    private Double scaleZ;

    public Transform toTransform() {
        Transform transform = new Transform();
        transform.setLocation(new Vector3(defaultValue(positionX, 0D), defaultValue(positionY, 0D), defaultValue(positionZ, 0D)));

        Rotator rotator = new Rotator();
        rotator.setPitch(defaultValue(rotationX, 0D));
        rotator.setYaw(defaultValue(rotationY, 0D));
        rotator.setRoll(defaultValue(rotationZ, 0D));
        transform.setRotation(rotator);

        transform.setScale(new Vector3(defaultValue(scaleX, 1D), defaultValue(scaleY, 1D), defaultValue(scaleZ, 1D)));
        return transform;
    }

    private Double defaultValue(Double value, Double defaultValue) {
        return value == null ? defaultValue : value;
    }
}
