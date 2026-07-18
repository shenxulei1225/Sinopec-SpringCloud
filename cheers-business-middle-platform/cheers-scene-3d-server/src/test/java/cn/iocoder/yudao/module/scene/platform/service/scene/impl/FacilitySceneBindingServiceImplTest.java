package cn.iocoder.yudao.module.scene.platform.service.scene.impl;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.module.scene.platform.controller.admin.scene.vo.FacilitySceneBindingRespVO;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.scene.FacilitySceneBindingDO;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.scene.SceneDO;
import cn.iocoder.yudao.module.scene.platform.dal.mysql.scene.FacilitySceneBindingMapper;
import cn.iocoder.yudao.module.scene.platform.dal.mysql.scene.SceneMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static cn.iocoder.yudao.module.scene.platform.enums.ErrorCodeConstants.FACILITY_SCENE_BINDING_NOT_EXISTS;
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
        binding.setSceneCode("SCENE-LUOYANG-SHENGRUI");
        when(facilitySceneBindingMapper.selectByFacilityId(45L)).thenReturn(binding);

        SceneDO scene = new SceneDO();
        scene.setId(10003L);
        scene.setSceneCode("SCENE-LUOYANG-SHENGRUI");
        when(sceneMapper.selectBySceneCode("SCENE-LUOYANG-SHENGRUI")).thenReturn(scene);

        FacilitySceneBindingRespVO respVO = service.getByFacilityId(45L);

        assertEquals(45L, respVO.getFacilityId());
        assertEquals("SCENE-LUOYANG-SHENGRUI", respVO.getSceneCode());
        assertEquals(10003L, respVO.getSceneId());
    }

    @Test
    void getByFacilityId_shouldThrow_whenBindingMissing() {
        when(facilitySceneBindingMapper.selectByFacilityId(999L)).thenReturn(null);

        ServiceException ex = assertThrows(ServiceException.class, () -> service.getByFacilityId(999L));
        assertEquals(FACILITY_SCENE_BINDING_NOT_EXISTS.getCode(), ex.getCode());
    }
}
