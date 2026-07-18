package cn.cheers.x.workorder.service.standard;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.workorder.controller.admin.vo.standard.FieldWorkStandardCreateReqVO;
import cn.cheers.x.workorder.controller.admin.vo.standard.FieldWorkStandardStepVO;
import cn.cheers.x.workorder.dal.dataobject.FieldWorkStandardDO;
import cn.cheers.x.workorder.dal.mysql.FieldWorkStandardMapper;
import cn.cheers.x.workorder.enums.FieldWorkStandardStatusEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * FieldWorkStandardService 发布版本逻辑单元测试（Mockito，不启 Spring）
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("FieldWorkStandardService 单元测试")
class FieldWorkStandardServiceImplTest {

    @Mock
    private FieldWorkStandardMapper fieldWorkStandardMapper;

    @InjectMocks
    private FieldWorkStandardServiceImpl fieldWorkStandardService;

    @Test
    @DisplayName("create 首条草稿 version_no=1")
    void createStandard_firstDraft_versionOne() {
        FieldWorkStandardCreateReqVO req = buildCreateReq();
        when(fieldWorkStandardMapper.selectMaxVersionNoByCode("pump-monthly")).thenReturn(null);
        doAnswer(invocation -> {
            FieldWorkStandardDO row = invocation.getArgument(0);
            row.setId(10L);
            return 1;
        }).when(fieldWorkStandardMapper).insert(any(FieldWorkStandardDO.class));

        Long id = fieldWorkStandardService.createStandard(req);

        assertEquals(10L, id);
        ArgumentCaptor<FieldWorkStandardDO> captor = ArgumentCaptor.forClass(FieldWorkStandardDO.class);
        verify(fieldWorkStandardMapper).insert(captor.capture());
        FieldWorkStandardDO inserted = captor.getValue();
        assertEquals(1, inserted.getVersionNo());
        assertEquals(FieldWorkStandardStatusEnum.DRAFT.getStatus(), inserted.getStatus());
        assertEquals("pump-monthly", inserted.getCode());
    }

    @Test
    @DisplayName("publish：草稿 v1 后首次发布插入 v2")
    void publishStandard_fromDraftV1_insertsPublishedV2() {
        FieldWorkStandardDO draft = FieldWorkStandardDO.builder()
                .id(10L)
                .code("pump-monthly")
                .name("离心泵月检")
                .scope("inspection")
                .versionNo(1)
                .stepsJson("[{\"code\":\"s1\",\"title\":\"外观检查\",\"required\":true,\"controlType\":\"checkbox\"}]")
                .status(FieldWorkStandardStatusEnum.DRAFT.getStatus())
                .build();
        when(fieldWorkStandardMapper.selectById(10L)).thenReturn(draft);
        when(fieldWorkStandardMapper.selectMaxVersionNoByCode("pump-monthly")).thenReturn(1);
        doAnswer(invocation -> {
            FieldWorkStandardDO row = invocation.getArgument(0);
            row.setId(20L);
            return 1;
        }).when(fieldWorkStandardMapper).insert(any(FieldWorkStandardDO.class));

        Long publishedId = fieldWorkStandardService.publishStandard(10L);

        assertEquals(20L, publishedId);
        ArgumentCaptor<FieldWorkStandardDO> captor = ArgumentCaptor.forClass(FieldWorkStandardDO.class);
        verify(fieldWorkStandardMapper).insert(captor.capture());
        FieldWorkStandardDO published = captor.getValue();
        assertEquals(2, published.getVersionNo());
        assertEquals(FieldWorkStandardStatusEnum.PUBLISHED.getStatus(), published.getStatus());
        assertEquals(draft.getStepsJson(), published.getStepsJson());
        assertEquals("pump-monthly", published.getCode());
    }

    @Test
    @DisplayName("publish：源记录不存在时抛错")
    void publishStandard_notExists() {
        when(fieldWorkStandardMapper.selectById(99L)).thenReturn(null);
        assertThrows(ServiceException.class, () -> fieldWorkStandardService.publishStandard(99L));
    }

    private static FieldWorkStandardCreateReqVO buildCreateReq() {
        FieldWorkStandardStepVO step = new FieldWorkStandardStepVO();
        step.setCode("s1");
        step.setTitle("外观检查");
        step.setRequired(true);
        step.setControlType("checkbox");

        FieldWorkStandardCreateReqVO req = new FieldWorkStandardCreateReqVO();
        req.setCode("pump-monthly");
        req.setName("离心泵月检");
        req.setScope("inspection");
        req.setSteps(List.of(step));
        return req;
    }

}
