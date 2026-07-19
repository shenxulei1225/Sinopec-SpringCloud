package cn.cheers.x.maintenance.service.binding;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.framework.common.util.object.BeanUtils;
import cn.cheers.x.maintenance.api.dto.BindingResolveReqDTO;
import cn.cheers.x.maintenance.api.dto.BindingResolveRespDTO;
import cn.cheers.x.maintenance.controller.admin.vo.binding.*;
import cn.cheers.x.maintenance.dal.dataobject.BindingRuleDO;
import cn.cheers.x.maintenance.dal.dataobject.FieldWorkStandardDO;
import cn.cheers.x.maintenance.dal.mysql.BindingRuleMapper;
import cn.cheers.x.maintenance.dal.mysql.FieldWorkStandardMapper;
import cn.cheers.x.maintenance.enums.BindingRuleStatusEnum;
import cn.cheers.x.maintenance.enums.FieldWorkStandardStatusEnum;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.cheers.x.maintenance.enums.ErrorCodeConstants.*;

@Service
@Validated
public class BindingRuleServiceImpl implements BindingRuleService {

    @Resource private BindingRuleMapper bindingRuleMapper;
    @Resource private FieldWorkStandardMapper fieldWorkStandardMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(BindingRuleCreateReqVO reqVO) {
        assertStandardPublished(reqVO.getFieldStandardId());
        BindingRuleDO row = BeanUtils.toBean(reqVO, BindingRuleDO.class);
        if (row.getPriority() == null) {
            row.setPriority(0);
        }
        row.setStatus(BindingRuleStatusEnum.DRAFT.getStatus());
        bindingRuleMapper.insert(row);
        return row.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, BindingRuleUpdateReqVO reqVO) {
        BindingRuleDO existing = validateExists(id);
        if (BindingRuleStatusEnum.PUBLISHED.getStatus().equals(existing.getStatus())) {
            throw exception(BINDING_RULE_PUBLISHED_IMMUTABLE);
        }
        assertStandardPublished(reqVO.getFieldStandardId());
        BindingRuleDO update = BeanUtils.toBean(reqVO, BindingRuleDO.class);
        update.setId(id);
        if (update.getPriority() == null) {
            update.setPriority(0);
        }
        bindingRuleMapper.updateById(update);
    }

    @Override
    public BindingRuleRespVO get(Long id) {
        return BeanUtils.toBean(validateExists(id), BindingRuleRespVO.class);
    }

    @Override
    public PageResult<BindingRuleRespVO> page(BindingRulePageReqVO reqVO) {
        PageResult<BindingRuleDO> page = bindingRuleMapper.selectPage(reqVO);
        return new PageResult<>(page.getList().stream()
                .map(r -> BeanUtils.toBean(r, BindingRuleRespVO.class)).toList(), page.getTotal());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long publish(Long id) {
        BindingRuleDO draft = validateExists(id);
        if (!BindingRuleStatusEnum.DRAFT.getStatus().equals(draft.getStatus())) {
            throw exception(BINDING_RULE_PUBLISH_NOT_DRAFT);
        }
        assertStandardPublished(draft.getFieldStandardId());
        BindingRuleDO published = BeanUtils.toBean(draft, BindingRuleDO.class);
        published.setId(null);
        published.setStatus(BindingRuleStatusEnum.PUBLISHED.getStatus());
        bindingRuleMapper.insert(published);
        return published.getId();
    }

    @Override
    public BindingResolveRespDTO resolve(BindingResolveReqDTO req) {
        List<BindingRuleDO> published = bindingRuleMapper.selectPublishedByScope(req.getScope());
        BindingRuleDO best = published.stream()
                .filter(rule -> matches(rule, req))
                .max(Comparator
                        .comparingInt(BindingRuleServiceImpl::specificity)
                        .thenComparing(BindingRuleDO::getPriority, Comparator.nullsFirst(Integer::compareTo))
                        .thenComparing(BindingRuleDO::getId))
                .orElse(null);
        if (best == null) {
            throw exception(BINDING_RULE_NOT_FOUND);
        }
        FieldWorkStandardDO standard = fieldWorkStandardMapper.selectById(best.getFieldStandardId());
        if (standard == null || !FieldWorkStandardStatusEnum.PUBLISHED.getStatus().equals(standard.getStatus())) {
            throw exception(FIELD_WORK_STANDARD_NOT_PUBLISHED);
        }
        return BindingResolveRespDTO.builder()
                .handbookId(best.getHandbookId())
                .fieldStandardId(best.getFieldStandardId())
                .standardVersionNo(standard.getVersionNo())
                .orchestrationTemplateCode(best.getOrchestrationTemplateCode())
                .build();
    }

    static int specificity(BindingRuleDO rule) {
        int score = 0;
        if (rule.getAssetId() != null) {
            score += 4;
        }
        if (StringUtils.hasText(rule.getAssetTypeCode())) {
            score += 2;
        }
        if (StringUtils.hasText(rule.getFrequencyCode())) {
            score += 1;
        }
        return score;
    }

    static boolean matches(BindingRuleDO rule, BindingResolveReqDTO req) {
        if (rule.getAssetId() != null && !Objects.equals(rule.getAssetId(), req.getAssetId())) {
            return false;
        }
        if (StringUtils.hasText(rule.getAssetTypeCode())
                && !rule.getAssetTypeCode().equals(req.getAssetTypeCode())) {
            return false;
        }
        if (StringUtils.hasText(rule.getFrequencyCode())
                && !rule.getFrequencyCode().equals(req.getFrequencyCode())) {
            return false;
        }
        return true;
    }

    private void assertStandardPublished(Long standardId) {
        FieldWorkStandardDO standard = fieldWorkStandardMapper.selectById(standardId);
        if (standard == null) {
            throw exception(FIELD_WORK_STANDARD_NOT_EXISTS);
        }
        if (!FieldWorkStandardStatusEnum.PUBLISHED.getStatus().equals(standard.getStatus())) {
            throw exception(FIELD_WORK_STANDARD_NOT_PUBLISHED);
        }
    }

    private BindingRuleDO validateExists(Long id) {
        BindingRuleDO row = bindingRuleMapper.selectById(id);
        if (row == null) {
            throw exception(BINDING_RULE_NOT_EXISTS);
        }
        return row;
    }
}
