package cn.cheers.x.scene.platform.adapter.spi;

public interface ActorConfigMapper {

    EngineActorConfig toEngineConfig(SceneActorConfigAggregate config);

    SceneActorConfigAggregate fromEngineConfig(EngineActorConfig engineConfig);
}
