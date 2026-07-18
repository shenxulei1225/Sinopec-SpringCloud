package cn.cheers.x.scene.platform.dal.dataobject.scene;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 过渡期：GIS 仅按 scene_code 解析 scene_id，只读 scene 表主键与编码。
 * 不依赖 scene-3d；后续改为调用方传入 sceneId 后可删除。
 */
@TableName("scene")
@Data
public class SceneIdentityDO {

    @TableId
    private Long id;

    private String sceneCode;
}
