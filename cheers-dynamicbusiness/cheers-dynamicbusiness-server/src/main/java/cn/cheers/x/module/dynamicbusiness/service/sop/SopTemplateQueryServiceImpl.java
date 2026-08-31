package cn.cheers.x.module.dynamicbusiness.service.sop;

import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntitySceneQueryRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.FieldFilterReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.sop.vo.SopTemplatePageReqVO;
import cn.cheers.x.module.dynamicbusiness.enums.entity.EntityQueryScene;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * SOP 模板库列表：复用实体 query-by-scene 能力，在服务端强制模板行筛选。
 *
 * <p>不负责：实例行、merge 生效配置、升格写操作。</p>
 */
@Service
public class SopTemplateQueryServiceImpl implements SopTemplateQueryService {

    private static final String SOP_ENTITY_TYPE_CODE = "sop";
    private static final String SOP_CATEGORY_TYPE_CODE = "sop";
    private static final String IS_TEMPLATE_FIELD = "is_template";

    @Resource
    private EntityService entityService;

    @Override
    public EntitySceneQueryRespVO pageTemplates(SopTemplatePageReqVO reqVO) {
        String categoryTypeCode = StringUtils.hasText(reqVO.getCategoryTypeCode())
                ? reqVO.getCategoryTypeCode().trim()
                : SOP_CATEGORY_TYPE_CODE;
        String resultDetail = StringUtils.hasText(reqVO.getResultDetail())
                ? reqVO.getResultDetail().trim()
                : "LIGHT";

        List<FieldFilterReqVO> filters = new ArrayList<>(1);
        FieldFilterReqVO templateOnly = new FieldFilterReqVO();
        templateOnly.setFieldCode(IS_TEMPLATE_FIELD);
        templateOnly.setOp("eq");
        templateOnly.setValue(true);
        filters.add(templateOnly);

        return entityService.queryEntities(
                EntityQueryScene.ENTITIES_BY_CATEGORY,
                "PAGE",
                resultDetail,
                categoryTypeCode,
                SOP_ENTITY_TYPE_CODE,
                null,
                null,
                reqVO.getCategoryIds(),
                reqVO.getCategoryIdGroups(),
                null,
                null,
                null,
                null,
                null,
                reqVO.getPageNo(),
                reqVO.getPageSize(),
                reqVO.getKeyword(),
                null,
                filters,
                reqVO.getOrderByColumn(),
                reqVO.getIsAsc(),
                reqVO.getSearchFieldCodes());
    }

}
