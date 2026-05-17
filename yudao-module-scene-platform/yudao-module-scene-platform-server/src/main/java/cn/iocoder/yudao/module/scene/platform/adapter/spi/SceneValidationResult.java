package cn.iocoder.yudao.module.scene.platform.adapter.spi;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SceneValidationResult {

    private boolean valid;

    private List<String> issues = new ArrayList<>();
}
