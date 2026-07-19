package cn.cheers.x.maintenance.service.standard;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.maintenance.controller.admin.vo.standard.FieldWorkStandardCreateReqVO;
import cn.cheers.x.maintenance.controller.admin.vo.standard.FieldWorkStandardStepVO;
import cn.cheers.x.maintenance.dal.dataobject.FieldWorkStandardDO;
import cn.cheers.x.maintenance.dal.mysql.FieldWorkStandardMapper;
import cn.cheers.x.maintenance.enums.FieldWorkStandardStatusEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FieldWorkStandardServiceImplTest {

    @Mock private FieldWorkStandardMapper fieldWorkStandardMapper;
    @InjectMocks private FieldWorkStandardServiceImpl fieldWorkStandardService;

    @Test
    @DisplayName("create 首条草稿 version_no=1")
    void createStandard_firstDraft_versionOne() {
        when(fieldWorkStandardMapper.selectMaxVersionNoByCode("pump-monthly")).thenReturn(null);
        doAnswer(inv -> { inv.getArgument(0, FieldWorkStandardDO.class).setId(10L); return 1; })
                .when(fieldWorkStandardMapper).insert(any(FieldWorkStandardDO.class));
        Long id = fieldWorkStandardService.createStandard(buildCreateReq());
        assertEquals(10L, id);
        ArgumentCaptor<FieldWorkStandardDO> captor = ArgumentCaptor.forClass(FieldWorkStandardDO.class);
        verify(fieldWorkStandardMapper).insert(captor.capture());
        assertEquals(1, captor.getValue().getVersionNo());
        assertEquals(FieldWorkStandardStatusEnum.DRAFT.getStatus(), captor.getValue().getStatus());
    }

    @Test
    @DisplayName("publish：草稿后首次发布插入新版本")
    void publishStandard_fromDraft_insertsPublished() {
        FieldWorkStandardDO draft = FieldWorkStandardDO.builder()
                .id(10L).code("pump-monthly").name("离心泵月检").scope("inspection").versionNo(1)
                .stepsJson("[{\"code\":\"s1\",\"title\":\"外观检查\",\"required\":true,\"controlType\":\"checkbox\"}]")
                .status(FieldWorkStandardStatusEnum.DRAFT.getStatus()).build();
        when(fieldWorkStandardMapper.selectById(10L)).thenReturn(draft);
        when(fieldWorkStandardMapper.selectMaxVersionNoByCode("pump-monthly")).thenReturn(1);
        doAnswer(inv -> { inv.getArgument(0, FieldWorkStandardDO.class).setId(20L); return 1; })
                .when(fieldWorkStandardMapper).insert(any(FieldWorkStandardDO.class));
        assertEquals(20L, fieldWorkStandardService.publishStandard(10L));
    }

    @Test
    @DisplayName("publish：已发布源不可再发布")
    void publishStandard_fromPublished_throws() {
        when(fieldWorkStandardMapper.selectById(10L)).thenReturn(FieldWorkStandardDO.builder()
                .id(10L).status(FieldWorkStandardStatusEnum.PUBLISHED.getStatus())
                .stepsJson("[]").build());
        assertThrows(ServiceException.class, () -> fieldWorkStandardService.publishStandard(10L));
    }

    private static FieldWorkStandardCreateReqVO buildCreateReq() {
        FieldWorkStandardStepVO step = new FieldWorkStandardStepVO();
        step.setCode("s1"); step.setTitle("外观检查"); step.setRequired(true); step.setControlType("checkbox");
        FieldWorkStandardCreateReqVO req = new FieldWorkStandardCreateReqVO();
        req.setCode("pump-monthly"); req.setName("离心泵月检"); req.setScope("inspection");
        req.setSteps(List.of(step));
        return req;
    }
}
