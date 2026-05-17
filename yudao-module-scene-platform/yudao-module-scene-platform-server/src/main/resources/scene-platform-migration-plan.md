scene-platform 新底座迁移计划

1. 新表并行启用
- scene_actor
- scene_layer
- scene_actor_asset_binding
- scene_actor_environment_config
- scene_actor_geo_layer_config
- scene_actor_camera_config
- scene_actor_light_config
- scene_actor_interaction_config

2. 旧表维持只读兼容，逐步弃用
- spatial_entity_layout -> 迁移到 scene_actor
- scene_asset_binding -> 迁移到 scene_actor_asset_binding
- geo_layer_config -> 迁移到 scene_actor_geo_layer_config

3. 新能力只落新表
- Actor 树管理
- Actor 资源绑定
- Actor 环境/图层/灯光/相机配置
- 新快照结构优先引用 actorCode / assetCode / layerKey

4. 弃用顺序建议
- 第一步：新增 actor API，不删除旧 entity API
- 第二步：前端/导入流程切到 actor API
- 第三步：快照与发布切到新表聚合
- 第四步：旧表改为只读兼容
- 第五步：确认无调用后正式下线旧表与旧 API
