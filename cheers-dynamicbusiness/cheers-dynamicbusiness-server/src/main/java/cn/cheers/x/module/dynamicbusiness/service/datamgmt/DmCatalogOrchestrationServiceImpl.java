package cn.cheers.x.module.dynamicbusiness.service.datamgmt;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmCatalogOrchestrationBundleRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmCatalogOrchestrationBundleSaveReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datamgmt.DmCatalogOrchestrationDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.datamgmt.DmCatalogOrchestrationMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import java.util.Set;

/**
 * 数据目录编排头：只读写是否启用、点树还是点列表行。
 * <p>
 * 不负责开哪些栏、详情跟谁。禁止再读写 What/How 槽。
 */
@Service
@Validated
public class DmCatalogOrchestrationServiceImpl implements DmCatalogOrchestrationService {

    /** 内存语义：当前记录来源于列表行或分类节点。 */
    private static final Set<String> ALLOWED_SELECTION_SOURCES = Set.of("LIST_ROW", "CATEGORY_NODE");

    @Resource
    private DmCatalogOrchestrationMapper orchestrationMapper;

    @Override
    public DmCatalogOrchestrationBundleRespVO getBundle(String registryCode) {
        String code = normalizeCode(registryCode);
        DmCatalogOrchestrationDO head = orchestrationMapper.selectByEntityTypeCode(code);
        if (head == null) {
            throw new ServiceException(404, "数据目录编排未配置：" + code);
        }
        return assembleBundle(code, code, head);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBundle(DmCatalogOrchestrationBundleSaveReqVO reqVO) {
        String code = normalizeCode(reqVO.getRegistryCode());
        validateBundle(reqVO);

        DmCatalogOrchestrationDO head = orchestrationMapper.selectByEntityTypeCode(code);
        if (head == null) {
            head = new DmCatalogOrchestrationDO();
            head.setEntityTypeCode(code);
        }
        head.setEnabled(reqVO.getSemantic().getEnabled() == null || reqVO.getSemantic().getEnabled());
        head.setSelectionSource(normalizeSelectionSource(reqVO.getSelectionSource()));

        if (head.getId() == null) {
            orchestrationMapper.insert(head);
        } else {
            orchestrationMapper.updateById(head);
        }
    }

    private DmCatalogOrchestrationBundleRespVO assembleBundle(
            String registryCode,
            String storageCode,
            DmCatalogOrchestrationDO head) {
        DmCatalogOrchestrationBundleRespVO bundle = new DmCatalogOrchestrationBundleRespVO();
        bundle.setRegistryCode(registryCode);
        bundle.setStorageEntityTypeCode(storageCode);

        DmCatalogOrchestrationBundleRespVO.Semantic semantic = new DmCatalogOrchestrationBundleRespVO.Semantic();
        semantic.setEnabled(head.getEnabled());
        bundle.setSemantic(semantic);
        String selectionSource = normalizeSelectionSource(head.getSelectionSource());
        bundle.setSelectionSource(selectionSource);
        return bundle;
    }

    private void validateBundle(DmCatalogOrchestrationBundleSaveReqVO reqVO) {
        String selectionSource = normalizeSelectionSource(reqVO.getSelectionSource());
        if (!ALLOWED_SELECTION_SOURCES.contains(selectionSource)) {
            throw new ServiceException(400, "无效的 selectionSource: " + selectionSource);
        }
    }

    private String normalizeCode(String code) {
        if (!StringUtils.hasText(code)) {
            throw new ServiceException(400, "registryCode 不能为空");
        }
        return code.trim();
    }

    private String normalizeSelectionSource(String value) {
        if (!StringUtils.hasText(value)) {
            return "LIST_ROW";
        }
        return value.trim().toUpperCase();
    }
}
