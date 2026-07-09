package cn.cheers.x.module.dynamicbusiness.service.business;

import cn.cheers.x.module.dynamicbusiness.controller.admin.business.vo.BusinessCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.business.vo.BusinessRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.business.vo.BusinessUpdateReqVO;

import java.util.List;

public interface BusinessService {

    Long create(BusinessCreateReqVO reqVO);

    void update(BusinessUpdateReqVO reqVO);

    void delete(Long id);

    BusinessRespVO get(Long id);

    BusinessRespVO getByCode(String code);

    List<BusinessRespVO> listTree();

    List<BusinessRespVO> listAll();

    void updateStatus(Long id, String status);
}
