package cn.cheers.x.module.dynamicbusiness.config;

import cn.cheers.x.module.dynamicbusiness.enums.entity.EntityQueryScene;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * query-by-scene 的 scene 参数按 code 解析（含旧码映射）。
 */
@Component
public class EntityQuerySceneConverter implements Converter<String, EntityQueryScene> {

    @Override
    public EntityQueryScene convert(String source) {
        EntityQueryScene scene = EntityQueryScene.getByCode(source);
        if (scene == null) {
            throw new IllegalArgumentException("不支持的查询场景: " + source);
        }
        return scene;
    }
}
