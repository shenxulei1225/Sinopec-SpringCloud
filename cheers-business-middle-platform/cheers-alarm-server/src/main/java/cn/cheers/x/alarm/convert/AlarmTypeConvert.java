package cn.cheers.x.alarm.convert;

import cn.cheers.x.alarm.controller.admin.vo.type.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 告警类型 Convert
 * 
 * <p>告警类型使用系统的三层模型（metadata_category/metadata_model/metadata_entity），
 * 此 Convert 用于将三层模型数据转换为告警类型 VO</p>
 * 
 * <p>注意：由于告警类型复用 metadata 模块的三层模型，实际的数据转换需要在 Service 层
 * 从 metadata 表查询数据后手动构建 VO 对象，此 Convert 主要用于辅助转换</p>
 *
 * 
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AlarmTypeConvert {

    AlarmTypeConvert INSTANCE = Mappers.getMapper(AlarmTypeConvert.class);

    // ========== 分类（Category）转换 ==========

    /**
     * 转换分类列表
     * 
     * @param list 分类 VO 列表
     * @return 分类 VO 列表（深拷贝）
     */
    List<AlarmTypeCategoryVO> convertCategoryList(List<AlarmTypeCategoryVO> list);

    /**
     * 深拷贝分类 VO
     * 
     * @param bean 分类 VO
     * @return 分类 VO（深拷贝）
     */
    AlarmTypeCategoryVO convertCategory(AlarmTypeCategoryVO bean);

    // ========== 模型（Model）转换 ==========

    /**
     * 转换模型列表
     * 
     * @param list 模型 VO 列表
     * @return 模型 VO 列表（深拷贝）
     */
    List<AlarmTypeModelVO> convertModelList(List<AlarmTypeModelVO> list);

    /**
     * 深拷贝模型 VO
     * 
     * @param bean 模型 VO
     * @return 模型 VO（深拷贝）
     */
    AlarmTypeModelVO convertModel(AlarmTypeModelVO bean);

    // ========== 实体（Entity）转换 ==========

    /**
     * 转换实体列表
     * 
     * @param list 实体 VO 列表
     * @return 实体 VO 列表（深拷贝）
     */
    List<AlarmTypeEntityVO> convertEntityList(List<AlarmTypeEntityVO> list);

    /**
     * 深拷贝实体 VO
     * 
     * @param bean 实体 VO
     * @return 实体 VO（深拷贝）
     */
    AlarmTypeEntityVO convertEntity(AlarmTypeEntityVO bean);

    // ========== 工厂方法（用于 Service 层构建 VO）==========

    /**
     * 创建分类 VO
     * 
     * <p>用于 Service 层从 metadata_category 表数据构建 VO</p>
     * 
     * @param id 分类ID
     * @param code 分类编码
     * @param name 分类名称
     * @param sort 排序
     * @param status 状态
     * @return 分类 VO
     */
    default AlarmTypeCategoryVO createCategoryVO(Long id, String code, String name, Integer sort, Integer status) {
        AlarmTypeCategoryVO vo = new AlarmTypeCategoryVO();
        vo.setId(id);
        vo.setCode(code);
        vo.setName(name);
        vo.setSort(sort);
        vo.setStatus(status);
        return vo;
    }

    /**
     * 创建模型 VO
     * 
     * <p>用于 Service 层从 metadata_model 表数据构建 VO</p>
     * 
     * @param id 模型ID
     * @param code 模型编码
     * @param name 模型名称
     * @param categoryId 所属分类ID
     * @param sort 排序
     * @param status 状态
     * @return 模型 VO
     */
    default AlarmTypeModelVO createModelVO(Long id, String code, String name, Long categoryId, Integer sort, Integer status) {
        AlarmTypeModelVO vo = new AlarmTypeModelVO();
        vo.setId(id);
        vo.setCode(code);
        vo.setName(name);
        vo.setCategoryId(categoryId);
        vo.setSort(sort);
        vo.setStatus(status);
        return vo;
    }

    /**
     * 创建实体 VO
     * 
     * <p>用于 Service 层从 metadata_entity 表数据构建 VO</p>
     * 
     * @param id 实体ID
     * @param code 实体编码
     * @param name 实体名称
     * @param modelId 所属模型ID
     * @param sort 排序
     * @param status 状态
     * @param fullPath 完整路径
     * @return 实体 VO
     */
    default AlarmTypeEntityVO createEntityVO(Long id, String code, String name, Long modelId, Integer sort, Integer status, String fullPath) {
        AlarmTypeEntityVO vo = new AlarmTypeEntityVO();
        vo.setId(id);
        vo.setCode(code);
        vo.setName(name);
        vo.setModelId(modelId);
        vo.setSort(sort);
        vo.setStatus(status);
        vo.setFullPath(fullPath);
        return vo;
    }

}
