package cn.cheers.x.module.dynamicbusiness.service.sop;

import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.sop.vo.FlowStandardPackUpsertReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.sop.FlowItemPackDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.sop.FlowScopeRuleDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.sop.FlowItemPackMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.sop.FlowScopeRuleMapper;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * SOP 标准包命令：验证覆盖保存写入 V103 新表，不回写旧绑定。
 */
@ExtendWith(MockitoExtension.class)
class FlowStandardPackCommandServiceImplTest {

    @Mock
    private EntityService entityService;

    @Mock
    private FlowScopeRuleMapper flowScopeRuleMapper;

    @Mock
    private FlowItemPackMapper flowItemPackMapper;

    @InjectMocks
    private FlowStandardPackCommandServiceImpl service;

    @Test
    void saveStandardPack_overwritesRulesAndItemPack() {
        // Arrange
        EntityRespVO sop = new EntityRespVO();
        sop.setId(10L);
        when(entityService.get(10L, FlowFieldCodes.ENTITY_TYPE_CODE)).thenReturn(sop);

        EntityRespVO inspectionItem = new EntityRespVO();
        inspectionItem.setId(101L);
        inspectionItem.setBaseFields(Map.of("name", "检查项A"));
        when(entityService.get(eq(101L), eq("inspection_item"))).thenReturn(inspectionItem);

        FlowStandardPackUpsertReqVO req = new FlowStandardPackUpsertReqVO();

        FlowStandardPackUpsertReqVO.ScopeRuleUpsert scope = new FlowStandardPackUpsertReqVO.ScopeRuleUpsert();
        scope.setScopeType("category");
        scope.setTargetId(2001L);
        scope.setSortNo(30);
        scope.setNote("设备分类范围");
        req.setScopeRules(List.of(scope));

        FlowStandardPackUpsertReqVO.ItemPackRowUpsert item = new FlowStandardPackUpsertReqVO.ItemPackRowUpsert();
        item.setInspectionItemId(101L);
        item.setRequired(true);
        item.setSortNo(20);
        item.setNote("必做");
        req.setItemPack(List.of(item));

        // Act
        service.saveStandardPack(10L, req);

        // Assert
        verify(flowScopeRuleMapper).deleteByFlowId(10L);
        verify(flowItemPackMapper).deleteByFlowId(10L);

        ArgumentCaptor<FlowScopeRuleDO> scopeCaptor = ArgumentCaptor.forClass(FlowScopeRuleDO.class);
        verify(flowScopeRuleMapper, times(1)).insert(scopeCaptor.capture());
        assertEquals(10L, scopeCaptor.getValue().getFlowId());
        assertEquals("CATEGORY", scopeCaptor.getValue().getScopeType());
        assertEquals(2001L, scopeCaptor.getValue().getTargetId());

        ArgumentCaptor<FlowItemPackDO> itemCaptor = ArgumentCaptor.forClass(FlowItemPackDO.class);
        verify(flowItemPackMapper, times(1)).insert(itemCaptor.capture());
        assertEquals(10L, itemCaptor.getValue().getFlowId());
        assertEquals(101L, itemCaptor.getValue().getInspectionItemId());
        assertEquals(true, itemCaptor.getValue().getRequired());
        assertEquals(20, itemCaptor.getValue().getSortNo());
    }
}
