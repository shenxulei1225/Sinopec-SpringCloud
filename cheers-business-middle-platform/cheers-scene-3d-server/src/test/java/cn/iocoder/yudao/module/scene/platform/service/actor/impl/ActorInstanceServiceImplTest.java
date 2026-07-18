package cn.iocoder.yudao.module.scene.platform.service.actor.impl;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.iocoder.yudao.module.scene.platform.controller.admin.actor.vo.ActorInstanceSpawnReqVO;
import cn.iocoder.yudao.module.scene.platform.controller.admin.actor.vo.ComponentTreeNodeVO;
import cn.iocoder.yudao.module.scene.platform.controller.admin.actor.vo.ComponentTreeVO;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.actor.ActorDO;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.actor.ActorInstanceComponentDO;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.actor.ActorInstanceDO;
import cn.iocoder.yudao.module.scene.platform.dal.mysql.actor.ActorInstanceComponentMapper;
import cn.iocoder.yudao.module.scene.platform.dal.mysql.actor.ActorInstanceMapper;
import cn.iocoder.yudao.module.scene.platform.dal.mysql.scene.SceneMapper;
import cn.iocoder.yudao.module.scene.platform.model.ComponentTree;
import cn.iocoder.yudao.module.scene.platform.model.ComponentTreeNode;
import cn.iocoder.yudao.module.scene.platform.service.actor.ActorComponentTreeService;
import cn.iocoder.yudao.module.scene.platform.service.actor.ActorInstanceComponentService;
import cn.iocoder.yudao.module.scene.platform.service.actor.ActorService;
import cn.iocoder.yudao.module.scene.platform.service.actor.support.ActorRenderConfigValidator;
import cn.iocoder.yudao.module.scene.platform.websocket.ActorInstanceWebSocketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ActorInstanceServiceImplTest {

    @InjectMocks
    private ActorInstanceServiceImpl service;

    @Mock
    private ActorInstanceMapper actorInstanceMapper;
    @Mock
    private ActorInstanceComponentMapper actorInstanceComponentMapper;
    @Mock
    private ActorService actorService;
    @Mock
    private ActorComponentTreeService componentTreeService;
    @Mock
    private ActorInstanceComponentService actorInstanceComponentService;
    @Mock
    private ActorInstanceWebSocketService webSocketService;
    @Mock
    private ActorRenderConfigValidator actorRenderConfigValidator;
    @Mock
    private SceneMapper sceneMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(sceneMapper.selectById(anyLong())).thenReturn(null);
    }

    @Test
    @SuppressWarnings("unchecked")
    void createActorInstance_shouldPass_andCaptureGeneratedComponents_whenTemplateRenderConfigValid() {
        ActorInstanceDO instanceDO = new ActorInstanceDO();
        instanceDO.setId(100L);
        instanceDO.setActorCode("pump_actor");
        instanceDO.setInstanceCode("instance-001");

        ActorDO actorDO = buildActorWithComponentTree(
                "pump_actor",
                "PumpActor",
                "StaticMeshComponent0",
                "StaticMeshComponent",
                "{\"renderType\":\"gltf\",\"modelUrl\":\"https://asset.example.com/models/pump.glb\"}"
        );

        when(actorService.getActorByCode("pump_actor")).thenReturn(actorDO);

        doAnswer(invocation -> {
            ActorInstanceDO arg = invocation.getArgument(0);
            if (arg.getId() == null) {
                arg.setId(100L);
            }
            return 1;
        }).when(actorInstanceMapper).insert(any(ActorInstanceDO.class));

        doNothing().when(actorRenderConfigValidator).validateComponents(anyList());

        ArgumentCaptor<List<ActorInstanceComponentDO>> captor =
                (ArgumentCaptor<List<ActorInstanceComponentDO>>) (ArgumentCaptor<?>) ArgumentCaptor.forClass(List.class);

        Long result = service.createActorInstance(instanceDO);

        assertEquals(100L, result);
        verify(actorInstanceMapper, times(1)).insert(any(ActorInstanceDO.class));
        verify(actorRenderConfigValidator, times(1)).validateComponents(captor.capture());
        verify(actorInstanceComponentMapper, atLeastOnce()).insert(any(ActorInstanceComponentDO.class));

        List<ActorInstanceComponentDO> captured = captor.getValue();
        assertNotNull(captured);
        assertEquals(1, captured.size());

        ActorInstanceComponentDO component = captured.get(0);
        assertEquals("StaticMeshComponent0", component.getComponentCode());
        assertEquals("StaticMeshComponent", component.getComponentTypeName());
        assertEquals("instance-001", component.getInstanceCode());
        assertEquals("{\"renderType\":\"gltf\",\"modelUrl\":\"https://asset.example.com/models/pump.glb\"}", component.getMetadataJson());
        assertEquals("{}", component.getOverrideJson());
    }

    @Test
    void createActorInstance_shouldThrow_whenTemplateRenderConfigInvalid() {
        ActorInstanceDO instanceDO = new ActorInstanceDO();
        instanceDO.setId(100L);
        instanceDO.setActorCode("pump_actor");
        instanceDO.setInstanceCode("instance-001");

        ActorDO actorDO = buildActorWithComponentTree(
                "pump_actor",
                "PumpActor",
                "StaticMeshComponent0",
                "StaticMeshComponent",
                "{\"renderType\":\"splat\"}"
        );

        when(actorService.getActorByCode("pump_actor")).thenReturn(actorDO);

        doAnswer(invocation -> {
            ActorInstanceDO arg = invocation.getArgument(0);
            if (arg.getId() == null) {
                arg.setId(100L);
            }
            return 1;
        }).when(actorInstanceMapper).insert(any(ActorInstanceDO.class));

        doThrow(new ServiceException(400, "组件【StaticMeshComponent0】渲染配置非法，原因：renderType=splat 时 splatUrl 不能为空"))
                .when(actorRenderConfigValidator).validateComponents(anyList());

        ServiceException exception = assertThrows(ServiceException.class, () -> service.createActorInstance(instanceDO));

        assertTrue(exception.getMessage().contains("渲染配置非法"));
        verify(actorRenderConfigValidator, times(1)).validateComponents(anyList());
        verify(actorInstanceComponentMapper, never()).insert(any(ActorInstanceComponentDO.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    void spawnActorInstance_shouldPass_andCaptureConvertedComponents_whenFrontendComponentTreeRenderConfigValid() {
        ActorDO actorDO = new ActorDO();
        actorDO.setActorCode("tank_actor");
        actorDO.setActorName("TankActor");

        when(actorService.getActorByCode("tank_actor")).thenReturn(actorDO);

        doAnswer(invocation -> {
            ActorInstanceDO arg = invocation.getArgument(0);
            arg.setId(200L);
            return 1;
        }).when(actorInstanceMapper).insert(any(ActorInstanceDO.class));

        doNothing().when(actorRenderConfigValidator).validateComponents(anyList());

        ActorInstanceSpawnReqVO reqVO = new ActorInstanceSpawnReqVO();
        reqVO.setSceneId(1L);
        reqVO.setActorCode("tank_actor");
        reqVO.setInstanceCode("tank_001");
        reqVO.setInstanceName("1#储罐");
        reqVO.setComponentTree(buildFrontendComponentTree(
                "StaticMeshComponent0",
                "StaticMeshComponent",
                "{\"renderType\":\"splat\",\"splatUrl\":\"https://asset.example.com/splats/tank.spz\"}"
        ));

        ArgumentCaptor<List<ActorInstanceComponentDO>> captor =
                (ArgumentCaptor<List<ActorInstanceComponentDO>>) (ArgumentCaptor<?>) ArgumentCaptor.forClass(List.class);

        Long result = service.spawnActorInstance(reqVO);

        assertEquals(200L, result);
        verify(actorInstanceMapper, times(1)).insert(any(ActorInstanceDO.class));
        verify(actorRenderConfigValidator, times(1)).validateComponents(captor.capture());
        verify(actorInstanceComponentMapper, atLeastOnce()).insert(any(ActorInstanceComponentDO.class));
        verify(webSocketService, times(1))
                .broadcastPositionUpdate(eq("1"), eq(200L), eq("tank_001"), any());

        List<ActorInstanceComponentDO> captured = captor.getValue();
        assertNotNull(captured);
        assertEquals(1, captured.size());

        ActorInstanceComponentDO component = captured.get(0);
        assertEquals("StaticMeshComponent0", component.getComponentCode());
        assertEquals("StaticMeshComponent", component.getComponentTypeName());
        assertEquals("tank_001", component.getInstanceCode());
        assertEquals("{}", component.getMetadataJson());
        assertEquals("{\"renderType\":\"splat\",\"splatUrl\":\"https://asset.example.com/splats/tank.spz\"}", component.getOverrideJson());
        assertEquals("{}", component.getPropertiesJson());
        assertEquals("{}", component.getConstructArgsJson());
    }

    @Test
    void spawnActorInstance_shouldThrow_whenFrontendComponentTreeRenderConfigInvalid() {
        ActorDO actorDO = new ActorDO();
        actorDO.setActorCode("tank_actor");
        actorDO.setActorName("TankActor");

        when(actorService.getActorByCode("tank_actor")).thenReturn(actorDO);

        doAnswer(invocation -> {
            ActorInstanceDO arg = invocation.getArgument(0);
            arg.setId(200L);
            return 1;
        }).when(actorInstanceMapper).insert(any(ActorInstanceDO.class));

        doThrow(new ServiceException(400, "组件【StaticMeshComponent0】渲染配置非法，原因：splatUrl 后缀非法"))
                .when(actorRenderConfigValidator).validateComponents(anyList());

        ActorInstanceSpawnReqVO reqVO = new ActorInstanceSpawnReqVO();
        reqVO.setSceneId(1L);
        reqVO.setActorCode("tank_actor");
        reqVO.setInstanceCode("tank_001");
        reqVO.setInstanceName("1#储罐");
        reqVO.setComponentTree(buildFrontendComponentTree(
                "StaticMeshComponent0",
                "StaticMeshComponent",
                "{\"renderType\":\"splat\",\"splatUrl\":\"https://asset.example.com/splats/tank.glb\"}"
        ));

        ServiceException exception = assertThrows(ServiceException.class, () -> service.spawnActorInstance(reqVO));

        assertTrue(exception.getMessage().contains("渲染配置非法"));
        verify(actorRenderConfigValidator, times(1)).validateComponents(anyList());
        verify(actorInstanceComponentMapper, never()).insert(any(ActorInstanceComponentDO.class));
        verify(webSocketService, never()).broadcastPositionUpdate(anyString(), anyLong(), anyString(), any());
    }

    @Test
    @SuppressWarnings("unchecked")
    void spawnActorInstance_shouldPass_andCaptureGeneratedComponents_whenComponentTreeMissingAndTemplateRenderConfigValid() {
        ActorDO actorDO = buildActorWithComponentTree(
                "tank_actor",
                "TankActor",
                "StaticMeshComponent0",
                "StaticMeshComponent",
                "{\"renderType\":\"splat\",\"splatUrl\":\"https://asset.example.com/splats/tank.spz\"}"
        );

        when(actorService.getActorByCode("tank_actor")).thenReturn(actorDO);

        doAnswer(invocation -> {
            ActorInstanceDO arg = invocation.getArgument(0);
            arg.setId(300L);
            return 1;
        }).when(actorInstanceMapper).insert(any(ActorInstanceDO.class));

        doNothing().when(actorRenderConfigValidator).validateComponents(anyList());

        ActorInstanceSpawnReqVO reqVO = new ActorInstanceSpawnReqVO();
        reqVO.setSceneId(1L);
        reqVO.setActorCode("tank_actor");
        reqVO.setInstanceCode("tank_003");
        reqVO.setInstanceName("3#储罐");
        reqVO.setComponentTree(null);

        ArgumentCaptor<List<ActorInstanceComponentDO>> captor =
                (ArgumentCaptor<List<ActorInstanceComponentDO>>) (ArgumentCaptor<?>) ArgumentCaptor.forClass(List.class);

        Long result = service.spawnActorInstance(reqVO);

        assertEquals(300L, result);
        verify(actorInstanceMapper, times(1)).insert(any(ActorInstanceDO.class));
        verify(actorRenderConfigValidator, times(1)).validateComponents(captor.capture());
        verify(actorInstanceComponentMapper, atLeastOnce()).insert(any(ActorInstanceComponentDO.class));
        verify(webSocketService, times(1))
                .broadcastPositionUpdate(eq("1"), eq(300L), eq("tank_003"), any());

        List<ActorInstanceComponentDO> captured = captor.getValue();
        assertNotNull(captured);
        assertEquals(1, captured.size());

        ActorInstanceComponentDO component = captured.get(0);
        assertEquals("StaticMeshComponent0", component.getComponentCode());
        assertEquals("StaticMeshComponent", component.getComponentTypeName());
        assertEquals("tank_003", component.getInstanceCode());
        assertEquals("{\"renderType\":\"splat\",\"splatUrl\":\"https://asset.example.com/splats/tank.spz\"}", component.getMetadataJson());
        assertEquals("{}", component.getOverrideJson());
    }

    @Test
    void spawnActorInstance_shouldThrow_whenComponentTreeMissingAndTemplateRenderConfigInvalid() {
        ActorDO actorDO = buildActorWithComponentTree(
                "tank_actor",
                "TankActor",
                "StaticMeshComponent0",
                "StaticMeshComponent",
                "{\"renderType\":\"splat\"}"
        );

        when(actorService.getActorByCode("tank_actor")).thenReturn(actorDO);

        doAnswer(invocation -> {
            ActorInstanceDO arg = invocation.getArgument(0);
            arg.setId(300L);
            return 1;
        }).when(actorInstanceMapper).insert(any(ActorInstanceDO.class));

        doThrow(new ServiceException(400, "组件【StaticMeshComponent0】渲染配置非法，原因：renderType=splat 时 splatUrl 不能为空"))
                .when(actorRenderConfigValidator).validateComponents(anyList());

        ActorInstanceSpawnReqVO reqVO = new ActorInstanceSpawnReqVO();
        reqVO.setSceneId(1L);
        reqVO.setActorCode("tank_actor");
        reqVO.setInstanceCode("tank_003");
        reqVO.setInstanceName("3#储罐");
        reqVO.setComponentTree(null);

        ServiceException exception = assertThrows(
                ServiceException.class,
                () -> service.spawnActorInstance(reqVO)
        );

        assertTrue(exception.getMessage().contains("渲染配置非法"));
        verify(actorRenderConfigValidator, times(1)).validateComponents(anyList());
        verify(actorInstanceComponentMapper, never()).insert(any(ActorInstanceComponentDO.class));
        verify(webSocketService, never()).broadcastPositionUpdate(anyString(), anyLong(), anyString(), any());
    }

    private ActorDO buildActorWithComponentTree(String actorCode,
                                                String actorName,
                                                String componentCode,
                                                String componentTypeName,
                                                String metadataJson) {
        ActorDO actorDO = new ActorDO();
        actorDO.setActorCode(actorCode);
        actorDO.setActorName(actorName);

        ComponentTreeNode root = new ComponentTreeNode();
        root.setComponentCode(componentCode);
        root.setComponentTypeName(componentTypeName);
        root.setEnabledFlag(Boolean.TRUE);
        root.setMetadataJson(metadataJson);
        root.setPropertiesJson("{}");
        root.setConstructArgsJson("{}");

        ComponentTree tree = new ComponentTree();
        tree.setRoot(root);
        actorDO.setComponentTree(tree);

        return actorDO;
    }

    private ComponentTreeVO buildFrontendComponentTree(String componentCode,
                                                       String componentTypeName,
                                                       String overrideJson) {
        ComponentTreeNodeVO root = new ComponentTreeNodeVO();
        root.setComponentCode(componentCode);
        root.setComponentTypeName(componentTypeName);
        root.setEnabledFlag(Boolean.TRUE);
        root.setMetadataJson("{}");
        root.setPropertiesJson("{}");
        root.setConstructArgsJson("{}");
        root.setOverrideJson(overrideJson);

        ComponentTreeVO tree = new ComponentTreeVO();
        tree.setRoot(root);
        return tree;
    }
}
