package cn.cheers.x.workorder.service.standard;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.framework.common.util.json.JsonUtils;
import cn.cheers.x.framework.common.util.object.BeanUtils;
import cn.cheers.x.workorder.controller.admin.vo.standard.FieldWorkStandardCreateReqVO;
import cn.cheers.x.workorder.controller.admin.vo.standard.FieldWorkStandardPageReqVO;
import cn.cheers.x.workorder.controller.admin.vo.standard.FieldWorkStandardRespVO;
import cn.cheers.x.workorder.controller.admin.vo.standard.FieldWorkStandardStepVO;
import cn.cheers.x.workorder.controller.admin.vo.standard.FieldWorkStandardUpdateReqVO;
import cn.cheers.x.workorder.dal.dataobject.FieldWorkStandardDO;
import cn.cheers.x.workorder.dal.mysql.FieldWorkStandardMapper;
import cn.cheers.x.workorder.enums.FieldWorkStandardStatusEnum;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Collections;
import java.util.List;

import static cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.cheers.x.workorder.enums.ErrorCodeConstants.FIELD_WORK_STANDARD_NOT_EXISTS;
import static cn.cheers.x.workorder.enums.ErrorCodeConstants.FIELD_WORK_STANDARD_PUBLISHED_IMMUTABLE;
import static cn.cheers.x.workorder.enums.ErrorCodeConstants.FIELD_WORK_STANDARD_STEPS_EMPTY;

/**
 * 现场作业标准 Service 实现
 */
@Service
@Validated
@Slf4j
public class FieldWorkStandardServiceImpl implements FieldWorkStandardService {

    @Resource
    private FieldWorkStandardMapper fieldWorkStandardMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createStandard(FieldWorkStandardCreateReqVO createReqVO) {
        validateStepsNotEmpty(createReqVO.getSteps());

        Integer maxVersionNo = fieldWorkStandardMapper.selectMaxVersionNoByCode(createReqVO.getCode());
        int nextVersionNo = maxVersionNo == null ? 1 : maxVersionNo + 1;

        FieldWorkStandardDO standard = new FieldWorkStandardDO();
        standard.setCode(createReqVO.getCode());
        standard.setName(createReqVO.getName());
        standard.setScope(createReqVO.getScope());
        standard.setVersionNo(nextVersionNo);
        standard.setStepsJson(JsonUtils.toJsonString(createReqVO.getSteps()));
        standard.setStatus(FieldWorkStandardStatusEnum.DRAFT.getStatus());
        fieldWorkStandardMapper.insert(standard);

        log.info("[createStandard][code={}, versionNo={}, id={}]",
                standard.getCode(), standard.getVersionNo(), standard.getId());
        return standard.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStandard(Long id, FieldWorkStandardUpdateReqVO updateReqVO) {
        FieldWorkStandardDO existing = validateExists(id);
        if (FieldWorkStandardStatusEnum.PUBLISHED.getStatus().equals(existing.getStatus())) {
            throw exception(FIELD_WORK_STANDARD_PUBLISHED_IMMUTABLE);
        }
        validateStepsNotEmpty(updateReqVO.getSteps());

        FieldWorkStandardDO updateObj = new FieldWorkStandardDO();
        updateObj.setId(id);
        updateObj.setName(updateReqVO.getName());
        updateObj.setScope(updateReqVO.getScope());
        updateObj.setStepsJson(JsonUtils.toJsonString(updateReqVO.getSteps()));
        fieldWorkStandardMapper.updateById(updateObj);

        log.info("[updateStandard][id={}]", id);
    }

    @Override
    public FieldWorkStandardRespVO getStandard(Long id) {
        return convert(validateExists(id));
    }

    @Override
    public PageResult<FieldWorkStandardRespVO> getStandardPage(FieldWorkStandardPageReqVO pageReqVO) {
        PageResult<FieldWorkStandardDO> page = fieldWorkStandardMapper.selectPage(pageReqVO);
        return new PageResult<>(page.getList().stream().map(this::convert).toList(), page.getTotal());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long publishStandard(Long id) {
        FieldWorkStandardDO draft = validateExists(id);
        List<FieldWorkStandardStepVO> steps = parseSteps(draft.getStepsJson());
        validateStepsNotEmpty(steps);

        Integer maxVersionNo = fieldWorkStandardMapper.selectMaxVersionNoByCode(draft.getCode());
        int nextVersionNo = maxVersionNo == null ? 1 : maxVersionNo + 1;

        FieldWorkStandardDO published = new FieldWorkStandardDO();
        published.setCode(draft.getCode());
        published.setName(draft.getName());
        published.setScope(draft.getScope());
        published.setVersionNo(nextVersionNo);
        published.setStepsJson(draft.getStepsJson());
        published.setStatus(FieldWorkStandardStatusEnum.PUBLISHED.getStatus());
        fieldWorkStandardMapper.insert(published);

        log.info("[publishStandard][sourceId={}, code={}, publishedId={}, versionNo={}]",
                id, published.getCode(), published.getId(), published.getVersionNo());
        return published.getId();
    }

    private FieldWorkStandardDO validateExists(Long id) {
        FieldWorkStandardDO standard = fieldWorkStandardMapper.selectById(id);
        if (standard == null) {
            throw exception(FIELD_WORK_STANDARD_NOT_EXISTS);
        }
        return standard;
    }

    private void validateStepsNotEmpty(List<FieldWorkStandardStepVO> steps) {
        if (steps == null || steps.isEmpty()) {
            throw exception(FIELD_WORK_STANDARD_STEPS_EMPTY);
        }
    }

    private FieldWorkStandardRespVO convert(FieldWorkStandardDO standard) {
        FieldWorkStandardRespVO respVO = BeanUtils.toBean(standard, FieldWorkStandardRespVO.class);
        respVO.setSteps(parseSteps(standard.getStepsJson()));
        return respVO;
    }

    private List<FieldWorkStandardStepVO> parseSteps(String stepsJson) {
        if (stepsJson == null || stepsJson.isBlank()) {
            return Collections.emptyList();
        }
        List<FieldWorkStandardStepVO> steps = JsonUtils.parseObject(stepsJson,
                new TypeReference<List<FieldWorkStandardStepVO>>() {});
        return steps == null ? Collections.emptyList() : steps;
    }

}
