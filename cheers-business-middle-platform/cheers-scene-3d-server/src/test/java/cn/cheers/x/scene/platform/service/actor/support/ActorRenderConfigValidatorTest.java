package cn.cheers.x.scene.platform.service.actor.support;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.scene.platform.dal.dataobject.actor.ActorInstanceComponentDO;
import cn.cheers.x.scene.platform.model.render.ActorRenderConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ActorRenderConfigValidatorTest {

    private ActorRenderConfigValidator validator;

    @BeforeEach
    void setUp() {
        validator = new ActorRenderConfigValidator();
    }

    @Test
    void shouldPreferOverrideJsonOverMetadataJson() {
        ActorInstanceComponentDO component = buildComponent(
                "StaticMeshComponent0",
                "StaticMeshComponent",
                "{\"renderType\":\"splat\",\"splatUrl\":\"https://asset.example.com/splats/pump.spz\"}",
                "{\"renderType\":\"gltf\",\"modelUrl\":\"https://asset.example.com/models/pump.glb\"}",
                "{}"
        );

        ActorRenderConfig config = validator.resolveAndValidate(component);

        assertEquals("splat", config.getRenderType());
        assertEquals("https://asset.example.com/splats/pump.spz", config.getSplatUrl());
        assertNull(config.getModelUrl());
        assertEquals("overrideJson", config.getSource());
    }

    @Test
    void shouldPassWhenConfigIsValidGltf() {
        ActorInstanceComponentDO component = buildComponent(
                "StaticMeshComponent0",
                "StaticMeshComponent",
                "{\"renderType\":\"gltf\",\"modelUrl\":\"https://asset.example.com/models/pump.glb\"}",
                "{}",
                "{}"
        );

        assertDoesNotThrow(() -> validator.resolveAndValidate(component));
    }

    @Test
    void shouldPassWhenConfigIsValidSplat() {
        ActorInstanceComponentDO component = buildComponent(
                "StaticMeshComponent0",
                "StaticMeshComponent",
                "{\"renderType\":\"splat\",\"splatUrl\":\"https://asset.example.com/splats/tank.spz\"}",
                "{}",
                "{}"
        );

        assertDoesNotThrow(() -> validator.resolveAndValidate(component));
    }

    @Test
    void shouldThrowWhenRenderTypeInvalid() {
        ActorInstanceComponentDO component = buildComponent(
                "StaticMeshComponent0",
                "StaticMeshComponent",
                "{\"renderType\":\"abc\",\"modelUrl\":\"https://asset.example.com/models/pump.glb\"}",
                "{}",
                "{}"
        );

        ServiceException exception = assertThrows(
                ServiceException.class,
                () -> validator.resolveAndValidate(component)
        );

        assertTrue(exception.getMessage().contains("renderType 非法"));
    }

    @Test
    void shouldThrowWhenSplatUrlMissing() {
        ActorInstanceComponentDO component = buildComponent(
                "StaticMeshComponent0",
                "StaticMeshComponent",
                "{\"renderType\":\"splat\"}",
                "{}",
                "{}"
        );

        ServiceException exception = assertThrows(
                ServiceException.class,
                () -> validator.resolveAndValidate(component)
        );

        assertTrue(exception.getMessage().contains("splatUrl 不能为空"));
    }

    @Test
    void shouldPassWhenConfigEmptyForLegacyData() {
        ActorInstanceComponentDO component = buildComponent(
                "StaticMeshComponent0",
                "StaticMeshComponent",
                "{}",
                "{}",
                "{}"
        );

        ActorRenderConfig config = validator.resolveAndValidate(component);

        assertTrue(config.isEmpty());
        assertEquals("none", config.getSource());
    }

    @Test
    void shouldThrowWhenJsonInvalid() {
        ActorInstanceComponentDO component = buildComponent(
                "StaticMeshComponent0",
                "StaticMeshComponent",
                "{renderType:splat}",
                "{}",
                "{}"
        );

        ServiceException exception = assertThrows(
                ServiceException.class,
                () -> validator.resolveAndValidate(component)
        );

        assertTrue(exception.getMessage().contains("渲染配置 JSON 非法"));
    }

    @Test
    void shouldThrowWhenGltfExtensionInvalid() {
        ActorInstanceComponentDO component = buildComponent(
                "StaticMeshComponent0",
                "StaticMeshComponent",
                "{\"renderType\":\"gltf\",\"modelUrl\":\"https://asset.example.com/models/pump.spz\"}",
                "{}",
                "{}"
        );

        ServiceException exception = assertThrows(
                ServiceException.class,
                () -> validator.resolveAndValidate(component)
        );

        assertTrue(exception.getMessage().contains("modelUrl 后缀非法"));
    }

    @Test
    void shouldThrowWhenSplatExtensionInvalid() {
        ActorInstanceComponentDO component = buildComponent(
                "StaticMeshComponent0",
                "StaticMeshComponent",
                "{\"renderType\":\"splat\",\"splatUrl\":\"https://asset.example.com/splats/pump.glb\"}",
                "{}",
                "{}"
        );

        ServiceException exception = assertThrows(
                ServiceException.class,
                () -> validator.resolveAndValidate(component)
        );

        assertTrue(exception.getMessage().contains("splatUrl 后缀非法"));
    }

    @Test
    void shouldThrowWhenComponentTypeNotRenderable() {
        ActorInstanceComponentDO component = buildComponent(
                "AudioComponent0",
                "AudioComponent",
                "{\"renderType\":\"splat\",\"splatUrl\":\"https://asset.example.com/splats/audio.spz\"}",
                "{}",
                "{}"
        );

        ServiceException exception = assertThrows(
                ServiceException.class,
                () -> validator.resolveAndValidate(component)
        );

        assertTrue(exception.getMessage().contains("组件类型不支持配置渲染资源"));
    }

    private ActorInstanceComponentDO buildComponent(String componentCode,
                                                    String componentTypeName,
                                                    String overrideJson,
                                                    String metadataJson,
                                                    String propertiesJson) {
        ActorInstanceComponentDO component = new ActorInstanceComponentDO();
        component.setInstanceCode("instance-001");
        component.setComponentCode(componentCode);
        component.setComponentTypeName(componentTypeName);
        component.setOverrideJson(overrideJson);
        component.setMetadataJson(metadataJson);
        component.setPropertiesJson(propertiesJson);
        return component;
    }
}
