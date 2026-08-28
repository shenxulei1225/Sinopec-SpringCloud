package cn.cheers.x.module.dynamicbusiness.dal.mysql.inspection;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.inspection.EquipmentInspectionSopBindingDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EquipmentInspectionSopBindingMapper extends BaseMapperX<EquipmentInspectionSopBindingDO> {

    default EquipmentInspectionSopBindingDO selectByIdentity(
            Long equipmentId, Long inspectionItemId, String executionMeans) {
        return selectOne(new LambdaQueryWrapperX<EquipmentInspectionSopBindingDO>()
                .eq(EquipmentInspectionSopBindingDO::getEquipmentId, equipmentId)
                .eq(EquipmentInspectionSopBindingDO::getInspectionItemId, inspectionItemId)
                .eq(EquipmentInspectionSopBindingDO::getExecutionMeans, executionMeans));
    }

    default List<EquipmentInspectionSopBindingDO> selectBySopInstanceId(Long sopInstanceId) {
        return selectList(new LambdaQueryWrapperX<EquipmentInspectionSopBindingDO>()
                .eq(EquipmentInspectionSopBindingDO::getSopInstanceId, sopInstanceId));
    }

    default List<EquipmentInspectionSopBindingDO> selectByEquipmentAndItem(
            Long equipmentId, Long inspectionItemId) {
        return selectList(new LambdaQueryWrapperX<EquipmentInspectionSopBindingDO>()
                .eq(EquipmentInspectionSopBindingDO::getEquipmentId, equipmentId)
                .eq(EquipmentInspectionSopBindingDO::getInspectionItemId, inspectionItemId)
                .orderByAsc(EquipmentInspectionSopBindingDO::getExecutionMeans));
    }
}
