package cn.cheers.x.scene.platform.adapter.spi;

public interface ActorMapper {

    EngineActor toEngineActor(SceneActorAggregate actor);

    SceneActorAggregate fromEngineActor(EngineActor engineActor);
}
