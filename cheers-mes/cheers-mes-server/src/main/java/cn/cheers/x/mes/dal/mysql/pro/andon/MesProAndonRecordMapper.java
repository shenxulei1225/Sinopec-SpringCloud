package cn.cheers.x.mes.dal.mysql.pro.andon;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.mes.controller.admin.pro.andon.vo.record.MesProAndonRecordPageReqVO;
import cn.cheers.x.mes.dal.dataobject.pro.andon.MesProAndonRecordDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * MES 安灯呼叫记录 Mapper
 *
 * 
 */
@Mapper
public interface MesProAndonRecordMapper extends BaseMapperX<MesProAndonRecordDO> {

    default PageResult<MesProAndonRecordDO> selectPage(MesProAndonRecordPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MesProAndonRecordDO>()
                .eqIfPresent(MesProAndonRecordDO::getWorkstationId, reqVO.getWorkstationId())
                .eqIfPresent(MesProAndonRecordDO::getUserId, reqVO.getUserId())
                .eqIfPresent(MesProAndonRecordDO::getHandlerUserId, reqVO.getHandlerUserId())
                .eqIfPresent(MesProAndonRecordDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(MesProAndonRecordDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(MesProAndonRecordDO::getId));
    }

}
