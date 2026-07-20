package cn.iocoder.yudao.module.emergency.convert.backup;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.emergency.controller.admin.backup.vo.BackupRecordRespVO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.backup.BackupRecordDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 备份记录 Convert
 *
 * @author 芋道源码
 */
@Mapper
public interface BackupRecordConvert {

    BackupRecordConvert INSTANCE = Mappers.getMapper(BackupRecordConvert.class);

    BackupRecordRespVO convert(BackupRecordDO bean);

    List<BackupRecordRespVO> convertList(List<BackupRecordDO> list);

    PageResult<BackupRecordRespVO> convertPage(PageResult<BackupRecordDO> page);
}








