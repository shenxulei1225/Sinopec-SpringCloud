package cn.iocoder.yudao.module.scene.platform.adapter.ue;

import cn.iocoder.yudao.module.scene.platform.adapter.spi.ActorPreviewContext;
import cn.iocoder.yudao.module.scene.platform.adapter.spi.ActorPreviewResult;
import cn.iocoder.yudao.module.scene.platform.adapter.spi.EngineScenePackage;
import cn.iocoder.yudao.module.scene.platform.adapter.spi.SceneEngineAdapter;
import cn.iocoder.yudao.module.scene.platform.adapter.spi.SceneExportContext;
import cn.iocoder.yudao.module.scene.platform.adapter.spi.SceneImportResult;
import cn.iocoder.yudao.module.scene.platform.adapter.spi.SceneValidationContext;
import cn.iocoder.yudao.module.scene.platform.adapter.spi.SceneValidationResult;
import org.springframework.stereotype.Component;

@Component
public class UeSceneEngineAdapter implements SceneEngineAdapter {

    @Override
    public String engineProfile() {
        return "UE";
    }

    @Override
    public EngineScenePackage exportScene(SceneExportContext context) {
        return new EngineScenePackage();
    }

    @Override
    public SceneImportResult importScene(EngineScenePackage scenePackage) {
        return new SceneImportResult();
    }

    @Override
    public SceneValidationResult validateScene(SceneValidationContext context) {
        return new SceneValidationResult();
    }

    @Override
    public ActorPreviewResult buildActorPreview(ActorPreviewContext context) {
        return new ActorPreviewResult();
    }
}
