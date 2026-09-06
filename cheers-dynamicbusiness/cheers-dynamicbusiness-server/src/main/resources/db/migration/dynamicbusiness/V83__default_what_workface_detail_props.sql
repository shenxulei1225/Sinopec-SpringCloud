-- 「看详情」编排按创建目录同一规则写入默认工作面：一块 entity.detail。
-- 只补 what_config 还没有 detailPropsId 的行；已有指针（检查库/SOP 配方）不改。
-- 读路径不补。

INSERT INTO platformresource.pr_component
    (component_code, type, name, icon, status, sort, description, tenant_id)
VALUES
    ('work-face', 'work-face', '工作面', 'layout', 1, 30,
     '数据组件：五维工作面块清单（props.blocks）', 1)
ON CONFLICT (component_code) DO UPDATE SET
    deleted = false,
    update_time = CURRENT_TIMESTAMP;

DO $$
DECLARE
    v_component_id bigint;
    v_props_id bigint;
    v_row record;
    v_blocks jsonb := jsonb_build_object(
        'blocks', jsonb_build_array(jsonb_build_object('code', 'entity.detail'))
    );
BEGIN
    SELECT id INTO v_component_id
    FROM platformresource.pr_component
    WHERE component_code = 'work-face' AND deleted = false;

    IF v_component_id IS NULL THEN
        RAISE EXCEPTION '缺少 work-face 组件，无法写入默认工作面';
    END IF;

    FOR v_row IN
        SELECT o.id, o.tenant_id, o.entity_type_code
        FROM dynamicbusiness.dm_five_w_orchestration o
        WHERE COALESCE(o.deleted, false) = false
          AND o.what_mode = 'VIEW_DETAIL'
          AND NULLIF(btrim(COALESCE(o.what_config ->> 'detailPropsId', '')), '') IS NULL
    LOOP
        INSERT INTO platformresource.pr_component_props (
            is_template, component_id, component_code, schema_version,
            props, name, creator, tenant_id
        ) VALUES (
            true, v_component_id, 'work-face', 'work-face@1',
            v_blocks::text,
            'What 工作面-' || v_row.entity_type_code,
            'flyway-v83',
            v_row.tenant_id
        ) RETURNING id INTO v_props_id;

        UPDATE dynamicbusiness.dm_five_w_orchestration
        SET what_config = COALESCE(what_config, '{}'::jsonb)
                || jsonb_build_object('detailPropsId', v_props_id),
            update_time = CURRENT_TIMESTAMP
        WHERE id = v_row.id;
    END LOOP;
END $$;
