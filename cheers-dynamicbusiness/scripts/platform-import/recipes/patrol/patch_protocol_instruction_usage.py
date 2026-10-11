# 给每条协议指令写功能与用法，详情介绍区直接显示。
# 说明写这条指令做什么、什么时候用。不把协议书标题再解释成另一个动作。
import psycopg2
from psycopg2.extras import Json

OPCODE = {
    "200101": "东古无人机从地面起飞。这是指令起飞，不是大疆机场里的一键起飞。",
    "200102": "移动到给出的位置。无人机是飞过去，机器人是走过去。",
    "200103": "东古无人机降落。这是指令降落，不是大疆机场里的一键返航。",
    "200104": "把当前飞行高度降低一截。",
    "200105": "把当前飞行高度升高一截。",
    "200201": "开始录像。东古协议书的标题是启动录像。收流地址写在这一条上。它不是直播。",
    "200202": "停止录像。东古协议书的标题是关闭录像。只有是否驻停和驻停秒数，没有云台角度。",
    "200301": "拍一张。要带偏航、俯仰、焦距和张数。发完这一张就结束，没有配套的停止。",
    "200401": "打开气体检测。",
    "200402": "关闭气体检测。",
    "200403": "设备把气体检测结果报上来。这是上报，不用下发。",
    "200501": "设备按自己的节拍把状态报上来。这是上报，不用下发。",
    "500101": "问一次设备现在的状态。电量在这一次的答卷里，不另发一条取电量的指令。",
    "500102": "打开或关闭后面的定时状态上报。",
    "500103": "设备按设定频率把状态报上来。这是上报，不用下发。",
    "500104": "一次下达整段巡检。移动、拍照这些步装在这个包里面，不各自另发一条。",
    "500105": "通信心跳。用来确认还连着，不是巡检动作。",
    "500106": "收到对方报文后的回执。只表示收到了，不表示这一步做完。",
    "500201": "指令包已经收下之后，到点启动这次任务。",
    "500202": "任务包里某一步做完之后的上报。外层都是这一条，具体是哪一步看包里的操作码。",
    "500203": "整次任务的状态上报，例如已经启动或已经结束。",
    "500204": "设备故障上报。",
    "500205": "终止已经在执行的任务。",
    "500301": "把已下发的指令包读回来。",
    "500302": "删除一份指令包。",
    "500303": "列出已有的任务模板。",
    "500401": "开始直播。协议书标题写成启动推流，做的是把画面推到平台。和直播是同一件事。",
    "500402": "停止直播。协议书标题写成停止推流。和停止直播是同一件事。",
    "500403": "设备上线或掉线的上报。",
}

METHOD = {
    "return_home": "飞机回到机场。这是飞行中单独发的一条报文，不写进航线文件。",
    "return_home_cancel": "取消已经开始的返航。",
    "return_specific_home": "蛙跳任务里指定回到哪一座机场。普通单机场任务不用这一条。",
    "takeoff_to_point": "飞机还在机场里时，起飞并飞到一个目标点后悬停。只用于机场，不用于手持遥控器。",
    "fly_to_point": "飞机已经在空中时，飞向一个目标点后悬停。",
    "fly_to_point_stop": "结束当前这次飞向目标点。",
    "fly_to_point_update": "飞向目标点的过程中改目标点。",
    "flight_authority_grab": "云端先拿到飞行控制权，再发飞行指令。",
    "payload_authority_grab": "云端先拿到负载控制权，再发云台和相机指令。",
    "drc_mode_enter": "打开指令飞行通道。探照灯、喊话和部分相机操作要先开这一条。",
    "drc_mode_exit": "关闭指令飞行通道。",
    "camera_photo_take": "拍一张。正常拍照发完就结束。全景模式下这一条是开始拍全景。",
    "camera_photo_stop": "停下正在进行的全景拍照。官网写明只支持全景模式。普通拍一张没有这一步。",
    "camera_recording_start": "开始录像。这是相机自己在录像，不是把画面推到平台的直播。",
    "camera_recording_stop": "停止相机录像。",
    "camera_mode_switch": "切换相机工作模式，例如拍照、录像或全景。",
    "camera_frame_zoom": "在画面上框一块区域，相机放大到这一块。",
    "camera_screen_drag": "按给定速度转动云台，用来拖画面。",
    "camera_aim": "双击画面上的一点，云台转到这一点。",
    "camera_focal_length_set": "把变焦倍数设到给定值。",
    "gimbal_reset": "云台回到给定姿态，例如回中或朝下。",
    "camera_look_at": "让云台看向一个经纬度点。",
    "camera_screen_split": "打开或关闭红外与可见光分屏。",
    "photo_storage_set": "选择拍照时存哪些镜头的照片。",
    "video_storage_set": "选择录像时存哪些镜头的视频。",
    "camera_exposure_mode_set": "设置曝光模式，例如自动、光圈优先或手动。",
    "camera_exposure_set": "调节曝光补偿。",
    "camera_focus_mode_set": "设置对焦模式，例如手动或自动。",
    "camera_focus_value_set": "把对焦值调到给定位置。",
    "camera_point_focus_action": "对画面上的一个点对焦。",
    "ir_metering_mode_set": "设置红外测温是关闭、点测温还是区域测温。",
    "ir_metering_point_set": "在画面上定一个红外测温点。",
    "ir_metering_area_set": "在画面上定一块红外测温区域。",
    "drc_camera_aperture_value_set": "飞行中设置光圈。走指令飞行通道，不写进航线文件。",
    "drc_camera_shutter_set": "飞行中设置快门。走指令飞行通道，不写进航线文件。",
    "drc_camera_iso_set": "飞行中设置感光度。走指令飞行通道，不写进航线文件。",
    "drc_camera_mode_switch": "飞行中切换拍摄模式。走指令飞行通道。",
    "live_start_push": "开始直播。飞机把画面推到平台。大疆这条的方法名是 live_start_push。",
    "live_stop_push": "停止直播。",
    "live_set_quality": "调整正在直播的清晰度。",
    "live_lens_change": "切换正在直播的镜头，例如广角、变焦或红外。",
    "live_camera_change": "机场舱内、舱外相机之间切换直播画面。",
    "speaker_audio_play_start": "喊话器播放一段已经在云端的音频文件。这是单独报文，不写进航线。",
    "speaker_tts_play_start": "喊话器把一段文字念出来。服务通道上的这一条，不写进航线。",
    "speaker_replay": "把上一次喊话再播一遍。",
    "speaker_play_stop": "停止当前喊话。",
    "speaker_play_mode_set": "设置喊话是播一次还是循环播。",
    "speaker_play_volume_set": "设置喊话音量。",
    "drc_speaker_play_stop": "飞行中停止喊话。走指令飞行通道，不写进航线。",
    "drc_speaker_replay": "飞行中把上一次喊话再播一遍。",
    "drc_speaker_play_volume_set": "飞行中设置喊话音量。",
    "drc_speaker_play_mode_set": "飞行中设置喊话播一次或循环播。",
    "drc_speaker_tts_play_start": "飞行中把一段文字念出来。",
    "drc_speaker_tts_set": "飞行中设置喊话的音量、男声或女声、中文或英文、语速。这一条只改设置，不开始念。",
    "drc_light_mode_set": "飞行中设置探照灯。关闭、常亮、爆闪、快速爆闪、交替爆闪是同一条指令里的不同模式。",
    "drc_light_brightness_set": "飞行中设置探照灯亮度。",
    "drc_light_calibration": "飞行中校准探照灯云台。",
    "drc_light_fine_tuning_set": "飞行中微调左灯或右灯的角度，范围是负 3 度到正 3 度。",
    "flighttask_prepare": "把航线任务下发到机场，此时还没起飞。",
    "flighttask_execute": "机场开始执行已经下发的航线任务。",
    "flighttask_pause": "暂停正在飞的航线。",
    "flighttask_recovery": "继续被暂停的航线。",
    "flighttask_undo": "取消还没执行的航线任务。",
    "flighttask_stop": "终止正在执行的航线任务。",
    "flighttask_resource_get": "设备来要航线文件地址。这是设备请求，云端在请求回复里给地址。",
    "storage_config_get": "设备来要上传文件的临时凭证。云端在请求回复里给凭证。",
    "upload_flighttask_media_prioritize": "把这次任务的媒体文件提到上传队列前面。",
    "flight_areas_update": "通知机场更新自定义飞行区。",
    "psdk_widget_value_set": "给 PSDK 负载上的一个控件设值。",
    "psdk_input_box_text_set": "给 PSDK 负载上的文本框送一段文字。",
    "custom_data_transmission_to_psdk": "把一段自定义数据发给 PSDK 负载。",
    "custom_data_transmission_to_esdk": "把一段自定义数据发给 ESDK。",
    "unlock_license_switch": "启用或停用某一份解禁证书。",
    "unlock_license_update": "更新飞机上的解禁证书。",
    "unlock_license_list": "读取飞机上的解禁证书列表。",
    "cover_open": "打开机场舱盖。",
    "cover_close": "关闭机场舱盖。",
    "cover_force_close": "强制关闭机场舱盖。",
    "debug_mode_open": "打开机场调试模式。调试指令要先开这个。",
    "debug_mode_close": "关闭机场调试模式。",
    "supplement_light_open": "打开机场补光灯。这是机场上的灯，不是飞机上的探照灯。",
    "supplement_light_close": "关闭机场补光灯。",
    "battery_maintenance_switch": "切换电池保养状态。",
    "air_conditioner_mode_switch": "切换机场空调模式。",
    "alarm_state_switch": "打开或关闭机场声光报警。",
    "battery_store_mode_switch": "切换电池运行模式。",
    "device_reboot": "重启机场。",
    "drone_open": "给机场里的飞机开机。",
    "drone_close": "给机场里的飞机关机。",
    "device_format": "格式化机场存储。",
    "drone_format": "格式化飞机存储。",
    "charge_open": "开始给飞机充电。",
    "charge_close": "停止给飞机充电。",
    "sdr_workmode_switch": "切换增强图传。",
    "esim_activate": "激活 eSIM。",
    "sim_slot_switch": "在 eSIM 和实体 SIM 之间切换。",
    "esim_operator_switch": "切换 eSIM 运营商。",
    "ota_create": "发起固件升级。",
    "fileupload_list": "读取设备上可以上传的日志文件列表。",
    "fileupload_start": "开始上传选定的日志文件。",
    "fileupload_update": "更新日志上传状态，例如取消。",
    "cloud_control_auth_request": "请遥控器上的人同意云端控制。人点同意之后，云端才能操作。",
    "cloud_control_release": "释放已经拿到的云端控制权，遥控器恢复本地操作。",
    "update_topo": "设备拓扑变化时的上报，例如飞机和遥控器的连接关系变了。",
}

WAYLINE = {
    "customDirName": "写进航线文件。飞到该点时新建一个媒体文件夹。不单独发报文。",
    "gimbalEvenlyRotate": "写进航线文件。在两个航点之间把云台俯仰匀速转到目标角度。不单独发报文。",
    "gimbalRotate": "写进航线文件。到点后把云台转到给定角度。不单独发报文。",
    "hover": "写进航线文件。在该点悬停给定时间。不单独发报文。",
    "orientedShoot": "写进航线文件。到点后按给定的云台角度、焦距和目标框拍一张。不单独发报文。",
    "panoShot": "写进航线文件。到点后拍全景。不单独发报文。飞行中要打断全景，用的是另一条停止全景的实时指令。",
    "rotateYaw": "写进航线文件。到点后把机头转到给定偏航。不单独发报文。",
    "searchlightOff": "写进航线文件。到点后关闭探照灯。只对应机场 3 的 M4D、M4TD。不单独发报文。",
    "searchlightOn": "写进航线文件。到点后打开探照灯。只对应机场 3 的 M4D、M4TD。不单独发报文。",
    "searchlightStrobe": "写进航线文件。到点后让探照灯爆闪。只对应机场 3 的 M4D、M4TD。不单独发报文。",
    "recordPointCloudStart": "写进航线文件。开始录点云。官网支持机型是 M300、M350。不单独发报文。",
    "recordPointCloudPause": "写进航线文件。暂停点云录制。不单独发报文。",
    "recordPointCloudResume": "写进航线文件。继续点云录制。不单独发报文。",
    "recordPointCloudStop": "写进航线文件。停止点云录制。不单独发报文。",
}

UPLINK = {
    "osd-aircraft": "飞机按固定频率把位置、电量和状态推上来。电量看这一条，不另发取电量的指令。",
    "osd-dock": "机场按固定频率把自己的状态推上来。Pilot 没有这一条。",
    "state": "设备属性发生变化时推上来。这里只记这条上报的外壳，不把某一种属性写死在里面。",
    "events": "设备发生事件时推上来。这里只记这条上报的外壳，不把某一种事件写死在里面。",
    "requests": "设备向云端要东西，例如上传凭证或航线文件地址。云端用请求回复回答。",
    "drc-up": "指令飞行通道上的飞机上行。探照灯、喊话这些指令的结果走这里。",
}


def describe(code, name, method):
    if code == "proto-sample-ws-heartbeat":
        return "示例心跳，不是现网要下发的指令。"
    if code.startswith("proto-dji-pilot-js-"):
        return f"遥控器 Pilot 页面里的调用：{name}。这一条不发给飞机，也不写进航线文件。"
    if code.startswith("proto-dji-pilot-https-"):
        return f"遥控器 Pilot 的网页接口：{name}。这一条不是飞行指令。"
    if code.startswith("proto-dji-pilot-ws-"):
        return f"遥控器 Pilot 的网页推送：{name}。这一条不是飞行指令。"
    if method and method in METHOD:
        return METHOD[method]
    marker = "-wpml-"
    if marker in code:
        suffix = code.split(marker, 1)[1]
        text = WAYLINE.get(suffix)
        if text:
            return text
    for opcode, text in OPCODE.items():
        if f"-{opcode}-" in code:
            return text
    for key, text in UPLINK.items():
        if code.endswith(key):
            return text
    return ""


PROTOCOL_MODELS = {
    1827: "data_protocol_ws",
    1854: "data_protocol_robot",
    1856: "data_protocol_dji_dock",
    1857: "data_protocol_dji_pilot",
    1907: "data_protocol_dji_dock_wayline",
    1908: "data_protocol_dji_pilot_https",
    1909: "data_protocol_dji_pilot_ws",
    1910: "data_protocol_dji_pilot_jsbridge",
    1911: "data_protocol_dji_pilot_wayline",
}


def ensure_usage_field(cur):
    """功能与用法挂在协议型号上。保存指令时这一句跟着走，不会被报文保存清掉。"""
    cur.execute(
        """
        SELECT id FROM dynamicbusiness.dynamic_field
        WHERE code = 'instruction_usage' AND tenant_id = 1 AND deleted = false
        """
    )
    row = cur.fetchone()
    if row:
        field_id = row[0]
    else:
        cur.execute(
            """
            INSERT INTO dynamicbusiness.dynamic_field
                (code, name, type, description, source, status, tenant_id, deleted)
            VALUES (
                'instruction_usage', '功能与用法', 'TEXT',
                '这条协议指令做什么、什么时候用。详情名称下面显示这一句。',
                'LIBRARY', 1, 1, false
            )
            RETURNING id
            """
        )
        field_id = cur.fetchone()[0]
    for model_id, model_code in PROTOCOL_MODELS.items():
        cur.execute(
            """
            SELECT id FROM dynamicbusiness.dynamic_model_field_assignment
            WHERE model_id = %s AND field_code = 'instruction_usage' AND deleted = false
            """,
            (model_id,),
        )
        if cur.fetchone():
            continue
        cur.execute(
            """
            INSERT INTO dynamicbusiness.dynamic_model_field_assignment
                (model_id, field_id, required, is_searchable, is_filterable, is_sortable,
                 sort, field_source, tenant_id, model_code, field_code, deleted)
            VALUES (%s, %s, false, false, false, false, 8, 'CUSTOM', 1, %s, 'instruction_usage', false)
            """,
            (model_id, field_id, model_code),
        )


def main():
    conn = psycopg2.connect(
        host="127.0.0.1", port=5432, dbname="sinopec", user="postgres", password="Coolhomer"
    )
    cur = conn.cursor()
    ensure_usage_field(cur)
    cur.execute(
        """
        SELECT id, name, code,
               coalesce(custom_fields->'command_json'->'outbound'->>'method',
                        command_json->'outbound'->>'method', '')
        FROM dynamicbusiness.ent_data_protocol_t1
        WHERE deleted = false
        ORDER BY id
        """
    )
    missing = []
    written = 0
    for row_id, name, code, method in cur.fetchall():
        text = describe(code, name, method)
        if not text:
            missing.append((row_id, name, code, method))
            continue
        cur.execute(
            """
            UPDATE dynamicbusiness.ent_data_protocol_t1
               SET custom_fields = jsonb_set(
                     jsonb_set(
                       coalesce(custom_fields, '{}'::jsonb),
                       '{description}',
                       %s::jsonb,
                       true
                     ),
                     '{instruction_usage}',
                     %s::jsonb,
                     true
                   )
             WHERE id = %s
            """,
            (Json(text), Json(text), row_id),
        )
        written += 1
    print("written", written)
    print("missing", len(missing))
    for item in missing:
        print(item)
    if missing:
        conn.rollback()
    else:
        conn.commit()
    conn.close()
    if missing:
        raise SystemExit(1)


if __name__ == "__main__":
    main()
