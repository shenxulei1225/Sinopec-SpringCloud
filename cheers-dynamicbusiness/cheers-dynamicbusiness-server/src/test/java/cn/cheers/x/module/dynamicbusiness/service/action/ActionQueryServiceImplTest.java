package cn.cheers.x.module.dynamicbusiness.service.action;

import cn.cheers.x.module.dynamicbusiness.controller.admin.action.vo.ActionListItemRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.action.vo.ActionListReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.action.ActionEnablementMapper;
import cn.cheers.x.module.dynamicbusiness.dal.repository.entity.EntityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 契约：传了 owner 且启用为空 → 空列表，且不得再查动作实体全库。
 */
@ExtendWith(MockitoExtension.class)
class ActionQueryServiceImplTest {

    @Mock
    private ActionEnablementMapper actionEnablementMapper;

    @Mock
    private EntityRepository entityRepository;

    @InjectMocks
    private ActionQueryServiceImpl actionQueryService;

    @Test
    void listActions_ownerWithEmptyEnablement_returnsEmpty_andDoesNotLoadActions() {
        when(actionEnablementMapper.selectByOwner("model", 99L)).thenReturn(List.of());

        ActionListReqVO req = new ActionListReqVO();
        req.setOwnerKind("model");
        req.setOwnerId(99L);

        List<ActionListItemRespVO> result = actionQueryService.listActions(req);

        assertTrue(result.isEmpty());
        verify(entityRepository, never()).findByIdsWithDedicatedBaseFields(anyList(), anyString());
        verify(entityRepository, never()).findPageIds(any());
    }

    @Test
    void toItem_returnsActionOwnershipContractFromDedicatedFields() {
        EntityDO entity = actionEntity(Map.of(
                ActionFieldCodes.ACTION_DOMAIN, "INTELLIGENT_ANALYSIS",
                ActionFieldCodes.ANALYSIS_CAPABILITY_ID, 41L));

        ActionListItemRespVO item = ReflectionTestUtils.invokeMethod(
                ActionQueryServiceImpl.class, "toItem", entity);

        assertEquals("INTELLIGENT_ANALYSIS", item.getActionDomain());
        assertEquals(41L, item.getAnalysisCapabilityId());
    }

    @Test
    void toItem_keepsMissingActionOwnershipContractNull() {
        ActionListItemRespVO item = ReflectionTestUtils.invokeMethod(
                ActionQueryServiceImpl.class, "toItem", actionEntity(Map.of()));

        assertNull(item.getActionDomain());
        assertNull(item.getAnalysisCapabilityId());
    }

    private static EntityDO actionEntity(Map<String, Object> dedicatedFields) {
        return EntityDO.builder()
                .id(1L)
                .code("act-analysis")
                .name("智能分析")
                .dedicatedBaseFieldValues(dedicatedFields)
                .build();
    }
}
