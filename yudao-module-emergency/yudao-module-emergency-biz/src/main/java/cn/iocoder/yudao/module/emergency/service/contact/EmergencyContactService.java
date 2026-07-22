package cn.iocoder.yudao.module.emergency.service.contact;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.emergency.controller.admin.contact.vo.*;

/**
 * 应急联络通讯录 Service 接口
 */
public interface EmergencyContactService {

    /**
     * 创建应急联络通讯录
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createContact(ContactCreateReqVO createReqVO);

    /**
     * 更新应急联络通讯录
     *
     * @param updateReqVO 更新信息
     */
    void updateContact(ContactUpdateReqVO updateReqVO);

    /**
     * 删除应急联络通讯录
     *
     * @param id 编号
     */
    void deleteContact(Long id);

    /**
     * 获得应急联络通讯录
     *
     * @param id 编号
     * @return 应急联络通讯录
     */
    ContactRespVO getContact(Long id);

    /**
     * 获得应急联络通讯录分页
     *
     * @param pageReqVO 分页查询
     * @return 应急联络通讯录分页
     */
    PageResult<ContactRespVO> getContactPage(ContactPageReqVO pageReqVO);

    /**
     * 搜索应急联络通讯录
     *
     * @param keyword 关键词
     * @param contactType 联系人类型
     * @return 应急联络通讯录列表
     */
    PageResult<ContactRespVO> searchContact(String keyword, String contactType);
}



