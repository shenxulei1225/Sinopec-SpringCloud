package cn.cheers.x.module.dynamicbusiness.controller.admin.model;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelFieldFilterReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelListBySceneReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelRespVO;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityService;
import cn.cheers.x.module.dynamicbusiness.service.model.ModelFacilityFootprintQueryService;
import cn.cheers.x.module.dynamicbusiness.service.model.ModelService;
import cn.cheers.x.module.dynamicbusiness.service.model.governance.LocalPackagePromotionService;
import cn.cheers.x.module.dynamicbusiness.service.model.relation.ModelCategoryRelationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ModelControllerListBySceneTest {

    @InjectMocks
    private ModelController modelController;

    @Mock
    private ModelService modelService;
    @Mock
    private ModelCategoryRelationService modelCategoryRelationService;
    @Mock
    private EntityService entityService;
    @Mock
    private LocalPackagePromotionService localPackagePromotionService;
    @Mock
    private ModelFacilityFootprintQueryService modelFacilityFootprintQueryService;

    @Test
    void listByScene_shouldApplyFieldFiltersAndKeywordTogether() {
        ModelListBySceneReqVO reqVO = new ModelListBySceneReqVO();
        reqVO.setEntityTypeCode("equipment");
        reqVO.setFilterMode("NONE");
        reqVO.setKeyword("泵");
        reqVO.setFieldFilters(List.of(buildFieldFilter("status", "EQ", 1)));

        when(modelService.listModelsByEntityType("equipment", null, "equipment", null))
                .thenReturn(List.of(
                        buildModel(1L, "主泵A", 1),
                        buildModel(2L, "主泵B", 0),
                        buildModel(3L, "风机C", 1)
                ));

        CommonResult<List<ModelRespVO>> result = modelController.listModelsByScene(reqVO);

        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals(1, result.getData().size());
        assertEquals("主泵A", result.getData().get(0).getName());
    }

    @Test
    void listByScene_withCategory_shouldKeepCategorySemanticsAndApplyFilters() {
        ModelListBySceneReqVO reqVO = new ModelListBySceneReqVO();
        reqVO.setEntityTypeCode("equipment");
        reqVO.setCategoryIds(List.of(101L));
        reqVO.setIncludeDescendants(true);
        reqVO.setDomain("巡检");
        reqVO.setKeyword("泵");
        reqVO.setFieldFilters(List.of(buildFieldFilter("status", "EQ", 1)));

        when(modelCategoryRelationService.listModelIdsByCategoryIdWithDescendants(101L, "equipment"))
                .thenReturn(List.of(11L, 12L, 13L));
        when(modelService.getModelsByIds(List.of(11L, 12L, 13L)))
                .thenReturn(List.of(
                        buildModel(11L, "巡检主泵", 1),
                        buildModel(12L, "巡检主泵停用", 0),
                        buildModel(13L, "巡检风机", 1)
                ));
        when(modelService.filterModelsByDomain(anyList(), eq("巡检")))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CommonResult<List<ModelRespVO>> result = modelController.listModelsByScene(reqVO);

        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals(1, result.getData().size());
        assertEquals(Long.valueOf(11L), result.getData().get(0).getId());
        verify(modelCategoryRelationService).listModelIdsByCategoryIdWithDescendants(101L, "equipment");
    }

    private static ModelFieldFilterReqVO buildFieldFilter(String fieldCode, String op, Object value) {
        ModelFieldFilterReqVO filter = new ModelFieldFilterReqVO();
        filter.setFieldCode(fieldCode);
        filter.setOp(op);
        filter.setValue(value);
        return filter;
    }

    private static ModelRespVO buildModel(Long id, String name, Integer status) {
        ModelRespVO model = new ModelRespVO();
        model.setId(id);
        model.setName(name);
        model.setStatus(status);
        model.setEntityTypeCode("equipment");
        return model;
    }
}
