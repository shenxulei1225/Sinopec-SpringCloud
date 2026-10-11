SET client_encoding TO 'UTF8';
SET search_path TO dynamicbusiness;

SELECT id, name, code, execution_means, left(param_slots_json::text, 400) AS slots
FROM ent_action_t1
WHERE deleted = false
  AND (
    name IN (
      '拍照','单拍','悬停观察','悬停等待','调整拍摄角度','旋转云台','定向拍照',
      '开始录像','结束录像','全景拍照','变焦','对焦','降落','返回充电','无人机返航',
      '到达指定位置','飞行器偏航','航段间均匀转动云台俯仰角'
    )
    OR code IN ('takePhoto','hover','act-hover','act-robot-shoot','act-robot-aim','startRecord','stopRecord')
  )
ORDER BY name, id;
