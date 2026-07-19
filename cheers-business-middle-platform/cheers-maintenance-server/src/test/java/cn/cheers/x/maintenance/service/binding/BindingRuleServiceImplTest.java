package cn.cheers.x.maintenance.service.binding;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.maintenance.api.dto.BindingResolveReqDTO;
import cn.cheers.x.maintenance.dal.dataobject.BindingRuleDO;
import cn.cheers.x.maintenance.dal.dataobject.FieldWorkStandardDO;
import cn.cheers.x.maintenance.dal.mysql.BindingRuleMapper;
import cn.cheers.x.maintenance.dal.mysql.FieldWorkStandardMapper;
import cn.cheers.x.maintenance.enums.BindingRuleStatusEnum;
import cn.cheers.x.maintenance.enums.FieldWorkStandardStatusEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BindingRuleServiceImplTest {

    @Mock private BindingRuleMapper bindingRuleMapper;
    @Mock private FieldWorkStandardMapper fieldWorkStandardMapper;
    @InjectMocks private BindingRuleServiceImpl bindingRuleService;

    @Test
    @DisplayName("精确三维命中")
    void resolve_exactMatch() {
        BindingRuleDO exact = rule(1L, 10L, "PUMP", "M", 9L, 0);
        BindingRuleDO typeOnly = rule(2L, null, "PUMP", "M", 8L, 10);
        when(bindingRuleMapper.selectPublishedByScope("inspection")).thenReturn(List.of(typeOnly, exact));
        when(fieldWorkStandardMapper.selectById(9L)).thenReturn(published(9L, 2));
        var resp = bindingRuleService.resolve(BindingResolveReqDTO.builder()
                .assetId(10L).assetTypeCode("PUMP").frequencyCode("M").scope("inspection").build());
        assertEquals(9L, resp.getFieldStandardId());
    }

    @Test
    @DisplayName("特异度更高者优先")
    void resolve_higherSpecificityWins() {
        BindingRuleDO typeFreq = rule(1L, null, "PUMP", "M", 8L, 0);
        BindingRuleDO typeOnly = rule(2L, null, "PUMP", null, 7L, 100);
        when(bindingRuleMapper.selectPublishedByScope("inspection")).thenReturn(List.of(typeOnly, typeFreq));
        when(fieldWorkStandardMapper.selectById(8L)).thenReturn(published(8L, 1));
        var resp = bindingRuleService.resolve(BindingResolveReqDTO.builder()
                .assetId(10L).assetTypeCode("PUMP").frequencyCode("M").scope("inspection").build());
        assertEquals(8L, resp.getFieldStandardId());
    }

    @Test
    @DisplayName("仅有资产规则但请求无资产 → 不匹配")
    void resolve_assetRuleWithoutAsset_notFound() {
        when(bindingRuleMapper.selectPublishedByScope("inspection"))
                .thenReturn(List.of(rule(1L, 10L, "PUMP", "M", 9L, 0)));
        assertThrows(ServiceException.class, () -> bindingRuleService.resolve(BindingResolveReqDTO.builder()
                .assetTypeCode("PUMP").frequencyCode("M").scope("inspection").build()));
    }

    @Test
    @DisplayName("空库 → NOT_FOUND")
    void resolve_empty_notFound() {
        when(bindingRuleMapper.selectPublishedByScope("inspection")).thenReturn(List.of());
        assertThrows(ServiceException.class, () -> bindingRuleService.resolve(BindingResolveReqDTO.builder()
                .scope("inspection").build()));
    }

    private static BindingRuleDO rule(Long id, Long assetId, String type, String freq, Long stdId, int priority) {
        return BindingRuleDO.builder().id(id).assetId(assetId).assetTypeCode(type).frequencyCode(freq)
                .fieldStandardId(stdId).priority(priority)
                .status(BindingRuleStatusEnum.PUBLISHED.getStatus()).scope("inspection").build();
    }

    private static FieldWorkStandardDO published(Long id, int ver) {
        return FieldWorkStandardDO.builder().id(id).versionNo(ver)
                .status(FieldWorkStandardStatusEnum.PUBLISHED.getStatus()).build();
    }
}
