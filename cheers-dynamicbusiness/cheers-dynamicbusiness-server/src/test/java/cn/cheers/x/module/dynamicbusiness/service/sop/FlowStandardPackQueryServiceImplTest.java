package cn.cheers.x.module.dynamicbusiness.service.sop;

import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.sop.vo.FlowStandardPackRespVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.sop.FlowItemPackDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.sop.FlowScopeRuleDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.sop.FlowItemPackMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.sop.FlowScopeRuleMapper;
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
class FlowStandardPackQueryServiceImplTest {

    @Mock
    private EntityService entityService;

    @Mock
    private FlowScopeRuleMapper flowScopeRuleMapper;

    @Mock
    private FlowItemPackMapper flowItemPackMapper;

    @InjectMocks
    private FlowStandardPackQueryServiceImpl service;

    @Test
    void getStandardPack_returnsScopeAndItemRows() {
        EntityRespVO sop = new EntityRespVO();
        sop.setId(55L);
        when(entityService.get(55L, FlowFieldCodes.ENTITY_TYPE_CODE)).thenReturn(sop);

        FlowScopeRuleDO scope = FlowScopeRuleDO.builder()
                .id(1L)
                .flowId(55L)
                .scopeType("CATEGORY")
                .targetId(8801L)
                .sortNo(10)
                .note("分类范围")
                .build();
        when(flowScopeRuleMapper.selectByFlowId(55L)).thenReturn(List.of(scope));

        FlowItemPackDO item = FlowItemPackDO.builder()
                .id(2L)
                .flowId(55L)
                .inspectionItemId(701L)
                .required(true)
                .sortNo(20)
                .note("标准项")
                .build();
        when(flowItemPackMapper.selectByFlowId(55L)).thenReturn(List.of(item));

        EntityRespVO inspectionItem = new EntityRespVO();
        inspectionItem.setId(701L);
        inspectionItem.setBaseFields(Map.of("name", "液位检查"));
        when(entityService.get(701L, "inspection_item")).thenReturn(inspectionItem);

        FlowStandardPackRespVO resp = service.getStandardPack(55L);
        assertEquals(55L, resp.getFlowId());
        assertEquals(1, resp.getScopeRules().size());
        assertEquals("CATEGORY", resp.getScopeRules().get(0).getScopeType());
        assertEquals(1, resp.getItemPack().size());
        assertEquals(701L, resp.getItemPack().get(0).getInspectionItemId());
        assertEquals("液位检查", resp.getItemPack().get(0).getInspectionItemName());
    }
}
