package cn.cheers.x.member.service.level;

import cn.hutool.core.util.StrUtil;
import cn.cheers.x.framework.common.pojo.PageParam;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.member.controller.admin.level.vo.experience.MemberExperienceRecordPageReqVO;
import cn.cheers.x.member.convert.level.MemberExperienceRecordConvert;
import cn.cheers.x.member.dal.dataobject.level.MemberExperienceRecordDO;
import cn.cheers.x.member.dal.mysql.level.MemberExperienceRecordMapper;
import cn.cheers.x.member.enums.MemberExperienceBizTypeEnum;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.List;

/**
 * 会员经验记录 Service 实现类
 *
 * @author owen
 */
@Service
@Validated
public class MemberExperienceRecordServiceImpl implements MemberExperienceRecordService {

    @Resource
    private MemberExperienceRecordMapper experienceLogMapper;

    @Override
    public MemberExperienceRecordDO getExperienceRecord(Long id) {
        return experienceLogMapper.selectById(id);
    }

    @Override
    public PageResult<MemberExperienceRecordDO> getExperienceRecordPage(MemberExperienceRecordPageReqVO pageReqVO) {
        return experienceLogMapper.selectPage(pageReqVO);
    }

    @Override
    public PageResult<MemberExperienceRecordDO> getExperienceRecordPage(Long userId, PageParam pageParam) {
         return experienceLogMapper.selectPage(userId, pageParam);
    }

    @Override
    public void createExperienceRecord(Long userId, Integer experience, Integer totalExperience,
                                       MemberExperienceBizTypeEnum bizType, String bizId) {
        String description = StrUtil.format(bizType.getDescription(), experience);
        MemberExperienceRecordDO record = MemberExperienceRecordConvert.INSTANCE.convert(
                userId, experience, totalExperience,
                bizId, bizType.getType(), bizType.getTitle(), description);
        experienceLogMapper.insert(record);
    }

}
