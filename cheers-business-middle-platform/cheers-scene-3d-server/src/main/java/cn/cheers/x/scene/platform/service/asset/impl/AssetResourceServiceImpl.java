package cn.cheers.x.scene.platform.service.asset.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.framework.common.util.json.JsonUtils;
import cn.cheers.x.framework.common.util.object.BeanUtils;
import cn.cheers.x.infra.api.file.FileApi;
import cn.cheers.x.scene.platform.controller.admin.asset.vo.AssetResourceSaveReqVO;
import cn.cheers.x.scene.platform.controller.admin.asset.vo.SceneAssetRespVO;
import cn.cheers.x.scene.platform.dal.dataobject.asset.AssetResourceDO;
import cn.cheers.x.scene.platform.dal.mysql.asset.AssetResourceMapper;
import cn.cheers.x.scene.platform.service.asset.AssetConvertService;
import cn.cheers.x.scene.platform.service.asset.AssetResourceService;
import cn.cheers.x.scene.platform.service.asset.SceneAssetCategoryValidator;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static cn.cheers.x.framework.common.exception.enums.GlobalErrorCodeConstants.BAD_REQUEST;
import static cn.cheers.x.framework.common.exception.enums.GlobalErrorCodeConstants.NOT_FOUND;

@Service
public class AssetResourceServiceImpl implements AssetResourceService {

    @Resource
    private AssetResourceMapper assetResourceMapper;

    @Resource
    private FileApi fileApi;

    @Resource
    private AssetConvertService assetConvertService;

    @Resource
    private SceneAssetCategoryValidator sceneAssetCategoryValidator;

    /**
     * 本地 profile 可选：不依赖 infra 文件服务时写入此目录，并返回 {@link #localBaseUrl} 前缀 URL。
     */
    @Value("${cheers.scene-platform.asset-upload.local-base-path:}")
    private String localBasePath;

    @Value("${cheers.scene-platform.asset-upload.local-base-url:}")
    private String localBaseUrl;

    @Override
    public List<SceneAssetRespVO> getAssetList() {
        return BeanUtils.toBean(assetResourceMapper.selectList(), SceneAssetRespVO.class,
                item -> item.setMetadataJson(null));
    }

    @Override
    public SceneAssetRespVO getAsset(Long id) {
        return BeanUtils.toBean(getRequiredAsset(id), SceneAssetRespVO.class);
    }

    @Override
    public Long createAsset(AssetResourceSaveReqVO reqVO) {
        sceneAssetCategoryValidator.validateAssetTypeCode(reqVO.getAssetType());
        AssetResourceDO asset = BeanUtils.toBean(reqVO, AssetResourceDO.class);
        asset.setAssetType(reqVO.getAssetType().trim());
        asset.setStatus(1);
        assetResourceMapper.insert(asset);
        return asset.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAsset(Long id, AssetResourceSaveReqVO reqVO) {
        sceneAssetCategoryValidator.validateAssetTypeCode(reqVO.getAssetType());
        AssetResourceDO asset = getRequiredAsset(id);
        BeanUtils.copyProperties(reqVO, asset);
        asset.setAssetType(reqVO.getAssetType().trim());
        assetResourceMapper.updateById(asset);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAsset(Long id) {
        getRequiredAsset(id);
        assetResourceMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @SneakyThrows
    public SceneAssetRespVO uploadAsset(MultipartFile file, String assetCode, String assetName, String assetType) {
        validateUploadRequest(file, assetCode, assetName, assetType);

        String originalFilename = file.getOriginalFilename();
        byte[] content = file.getBytes();
        String contentType = file.getContentType();
        String assetUrl = storeFile(content, originalFilename, contentType);
        String format = AssetConvertServiceImpl.resolveFormat(null, originalFilename);

        AssetResourceDO asset = new AssetResourceDO();
        asset.setAssetCode(assetCode.trim());
        asset.setAssetName(assetName.trim());
        asset.setAssetType(assetType.trim());
        asset.setAssetUrl(assetUrl);
        asset.setFormat(format);
        asset.setEngineProfile("three-gltf");
        asset.setStatus(1);
        asset.setMetadataJson(buildInitialMetadataJson());
        assetResourceMapper.insert(asset);

        assetConvertService.convertToGlb(asset.getId());
        return getAsset(asset.getId());
    }

    private void validateUploadRequest(MultipartFile file, String assetCode, String assetName, String assetType) {
        if (file == null || file.isEmpty()) {
            throw ServiceExceptionUtil.exception(BAD_REQUEST, "上传文件不能为空");
        }
        if (StrUtil.hasBlank(assetCode, assetName, assetType)) {
            throw ServiceExceptionUtil.exception(BAD_REQUEST, "assetCode、assetName、assetType 不能为空");
        }
        sceneAssetCategoryValidator.validateAssetTypeCode(assetType);
    }

    private String storeFile(byte[] content, String originalFilename, String contentType) {
        if (StrUtil.isNotBlank(localBasePath)) {
            return storeFileLocally(content, originalFilename);
        }
        return fileApi.createFile(content, originalFilename, "scene-assets", contentType);
    }

    @SneakyThrows
    private String storeFileLocally(byte[] content, String originalFilename) {
        String safeName = StrUtil.blankToDefault(FileUtil.getName(originalFilename), "asset.bin");
        Path target = Paths.get(localBasePath, "scene-assets", safeName);
        Files.createDirectories(target.getParent());
        Files.write(target, content);
        String urlPrefix = StrUtil.blankToDefault(localBaseUrl, "file://" + localBasePath + "/scene-assets/");
        if (!urlPrefix.endsWith("/")) {
            urlPrefix = urlPrefix + "/";
        }
        return urlPrefix + safeName;
    }

    private static String buildInitialMetadataJson() {
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put(AssetConvertServiceImpl.METADATA_CONVERT_STATUS, AssetConvertServiceImpl.STATUS_PENDING);
        return JsonUtils.toJsonString(metadata);
    }

    private AssetResourceDO getRequiredAsset(Long id) {
        AssetResourceDO asset = assetResourceMapper.selectById(id);
        if (asset == null) {
            throw ServiceExceptionUtil.exception(NOT_FOUND, "资源不存在");
        }
        return asset;
    }
}
