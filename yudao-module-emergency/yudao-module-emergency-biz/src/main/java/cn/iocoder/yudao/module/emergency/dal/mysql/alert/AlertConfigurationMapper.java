package cn.iocoder.yudao.module.emergency.dal.mysql.alert;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.emergency.dal.dataobject.alert.AlertConfigurationDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 告警配置 Mapper
 */
@Mapper
public interface AlertConfigurationMapper extends BaseMapperX<AlertConfigurationDO> {

    default AlertConfigurationDO selectByTypeAndLevel(String alertType, String alertLevel) {
        return selectOne(new LambdaQueryWrapperX<AlertConfigurationDO>()
                .eq(AlertConfigurationDO::getAlertType, alertType)
                .eq(AlertConfigurationDO::getAlertLevel, alertLevel)
                .eq(AlertConfigurationDO::getEnabled, true));
    }
}

