package cn.cheers.x.workorder.service.standard;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.workorder.controller.admin.vo.standard.FieldWorkStandardCreateReqVO;
import cn.cheers.x.workorder.controller.admin.vo.standard.FieldWorkStandardPageReqVO;
import cn.cheers.x.workorder.controller.admin.vo.standard.FieldWorkStandardRespVO;
import cn.cheers.x.workorder.controller.admin.vo.standard.FieldWorkStandardUpdateReqVO;
import jakarta.validation.Valid;

/**
 * 现场作业标准 Service
 */
public interface FieldWorkStandardService {

    /**
     * 创建草稿标准。version_no = 该 code 当前最大版本 + 1（首条为 1），status = 草稿。
     *
     * @param createReqVO 创建请求
     * @return 新建记录 id
     */
    Long createStandard(@Valid FieldWorkStandardCreateReqVO createReqVO);

    /**
     * 更新草稿标准（名称 / scope / steps）。已发布版本不可改。
     *
     * @param id          标准 id
     * @param updateReqVO 更新请求
     */
    void updateStandard(Long id, @Valid FieldWorkStandardUpdateReqVO updateReqVO);

    /**
     * 按 id 查询
     *
     * @param id 标准 id
     * @return 响应 VO
     */
    FieldWorkStandardRespVO getStandard(Long id);

    /**
     * 分页查询
     *
     * @param pageReqVO 分页条件
     * @return 分页结果
     */
    PageResult<FieldWorkStandardRespVO> getStandardPage(FieldWorkStandardPageReqVO pageReqVO);

    /**
     * 发布新版本：以草稿 code 取最大 version_no，插入 version_no+1 的已发布行；
     * steps_json 取自草稿；旧版本保持可查。
     *
     * <p>约定：create 首条草稿 version_no=1；首次 publish 插入 version_no=2。</p>
     *
     * @param id 草稿（或源）标准 id
     * @return 新发布版本 id
     */
    Long publishStandard(Long id);

}
