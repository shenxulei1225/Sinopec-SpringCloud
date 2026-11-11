-- 宇视感知联网平台 OpenAPI pro V1.23 · HTTPS 说明书（第一批）
-- 权威：OpenAPI pro 接口说明书（感知联网平台）V1.23 + 官方登录/资源/实况 demo
-- 只写登录、保活、退出、查摄像机目录。实况取流、云台、回放下一批再写。
-- 会话 / 心跳 / 目录查询不是任务动作，本文件不建动作，也不写指令协议对接。
-- 不写入站场账号口令，不编播放地址，不自动创建设备台账。
-- 租户：智慧巡检(1)、智慧管廊(123)。目录须已用创建接口建好（含专用表 ent_data_protocol_t{tenant}）。
SET search_path TO dynamicbusiness, public;

BEGIN;

CREATE TEMP TABLE uniview_https_command (
  code text,
  name text,
  exchange_mode text,
  http_method text,
  http_path text,
  custom_fields jsonb
) ON COMMIT DROP;

INSERT INTO uniview_https_command (
  code, name, exchange_mode, http_method, http_path, custom_fields
) VALUES
  (
    'proto-uniview-https-login',
    '登录',
    'sync_wait_result',
    'POST',
    '/VIID/login/v2',
    $uv${
      "opcode": 0,
      "command_direction": "downlink",
      "exchange_mode": "sync_wait_result",
      "protocol_version": ["uniview-openapi-1.23"],
      "communication_protocol": "https",
      "http_method": "POST",
      "http_path": "/VIID/login/v2",
      "command_json": {
        "outbound": {
          "httpMethod": "POST",
          "path": "/VIID/login/v2",
          "steps": [
            {"step": 1, "header": {}, "body": {}},
            {"step": 2, "header": {"Content-Type": "application/json"}, "body": {"UserName": "", "AccessCode": "", "LoginSignature": ""}}
          ]
        },
        "inboundSuccess": {"AccessToken": "", "ErrCode": 0, "ErrMsg": ""},
        "inboundFailure": {"ErrCode": 0, "ErrMsg": ""}
      },
      "field_description_json": {
        "outbound": [
          {"code": "httpMethod", "path": "httpMethod", "type": "string", "label": "HTTP方法", "required": true, "description": "POST"},
          {"code": "path", "path": "path", "type": "string", "label": "HTTP路径", "required": true, "description": "/VIID/login/v2。旧 demo 曾用 /VIID/login，现网登录按 v2。"},
          {"code": "steps", "path": "steps", "type": "array", "label": "登录两步", "required": true, "description": "第一步空体取 AccessCode；第二步带 UserName、AccessCode、LoginSignature。"},
          {"code": "UserName", "path": "steps.1.body.UserName", "type": "string", "label": "用户名", "required": true, "description": "调用时填写，种子不预填站场账号。"},
          {"code": "AccessCode", "path": "steps.1.body.AccessCode", "type": "string", "label": "接入码", "required": true, "description": "第一步响应里的 AccessCode。"},
          {"code": "LoginSignature", "path": "steps.1.body.LoginSignature", "type": "string", "label": "登录签名", "required": true, "description": "MD5(Base64(用户名) + AccessCode + MD5(密码))。"}
        ],
        "inboundSuccess": [
          {"code": "AccessToken", "path": "AccessToken", "type": "string", "label": "访问令牌", "required": true, "description": "后续请求放在 Authorization 请求头。"},
          {"code": "ErrCode", "path": "ErrCode", "type": "integer", "label": "错误码", "required": false, "description": "0 表示成功。"},
          {"code": "ErrMsg", "path": "ErrMsg", "type": "string", "label": "错误说明", "required": false, "description": null}
        ],
        "inboundFailure": [
          {"code": "ErrCode", "path": "ErrCode", "type": "integer", "label": "错误码", "required": true, "description": null},
          {"code": "ErrMsg", "path": "ErrMsg", "type": "string", "label": "错误说明", "required": true, "description": null}
        ]
      },
      "sample_json": {
        "outbound": {
          "httpMethod": "POST",
          "path": "/VIID/login/v2",
          "steps": [
            {"step": 1, "header": {}, "body": {}},
            {"step": 2, "header": {"Content-Type": "application/json"}, "body": {"UserName": "", "AccessCode": "", "LoginSignature": ""}}
          ]
        },
        "inboundSuccess": {"AccessToken": "", "ErrCode": 0, "ErrMsg": ""}
      }
    }$uv$::jsonb
  ),
  (
    'proto-uniview-https-keepalive',
    '保活',
    'heartbeat',
    'POST',
    '/VIID/hadesadapter/user/keepalive',
    $uv${
      "opcode": 0,
      "command_direction": "downlink",
      "exchange_mode": "heartbeat",
      "protocol_version": ["uniview-openapi-1.23"],
      "communication_protocol": "https",
      "http_method": "POST",
      "http_path": "/VIID/hadesadapter/user/keepalive",
      "command_json": {
        "outbound": {
          "httpMethod": "POST",
          "path": "/VIID/hadesadapter/user/keepalive",
          "header": {"Authorization": "", "Content-Type": "application/json"},
          "body": {}
        },
        "inboundSuccess": {"ErrCode": 0, "ErrMsg": ""},
        "inboundFailure": {"ErrCode": 0, "ErrMsg": ""}
      },
      "field_description_json": {
        "outbound": [
          {"code": "httpMethod", "path": "httpMethod", "type": "string", "label": "HTTP方法", "required": true, "description": "POST"},
          {"code": "path", "path": "path", "type": "string", "label": "HTTP路径", "required": true, "description": "/VIID/hadesadapter/user/keepalive"},
          {"code": "header", "path": "header", "type": "object", "label": "请求头", "required": true, "description": "Authorization 填登录拿到的 AccessToken，种子留空。"},
          {"code": "body", "path": "body", "type": "object", "label": "请求体", "required": false, "description": "官方实况 demo 空体发送。"}
        ],
        "inboundSuccess": [
          {"code": "ErrCode", "path": "ErrCode", "type": "integer", "label": "错误码", "required": true, "description": "0 表示成功。"},
          {"code": "ErrMsg", "path": "ErrMsg", "type": "string", "label": "错误说明", "required": false, "description": null}
        ],
        "inboundFailure": [
          {"code": "ErrCode", "path": "ErrCode", "type": "integer", "label": "错误码", "required": true, "description": null},
          {"code": "ErrMsg", "path": "ErrMsg", "type": "string", "label": "错误说明", "required": true, "description": null}
        ]
      },
      "sample_json": {
        "outbound": {
          "httpMethod": "POST",
          "path": "/VIID/hadesadapter/user/keepalive",
          "header": {"Authorization": "", "Content-Type": "application/json"},
          "body": {}
        },
        "inboundSuccess": {"ErrCode": 0, "ErrMsg": ""}
      }
    }$uv$::jsonb
  ),
  (
    'proto-uniview-https-logout',
    '退出',
    'sync_wait_result',
    'POST',
    '/VIID/logout',
    $uv${
      "opcode": 0,
      "command_direction": "downlink",
      "exchange_mode": "sync_wait_result",
      "protocol_version": ["uniview-openapi-1.23"],
      "communication_protocol": "https",
      "http_method": "POST",
      "http_path": "/VIID/logout",
      "command_json": {
        "outbound": {
          "httpMethod": "POST",
          "path": "/VIID/logout",
          "header": {"Authorization": ""},
          "body": {}
        },
        "inboundSuccess": {"ErrCode": 0, "ErrMsg": ""},
        "inboundFailure": {"ErrCode": 0, "ErrMsg": ""}
      },
      "field_description_json": {
        "outbound": [
          {"code": "httpMethod", "path": "httpMethod", "type": "string", "label": "HTTP方法", "required": true, "description": "POST"},
          {"code": "path", "path": "path", "type": "string", "label": "HTTP路径", "required": true, "description": "/VIID/logout"},
          {"code": "header", "path": "header", "type": "object", "label": "请求头", "required": true, "description": "Authorization 填 AccessToken，种子留空。"},
          {"code": "body", "path": "body", "type": "object", "label": "请求体", "required": false, "description": "官方 demo 空体发送。"}
        ],
        "inboundSuccess": [
          {"code": "ErrCode", "path": "ErrCode", "type": "integer", "label": "错误码", "required": true, "description": "0 表示成功。"},
          {"code": "ErrMsg", "path": "ErrMsg", "type": "string", "label": "错误说明", "required": false, "description": null}
        ],
        "inboundFailure": [
          {"code": "ErrCode", "path": "ErrCode", "type": "integer", "label": "错误码", "required": true, "description": null},
          {"code": "ErrMsg", "path": "ErrMsg", "type": "string", "label": "错误说明", "required": true, "description": null}
        ]
      },
      "sample_json": {
        "outbound": {
          "httpMethod": "POST",
          "path": "/VIID/logout",
          "header": {"Authorization": ""},
          "body": {}
        },
        "inboundSuccess": {"ErrCode": 0, "ErrMsg": ""}
      }
    }$uv$::jsonb
  ),
  (
    'proto-uniview-https-query-camera',
    '查摄像机目录',
    'sync_wait_result',
    'GET',
    '/VIID/query',
    $uv${
      "opcode": 0,
      "command_direction": "downlink",
      "exchange_mode": "sync_wait_result",
      "protocol_version": ["uniview-openapi-1.23"],
      "communication_protocol": "https",
      "http_method": "GET",
      "http_path": "/VIID/query",
      "command_json": {
        "outbound": {
          "httpMethod": "GET",
          "path": "/VIID/query",
          "header": {"Authorization": ""},
          "query": {
            "org": "",
            "condition": {
              "ItemNum": 0,
              "Condition": [
                {"QueryType": 256, "LogicFlag": 0, "QueryData": ""},
                {"QueryType": 257, "LogicFlag": 0, "QueryData": "1"},
                {"QueryType": 1, "LogicFlag": 5, "QueryData": ""}
              ],
              "QueryCount": 1,
              "PageFirstRowNumber": 0,
              "PageRowNum": 200
            }
          }
        },
        "inboundSuccess": {
          "ErrCode": 0,
          "ErrMsg": "",
          "Result": {
            "InfoList": [
              {"ResItemV1": {"ResName": "", "ResCode": "", "ResType": 0, "ResStatus": 0, "OrgCode": ""}}
            ]
          }
        },
        "inboundFailure": {"ErrCode": 0, "ErrMsg": ""}
      },
      "field_description_json": {
        "outbound": [
          {"code": "httpMethod", "path": "httpMethod", "type": "string", "label": "HTTP方法", "required": true, "description": "GET"},
          {"code": "path", "path": "path", "type": "string", "label": "HTTP路径", "required": true, "description": "/VIID/query"},
          {"code": "header", "path": "header", "type": "object", "label": "请求头", "required": true, "description": "Authorization 填 AccessToken，种子留空。"},
          {"code": "org", "path": "query.org", "type": "string", "label": "组织编码", "required": true, "description": "本地域组织编码，调用时填写，种子不预填。"},
          {"code": "condition", "path": "query.condition", "type": "object", "label": "查询条件", "required": true, "description": "官方 demo 把 condition 序列化成 JSON 字符串作为查询参数。"},
          {"code": "QueryType256", "path": "query.condition.Condition.0", "type": "object", "label": "资源类型条件", "required": true, "description": "QueryType=256。QueryData 填摄像机资源类型码，由现场 VM 配置决定，种子不写死。"},
          {"code": "QueryType257", "path": "query.condition.Condition.1", "type": "object", "label": "是否含子组织", "required": true, "description": "QueryType=257。QueryData=1 表示包含子组织。"},
          {"code": "QueryType1", "path": "query.condition.Condition.2", "type": "object", "label": "名称条件", "required": false, "description": "QueryType=1。按名称过滤时填写，可空。"}
        ],
        "inboundSuccess": [
          {"code": "ErrCode", "path": "ErrCode", "type": "integer", "label": "错误码", "required": true, "description": "0 表示成功。"},
          {"code": "ErrMsg", "path": "ErrMsg", "type": "string", "label": "错误说明", "required": false, "description": null},
          {"code": "InfoList", "path": "Result.InfoList", "type": "array", "label": "资源列表", "required": true, "description": "每项含 ResItemV1。"},
          {"code": "ResName", "path": "Result.InfoList[].ResItemV1.ResName", "type": "string", "label": "资源名称", "required": true, "description": "与设备台账名称唯一匹配时用这个字段。"},
          {"code": "ResCode", "path": "Result.InfoList[].ResItemV1.ResCode", "type": "string", "label": "摄像机编码", "required": true, "description": "写入设备台账流编号的权威值。"},
          {"code": "ResType", "path": "Result.InfoList[].ResItemV1.ResType", "type": "integer", "label": "资源类型", "required": false, "description": null},
          {"code": "ResStatus", "path": "Result.InfoList[].ResItemV1.ResStatus", "type": "integer", "label": "资源状态", "required": false, "description": "1 在线，2 离线，0 不可用。"},
          {"code": "OrgCode", "path": "Result.InfoList[].ResItemV1.OrgCode", "type": "string", "label": "所属组织编码", "required": false, "description": null}
        ],
        "inboundFailure": [
          {"code": "ErrCode", "path": "ErrCode", "type": "integer", "label": "错误码", "required": true, "description": null},
          {"code": "ErrMsg", "path": "ErrMsg", "type": "string", "label": "错误说明", "required": true, "description": null}
        ]
      },
      "sample_json": {
        "outbound": {
          "httpMethod": "GET",
          "path": "/VIID/query",
          "header": {"Authorization": ""},
          "query": {
            "org": "",
            "condition": {
              "ItemNum": 3,
              "Condition": [
                {"QueryType": 256, "LogicFlag": 0, "QueryData": ""},
                {"QueryType": 257, "LogicFlag": 0, "QueryData": "1"},
                {"QueryType": 1, "LogicFlag": 5, "QueryData": ""}
              ],
              "QueryCount": 1,
              "PageFirstRowNumber": 0,
              "PageRowNum": 200
            }
          }
        },
        "inboundSuccess": {
          "ErrCode": 0,
          "ErrMsg": "",
          "Result": {
            "InfoList": [
              {"ResItemV1": {"ResName": "", "ResCode": "", "ResType": 0, "ResStatus": 1, "OrgCode": ""}}
            ]
          }
        }
      }
    }$uv$::jsonb
  );

DO $$
DECLARE
  tid bigint;
  ent_table text;
  cat_rel_table text;
  idx_table text;
  model_cat_table text;
  root_id bigint;
  model_id bigint;
  cat_id bigint;
  proto_options text := '[{"label":"机器人对接协议","value":"robot-ws"},{"label":"无人机对接协议","value":"uav-ws"},{"label":"大疆机场 1.16.1","value":"dji-dock-1.16.1"},{"label":"大疆 Pilot 1.16.1","value":"dji-pilot-1.16.1"},{"label":"宇视 OpenAPI 1.23","value":"uniview-openapi-1.23"}]';
BEGIN
  FOREACH tid IN ARRAY ARRAY[1::bigint, 123::bigint]
  LOOP
    IF NOT EXISTS (
      SELECT 1 FROM dynamic_entity_type
      WHERE deleted = false AND tenant_id = tid AND code = 'data_protocol'
    ) THEN
      RAISE NOTICE 'skip tenant %: data_protocol catalog missing', tid;
      CONTINUE;
    END IF;

    ent_table := format('ent_data_protocol_t%s', tid);
    cat_rel_table := format('dynamic_entity_category_relation_t%s', tid);
    idx_table := format('dynamic_entity_field_index_t%s', tid);
    model_cat_table := format('dynamic_model_category_relation_t%s', tid);

    IF to_regclass('dynamicbusiness.' || ent_table) IS NULL THEN
      RAISE NOTICE 'skip tenant %: table % missing', tid, ent_table;
      CONTINUE;
    END IF;

    UPDATE dynamic_field
    SET options = proto_options::jsonb,
        updater = 'seed',
        update_time = CURRENT_TIMESTAMP
    WHERE deleted = false
      AND tenant_id = tid
      AND code IN ('protocol_version', 'protocol_versions', 'platform_protocol', 'mapped_protocol_version');

    SELECT id INTO root_id
    FROM dynamic_category
    WHERE deleted = false AND tenant_id = tid AND code = 'data_protocol_root'
    LIMIT 1;

    IF root_id IS NULL THEN
      RAISE NOTICE 'skip tenant %: data_protocol_root missing', tid;
      CONTINUE;
    END IF;

    INSERT INTO dynamic_category (
      parent_id, name, code, category_type_code, tree_path, level, sort, status,
      description, tenant_id, creator, parent_code
    )
    SELECT
      root_id,
      '宇视视频',
      'cat-protocol-uniview-video',
      'data_protocol',
      p.tree_path,
      COALESCE(p.level, 1) + 1,
      30,
      1,
      '宇视感知联网平台（VM）OpenAPI HTTPS 指令',
      tid,
      'seed',
      p.code
    FROM dynamic_category p
    WHERE p.id = root_id
      AND NOT EXISTS (
        SELECT 1 FROM dynamic_category c
        WHERE c.deleted = false AND c.tenant_id = tid
          AND c.category_type_code = 'data_protocol'
          AND (c.code = 'cat-protocol-uniview-video' OR c.name = '宇视视频')
      );

    UPDATE dynamic_category c
    SET
      code = 'cat-protocol-uniview-video',
      name = '宇视视频',
      description = '宇视感知联网平台（VM）OpenAPI HTTPS 指令',
      parent_id = root_id,
      parent_code = 'data_protocol_root',
      tree_path = p.tree_path || c.id || '/',
      level = COALESCE(p.level, 1) + 1,
      sort = 30,
      status = 1,
      deleted = false,
      updater = 'seed',
      update_time = CURRENT_TIMESTAMP
    FROM dynamic_category p
    WHERE c.deleted = false
      AND c.tenant_id = tid
      AND c.category_type_code = 'data_protocol'
      AND (c.code = 'cat-protocol-uniview-video' OR c.name = '宇视视频')
      AND p.id = root_id;

    SELECT id INTO cat_id
    FROM dynamic_category
    WHERE deleted = false AND tenant_id = tid AND code = 'cat-protocol-uniview-video'
    LIMIT 1;

    INSERT INTO dynamic_model (
      code, name, entity_type_code, domain, description, status, sort, tenant_id, creator
    ) VALUES (
      'data_protocol_uniview_https',
      '宇视视频 HTTPS',
      'data_protocol',
      NULL,
      '宇视感知联网平台 OpenAPI 走 HTTPS 的指令。基址为 VM:8088，路径前缀 /VIID/',
      1,
      40,
      tid,
      'seed'
    )
    ON CONFLICT (code, tenant_id) WHERE deleted = false
    DO UPDATE SET
      name = EXCLUDED.name,
      description = EXCLUDED.description,
      status = 1,
      deleted = false,
      updater = 'seed',
      update_time = CURRENT_TIMESTAMP;

    SELECT id INTO model_id
    FROM dynamic_model
    WHERE deleted = false AND tenant_id = tid AND code = 'data_protocol_uniview_https'
    LIMIT 1;

    INSERT INTO dynamic_model_field_assignment (
      model_id, field_id, model_code, field_code, required, is_searchable, is_filterable, is_sortable,
      sort, field_source, tenant_id, creator
    )
    SELECT
      model_id, f.id, 'data_protocol_uniview_https', f.code, false,
      v.is_searchable, v.is_filterable, v.is_sortable,
      v.sort_order, v.field_source, tid, 'seed'
    FROM (
      VALUES
        ('command_json', 300, false, false, false, 'CUSTOM'),
        ('field_description_json', 310, false, false, false, 'CUSTOM'),
        ('sample_json', 320, false, false, false, 'CUSTOM'),
        ('command_direction', 12, true, true, true, 'BASE'),
        ('opcode', 10, true, true, false, 'CUSTOM'),
        ('exchange_mode', 14, true, true, false, 'CUSTOM'),
        ('protocol_version', 16, true, true, false, 'BASE'),
        ('communication_protocol', 18, true, true, false, 'BASE'),
        ('http_method', 22, true, true, false, 'CUSTOM'),
        ('http_path', 23, true, true, false, 'CUSTOM')
    ) AS v(field_code, sort_order, is_searchable, is_filterable, is_sortable, field_source)
    JOIN dynamic_field f
      ON f.deleted = false AND f.tenant_id = tid AND f.code = v.field_code
    ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
    DO UPDATE SET
      field_id = EXCLUDED.field_id,
      model_id = EXCLUDED.model_id,
      is_searchable = EXCLUDED.is_searchable,
      is_filterable = EXCLUDED.is_filterable,
      is_sortable = EXCLUDED.is_sortable,
      sort = EXCLUDED.sort,
      field_source = EXCLUDED.field_source,
      deleted = false,
      updater = 'seed',
      update_time = CURRENT_TIMESTAMP;

    EXECUTE format(
      'INSERT INTO %I (
         model_id, category_id, entity_type_code, sort, creator, tenant_id, model_code, category_code, deleted
       ) VALUES ($1, $2, ''data_protocol'', 40, ''seed'', $3, ''data_protocol_uniview_https'', ''cat-protocol-uniview-video'', false)
       ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
       DO UPDATE SET
         model_id = EXCLUDED.model_id,
         category_id = EXCLUDED.category_id,
         deleted = false,
         updater = ''seed'',
         update_time = CURRENT_TIMESTAMP',
      model_cat_table
    ) USING model_id, cat_id, tid;

    EXECUTE format(
      'INSERT INTO %I (
         tenant_id, entity_type_code, model_id, name, code, status, custom_fields,
         command_direction, protocol_version, communication_protocol, creator, deleted
       )
       SELECT
         $1, ''data_protocol'', $2, c.name, c.code, 1,
         c.custom_fields - ''protocol_version'' - ''communication_protocol'',
         ''downlink'', ''["uniview-openapi-1.23"]''::jsonb, ''https'', ''seed'', false
       FROM uniview_https_command c
       WHERE NOT EXISTS (
         SELECT 1 FROM %I e
         WHERE e.tenant_id = $1 AND e.code = c.code AND e.deleted = false
       )',
      ent_table, ent_table
    ) USING tid, model_id;

    EXECUTE format(
      'UPDATE %I e
       SET
         name = c.name,
         model_id = $1,
         command_direction = ''downlink'',
         protocol_version = ''["uniview-openapi-1.23"]''::jsonb,
         communication_protocol = ''https'',
         status = 1,
         deleted = false,
         custom_fields = c.custom_fields - ''protocol_version'' - ''communication_protocol'',
         updater = ''seed'',
         update_time = CURRENT_TIMESTAMP
       FROM uniview_https_command c
       WHERE e.tenant_id = $2 AND e.code = c.code',
      ent_table
    ) USING model_id, tid;

    EXECUTE format(
      'INSERT INTO %I (
         tenant_id, category_id, entity_type_code, entity_id, domain, sort, creator, deleted
       )
       SELECT $1, $2, ''data_protocol'', e.id, NULL, 0, ''seed'', false
       FROM uniview_https_command c
       JOIN %I e ON e.deleted = false AND e.tenant_id = $1 AND e.code = c.code
       WHERE NOT EXISTS (
         SELECT 1 FROM %I r
         WHERE r.tenant_id = $1
           AND r.entity_type_code = ''data_protocol''
           AND r.entity_id = e.id
           AND r.category_id = $2
       )',
      cat_rel_table, ent_table, cat_rel_table
    ) USING tid, cat_id;

    EXECUTE format(
      'INSERT INTO %I (
         entity_id, model_id, field_code, value_string, value_number, creator, tenant_id, deleted
       )
       SELECT e.id, e.model_id, v.field_code, NULLIF(v.value_string, ''''), NULL, ''seed'', $1, false
       FROM uniview_https_command c
       JOIN %I e ON e.deleted = false AND e.tenant_id = $1 AND e.code = c.code
       CROSS JOIN LATERAL (VALUES
         (''http_method'', c.http_method),
         (''http_path'', c.http_path),
         (''exchange_mode'', c.exchange_mode)
       ) AS v(field_code, value_string)
       WHERE v.value_string IS NOT NULL
         AND NOT EXISTS (
           SELECT 1 FROM %I idx
           WHERE idx.deleted = false AND idx.tenant_id = $1
             AND idx.entity_id = e.id AND idx.field_code = v.field_code
         )',
      idx_table, ent_table, idx_table
    ) USING tid;

    EXECUTE format(
      'UPDATE %I idx
       SET value_string = v.value_string, model_id = e.model_id, deleted = false,
           updater = ''seed'', update_time = CURRENT_TIMESTAMP
       FROM uniview_https_command c
       JOIN %I e ON e.deleted = false AND e.tenant_id = $1 AND e.code = c.code
       CROSS JOIN LATERAL (VALUES
         (''http_method'', c.http_method),
         (''http_path'', c.http_path),
         (''exchange_mode'', c.exchange_mode)
       ) AS v(field_code, value_string)
       WHERE idx.tenant_id = $1 AND idx.entity_id = e.id
         AND idx.field_code = v.field_code AND v.value_string IS NOT NULL',
      idx_table, ent_table
    ) USING tid;

    EXECUTE format(
      'INSERT INTO %I (
         entity_id, model_id, field_code, value_string, value_number, creator, tenant_id, deleted
       )
       SELECT e.id, e.model_id, ''opcode'', NULL, 0, ''seed'', $1, false
       FROM uniview_https_command c
       JOIN %I e ON e.deleted = false AND e.tenant_id = $1 AND e.code = c.code
       WHERE NOT EXISTS (
         SELECT 1 FROM %I idx
         WHERE idx.deleted = false AND idx.tenant_id = $1
           AND idx.entity_id = e.id AND idx.field_code = ''opcode''
       )',
      idx_table, ent_table, idx_table
    ) USING tid;

    EXECUTE format(
      'UPDATE %I idx
       SET value_number = 0, value_string = NULL, model_id = e.model_id, deleted = false,
           updater = ''seed'', update_time = CURRENT_TIMESTAMP
       FROM uniview_https_command c
       JOIN %I e ON e.deleted = false AND e.tenant_id = $1 AND e.code = c.code
       WHERE idx.tenant_id = $1 AND idx.entity_id = e.id AND idx.field_code = ''opcode''',
      idx_table, ent_table
    ) USING tid;

    RAISE NOTICE 'seeded uniview https for tenant %', tid;
  END LOOP;
END $$;

-- 智慧管廊租户管理员要能打开新建的两本目录。已有授权行时不会自动带上新目录。
INSERT INTO dynamic_catalog_role_grant (
  role_id, registry_code, operation, subject, creator, updater, tenant_id, deleted
)
SELECT r.id, c.registry_code, op.operation, s.subject, 'seed', 'seed', r.tenant_id, false
FROM public.system_role r
CROSS JOIN (VALUES ('data_protocol'), ('protocol_mapping')) AS c(registry_code)
CROSS JOIN (VALUES ('query'), ('create'), ('update'), ('delete')) AS op(operation)
CROSS JOIN (VALUES ('entity'), ('model'), ('category'), ('detail')) AS s(subject)
WHERE r.deleted = false
  AND r.tenant_id = 123
  AND r.code = 'tenant_admin'
  AND NOT EXISTS (
    SELECT 1
    FROM dynamic_catalog_role_grant g
    WHERE g.deleted = false
      AND g.role_id = r.id
      AND g.tenant_id = r.tenant_id
      AND g.registry_code = c.registry_code
      AND g.operation = op.operation
      AND g.subject = s.subject
  );

COMMIT;
