package cn.cheers.x.iot.dal.mysql.device;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.iot.controller.admin.device.vo.modbus.IotDeviceModbusPointPageReqVO;
import cn.cheers.x.iot.dal.dataobject.device.IotDeviceModbusPointDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

/**
 * IoT 设备 Modbus 点位配置 Mapper
 *
 * 
 */
@Mapper
public interface IotDeviceModbusPointMapper extends BaseMapperX<IotDeviceModbusPointDO> {

    default PageResult<IotDeviceModbusPointDO> selectPage(IotDeviceModbusPointPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<IotDeviceModbusPointDO>()
                .eqIfPresent(IotDeviceModbusPointDO::getDeviceId, reqVO.getDeviceId())
                .likeIfPresent(IotDeviceModbusPointDO::getIdentifier, reqVO.getIdentifier())
                .likeIfPresent(IotDeviceModbusPointDO::getName, reqVO.getName())
                .eqIfPresent(IotDeviceModbusPointDO::getFunctionCode, reqVO.getFunctionCode())
                .eqIfPresent(IotDeviceModbusPointDO::getStatus, reqVO.getStatus())
                .orderByDesc(IotDeviceModbusPointDO::getId));
    }

    default List<IotDeviceModbusPointDO> selectListByDeviceIdsAndStatus(Collection<Long> deviceIds, Integer status) {
        return selectList(new LambdaQueryWrapperX<IotDeviceModbusPointDO>()
                .in(IotDeviceModbusPointDO::getDeviceId, deviceIds)
                .eq(IotDeviceModbusPointDO::getStatus, status));
    }

    default IotDeviceModbusPointDO selectByDeviceIdAndIdentifier(Long deviceId, String identifier) {
        return selectOne(IotDeviceModbusPointDO::getDeviceId, deviceId,
                IotDeviceModbusPointDO::getIdentifier, identifier);
    }

    default void updateByThingModelId(Long thingModelId, IotDeviceModbusPointDO updateObj) {
        update(updateObj, new LambdaQueryWrapperX<IotDeviceModbusPointDO>()
                .eq(IotDeviceModbusPointDO::getThingModelId, thingModelId));
    }

}
