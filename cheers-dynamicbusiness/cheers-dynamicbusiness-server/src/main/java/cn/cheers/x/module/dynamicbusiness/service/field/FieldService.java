package cn.cheers.x.module.dynamicbusiness.service.field;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.field.vo.FieldCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.field.vo.FieldPageReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.field.vo.FieldRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.field.vo.FieldUpdateReqVO;

import java.util.List;

public interface FieldService {

    Long createField(FieldCreateReqVO reqVO);

    /**
     * 新建一个指定编码的单选引用字段，指向某本数据目录。
     * 数据资源管理里选「按某目录管理」、而字段库里还没有「目录编码_id」这个字段时调用。
     * 编码已被占用时报 400，不改已有字段。
     *
     * @param code              字段编码，例如 pipeline_id
     * @param name              字段名称，例如「所属管线」
     * @param targetEntityType  指向的数据目录注册编码
     * @return 新字段编号
     */
    Long createEntityRefField(String code, String name, String targetEntityType);

    void updateField(FieldUpdateReqVO reqVO);

    void deleteField(Long id);

    FieldRespVO getField(Long id);

    /** 按字段编号取一条。动作参数上记的是这个编号，不走全量列表。 */
    FieldRespVO getFieldByCode(String code);

    List<FieldRespVO> search(String keyword, String type, String source, Integer status,
                             Long effectiveFacilityId);

    /** 字段库全量列表（不分页），供前端字段池加载 */
    List<FieldRespVO> listAll(String type, String source, Integer status, Long effectiveFacilityId);

    PageResult<FieldRespVO> page(FieldPageReqVO reqVO);

    void enable(Long id);

    void disable(Long id);
}

