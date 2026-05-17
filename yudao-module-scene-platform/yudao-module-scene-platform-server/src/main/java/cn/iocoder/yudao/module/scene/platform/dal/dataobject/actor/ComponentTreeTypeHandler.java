package cn.iocoder.yudao.module.scene.platform.dal.dataobject.actor;

import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.module.scene.platform.model.ComponentTree;
import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;

import java.lang.reflect.Field;

/**
 * {@link ComponentTree} 的 JSONB 处理器。
 */
public class ComponentTreeTypeHandler extends AbstractJsonTypeHandler<ComponentTree> {

    public ComponentTreeTypeHandler(Class<?> type) {
        super(type);
    }

    public ComponentTreeTypeHandler(Class<?> type, Field field) {
        super(type, field);
    }

    @Override
    public ComponentTree parse(String json) {
        return JsonUtils.parseObject(json, ComponentTree.class);
    }

    @Override
    public String toJson(ComponentTree obj) {
        return JsonUtils.toJsonString(obj);
    }
}
