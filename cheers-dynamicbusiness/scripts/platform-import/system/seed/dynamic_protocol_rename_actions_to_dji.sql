-- 动作名称改成大疆文档里的叫法。
-- 负责：已对上某条服务指令的动作，用文档里的中文名称。
-- 不负责：没有对应文档条目的动作（调整拍摄角度、拍照、降落）不另起一个文档里没有的名字。

SET search_path TO dynamicbusiness;

BEGIN;

UPDATE ent_action_t1 a
SET name = v.new_name,
    custom_fields = CASE
      WHEN a.custom_fields ? 'name'
        THEN jsonb_set(a.custom_fields, '{name}', to_jsonb(v.new_name))
      ELSE a.custom_fields
    END,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM (VALUES
  ('action-065340437bcb46788b7dc261fab81c63', '飞向目标点'),
  ('action-2b44a1f882f54438b6a1864e3636c889', '一键起飞'),
  ('action-1022d966da2b45f7b73e776112e6884b', '一键返航'),
  ('act-uav-return-charge', '一键返航'),
  ('action-a2bf6d76c8d04602bab0d18aa62f6958', '开始直播'),
  ('action-a2f8ee6c1ab5495fa565742c82d97413', '停止直播'),
  ('megaphone', '喊话器-开始播放音频'),
  ('focus', '点对焦'),
  ('takePhoto', '开始拍照'),
  ('stopRecord', '停止录像')
) AS v(code, new_name)
WHERE a.deleted = false
  AND a.tenant_id = 1
  AND a.code = v.code;

COMMIT;
