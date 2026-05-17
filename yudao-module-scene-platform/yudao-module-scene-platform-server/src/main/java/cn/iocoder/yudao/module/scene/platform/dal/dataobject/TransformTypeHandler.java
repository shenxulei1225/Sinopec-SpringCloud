package cn.iocoder.yudao.module.scene.platform.dal.dataobject;

import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.module.scene.platform.model.Transform;
import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;

import java.lang.reflect.Field;

/**
 * {@link Transform} 的 JSONB 处理器。
 */
public class TransformTypeHandler extends AbstractJsonTypeHandler<Transform> {

    public TransformTypeHandler(Class<?> type) {
        super(type);
    }

    public TransformTypeHandler(Class<?> type, Field field) {
        super(type, field);
    }

    @Override
    public Transform parse(String json) {
        return JsonUtils.parseObject(json, Transform.class);
    }

    @Override
    public String toJson(Transform obj) {
        return JsonUtils.toJsonString(obj);
    }
}
