package cn.cheers.x.module.dynamicbusiness.service.sop;

import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.sop.vo.SopStandardPackUpsertReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.sop.SopItemPackDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.sop.SopScopeRuleDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.sop.SopItemPackMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.sop.SopScopeRuleMapper;
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
class SopStandardPackCommandServiceImplTest {

    @Mock
    private EntityService entityService;

    @Mock
    private SopScopeRuleMapper sopScopeRuleMapper;

    @Mock
    private SopItemPackMapper sopItemPackMapper;

    @InjectMocks
    private SopStandardPackCommandServiceImpl service;

    @Test
    void saveStandardPack_overwritesRulesAndItemPack() {
        // Arrange
        EntityRespVO sop = new EntityRespVO();
        sop.setId(10L);
        when(entityService.get(10L, SopFieldCodes.ENTITY_TYPE_CODE)).thenReturn(sop);

        EntityRespVO inspectionItem = new EntityRespVO();
        inspectionItem.setId(101L);
        inspectionItem.setBaseFields(Map.of("name", "检查项A"));
        when(entityService.get(eq(101L), eq("inspection_item"))).thenReturn(inspectionItem);

        SopStandardPackUpsertReqVO req = new SopStandardPackUpsertReqVO();

        SopStandardPackUpsertReqVO.ScopeRuleUpsert scope = new SopStandardPackUpsertReqVO.ScopeRuleUpsert();
        scope.setScopeType("category");
        scope.setTargetId(2001L);
        scope.setSortNo(30);
        scope.setNote("设备分类范围");
        req.setScopeRules(List.of(scope));

        SopStandardPackUpsertReqVO.ItemPackRowUpsert item = new SopStandardPackUpsertReqVO.ItemPackRowUpsert();
        item.setInspectionItemId(101L);
        item.setRequired(true);
        item.setSortNo(20);
        item.setNote("必做");
        req.setItemPack(List.of(item));

        // Act
        service.saveStandardPack(10L, req);

        // Assert
        verify(sopScopeRuleMapper).deleteBySopId(10L);
        verify(sopItemPackMapper).deleteBySopId(10L);

        ArgumentCaptor<SopScopeRuleDO> scopeCaptor = ArgumentCaptor.forClass(SopScopeRuleDO.class);
        verify(sopScopeRuleMapper, times(1)).insert(scopeCaptor.capture());
        assertEquals(10L, scopeCaptor.getValue().getSopId());
        assertEquals("CATEGORY", scopeCaptor.getValue().getScopeType());
        assertEquals(2001L, scopeCaptor.getValue().getTargetId());

        ArgumentCaptor<SopItemPackDO> itemCaptor = ArgumentCaptor.forClass(SopItemPackDO.class);
        verify(sopItemPackMapper, times(1)).insert(itemCaptor.capture());
        assertEquals(10L, itemCaptor.getValue().getSopId());
        assertEquals(101L, itemCaptor.getValue().getInspectionItemId());
        assertEquals(true, itemCaptor.getValue().getRequired());
        assertEquals(20, itemCaptor.getValue().getSortNo());
    }
}
