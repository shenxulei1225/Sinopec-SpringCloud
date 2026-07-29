-- 补齐设备专用表自增主键：V1 建表时 ent_equipment.id 无序列/默认值，
-- MyBatis-Plus IdType.AUTO 插入不写 id，导致 NOT NULL 约束失败（创建实体 500）。

CREATE SEQUENCE IF NOT EXISTS dynamicbusiness.ent_equipment_id_seq
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

-- 有数据：下一值为 MAX(id)+1；空表：下一值为 1
SELECT setval(
    'dynamicbusiness.ent_equipment_id_seq',
    COALESCE((SELECT MAX(id) FROM dynamicbusiness.ent_equipment), 1),
    EXISTS (SELECT 1 FROM dynamicbusiness.ent_equipment)
);

ALTER TABLE dynamicbusiness.ent_equipment
    ALTER COLUMN id SET DEFAULT nextval('dynamicbusiness.ent_equipment_id_seq'::regclass);

ALTER SEQUENCE dynamicbusiness.ent_equipment_id_seq
    OWNED BY dynamicbusiness.ent_equipment.id;
