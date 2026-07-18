package cn.iocoder.yudao.module.scene.platform.adapter.spi;

public interface SceneEngineAdapter {

    String engineProfile();

    EngineScenePackage exportScene(SceneExportContext context);

    SceneImportResult importScene(EngineScenePackage scenePackage);

    SceneValidationResult validateScene(SceneValidationContext context);

    ActorPreviewResult buildActorPreview(ActorPreviewContext context);
}
