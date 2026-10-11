# 适用型号改到真正挂着设备的那一层。
# 大疆航线动作按官网「支持机型」收窄，不再把 14 架都勾上。
# 东古协议只登记东古飞机。机器人协议只登记两台轮式机器人。没有协议对照的不猜型号。
import psycopg2
from psycopg2.extras import Json

DJI = [1864, 1865, 1866, 1867, 1868, 1869, 1870, 1871, 1872, 1873, 1874, 1875, 1876, 1877]
DONG = [1912, 1913]
# 机型编号：1864 M400，1865 M350，1866 M300，1867 M30，1868 M30T，
# 1869 M3E，1870 M3T，1871 M3TA，1872 M3D，1873 M3TD，
# 1874 M4D，1875 M4TD，1876 M4E，1877 M4T。
# 航线通用动作支持机型不含 M400、不含 M3TA。官网写的是 M3E/M3T/M3M，库里没有 M3M。
WPML = [1865, 1866, 1867, 1868, 1869, 1870, 1872, 1873, 1874, 1875, 1876, 1877]
# 全景：M30/M30T、M3D/M3TD、M4D/M4TD、M4E/M4T。
PANO = [1867, 1868, 1872, 1873, 1874, 1875, 1876, 1877]
# 点云：只有 M300、M350。
POINT_CLOUD = [1865, 1866]
# 探照灯、喊话器：只有 M4D、M4TD。
M4_DOCK = [1874, 1875]
# 一键起飞、一键返航只走机场指令，对应机场里的飞机。
DOCK = [1867, 1868, 1872, 1873, 1874, 1875]
# 飞向目标点：机场飞机，加上遥控器上写明支持指点飞行的 M4E/M4T，再加上 M300/M350。
# 官网写 Mavic 3 行业系列只做负载操控，M400 不在航线支持机型里，这两类不放。
FLY = [1865, 1866, 1867, 1868, 1872, 1873, 1874, 1875, 1876, 1877]
# 红外测温只给机身上带红外镜头的型号。M300/M350/M400 要另挂红外吊舱，不放。
IR = [1868, 1870, 1871, 1873, 1875, 1877]
# 光圈、快门、感光度、曝光：定向拍照参数表列出的机型。
EXPOSURE = [1867, 1868, 1869, 1870, 1872, 1873, 1874, 1875, 1876, 1877]
# 照片存储、视频存储：官网存储镜头那一栏的机型。
LENS = [1867, 1868, 1872, 1873, 1874, 1875, 1876, 1877]
# 定向拍照对应的云台相机。不含机头相机、机场相机、禅思吊舱。
ORIENTED_CAMERAS = [1895, 1896, 1897, 1898, 1900, 1901, 1902, 1903, 1904, 1905]

# 动作名 → 大疆飞机名单。东古协议另加在后面。
OFFICIAL_AIRCRAFT = {
    "创建新文件夹": WPML,
    "飞行器偏航": WPML,
    "航段间均匀转动云台俯仰角": WPML,
    "悬停等待": WPML,
    "旋转云台": WPML,
    "准备并启动航线": WPML,
    "开始拍照": WPML,
    "开始录像": WPML,
    "停止录像": WPML,
    "手动点对焦": WPML,
    "手动设置变焦": WPML,
    "手动设置对焦模式": WPML,
    "手动设置对焦值": WPML,
    "手动框选变焦": WPML,
    "手动画面拖动": WPML,
    "手动双击瞄准": WPML,
    "手动重置云台": WPML,
    "手动开始录像": WPML,
    "手动停止录像": WPML,
    "手动拍一张": WPML,
    "手动停止拍照": WPML,
    "手动切换拍摄模式": WPML,
    "全景拍照": PANO,
    "开始点云录制": POINT_CLOUD,
    "暂停点云录制": POINT_CLOUD,
    "继续点云录制": POINT_CLOUD,
    "停止点云录制": POINT_CLOUD,
    "开启探照灯": M4_DOCK,
    "关闭探照灯": M4_DOCK,
    "开启探照灯爆闪": M4_DOCK,
    "喊话器-开始播放音频": M4_DOCK,
    "手动关闭探照灯": M4_DOCK,
    "手动开启探照灯": M4_DOCK,
    "手动探照灯爆闪": M4_DOCK,
    "手动探照灯快速爆闪": M4_DOCK,
    "手动探照灯交替爆闪": M4_DOCK,
    "手动设置探照灯亮度": M4_DOCK,
    "手动停止喊话": M4_DOCK,
    "手动重新喊话": M4_DOCK,
    "手动设置喊话音量": M4_DOCK,
    "手动喊话单次播放": M4_DOCK,
    "手动喊话循环播放": M4_DOCK,
    "手动播放喊话文本": M4_DOCK,
    "手动校准探照灯": M4_DOCK,
    "手动微调左探照灯": M4_DOCK,
    "手动微调右探照灯": M4_DOCK,
    "手动设置喊话语音": M4_DOCK,
    "一键起飞": DOCK,
    "一键返航": DOCK,
    "飞向目标点": FLY,
    "手动设置红外测温点": IR,
    "手动设置红外测温模式": IR,
    "手动设置红外测温区域": IR,
    "手动设置光圈": EXPOSURE,
    "手动设置快门": EXPOSURE,
    "手动设置感光度": EXPOSURE,
    "手动设置曝光模式": EXPOSURE,
    "手动设置曝光补偿": EXPOSURE,
    "手动设置分屏": LENS,
    "手动设置照片存储": LENS,
    "手动设置视频存储": LENS,
}
DRONES = set(DJI + DONG)
ROBOTS = [1772, 1773]
CAT_DRONE = 2104822
CAT_ROBOT = 2104821
CAT_CAMERA = 2104809


def protocols_of(versions):
    found = {item for item in versions if item}
    dji = any(item.startswith("dji-") for item in found)
    return dji, "uav-ws" in found, "robot-ws" in found


def category_for(model_ids, camera_ids):
    ids = set(model_ids)
    if not ids:
        return []
    if ids <= DRONES:
        return [CAT_DRONE]
    if ids <= set(ROBOTS):
        return [CAT_ROBOT]
    if ids <= camera_ids:
        return [CAT_CAMERA]
    return []


def target_for(means, current_ids, dji, dong, robot, has_protocol, camera_ids):
    if means == "UAV":
        camera_count = len(set(current_ids) & camera_ids)
        drone_count = len(set(current_ids) & DRONES)
        if current_ids and camera_count > drone_count:
            # 机头观察没有设备分类。名单里带着它时不写分类，打开弹窗才不会把它去掉。
            ids = list(current_ids)
            if 1885 in ids:
                return ids, []
            return ids, category_for(ids, camera_ids)
        if dji and dong:
            return DJI + DONG, [CAT_DRONE]
        if dji:
            return list(DJI), [CAT_DRONE]
        if dong:
            return list(DONG), [CAT_DRONE]
        if has_protocol:
            return [], []
        return list(current_ids), category_for(current_ids, camera_ids)
    if means == "ROBOT":
        if robot:
            return list(ROBOTS), [CAT_ROBOT]
        if has_protocol:
            return [], []
        return list(current_ids), category_for(current_ids, camera_ids)
    return None


def main():
    conn = psycopg2.connect(
        host="127.0.0.1", port=5432, dbname="sinopec", user="postgres", password="Coolhomer"
    )
    cur = conn.cursor()
    cur.execute(
        """
        SELECT r.model_id
        FROM dynamicbusiness.dynamic_model_category_relation_t1 r
        WHERE r.category_id = %s AND COALESCE(r.deleted, false) = false
        """,
        (CAT_CAMERA,),
    )
    camera_ids = {row[0] for row in cur.fetchall()}
    cur.execute(
        """
        SELECT a.id, a.name, a.execution_means, a.param_slots_json,
               array_agg(DISTINCT m.custom_fields->>'mapped_protocol_version')
        FROM dynamicbusiness.ent_action_t1 a
        LEFT JOIN dynamicbusiness.ent_protocol_mapping_t1 m
          ON (m.custom_fields->'action_ref'->>'id')::bigint = a.id
         AND m.deleted = false
        WHERE a.deleted = false
        GROUP BY a.id
        """
    )
    changed = 0
    for aid, name, means_list, slots, versions in cur.fetchall():
        legacy_fields = None
        if not isinstance(slots, dict):
            slots = {"version": 2, "methods": []}
        methods = slots.get("methods")
        if not isinstance(methods, list):
            # 旧数据只有一份参数，没有按执行方式拆开。补上执行方式这一行，原来的参数留在这一行上。
            fields = slots.get("fields") if isinstance(slots.get("fields"), list) else []
            methods = []
            slots["methods"] = methods
            slots.setdefault("version", 2)
            legacy_fields = fields
        enabled = {str(item).upper() for item in (means_list or [])}
        dji, dong, robot = protocols_of(versions or [])
        has_protocol = dji or dong or robot
        wanted = {}
        for means in ("UAV", "ROBOT"):
            if means not in enabled:
                continue
            current = []
            for row in methods:
                if not isinstance(row, dict):
                    continue
                if str(row.get("executionMeans", "")).upper() != means:
                    continue
                if row.get("equipmentModelId") not in (None, ""):
                    continue
                current = [int(item) for item in (row.get("applicableModelIds") or [])]
            wanted[means] = target_for(means, current, dji, dong, robot, has_protocol, camera_ids)
        if not has_protocol:
            # 没有协议对照时，原先勾满全部飞机是猜的。清掉，不替用户选型号。
            for means in ("UAV", "ROBOT"):
                if means not in wanted:
                    continue
                ids, _cats = wanted[means]
                if not ids or set(ids) <= DRONES:
                    wanted[means] = ([], [])
        if name in OFFICIAL_AIRCRAFT and "UAV" in wanted:
            ids = list(OFFICIAL_AIRCRAFT[name])
            if dong:
                ids = ids + [item for item in DONG if item not in ids]
            wanted["UAV"] = (ids, [CAT_DRONE])
        elif name in OFFICIAL_AIRCRAFT and "UAV" in enabled:
            ids = list(OFFICIAL_AIRCRAFT[name])
            if dong:
                ids = ids + [item for item in DONG if item not in ids]
            wanted["UAV"] = (ids, [CAT_DRONE])
        if name == "定点拍照" and "UAV" in wanted:
            # 定向拍照按官网相机型号勾云台相机。机头相机不在这张表里。
            wanted["UAV"] = (list(ORIENTED_CAMERAS), [CAT_CAMERA])

        next_methods = []
        seen = set()
        for row in methods:
            if not isinstance(row, dict):
                next_methods.append(row)
                continue
            means = str(row.get("executionMeans", "")).upper()
            model_id = row.get("equipmentModelId")
            if means in wanted and model_id not in (None, ""):
                if int(model_id) not in set(wanted[means][0]):
                    continue
            if means in wanted and model_id in (None, ""):
                ids, cats = wanted[means]
                row["applicableModelIds"] = ids
                row["applicableCategoryIds"] = cats
                row.pop("applicableCategoryId", None)
                seen.add(means)
            elif (
                means in ("UAV", "ROBOT")
                and not has_protocol
                and name not in OFFICIAL_AIRCRAFT
                and name != "定点拍照"
            ):
                # 执行方式没勾上时，方法行里仍可能留着猜出来的飞机。一并清掉。
                if model_id not in (None, ""):
                    if int(model_id) in DRONES:
                        continue
                else:
                    ids = [int(item) for item in (row.get("applicableModelIds") or [])]
                    if not ids or set(ids) <= DRONES:
                        row["applicableModelIds"] = []
                        row["applicableCategoryIds"] = []
                        row.pop("applicableCategoryId", None)
            elif means == "ROBOT" and model_id in (None, "") and has_protocol and not robot:
                # 这条动作没有机器人协议。执行方式上残留的机器人型号清掉。
                row["applicableModelIds"] = []
                row["applicableCategoryIds"] = []
                row.pop("applicableCategoryId", None)
            elif row.get("applicableCategoryId") and "applicableCategoryIds" not in row:
                category_id = int(row["applicableCategoryId"])
                if category_id > 0:
                    row["applicableCategoryIds"] = [category_id]
                row.pop("applicableCategoryId", None)
            next_methods.append(row)
        for means, (ids, cats) in wanted.items():
            if means in seen:
                continue
            next_methods.append(
                {
                    "executionMeans": means,
                    "fields": list(legacy_fields) if isinstance(legacy_fields, list) else [],
                    "applicableModelIds": ids,
                    "applicableCategoryIds": cats,
                    "paramsPerEquipmentModel": False,
                    "paramsSharedAcrossModels": True,
                }
            )
        for row in next_methods:
            if not isinstance(row, dict) or row.get("equipmentModelId") not in (None, ""):
                continue
            means = str(row.get("executionMeans", "")).upper()
            if means not in ("UAV", "ROBOT"):
                continue
            ids = [int(item) for item in (row.get("applicableModelIds") or [])]
            if not ids:
                continue
            # 型号都在同一层时，分类写成这一层。父级「无人机」「巡检设备」打开弹窗对不上已勾型号。
            if set(ids) <= DRONES:
                row["applicableCategoryIds"] = [CAT_DRONE]
                row.pop("applicableCategoryId", None)
            elif set(ids) <= set(ROBOTS):
                row["applicableCategoryIds"] = [CAT_ROBOT]
                row.pop("applicableCategoryId", None)
        slots["methods"] = next_methods
        cur.execute(
            "UPDATE dynamicbusiness.ent_action_t1 SET param_slots_json = %s WHERE id = %s",
            (Json(slots), aid),
        )
        changed += 1
        for means, (ids, cats) in wanted.items():
            print(f"{name} {means} models={len(ids)} categories={cats}")
    conn.commit()
    print("updated", changed)
    conn.close()


if __name__ == "__main__":
    main()
