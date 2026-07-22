package cn.cheers.x.scene.platform.service.scene.impl;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.scene.platform.controller.admin.scene.vo.FacilitySceneBindingRespVO;
import cn.cheers.x.scene.platform.dal.dataobject.scene.FacilitySceneBindingDO;
import cn.cheers.x.scene.platform.dal.dataobject.scene.SceneDO;
import cn.cheers.x.scene.platform.dal.mysql.scene.FacilitySceneBindingMapper;
import cn.cheers.x.scene.platform.dal.mysql.scene.SceneMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static cn.cheers.x.scene.platform.enums.ErrorCodeConstants.FACILITY_SCENE_BINDING_NOT_EXISTS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class FacilitySceneBindingServiceImplTest {

    @InjectMocks
    private FacilitySceneBindingServiceImpl service;

    @Mock
    private FacilitySceneBindingMapper facilitySceneBindingMapper;

    @Mock
    private SceneMapper sceneMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getByFacilityId_shouldReturnBindingWithSceneId_whenBindingExists() {
        FacilitySceneBindingDO binding = new FacilitySceneBindingDO();
        binding.setFacilityId(45L);
        binding.setFacilityCode("FAC-LUOYANG-SHENGRUI");
        binding.setSceneCode("SCENE-LUOYANG-SHENGRUI");
        when(facilitySceneBindingMapper.selectByFacilityId(45L)).thenReturn(binding);

        SceneDO scene = new SceneDO();
        scene.setId(10003L);
        scene.setSceneCode("SCENE-LUOYANG-SHENGRUI");
        when(sceneMapper.selectBySceneCode("SCENE-LUOYANG-SHENGRUI")).thenReturn(scene);

        FacilitySceneBindingRespVO respVO = service.getByFacilityId(45L);

        assertEquals(45L, respVO.getFacilityId());
        assertEquals("FAC-LUOYANG-SHENGRUI", respVO.getFacilityCode());
        assertEquals("SCENE-LUOYANG-SHENGRUI", respVO.getSceneCode());
        assertEquals(10003L, respVO.getSceneId());
    }

    @Test
    void getByFacilityId_shouldThrow_whenBindingMissing() {
        when(facilitySceneBindingMapper.selectByFacilityId(999L)).thenReturn(null);

        ServiceException ex = assertThrows(ServiceException.class, () -> service.getByFacilityId(999L));
        assertEquals(FACILITY_SCENE_BINDING_NOT_EXISTS.getCode(), ex.getCode());
    }

    @Test
    void getByFacilityCode_shouldReturnBinding_whenExists() {
        FacilitySceneBindingDO binding = new FacilitySceneBindingDO();
        binding.setFacilityId(44L);
        binding.setFacilityCode("FAC-JINQIAO");
        binding.setSceneCode("SCENE-JINQIAO");
        when(facilitySceneBindingMapper.selectByFacilityCode("FAC-JINQIAO")).thenReturn(binding);

        SceneDO scene = new SceneDO();
        scene.setId(10002L);
        scene.setSceneCode("SCENE-JINQIAO");
        when(sceneMapper.selectBySceneCode("SCENE-JINQIAO")).thenReturn(scene);

        FacilitySceneBindingRespVO respVO = service.getByFacilityCode("FAC-JINQIAO");

        assertEquals(44L, respVO.getFacilityId());
        assertEquals("FAC-JINQIAO", respVO.getFacilityCode());
        assertEquals(10002L, respVO.getSceneId());
    }

    @Test
    void listBySceneCode_shouldReturnAllMembers() {
        FacilitySceneBindingDO a = new FacilitySceneBindingDO();
        a.setFacilityId(44L);
        a.setFacilityCode("FAC-JINQIAO");
        a.setSceneCode("SCENE-SHARED");
        FacilitySceneBindingDO b = new FacilitySceneBindingDO();
        b.setFacilityId(45L);
        b.setFacilityCode("FAC-LUOYANG-SHENGRUI");
        b.setSceneCode("SCENE-SHARED");
        when(facilitySceneBindingMapper.selectListBySceneCode("SCENE-SHARED")).thenReturn(List.of(a, b));

        SceneDO scene = new SceneDO();
        scene.setId(20001L);
        scene.setSceneCode("SCENE-SHARED");
        when(sceneMapper.selectBySceneCode("SCENE-SHARED")).thenReturn(scene);

        List<FacilitySceneBindingRespVO> list = service.listBySceneCode("SCENE-SHARED");

        assertEquals(2, list.size());
        assertEquals(44L, list.get(0).getFacilityId());
        assertEquals(45L, list.get(1).getFacilityId());
        assertEquals(20001L, list.get(0).getSceneId());
        assertEquals(20001L, list.get(1).getSceneId());
    }
}
