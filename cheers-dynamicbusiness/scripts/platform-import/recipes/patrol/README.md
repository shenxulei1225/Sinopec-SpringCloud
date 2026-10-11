# 巡检 · platform-import 补丁

## 对齐审计（一次性跑）

```bash
psql ... -f audit_protocol_alignment.sql
python audit_mapping_gaps.py   # 指令必填路径 & ACTION_PARAM 槽 vs 动作 param_slots
```

现网批量补齐（建议在 uav-ws 录像补丁之后执行）：

```bash
psql ... -f patch_action_alignment_batch.sql
psql ... -f patch_dji_payload_and_source_kind.sql
```

来源分组：**ACTION_PARAM**（界面「动作参数」）= 检查项/设备检查里填写并冻结进步骤的字段；旧键 **USER** 读入时等同 ACTION_PARAM，与登录用户无关。

## 已知的试译缺槽根因（对照表之外）

| 现象 | 根因层 | 处理 |
|------|--------|------|
| `gimbalPitchRotateAngle` 等 | 步骤图冻结：`orientedShoot` 未走 `resolveStepParams` | **代码** `TaskStepTreeGenerateService`（已修） |
| `hoverTime` | 动作 `hover` 无 param_slots | `patch_hover_param_slots.sql` |
| uav-ws 录像「参数未齐」 | 对照 `param_slot_mapping_json` 为空 | `patch_uav_ws_task_record_mapping.sql` |
| 底部「耗时没算出来」 | 动作库 `action_duration`/型号耗时 + 任务已选设备型号 | 动作库与设备型号，不是对照表 |

## patch_uav_ws_task_record_mapping.sql

**无人机对接协议（uav-ws）** 下「开始录像 / 结束录像」对照行若已存在但 **`param_slot_mapping_json` 为空**，指令协议对接页会显示 **「参数未齐」**（不是动作库缺字段）。本补丁写入与种子相同的 7/2 条对照。

```bash
psql -h 127.0.0.1 -U postgres -d sinopec -v ON_ERROR_STOP=1 \
  -f patch_uav_ws_task_record_mapping.sql
```

## patch_robot_ws_task_record_mapping.sql

为 **robot-ws** 补任务级 `startRecord` / `stopRecord` 与 `200201` / `200202` 的指令协议对接。新装环境已在 `system/seed/dynamic_protocol_standard_action_mapping_rest.sql` 里包含，**现网缺对照行时**单独执行本补丁。

```bash
psql -h 127.0.0.1 -U postgres -d sinopec -v ON_ERROR_STOP=1 \
  -f patch_robot_ws_task_record_mapping.sql
```

执行后可在「指令协议对接」数据目录检索 `map-start-record-robot-ws`、`map-stop-record-robot-ws`。

## patch_hover_param_slots.sql

为 **`hover`（悬停等待）** 补动作参数槽 `hoverTime`（默认 5 秒）。协议对照已映射 `params.hoverTime`，若动作库没有该槽，任务向导第 3 步试译会报「缺少动作参数来源字段 hoverTime」。

```bash
psql -h 127.0.0.1 -U postgres -d sinopec -v ON_ERROR_STOP=1 \
  -f patch_hover_param_slots.sql
```

执行后回到任务向导第 3 步，改排期或重选设备触发重新试排即可。
