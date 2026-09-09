package cn.cheers.x.module.dynamicbusiness.service.sop;

import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.sop.vo.SopStandardPackRespVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.sop.SopItemPackDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.sop.SopScopeRuleDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.sop.SopItemPackMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.sop.SopScopeRuleMapper;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/**
 * SOP 标准包查询：只读 V103 范围/项包并补检查项展示名。
 */
@ExtendWith(MockitoExtension.class)
class SopStandardPackQueryServiceImplTest {

    @Mock
    private EntityService entityService;

    @Mock
    private SopScopeRuleMapper sopScopeRuleMapper;

    @Mock
    private SopItemPackMapper sopItemPackMapper;

    @InjectMocks
    private SopStandardPackQueryServiceImpl service;

    @Test
    void getStandardPack_returnsScopeAndItemRows() {
        EntityRespVO sop = new EntityRespVO();
        sop.setId(55L);
        when(entityService.get(55L, SopFieldCodes.ENTITY_TYPE_CODE)).thenReturn(sop);

        SopScopeRuleDO scope = SopScopeRuleDO.builder()
                .id(1L)
                .sopId(55L)
                .scopeType("CATEGORY")
                .targetId(8801L)
                .sortNo(10)
                .note("分类范围")
                .build();
        when(sopScopeRuleMapper.selectBySopId(55L)).thenReturn(List.of(scope));

        SopItemPackDO item = SopItemPackDO.builder()
                .id(2L)
                .sopId(55L)
                .inspectionItemId(701L)
                .required(true)
                .sortNo(20)
                .note("标准项")
                .build();
        when(sopItemPackMapper.selectBySopId(55L)).thenReturn(List.of(item));

        EntityRespVO inspectionItem = new EntityRespVO();
        inspectionItem.setId(701L);
        inspectionItem.setBaseFields(Map.of("name", "液位检查"));
        when(entityService.get(701L, "inspection_item")).thenReturn(inspectionItem);

        SopStandardPackRespVO resp = service.getStandardPack(55L);
        assertEquals(55L, resp.getSopId());
        assertEquals(1, resp.getScopeRules().size());
        assertEquals("CATEGORY", resp.getScopeRules().get(0).getScopeType());
        assertEquals(1, resp.getItemPack().size());
        assertEquals(701L, resp.getItemPack().get(0).getInspectionItemId());
        assertEquals("液位检查", resp.getItemPack().get(0).getInspectionItemName());
    }
}
