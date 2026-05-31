package cn.iocoder.yudao.module.system.service.view;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.system.controller.admin.view.vo.ViewCreateReqVO;
import cn.iocoder.yudao.module.system.controller.admin.view.vo.ViewRespVO;
import cn.iocoder.yudao.module.system.controller.admin.view.vo.ViewUpdateReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.view.ViewDO;
import cn.iocoder.yudao.module.system.dal.mysql.view.ViewMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 视图 Service 实现
 */
@Service
public class ViewServiceImpl implements ViewService {

    @Resource
    private ViewMapper viewMapper;

    @Resource
    private ObjectMapper objectMapper;

    @Override
    public Map<String, ViewRespVO> getEnabledViews() {
        List<ViewDO> list = viewMapper.selectEnabledList();
        Map<String, ViewRespVO> result = new LinkedHashMap<>();
        for (ViewDO view : list) {
            result.put(view.getKey(), convertToRespVO(view));
        }
        return result;
    }

    @Override
    public ViewRespVO getView(String key) {
        ViewDO view = viewMapper.selectByKey(key);
        return view == null ? null : convertToRespVO(view);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createView(ViewCreateReqVO reqVO) {
        if (viewMapper.existsByKey(reqVO.getKey())) {
            throw new RuntimeException("视图 key 已存在: " + reqVO.getKey());
        }

        ViewDO view = new ViewDO();
        view.setKey(reqVO.getKey());
        view.setLabel(reqVO.getLabel());
        view.setIcon(reqVO.getIcon());
        view.setComposition(toJson(reqVO.getComposition()));
        view.setIsTemplate(reqVO.getIsTemplate() != null ? reqVO.getIsTemplate() : Boolean.FALSE);
        view.setUiConfig(toJson(reqVO.getUiConfig()));
        view.setLayoutConfig(toJson(reqVO.getLayoutConfig()));
        view.setLayouts(toJson(reqVO.getLayouts()));
        view.setSort(reqVO.getSort());
        view.setDescription(reqVO.getDescription());
        view.setStatus(1);

        viewMapper.insert(view);
        return view.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateView(String key, ViewUpdateReqVO reqVO) {
        ViewDO view = viewMapper.selectByKey(key);
        if (view == null) {
            throw new RuntimeException("视图不存在: " + key);
        }

        if (reqVO.getLabel() != null) view.setLabel(reqVO.getLabel());
        if (reqVO.getIcon() != null) view.setIcon(reqVO.getIcon());
        if (reqVO.getComposition() != null) view.setComposition(toJson(reqVO.getComposition()));
        if (reqVO.getIsTemplate() != null) view.setIsTemplate(reqVO.getIsTemplate());
        if (reqVO.getUiConfig() != null) view.setUiConfig(toJson(reqVO.getUiConfig()));
        if (reqVO.getLayoutConfig() != null) view.setLayoutConfig(toJson(reqVO.getLayoutConfig()));
        if (reqVO.getLayouts() != null) view.setLayouts(toJson(reqVO.getLayouts()));
        if (reqVO.getStatus() != null) view.setStatus(reqVO.getStatus());
        if (reqVO.getSort() != null) view.setSort(reqVO.getSort());
        if (reqVO.getDescription() != null) view.setDescription(reqVO.getDescription());

        viewMapper.updateById(view);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteView(String key) {
        viewMapper.delete(new LambdaQueryWrapperX<ViewDO>()
                .eq(ViewDO::getKey, key));
    }

    @Override
    public List<ViewRespVO> getViewList() {
        return viewMapper.selectList(new LambdaQueryWrapperX<ViewDO>()
                .orderByAsc(ViewDO::getSort))
                .stream()
                .map(this::convertToRespVO)
                .toList();
    }

    // ==================== 私有方法 ====================

    private ViewRespVO convertToRespVO(ViewDO view) {
        ViewRespVO vo = BeanUtils.toBean(view, ViewRespVO.class);
        vo.setComposition(parseJson(view.getComposition(), Object.class));
        vo.setUiConfig(parseJson(view.getUiConfig(), new TypeReference<Map<String, Object>>() {}));
        vo.setLayoutConfig(parseJson(view.getLayoutConfig(), ViewRespVO.LayoutConfig.class));
        vo.setLayouts(parseJson(view.getLayouts(), new TypeReference<List<ViewRespVO.LayoutTemplate>>() {}));
        return vo;
    }

    private String toJson(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("JSON 序列化失败", e);
        }
    }

    private <T> T parseJson(String json, Class<T> clazz) {
        if (StrUtil.isEmpty(json)) {
            return null;
        }
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException("JSON 反序列化失败", e);
        }
    }

    private <T> T parseJson(String json, TypeReference<T> typeRef) {
        if (StrUtil.isEmpty(json)) {
            return null;
        }
        try {
            return objectMapper.readValue(json, typeRef);
        } catch (Exception e) {
            throw new RuntimeException("JSON 反序列化失败", e);
        }
    }

}