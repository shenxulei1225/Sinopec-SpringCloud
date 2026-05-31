package cn.cheers.x.module.dynamicbusiness.service.field;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.field.vo.FieldCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.field.vo.FieldPageReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.field.vo.FieldRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.field.vo.FieldUpdateReqVO;

import java.util.List;

public interface FieldService {

    Long createField(FieldCreateReqVO reqVO);

    void updateField(FieldUpdateReqVO reqVO);

    void deleteField(Long id);

    FieldRespVO getField(Long id);

    List<FieldRespVO> search(String keyword, String type, String source, Integer status);

    PageResult<FieldRespVO> page(FieldPageReqVO reqVO);

    void enable(Long id);

    void disable(Long id);
}

