package cn.cheers.x.module.dynamicbusiness.service.orchestration;

import cn.cheers.x.framework.common.util.object.BeanUtils;
import cn.cheers.x.module.dynamicbusiness.controller.admin.orchestration.vo.BusinessOrchestrationRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.orchestration.vo.BusinessOrchestrationUpsertReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.orchestration.BusinessOrchestrationDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.orchestration.BusinessOrchestrationMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import static cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.cheers.x.framework.common.exception.enums.GlobalErrorCodeConstants.BAD_REQUEST;

/**
 * 业务编排服务实现。
 */
@Service
public class BusinessOrchestrationServiceImpl implements BusinessOrchestrationService {

    @Resource
    private BusinessOrchestrationMapper businessOrchestrationMapper;

    @Resource
    private ObjectMapper objectMapper;

    @Override
    public BusinessOrchestrationRespVO getByBusinessCode(String businessCode) {
        if (!StringUtils.hasText(businessCode)) {
            return null;
        }
        BusinessOrchestrationDO row =
                businessOrchestrationMapper.selectByBusinessCode(businessCode.trim());
        return row == null ? null : BeanUtils.toBean(row, BusinessOrchestrationRespVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long upsert(BusinessOrchestrationUpsertReqVO reqVO) {
        String code = reqVO.getBusinessCode().trim();
        String orchJson = requireJsonObject(reqVO.getOrchestrationJson(), "orchestrationJson");
        String canvasJson = null;
        if (StringUtils.hasText(reqVO.getCanvasJson())) {
            canvasJson = requireJsonObject(reqVO.getCanvasJson(), "canvasJson");
        }

        // 与权威 JSON 内 businessCode 对齐（若带了该字段）
        try {
            JsonNode root = objectMapper.readTree(orchJson);
            JsonNode codeNode = root.get("businessCode");
            if (codeNode != null && codeNode.isTextual() && StringUtils.hasText(codeNode.asText())) {
                if (!code.equals(codeNode.asText().trim())) {
                    throw exception(BAD_REQUEST, "编排 JSON 内 businessCode 与请求编码不一致");
                }
            }
        } catch (cn.cheers.x.framework.common.exception.ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw exception(BAD_REQUEST, "编排 JSON 无法解析");
        }

        BusinessOrchestrationDO existing = businessOrchestrationMapper.selectByBusinessCode(code);
        if (existing == null) {
            BusinessOrchestrationDO created = BusinessOrchestrationDO.builder()
                    .businessCode(code)
                    .businessName(trimToNull(reqVO.getBusinessName()))
                    .taskDomain(trimToNull(reqVO.getTaskDomain()))
                    .orchestrationJson(orchJson)
                    .canvasJson(canvasJson)
                    .build();
            businessOrchestrationMapper.insert(created);
            return created.getId();
        }

        existing.setBusinessName(trimToNull(reqVO.getBusinessName()));
        existing.setTaskDomain(trimToNull(reqVO.getTaskDomain()));
        existing.setOrchestrationJson(orchJson);
        existing.setCanvasJson(canvasJson);
        businessOrchestrationMapper.updateById(existing);
        return existing.getId();
    }

    private String requireJsonObject(String raw, String field) {
        String text = raw == null ? "" : raw.trim();
        if (!StringUtils.hasText(text)) {
            throw exception(BAD_REQUEST, field + " 不能为空");
        }
        try {
            JsonNode node = objectMapper.readTree(text);
            if (node == null || !node.isObject()) {
                throw exception(BAD_REQUEST, field + " 必须是 JSON 对象");
            }
            return objectMapper.writeValueAsString(node);
        } catch (cn.cheers.x.framework.common.exception.ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw exception(BAD_REQUEST, field + " 不是合法 JSON");
        }
    }

    private static String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }
}
