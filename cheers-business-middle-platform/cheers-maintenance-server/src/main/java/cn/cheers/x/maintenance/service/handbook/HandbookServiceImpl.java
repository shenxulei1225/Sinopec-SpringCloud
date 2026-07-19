package cn.cheers.x.maintenance.service.handbook;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.framework.common.util.object.BeanUtils;
import cn.cheers.x.maintenance.controller.admin.vo.handbook.*;
import cn.cheers.x.maintenance.dal.dataobject.FieldWorkStandardDO;
import cn.cheers.x.maintenance.dal.dataobject.HandbookDO;
import cn.cheers.x.maintenance.dal.mysql.FieldWorkStandardMapper;
import cn.cheers.x.maintenance.dal.mysql.HandbookMapper;
import cn.cheers.x.maintenance.enums.FieldWorkStandardStatusEnum;
import cn.cheers.x.maintenance.enums.HandbookStatusEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import static cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.cheers.x.maintenance.enums.ErrorCodeConstants.*;

@Service
@Validated
@Slf4j
public class HandbookServiceImpl implements HandbookService {

    @Resource
    private HandbookMapper handbookMapper;
    @Resource
    private FieldWorkStandardMapper fieldWorkStandardMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createHandbook(HandbookCreateReqVO createReqVO) {
        assertStandardExists(createReqVO.getFieldStandardId());
        Integer maxVersionNo = handbookMapper.selectMaxVersionNoByCode(createReqVO.getCode());
        int nextVersionNo = maxVersionNo == null ? 1 : maxVersionNo + 1;
        HandbookDO row = BeanUtils.toBean(createReqVO, HandbookDO.class);
        row.setVersionNo(nextVersionNo);
        row.setStatus(HandbookStatusEnum.DRAFT.getStatus());
        handbookMapper.insert(row);
        return row.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateHandbook(Long id, HandbookUpdateReqVO updateReqVO) {
        HandbookDO existing = validateExists(id);
        if (HandbookStatusEnum.PUBLISHED.getStatus().equals(existing.getStatus())) {
            throw exception(HANDBOOK_PUBLISHED_IMMUTABLE);
        }
        assertStandardExists(updateReqVO.getFieldStandardId());
        HandbookDO update = BeanUtils.toBean(updateReqVO, HandbookDO.class);
        update.setId(id);
        handbookMapper.updateById(update);
    }

    @Override
    public HandbookRespVO getHandbook(Long id) {
        return BeanUtils.toBean(validateExists(id), HandbookRespVO.class);
    }

    @Override
    public PageResult<HandbookRespVO> getHandbookPage(HandbookPageReqVO pageReqVO) {
        PageResult<HandbookDO> page = handbookMapper.selectPage(pageReqVO);
        return new PageResult<>(page.getList().stream()
                .map(row -> BeanUtils.toBean(row, HandbookRespVO.class)).toList(), page.getTotal());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long publishHandbook(Long id) {
        HandbookDO draft = validateExists(id);
        if (!HandbookStatusEnum.DRAFT.getStatus().equals(draft.getStatus())) {
            throw exception(HANDBOOK_PUBLISH_NOT_DRAFT);
        }
        FieldWorkStandardDO standard = fieldWorkStandardMapper.selectById(draft.getFieldStandardId());
        if (standard == null) {
            throw exception(FIELD_WORK_STANDARD_NOT_EXISTS);
        }
        if (!FieldWorkStandardStatusEnum.PUBLISHED.getStatus().equals(standard.getStatus())) {
            throw exception(HANDBOOK_STANDARD_NOT_PUBLISHED);
        }
        Integer maxVersionNo = handbookMapper.selectMaxVersionNoByCode(draft.getCode());
        int nextVersionNo = maxVersionNo == null ? 1 : maxVersionNo + 1;
        HandbookDO published = BeanUtils.toBean(draft, HandbookDO.class);
        published.setId(null);
        published.setVersionNo(nextVersionNo);
        published.setStatus(HandbookStatusEnum.PUBLISHED.getStatus());
        handbookMapper.insert(published);
        return published.getId();
    }

    private void assertStandardExists(Long fieldStandardId) {
        if (fieldWorkStandardMapper.selectById(fieldStandardId) == null) {
            throw exception(FIELD_WORK_STANDARD_NOT_EXISTS);
        }
    }

    private HandbookDO validateExists(Long id) {
        HandbookDO row = handbookMapper.selectById(id);
        if (row == null) {
            throw exception(HANDBOOK_NOT_EXISTS);
        }
        return row;
    }
}
