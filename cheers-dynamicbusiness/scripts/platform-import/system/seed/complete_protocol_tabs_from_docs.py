#!/usr/bin/env python3
"""按协议原文把指令空包写进字段说明 / 指令 JSON / 样例，并写方向、交互方式。

权威：`参考资料/知仁提供-平台与机器人对接协议-协商.docx`、
`参考资料/平台与无人机对接协议-websocket版-v0.5.2.docx`、
`参考资料/进行指令报文.txt`。
同步要结果才写正常/异常响应；上报、回执、心跳、只定义动作内容的指令只留主包。
2 开头塞进 500104 是算任务步骤时的事，交互方式不记包内一步。
500202 是步骤做完后的共用上报外壳，不是某条下发的同步答卷；外层永远 500202，步身份在 packages 对象里的内层 opcode + sequence。
空包字段只认对端协议书；联调 txt 多出来的键只进样例，不进空包。
不负责：改通信层分流、对接表、巡检回写。
禁止：读路径猜字段；把回执写成业务成功卷；用 resultCode 顶替外壳 code；给协议没有的指令发明水平角/垂直角/焦距；把 500202 的 packages 写成数组或漏掉内层操作码。
"""

from __future__ import annotations

import json
import os
import subprocess
from pathlib import Path

ROLES = ("outbound", "inboundSuccess", "inboundFailure")
SEED_DIR = Path(__file__).resolve().parent
SQL_OUT = SEED_DIR / "dynamic_protocol_ground_station_tabs.sql"
MSG = "1618987228144"
DEVICE = "2c8ac20fa02f4912aa06f9535cddfa2caas"
TASK = "111122223301"
TEMPLATE = "100111"
POINT = "16de65073e804a4898305c5886246467"
# 协议返回码：200=成功；非 200=失败。失败必须写原因。不是空包占位整数 0。
SUCCESS_ACK_CODE = 200
FAILURE_ACK_CODE = 500
FAILURE_ACK_MSG = "设备离线"


def field(path: str, typ: str, label: str, required: bool = True, description: str | None = None) -> dict:
    return {
        "code": path.split(".")[-1].replace("[]", ""),
        "path": path,
        "type": typ,
        "label": label,
        "required": required,
        "description": description,
    }


def blank_value(typ: str):
    if typ in {"integer", "number", "long"}:
        return 0
    if typ == "boolean":
        return False
    if typ == "array":
        return []
    if typ == "object":
        return {}
    return ""


def assign_path(root: dict, path: str, value):
    parts = path.replace("[]", ".0").split(".")
    cursor: object = root
    for i, part in enumerate(parts):
        last = i == len(parts) - 1
        if part == "0":
            if not isinstance(cursor, list):
                raise TypeError(path)
            if not cursor:
                cursor.append({})
            if last:
                return
            cursor = cursor[0]
            continue
        if not isinstance(cursor, dict):
            raise TypeError(path)
        if last:
            if part not in cursor:
                cursor[part] = value
            return
        nxt = parts[i + 1]
        if nxt == "0":
            cursor = cursor.setdefault(part, [])
        else:
            cursor = cursor.setdefault(part, {})


def fields_to_blank(rows: list[dict]) -> dict:
    packet: dict = {}
    for row in rows:
        assign_path(packet, row["path"], blank_value(row["type"]))
    return packet


def overwrite_path(root: dict, path: str, value) -> None:
    parts = path.replace("[]", ".0").split(".")
    cursor: object = root
    for i, part in enumerate(parts):
        last = i == len(parts) - 1
        if part == "0":
            if not isinstance(cursor, list) or not cursor:
                return
            if last:
                return
            cursor = cursor[0]
            continue
        if not isinstance(cursor, dict):
            return
        if last:
            if part in cursor:
                cursor[part] = value
            return
        nxt = parts[i + 1]
        cursor = cursor.get(part)
        if nxt == "0" and not isinstance(cursor, list):
            return


def is_ack_code_path(path: str) -> bool:
    leaf = path.split(".")[-1].replace("[]", "")
    return leaf in {"code", "resultCode"}


def is_failure_reason_path(path: str, label: str) -> bool:
    leaf = path.split(".")[-1].replace("[]", "")
    if leaf != "msg":
        return False
    if path == "msg":
        return True
    return "失败" in (label or "") or label in {"应答说明", "操作结果说明", "返回码的描述"}


def stamp_ack_semantics(packet: dict, rows: list[dict], *, failure: bool) -> None:
    """应答码/结果码不是空包占位 0：成功固定 200，失败非 200 且必须有原因。"""
    for row in rows:
        path = row["path"]
        if is_ack_code_path(path):
            overwrite_path(packet, path, FAILURE_ACK_CODE if failure else SUCCESS_ACK_CODE)
        if failure and is_failure_reason_path(path, row.get("label") or ""):
            overwrite_path(packet, path, FAILURE_ACK_MSG)


def ack_fields(failure: bool = False, data_rows: list[dict] | None = None) -> list[dict]:
    """协议应答固定外壳：opcode / msgId / code / msg；成功卷还必须有 data。"""
    rows = [
        field("opcode", "integer", "对应请求报文的操作码"),
        field("msgId", "string", "对应请求报文的消息id"),
        field("code", "integer", "返回码", description="200表示成功响应，其他非200的值表示失败响应。"),
        field("msg", "string", "返回码的描述", description="失败返回，需要填写对应的失败的原因"),
    ]
    if failure:
        return rows
    rows.append(field("data", "object", "返回消息体"))
    if data_rows:
        rows.extend(row for row in data_rows if row["path"] != "data")
    return rows


def ack_sample(opcode: int, failure: bool = False, data: dict | None = None, msg: str = "ok") -> dict:
    if failure:
        return {"opcode": opcode, "msgId": MSG, "code": FAILURE_ACK_CODE, "msg": FAILURE_ACK_MSG}
    return {"opcode": opcode, "msgId": MSG, "code": SUCCESS_ACK_CODE, "msg": msg, "data": {} if data is None else data}


def has_ack_envelope(rows: list[dict]) -> bool:
    return any(row["path"] == "code" for row in rows)


def prefix_data_paths(rows: list[dict]) -> list[dict]:
    out = []
    for row in rows:
        path = row["path"]
        if path == "data" or path.startswith("data."):
            out.append(row)
            continue
        nxt = dict(row)
        nxt["path"] = f"data.{path}"
        out.append(nxt)
    return out


def wrap_success_as_ack(opcode: int, rows: list[dict], sample: dict | None) -> tuple[list[dict], dict]:
    if has_ack_envelope(rows):
        if not any(row["path"] == "data" for row in rows):
            rows = [*rows, field("data", "object", "返回消息体")]
        if sample is None:
            sample = ack_sample(opcode, data={})
        elif "data" not in sample:
            sample = {**sample, "data": {}}
        return rows, sample
    return ack_fields(data_rows=prefix_data_paths(rows)), ack_sample(opcode, data={} if sample is None else sample)


def wrap_failure_as_ack(opcode: int, rows: list[dict] | None, sample: dict | None) -> tuple[list[dict], dict]:
    fail_rows = rows or ack_fields(failure=True)
    if has_ack_envelope(fail_rows):
        return fail_rows, sample or ack_sample(opcode, failure=True)
    if sample and "code" not in sample and "resultCode" in sample:
        sample = {
            "opcode": opcode,
            "msgId": MSG,
            "code": sample["resultCode"],
            "msg": sample.get("msg") or FAILURE_ACK_MSG,
        }
    return ack_fields(failure=True), sample or ack_sample(opcode, failure=True)


def operate_point_fields(prefix: str) -> list[dict]:
    return [
        field(f"{prefix}.lat", "string", "纬度"),
        field(f"{prefix}.lng", "string", "经度"),
        field(f"{prefix}.height", "string", "GPS海拔高度"),
        field(f"{prefix}.pointId", "string", "点位标识"),
        field(f"{prefix}.optime", "integer", "操作时间（秒）"),
        field(f"{prefix}.electric", "string", "当前电压百分比"),
        field(f"{prefix}.distance", "string", "行驶距离（米）", required=False),
        field(f"{prefix}.fault", "integer", "设备故障码", required=False),
        field(f"{prefix}.status", "integer", "设备任务状态", required=False),
    ]


def operate_point_sample() -> dict:
    return {
        "lat": "39.1277217676",
        "lng": "117.0190597072",
        "height": "17.874",
        "pointId": POINT,
        "optime": 1624511261,
        "electric": "82.9",
        "distance": "12",
        "fault": 100000,
        "status": 400102,
    }


def result_fields(prefix: str | None = None, extra: list[dict] | None = None) -> list[dict]:
    if prefix:
        rows = [
            field(prefix, "object", "指令执行结果"),
            field(f"{prefix}.resultCode", "integer", "指令执行结果码"),
            field(f"{prefix}.msg", "string", "操作结果说明"),
            field(f"{prefix}.operatePoint", "object", "操作点"),
            *operate_point_fields(f"{prefix}.operatePoint"),
        ]
        extra_rows = extra or []
    else:
        rows = [
            field("resultCode", "integer", "指令执行结果码"),
            field("msg", "string", "操作结果说明"),
            field("operatePoint", "object", "操作点"),
            *operate_point_fields("operatePoint"),
        ]
        extra_rows = extra or []
    rows.extend(extra_rows)
    return rows


def result_sample(extra: dict | None = None) -> dict:
    packet = {"resultCode": 200, "msg": "ok", "operatePoint": operate_point_sample()}
    if extra:
        packet.update(extra)
    return packet


def report_point_fields(prefix: str) -> list[dict]:
    return [
        field(f"{prefix}.reportTime", "integer", "上报时间（秒）"),
        field(f"{prefix}.pointId", "string", "勘察地图点位id"),
        field(f"{prefix}.lng", "string", "经度"),
        field(f"{prefix}.lat", "string", "纬度"),
        field(f"{prefix}.height", "string", "相对地面高度"),
        field(f"{prefix}.electric", "string", "当前电压"),
        field(f"{prefix}.distance", "string", "行驶里程（米）", required=False),
    ]


def report_point_sample() -> dict:
    return {
        "reportTime": 1624511261,
        "pointId": POINT,
        "lng": "117.0190597072",
        "lat": "39.1277217676",
        "height": "17.874",
        "electric": "82.9",
        "distance": "12",
    }


def step_result_package_fields() -> list[dict]:
    # 对端协议：外层字段名 packages，类型是一条 resultPackage，不是数组。
    # 旧模拟器把同一对象写在 result 键下；空包跟协议书，用 packages。
    return [
        field("packages", "object", "这一步的操作结果", description="一条 resultPackage，不是数组。对上 500104 里第几步，看里面的 opcode + sequence。"),
        field("packages.opcode", "integer", "被报结果的那一步内层动作码", description="如 200102 移动、200301 拍照。外层 opcode 永远是 500202。"),
        field("packages.sequence", "integer", "该步在指令包中的序号"),
        field("packages.deviceId", "string", "执行该步的设备逻辑标识"),
        field("packages.reportPoint", "object", "上报点"),
        *report_point_fields("packages.reportPoint"),
        field("packages.result", "object", "该步操作结果", description="具体字段跟内层动作码走；公共有 resultCode / msg / operatePoint。"),
        field("packages.result.resultCode", "integer", "指令执行结果码", description="200 成功 / 900201 格式错误 / 900202 操作失败"),
        field("packages.result.msg", "string", "操作结果说明"),
        field("packages.result.operatePoint", "object", "操作点"),
        *operate_point_fields("packages.result.operatePoint"),
        field("packages.result.photos", "array", "照片列表", required=False),
        field("packages.result.photos[].type", "string", "照片格式", required=False),
        field("packages.result.photos[].size", "integer", "照片字节数", required=False),
        field("packages.result.photos[].data", "string", "照片Base64", required=False),
        field("packages.result.photos[].photoTime", "integer", "拍照时间（秒）", required=False),
    ]


def inner_request_fields(rows: list[dict]) -> list[dict]:
    return [
        field("sequence", "integer", "动作序号"),
        field("opcode", "integer", "内层动作码"),
        field("request", "object", "动作请求参数"),
        *rows,
    ]


def status_data_fields(parent: str, robot: bool) -> list[dict]:
    rows = [
        field(f"{parent}.deviceId", "string", "设备逻辑标识"),
        field(f"{parent}.time", "integer", "状态时间（秒）"),
        field(f"{parent}.status", "integer", "设备状态码"),
        field(f"{parent}.fault", "integer", "设备故障码", required=robot),
        field(f"{parent}.electricity", "string", "设备电量或电压"),
        field(f"{parent}.lat", "string", "纬度"),
        field(f"{parent}.lng", "string", "经度"),
        field(f"{parent}.height", "string", "GPS海拔高度"),
    ]
    if robot:
        rows[4:4] = [
            field(f"{parent}.faultReasion", "string", "故障原因"),
            field(f"{parent}.dataLink", "integer", "数据链路（0断开 1连接）"),
        ]
    else:
        rows.append(field(f"{parent}.distance", "integer", "行驶里程（米）"))
    return rows


def status_data_sample(robot: bool) -> dict:
    data = {
        "deviceId": DEVICE,
        "time": 1624511261,
        "status": 400101,
        "fault": 100000,
        "electricity": "82.9",
        "lat": "31.0383416830",
        "lng": "121.2668456836",
        "height": "17.874",
    }
    if robot:
        data["faultReasion"] = "no error"
        data["dataLink"] = 1
    else:
        data["distance"] = 12
    return data


INNER_FAILURE_FIELDS = [
    field("resultCode", "integer", "指令执行结果码"),
    field("msg", "string", "失败原因"),
]


def command(opcode: int, outbound: list[dict], outbound_sample: dict, **kwargs) -> dict:
    success_rows = kwargs.get("success")
    failure = kwargs.get("failure", True)
    failure_rows = kwargs.get("failure_rows")
    success_sample = kwargs.get("success_sample")
    failure_sample = kwargs.get("failure_sample")
    fields = {"outbound": outbound}
    packets = {"outbound": fields_to_blank(outbound)}
    if "opcode" in packets["outbound"]:
        packets["outbound"]["opcode"] = opcode
    samples = {"outbound": outbound_sample}
    if success_rows is not None:
        success_rows, success_sample = wrap_success_as_ack(opcode, success_rows, success_sample)
        fields["inboundSuccess"] = success_rows
        packets["inboundSuccess"] = fields_to_blank(success_rows)
        stamp_ack_semantics(packets["inboundSuccess"], success_rows, failure=False)
        packets["inboundSuccess"]["opcode"] = opcode
        samples["inboundSuccess"] = success_sample
        if failure:
            fail_rows, failure_sample = wrap_failure_as_ack(opcode, failure_rows, failure_sample)
            fields["inboundFailure"] = fail_rows
            packets["inboundFailure"] = fields_to_blank(fail_rows)
            stamp_ack_semantics(packets["inboundFailure"], fail_rows, failure=True)
            packets["inboundFailure"]["opcode"] = opcode
            samples["inboundFailure"] = failure_sample
    return {
        "opcode": opcode,
        "field_description_json": fields,
        "command_json": packets,
        "sample_json": samples,
    }


def gs_ack(opcode: int, outbound: list[dict], outbound_sample: dict, data_rows: list[dict] | None = None, data_sample=None) -> dict:
    success = ack_fields(data_rows=data_rows)
    success_sample = ack_sample(opcode, data={} if data_rows and data_sample is None else data_sample)
    if data_rows and data_sample is None:
        success_sample["data"] = fields_to_blank(data_rows)
        # 样例里补常见值
        if "data.lineId" in {row["path"] for row in data_rows}:
            success_sample["data"]["lineId"] = "line-100111"
        if "data.templateId" in {row["path"] for row in data_rows}:
            success_sample["data"]["templateId"] = TEMPLATE
        if "data.packages" in {row["path"] for row in data_rows}:
            success_sample["data"]["packages"] = "[]"
        if "data.templates" in {row["path"] for row in data_rows}:
            success_sample["data"]["templates"] = [{"templateId": TEMPLATE, "lineId": "line-100111"}]
    return command(opcode, outbound, outbound_sample, success=success, success_sample=success_sample)


def build_catalog() -> dict[str, dict]:
    report_point = [
        field("reportPoint", "object", "上报点"),
        *operate_point_fields("reportPoint"),
    ]
    report_sample = operate_point_sample()
    # 机器人对接协议：移动请求只认点位 id，不把联调 txt 里的经纬/航向写进空包。
    move_robot = inner_request_fields([
        field("request.pointId", "string", "对应机器人勘察地图上的点位id"),
    ])
    # 机器人对接协议：拍照请求只有目标位姿与驻停，没有 pointId / heading。
    photo_robot = inner_request_fields([
        field("request.lat", "string", "拍摄目标的纬度"),
        field("request.lng", "string", "拍摄目标的经度"),
        field("request.height", "string", "拍摄目标的地面高度"),
        field("request.angleLevel", "string", "拍摄目标的偏角"),
        field("request.angleVertical", "string", "拍摄目标的仰角"),
        field("request.number", "integer", "要求拍摄的照片张数"),
        field("request.focuses", "string", "照相机的焦距"),
        field("request.parking", "boolean", "表示该操作是否需要驻停"),
        field("request.time", "integer", "表示驻停的时长,秒为单位"),
    ])
    photo_result_extra = [
        field("photos", "array", "照片列表"),
        field("photos[].type", "string", "照片格式"),
        field("photos[].size", "integer", "照片字节数"),
        field("photos[].data", "string", "照片Base64"),
        field("photos[].photoTime", "integer", "拍照时间（秒）"),
    ]
    gas_result_extra = [
        field("collect", "object", "气体浓度"),
        field("collect.CH4", "string", "甲烷浓度", required=False),
        field("collect.H2S", "string", "硫化氢浓度", required=False),
    ]
    catalog: dict[str, dict] = {
        "proto-gs-500101-status": gs_ack(
            500101,
            [
                field("deviceId", "string", "设备逻辑标识"),
                field("opcode", "integer", "指令编码"),
                field("msgId", "string", "消息编号"),
            ],
            {"msgId": MSG, "opcode": 500101, "deviceId": DEVICE},
            data_rows=[
                field("data", "object", "状态数据"),
                *status_data_fields("data", robot=True),
            ],
            data_sample=status_data_sample(True),
        ),
        "proto-gs-500102-report-switch": gs_ack(
            500102,
            [
                field("deviceId", "string", "设备逻辑标识"),
                field("opcode", "integer", "指令编码"),
                field("msgId", "string", "消息编号"),
                field("enable", "boolean", "是否启动上报"),
                field("frequency", "integer", "上报频率（秒）", description="enable=true 时必填"),
                field("total", "integer", "上报总次数（-1不限制）", description="enable=true 时必填"),
            ],
            {"msgId": "1618987228145", "opcode": 500102, "deviceId": DEVICE, "enable": True, "frequency": 10, "total": 100},
        ),
        "proto-gs-500103-report-push": gs_ack(
            500103,
            [
                field("opcode", "integer", "指令编码"),
                field("msgId", "string", "消息编号"),
                field("deviceId", "string", "设备逻辑标识"),
                field("result", "object", "定时上报状态包"),
                field("result.operatePoint", "object", "操作点"),
                *operate_point_fields("result.operatePoint"),
                field("result.deviceId", "string", "设备逻辑标识"),
                field("result.sequence", "integer", "第几次上报"),
                field("result.time", "integer", "状态时间（秒）"),
                field("result.status", "integer", "设备状态码"),
                field("result.fault", "integer", "设备故障码"),
                field("result.faultReason", "string", "故障原因"),
                field("result.dataLink", "integer", "数据链路（0断开 1连接）"),
                field("result.electricity", "string", "剩余电量"),
                field("result.lat", "string", "纬度"),
                field("result.lng", "string", "经度"),
                field("result.height", "string", "GPS海拔高度"),
            ],
            {
                "opcode": 500103,
                "msgId": MSG,
                "deviceId": DEVICE,
                "result": {
                    "operatePoint": operate_point_sample(),
                    "deviceId": DEVICE,
                    "sequence": 1,
                    "time": 1624511261,
                    "status": 400102,
                    "fault": 100000,
                    "faultReason": "no error",
                    "dataLink": 1,
                    "electricity": "80",
                    "lat": "31.0",
                    "lng": "121.2",
                    "height": "17.874",
                },
            },
        ),
        "proto-gs-500105-heartbeat": command(
            500105,
            [
                field("opcode", "integer", "指令编码"),
                field("msgId", "string", "消息编号"),
                field("time", "integer", "请求时间戳（秒）"),
            ],
            {"opcode": 500105, "msgId": "hb-1", "time": 1624511261},
            success=None,
        ),
        "proto-gs-500106-ack": command(
            500106,
            [
                field("opcode", "integer", "应答操作码"),
                field("msgId", "string", "消息编号"),
                field("data", "object", "应答内容"),
                field("data.opcode", "integer", "被应答的操作码"),
                field("data.time", "integer", "应答时间（秒）"),
            ],
            {"opcode": 500106, "msgId": "ack-1", "data": {"opcode": 500105, "time": 1624511261}},
            success=None,
        ),
        "proto-gs-500201-start": gs_ack(
            500201,
            [
                field("opcode", "integer", "指令编码"),
                field("msgId", "string", "消息编号"),
                field("taskId", "string", "任务编号"),
                field("deviceId", "string", "设备逻辑标识"),
                field("templateId", "string", "任务模板编号"),
                field("executionTime", "integer", "计划执行时间（秒）"),
            ],
            {
                "opcode": 500201,
                "msgId": "1617007530982",
                "taskId": TASK,
                "deviceId": DEVICE,
                "templateId": "100112",
                "executionTime": 1783318683,
            },
        ),
        "proto-gs-500202-action-result": command(
            500202,
            [
                field("opcode", "integer", "指令编码", description="外层永远是 500202，不是被报的那一步。"),
                field("msgId", "string", "消息编号"),
                field("taskId", "string", "任务编号"),
                field("deviceId", "string", "设备逻辑标识"),
                *step_result_package_fields(),
            ],
            {
                "opcode": 500202,
                "msgId": MSG,
                "taskId": TASK,
                "deviceId": DEVICE,
                "packages": {
                    "opcode": 200301,
                    "sequence": 3,
                    "deviceId": DEVICE,
                    "reportPoint": report_point_sample(),
                    "result": result_sample(
                        {"photos": [{"type": "jpg", "size": 1024, "data": "", "photoTime": 1624511261}]}
                    ),
                },
            },
        ),
        "proto-gs-500203-task-status": gs_ack(
            500203,
            [
                field("opcode", "integer", "指令编码"),
                field("msgId", "string", "消息编号"),
                field("taskId", "string", "任务编号"),
                field("deviceId", "string", "设备逻辑标识"),
                field("status", "integer", "任务状态码", description="300101启动成功 / 300102启动失败 / 300103终止成功 / 300104终止失败 / 300105已完成"),
                field("content", "string", "任务状态说明"),
                *report_point,
            ],
            {
                "opcode": 500203,
                "msgId": MSG,
                "taskId": TASK,
                "deviceId": DEVICE,
                "status": 300101,
                "content": "任务启动成功",
                "reportPoint": report_sample,
            },
        ),
        "proto-gs-500204-fault": gs_ack(
            500204,
            [
                field("opcode", "integer", "指令编码"),
                field("msgId", "string", "消息编号"),
                field("deviceId", "string", "设备逻辑标识"),
                field("status", "integer", "设备故障码"),
                field("content", "string", "故障说明"),
                *report_point,
            ],
            {
                "opcode": 500204,
                "msgId": MSG,
                "deviceId": DEVICE,
                "status": 101001,
                "content": "机器人机械故障-无法进行移动",
                "reportPoint": report_sample,
            },
        ),
        "proto-gs-500205-stop": gs_ack(
            500205,
            [
                field("opcode", "integer", "指令编码"),
                field("msgId", "string", "消息编号"),
                field("taskId", "string", "任务编号"),
                field("deviceId", "string", "设备逻辑标识"),
            ],
            {"opcode": 500205, "msgId": "1617007530982", "taskId": TASK, "deviceId": DEVICE},
        ),
        "proto-gs-500301-get-package": gs_ack(
            500301,
            [
                field("opcode", "integer", "指令编码"),
                field("msgId", "string", "消息编号"),
                field("templateId", "string", "任务模板编号"),
            ],
            {"opcode": 500301, "msgId": "1617007530982", "templateId": TEMPLATE},
            data_rows=[
                field("data", "object", "指令包结构"),
                field("data.templateId", "string", "任务模板编号"),
                field("data.lineId", "string", "线路id"),
                field("data.packages", "string", "指令包文本"),
            ],
            data_sample={"templateId": TEMPLATE, "lineId": "line-100111", "packages": "[]"},
        ),
        "proto-gs-500302-delete-package": gs_ack(
            500302,
            [
                field("opcode", "integer", "指令编码"),
                field("msgId", "string", "消息编号"),
                field("templateId", "string", "任务模板编号"),
            ],
            {"opcode": 500302, "msgId": "1617007530986", "templateId": TEMPLATE},
            data_rows=[field("data", "object", "删除结果")],
            data_sample={},
        ),
        "proto-gs-500303-list-templates": gs_ack(
            500303,
            [
                field("opcode", "integer", "指令编码"),
                field("msgId", "string", "消息编号"),
            ],
            {"opcode": 500303, "msgId": "1617007530984"},
            data_rows=[
                field("data", "object", "模板列表"),
                field("data.templates", "array", "任务模板"),
                field("data.templates[].templateId", "string", "任务模板编号"),
                field("data.templates[].lineId", "string", "线路id"),
            ],
            data_sample={"templates": [{"templateId": TEMPLATE, "lineId": "line-100111"}]},
        ),
        "proto-gs-500403-online-event": gs_ack(
            500403,
            [
                field("opcode", "integer", "指令编码"),
                field("deviceId", "string", "设备逻辑标识"),
                field("msgId", "string", "消息编号"),
                field("event", "integer", "事件码", description="700101掉线 / 700102上线"),
                field("content", "string", "事件说明"),
                field("time", "integer", "事件时间（秒）", required=False, description="修订记录补充"),
            ],
            {
                "opcode": 500403,
                "deviceId": DEVICE,
                "msgId": MSG,
                "event": 700102,
                "content": "机器人上线",
                "time": 1624511261,
            },
        ),
    }

    catalog["proto-robot-200102-move"] = command(
        200102,
        move_robot,
        {"sequence": 0, "opcode": 200102, "request": {"pointId": POINT}},
        success=result_fields(),
        success_sample=result_sample(),
        failure_rows=INNER_FAILURE_FIELDS,
        failure_sample={"resultCode": 900202, "msg": "指令操作失败"},
    )
    catalog["proto-robot-200301-photo"] = command(
        200301,
        photo_robot,
        {
            "sequence": 3,
            "opcode": 200301,
            "request": {
                "lat": "39.1277217676",
                "lng": "117.0190597072",
                "height": "30",
                "angleLevel": "10",
                "angleVertical": "10",
                "number": 1,
                "focuses": "2",
                "parking": True,
                "time": 20,
            },
        },
        success=result_fields(extra=photo_result_extra),
        success_sample=result_sample(
            {"photos": [{"type": "jpg", "size": 1024, "data": "", "photoTime": 1624511261}]}
        ),
        failure_rows=INNER_FAILURE_FIELDS,
        failure_sample={"resultCode": 900202, "msg": "指令操作失败"},
    )
    catalog["proto-robot-200501-status-report"] = command(
        200501,
        inner_request_fields([]),
        {"sequence": 0, "opcode": 200501, "request": {}},
        success=[
            field("operatePoint", "object", "操作点"),
            *operate_point_fields("operatePoint"),
            field("deviceId", "string", "设备逻辑标识"),
            field("sequence", "integer", "第几次上报"),
            field("time", "integer", "状态时间（秒）"),
            field("status", "integer", "设备状态码"),
            field("fault", "integer", "设备故障码"),
            field("faultReason", "string", "故障原因"),
            field("dataLink", "integer", "数据链路（0断开 1连接）"),
            field("electricity", "string", "剩余电量"),
            field("lat", "string", "纬度"),
            field("lng", "string", "经度"),
            field("height", "string", "GPS海拔高度"),
        ],
        success_sample={
            "operatePoint": operate_point_sample(),
            "deviceId": DEVICE,
            "sequence": 1,
            "time": 1624511261,
            "status": 400102,
            "fault": 100000,
            "faultReason": "no error",
            "dataLink": 1,
            "electricity": "80",
            "lat": "31.0",
            "lng": "121.2",
            "height": "17.874",
        },
        failure_rows=INNER_FAILURE_FIELDS,
        failure_sample={"resultCode": 900202, "msg": "指令操作失败"},
    )
    catalog["proto-robot-200403-gas-report"] = command(
        200403,
        [
            field("opcode", "integer", "内层动作码"),
            field("collect", "object", "气体浓度"),
            field("collect.CH4", "string", "甲烷浓度", required=False),
            field("collect.H2S", "string", "硫化氢浓度", required=False),
        ],
        {"opcode": 200403, "collect": {"CH4": "0.1", "H2S": "0.0"}},
        success=result_fields(extra=gas_result_extra),
        success_sample=result_sample({"collect": {"CH4": "0.1", "H2S": "0.0"}}),
        failure_rows=INNER_FAILURE_FIELDS,
        failure_sample={"resultCode": 900202, "msg": "指令操作失败"},
    )
    catalog["proto-robot-500104-package"] = gs_ack(
        500104,
        [
            field("opcode", "integer", "指令编码"),
            field("msgId", "string", "消息编号"),
            field("templateId", "string", "任务模板编号"),
            field("packages", "array", "有序动作集合", description="线上帧是 JSON 数组文本"),
            field("packages[].sequence", "integer", "动作序号"),
            field("packages[].opcode", "integer", "内层动作码"),
            field("packages[].request", "object", "动作请求参数"),
            field("packages[].request.pointId", "string", "点位标识", required=False),
            field("packages[].request.lat", "string", "纬度", required=False),
            field("packages[].request.lng", "string", "经度", required=False),
            field("packages[].request.height", "string", "地面高度", required=False),
            field("packages[].request.angleLevel", "string", "偏角", required=False),
            field("packages[].request.angleVertical", "string", "仰角", required=False),
            field("packages[].request.number", "string", "拍照张数", required=False),
            field("packages[].request.focuses", "string", "焦距", required=False),
            field("packages[].request.parking", "string", "是否驻停", required=False),
            field("packages[].request.time", "string", "驻停秒数", required=False),
        ],
        {
            "msgId": "ce577e3c81af4687b55a3982abc28a70",
            "opcode": 500104,
            "templateId": "1001234",
            "packages": [
                {
                    "request": {"pointId": POINT},
                    "sequence": 0,
                    "opcode": 200102,
                },
                {
                    "request": {
                        "lat": "39.1277217676",
                        "lng": "117.0190597072",
                        "height": "30",
                        "angleLevel": "10",
                        "angleVertical": "10",
                        "number": 1,
                        "focuses": "2",
                        "parking": True,
                        "time": 20,
                    },
                    "sequence": 3,
                    "opcode": 200301,
                },
            ],
        },
        data_rows=[
            field("data", "object", "返回消息体"),
            field("data.lineId", "string", "线路id"),
        ],
        data_sample={"lineId": "line-1001234"},
    )

    catalog["proto-uav-200101-takeoff"] = command(
        200101,
        inner_request_fields([
            field("request.lng", "string", "起飞截止点经度"),
            field("request.lat", "string", "起飞截止点纬度"),
            field("request.height", "string", "起飞截止点高度"),
        ]),
        {"sequence": 0, "opcode": 200101, "request": {"lng": "117.0190597072", "lat": "39.1277217676", "height": "30"}},
        success=result_fields(),
        success_sample=result_sample(),
        failure_rows=INNER_FAILURE_FIELDS,
        failure_sample={"resultCode": 900202, "msg": "指令操作失败"},
    )
    catalog["proto-uav-200102-move"] = command(
        200102,
        inner_request_fields([
            field("request.lng", "string", "指定点经度"),
            field("request.lat", "string", "指定点纬度"),
            field("request.height", "string", "指定点地面高度"),
            field("request.pointId", "string", "勘察地图点位id"),
        ]),
        {"sequence": 0, "opcode": 200102, "request": {"lng": "117.0190597072", "lat": "39.1277217676", "height": "30", "pointId": "uav-p1"}},
        success=result_fields(),
        success_sample=result_sample(),
        failure_rows=INNER_FAILURE_FIELDS,
        failure_sample={"resultCode": 900202, "msg": "指令操作失败"},
    )
    catalog["proto-uav-200103-land"] = command(
        200103,
        inner_request_fields([field("request.blank", "string", "占位字段，默认0000")]),
        {"sequence": 0, "opcode": 200103, "request": {"blank": "0000"}},
        success=result_fields(),
        success_sample=result_sample(),
        failure_rows=INNER_FAILURE_FIELDS,
        failure_sample={"resultCode": 900202, "msg": "指令操作失败"},
    )
    catalog["proto-uav-200104-descend"] = command(
        200104,
        inner_request_fields([field("request.height", "string", "降低点地面高度（米）")]),
        {"sequence": 0, "opcode": 200104, "request": {"height": "15"}},
        success=result_fields(),
        success_sample=result_sample(),
        failure_rows=INNER_FAILURE_FIELDS,
        failure_sample={"resultCode": 900202, "msg": "指令操作失败"},
    )
    catalog["proto-uav-200105-climb"] = command(
        200105,
        inner_request_fields([field("request.height", "string", "爬升点地面高度（米）")]),
        {"sequence": 0, "opcode": 200105, "request": {"height": "40"}},
        success=result_fields(),
        success_sample=result_sample(),
        failure_rows=INNER_FAILURE_FIELDS,
        failure_sample={"resultCode": 900202, "msg": "指令操作失败"},
    )
    catalog["proto-uav-200201-record-on"] = command(
        200201,
        inner_request_fields([
            field("request.rstp", "string", "指定视频流收集的服务地址", description="用于接收视频流的服务器地址，目前使用 rtsp。"),
            field("request.angleLevel", "string", "无人机摄像机云台控制水平旋转度数"),
            field("request.angleVertical", "string", "无人机摄像机云台控制垂直旋转度数"),
            field("request.focuses", "integer", "摄像头的焦距"),
            field("request.parking", "boolean", "表示该操作是否需要驻停"),
            field("request.time", "integer", "表示需要驻停的时长,秒为单位", description="协议标注为扩充字段；驻停长短也可由无人机主动确定。"),
            field(
                "request.rotate",
                "string",
                "摄像头在拍摄中是否需要进行旋转拍摄",
                description="对端协议必填，取值含义待与设备侧确认，禁止用空包发明枚举。",
            ),
        ]),
        {
            "sequence": 0,
            "opcode": 200201,
            "request": {
                "rstp": "rtsp://admin:12345@192.168.1.64:554/Streaming/Channels/1",
                "angleLevel": "0",
                "angleVertical": "10",
                "focuses": 2,
                "parking": True,
                "time": 10,
                "rotate": "",
            },
        },
        success=result_fields(),
        success_sample=result_sample(),
        failure_rows=INNER_FAILURE_FIELDS,
        failure_sample={"resultCode": 900202, "msg": "指令操作失败"},
    )
    # 无人机对接协议 200202：只有占位、是否驻停、驻停秒数。没有水平角、垂直角、焦距。
    # 拍摄后若要摄像头归位，不能把对准字段塞进本条；协议没有单独归位操作码时不得发明字段。
    catalog["proto-uav-200202-record-off"] = command(
        200202,
        inner_request_fields([
            field(
                "request.blank",
                "string",
                "占位字段，默认填写0000",
                description="该字段无任何逻辑含义仅用于占位使用。对端协议没有水平角、垂直角、焦距。",
            ),
            field("request.parking", "boolean", "表示该操作是否需要驻停"),
            field("request.time", "integer", "表示驻停的时长。秒为单位"),
        ]),
        {"sequence": 0, "opcode": 200202, "request": {"blank": "0000", "parking": False, "time": 0}},
        success=result_fields(),
        success_sample=result_sample(),
        failure_rows=INNER_FAILURE_FIELDS,
        failure_sample={"resultCode": 900202, "msg": "指令操作失败"},
    )
    catalog["proto-uav-200301-photo"] = command(
        200301,
        inner_request_fields([
            field("request.lat", "string", "拍摄目标纬度"),
            field("request.lng", "string", "拍摄目标经度"),
            field("request.height", "string", "拍摄目标地面高度"),
            field("request.angleLevel", "string", "拍摄偏角"),
            field("request.angleVertical", "string", "拍摄仰角"),
            field("request.number", "integer", "拍照张数"),
            field("request.focuses", "string", "焦距"),
            field("request.parking", "boolean", "是否驻停"),
            field("request.time", "integer", "驻停秒数"),
        ]),
        {
            "sequence": 0,
            "opcode": 200301,
            "request": {
                "lat": "39.1277217676",
                "lng": "117.0190597072",
                "height": "30",
                "angleLevel": "10",
                "angleVertical": "10",
                "number": 1,
                "focuses": "2",
                "parking": True,
                "time": 20,
            },
        },
        success=result_fields(extra=photo_result_extra),
        success_sample=result_sample(
            {"photos": [{"type": "jpg", "size": 1024, "data": "", "photoTime": 1624511261}]}
        ),
        failure_rows=INNER_FAILURE_FIELDS,
        failure_sample={"resultCode": 900202, "msg": "指令操作失败"},
    )
    catalog["proto-uav-200401-gas-on"] = command(
        200401,
        inner_request_fields([
            field("request.frequency", "integer", "气体上报频率（秒）"),
            field("request.parking", "boolean", "是否驻停"),
            field("request.time", "integer", "驻停秒数"),
        ]),
        {"sequence": 0, "opcode": 200401, "request": {"frequency": 30, "parking": True, "time": 10}},
        success=result_fields(extra=gas_result_extra),
        success_sample=result_sample({"collect": {"CH4": "0.1", "H2S": "0.0"}}),
        failure_rows=INNER_FAILURE_FIELDS,
        failure_sample={"resultCode": 900202, "msg": "指令操作失败"},
    )
    catalog["proto-uav-200402-gas-off"] = command(
        200402,
        inner_request_fields([
            field("request.blank", "string", "占位字段，默认0000"),
            field("request.parking", "boolean", "是否驻停"),
            field("request.time", "integer", "驻停秒数"),
        ]),
        {"sequence": 0, "opcode": 200402, "request": {"blank": "0000", "parking": False, "time": 0}},
        success=result_fields(),
        success_sample=result_sample(),
        failure_rows=INNER_FAILURE_FIELDS,
        failure_sample={"resultCode": 900202, "msg": "指令操作失败"},
    )
    catalog["proto-uav-200501-status-report"] = command(
        200501,
        inner_request_fields([]),
        {"sequence": 0, "opcode": 200501, "request": {}},
        success=[
            field("operatePoint", "object", "操作点"),
            *operate_point_fields("operatePoint"),
            field("deviceId", "string", "设备逻辑标识"),
            field("sequence", "integer", "第几次上报"),
            field("time", "integer", "状态时间（秒）"),
            field("status", "integer", "设备状态码"),
            field("fault", "integer", "设备故障码", required=False),
            field("electricity", "string", "当前电压"),
            field("lat", "string", "纬度"),
            field("lng", "string", "经度"),
            field("height", "string", "高度"),
            field("distance", "integer", "行驶里程（米）"),
        ],
        success_sample={
            "operatePoint": operate_point_sample(),
            **status_data_sample(False),
            "sequence": 1,
        },
        failure_rows=INNER_FAILURE_FIELDS,
        failure_sample={"resultCode": 900202, "msg": "指令操作失败"},
    )
    catalog["proto-uav-500104-package"] = gs_ack(
        500104,
        [
            field("opcode", "integer", "指令编码"),
            field("msgId", "string", "消息编号"),
            field("templateId", "string", "任务模板编号"),
            field("packages", "array", "有序动作集合", description="线上帧是 JSON 数组文本"),
            field("packages[].sequence", "integer", "动作序号"),
            field("packages[].opcode", "integer", "内层动作码"),
            field("packages[].request", "object", "动作请求参数"),
            field("packages[].request.lng", "string", "经度", required=False),
            field("packages[].request.lat", "string", "纬度", required=False),
            field("packages[].request.height", "string", "高度", required=False),
            field("packages[].request.pointId", "string", "点位标识", required=False),
            field("packages[].request.blank", "string", "占位字段", required=False),
            field("packages[].request.rstp", "string", "视频流收集地址", required=False),
            field("packages[].request.angleLevel", "string", "偏角", required=False),
            field("packages[].request.angleVertical", "string", "仰角", required=False),
            field("packages[].request.number", "string", "拍照张数", required=False),
            field("packages[].request.focuses", "string", "焦距", required=False),
            field("packages[].request.parking", "string", "是否驻停", required=False),
            field("packages[].request.time", "string", "驻停秒数", required=False),
            field("packages[].request.rotate", "string", "是否旋转拍摄", required=False),
            field("packages[].request.frequency", "integer", "气体上报频率（秒）", required=False),
        ],
        {
            "msgId": "uav-500104-demo",
            "opcode": 500104,
            "templateId": "100201",
            "packages": [
                {"sequence": 0, "opcode": 200101, "request": {"lng": "117.0190597072", "lat": "39.1277217676", "height": "30"}},
                {"sequence": 1, "opcode": 200102, "request": {"lng": "117.01906", "lat": "39.12773", "height": "30", "pointId": "uav-p1"}},
                {
                    "sequence": 2,
                    "opcode": 200301,
                    "request": {
                        "lat": "39.12773",
                        "lng": "117.01906",
                        "height": "30",
                        "angleLevel": "10",
                        "angleVertical": "10",
                        "number": 1,
                        "focuses": "2",
                        "parking": True,
                        "time": 20,
                    },
                },
                {"sequence": 3, "opcode": 200202, "request": {"blank": "0000", "parking": False, "time": 0}},
                {"sequence": 4, "opcode": 200103, "request": {"blank": "0000"}},
            ],
        },
        data_rows=[
            field("data", "object", "返回消息体"),
            field("data.lineId", "string", "线路id"),
        ],
        data_sample={"lineId": "line-100201"},
    )
    catalog["proto-gs-500401-stream-on"] = gs_ack(
        500401,
        [
            field("opcode", "integer", "指令编码"),
            field("msgId", "string", "消息编号"),
            field("deviceId", "string", "设备逻辑标识"),
            field("rtmp", "string", "推流地址"),
        ],
        {"opcode": 500401, "msgId": MSG, "deviceId": DEVICE, "rtmp": "rtmp://example/live"},
    )
    catalog["proto-gs-500402-stream-off"] = gs_ack(
        500402,
        [
            field("opcode", "integer", "指令编码"),
            field("msgId", "string", "消息编号"),
            field("deviceId", "string", "设备逻辑标识"),
        ],
        {"opcode": 500402, "msgId": MSG, "deviceId": DEVICE},
    )
    return catalog


SHARED = ["robot-ws", "uav-ws"]
ROBOT = ["robot-ws"]
UAV = ["uav-ws"]


def protocol_version_index(value) -> str | None:
    if value is None:
        return None
    if isinstance(value, list):
        tokens = [str(item).strip() for item in value if str(item).strip()]
        return ",".join(tokens) if tokens else None
    text = str(value).strip()
    return text or None

# 名称、方向、交互方式、协议版本。交互方式不是 sync_wait_result 时只留主包。
IDENTITIES = {
    "proto-gs-500101-status": ("获取设备实时状态", "downlink", "sync_wait_result", SHARED),
    "proto-gs-500102-report-switch": ("启动或关闭定时上报", "downlink", "sync_wait_result", SHARED),
    "proto-gs-500103-report-push": ("定时上报设备状态", "uplink", "active_uplink", SHARED),
    "proto-gs-500105-heartbeat": ("心跳", "downlink", "heartbeat", SHARED),
    "proto-gs-500106-ack": ("回执", "ack", "", SHARED),
    "proto-gs-500201-start": ("启动已下发任务", "downlink", "sync_wait_result", SHARED),
    "proto-gs-500202-action-result": ("单条指令操作结果", "uplink", "async_result", SHARED),
    "proto-gs-500203-task-status": ("任务状态", "uplink", "active_uplink", SHARED),
    "proto-gs-500204-fault": ("设备故障", "uplink", "active_uplink", SHARED),
    "proto-gs-500205-stop": ("终止任务", "downlink", "sync_wait_result", SHARED),
    "proto-gs-500301-get-package": ("获取指令包", "downlink", "sync_wait_result", SHARED),
    "proto-gs-500302-delete-package": ("删除指令包", "downlink", "sync_wait_result", SHARED),
    "proto-gs-500303-list-templates": ("列出任务模板", "downlink", "sync_wait_result", SHARED),
    "proto-gs-500401-stream-on": ("启动推流", "downlink", "sync_wait_result", SHARED),
    "proto-gs-500402-stream-off": ("停止推流", "downlink", "sync_wait_result", SHARED),
    "proto-gs-500403-online-event": ("在线或掉线", "uplink", "active_uplink", SHARED),
    "proto-robot-200102-move": ("移动", "downlink", "", ROBOT),
    "proto-robot-200301-photo": ("拍照", "downlink", "", ROBOT),
    "proto-robot-200501-status-report": ("定时上报设备状态", "uplink", "active_uplink", ROBOT),
    "proto-robot-200403-gas-report": ("气体检测数据上报", "uplink", "active_uplink", ROBOT),
    "proto-robot-500104-package": ("巡检任务指令包", "downlink", "sync_wait_result", ROBOT),
    "proto-uav-200101-takeoff": ("起飞", "downlink", "", UAV),
    "proto-uav-200102-move": ("移动", "downlink", "", UAV),
    "proto-uav-200103-land": ("降落", "downlink", "", UAV),
    "proto-uav-200104-descend": ("降低飞行高度", "downlink", "", UAV),
    "proto-uav-200105-climb": ("爬升飞行高度", "downlink", "", UAV),
    "proto-uav-200201-record-on": ("启动录像", "downlink", "", UAV),
    "proto-uav-200202-record-off": ("关闭录像", "downlink", "", UAV),
    "proto-uav-200301-photo": ("拍照", "downlink", "", UAV),
    "proto-uav-200401-gas-on": ("启动气体检测", "downlink", "", UAV),
    "proto-uav-200402-gas-off": ("关闭气体检测", "downlink", "", UAV),
    "proto-uav-200501-status-report": ("定时上报设备状态", "uplink", "active_uplink", UAV),
    "proto-uav-500104-package": ("巡检任务指令包", "downlink", "sync_wait_result", UAV),
}


def keep_main_packet(blob: dict) -> dict:
    if not isinstance(blob, dict):
        return {"outbound": blob}
    outbound = blob.get("outbound")
    return {"outbound": outbound} if outbound is not None else {}


def apply_identities(catalog: dict[str, dict]) -> dict[str, dict]:
    missing = [code for code in catalog if code not in IDENTITIES]
    extra = [code for code in IDENTITIES if code not in catalog]
    if missing or extra:
        raise AssertionError(f"指令身份表对不上 catalog missing={missing} extra={extra}")
    for code, item in catalog.items():
        name, direction, exchange, versions = IDENTITIES[code]
        item["name"] = name
        item["command_direction"] = direction
        item["exchange_mode"] = exchange
        item["protocol_version"] = versions
        if exchange != "sync_wait_result":
            item["field_description_json"] = keep_main_packet(item["field_description_json"])
            item["command_json"] = keep_main_packet(item["command_json"])
            item["sample_json"] = keep_main_packet(item["sample_json"])
    return catalog


def merge_custom_fields(existing: dict, complete: dict) -> dict:
    drop = {
        "command_schema_json",
        "field_translation_rules_json",
        "step_signal_mapping_rules_json",
        "sample_messages_json",
        "protocol_command_schema_json",
        "protocol_field_translation_rules_json",
        "protocol_step_signal_mapping_rules_json",
        "protocol_sample_messages_json",
    }
    nxt = {key: value for key, value in existing.items() if key not in drop}
    nxt["opcode"] = complete["opcode"]
    nxt["command_json"] = complete["command_json"]
    nxt["field_description_json"] = complete["field_description_json"]
    nxt["sample_json"] = complete["sample_json"]
    nxt.pop("command_direction", None)
    nxt["exchange_mode"] = complete["exchange_mode"]
    nxt["protocol_version"] = complete["protocol_version"]
    return nxt


def psql(sql: str) -> str:
    env = os.environ.copy()
    env.setdefault("PGPASSWORD", "Coolhomer")
    result = subprocess.run(
        [
            "psql",
            "-h",
            env.get("PGHOST", "127.0.0.1"),
            "-U",
            env.get("PGUSER", "postgres"),
            "-d",
            env.get("PGDATABASE", "sinopec"),
            "-v",
            "ON_ERROR_STOP=1",
            "-At",
            "-c",
            sql,
        ],
        check=True,
        capture_output=True,
        text=True,
        env=env,
    )
    return result.stdout


def write_seed_sql(catalog: dict[str, dict]) -> None:
    lines = [
        "-- 按协议原文回填三卷：字段说明 / 指令 JSON / 样例。由 complete_protocol_tabs_from_docs.py 生成。",
        "SET search_path TO dynamicbusiness, public;",
        "UPDATE ent_data_protocol_t1 e",
        "SET custom_fields = v.custom_fields::jsonb, updater = 'seed', update_time = CURRENT_TIMESTAMP",
        "FROM (VALUES",
    ]
    values = []
    for code, complete in catalog.items():
        payload = json.dumps(
            {
                "opcode": complete["opcode"],
                "command_direction": complete["command_direction"],
                "exchange_mode": complete["exchange_mode"],
                "protocol_version": complete["protocol_version"],
                "command_json": complete["command_json"],
                "field_description_json": complete["field_description_json"],
                "sample_json": complete["sample_json"],
            },
            ensure_ascii=False,
            separators=(",", ":"),
        ).replace("'", "''")
        values.append(f"  ('{code}', $${payload}$$)")
    lines.append(",\n".join(values))
    lines.append(") AS v(code, custom_fields)")
    lines.append("WHERE e.deleted = false AND e.tenant_id = 1 AND e.code = v.code;")
    lines.append(
        "UPDATE ent_data_protocol_t1 e\n"
        "SET custom_fields = e.custom_fields || jsonb_build_object('opcode', v.opcode)\n"
        "FROM (VALUES\n"
        + ",\n".join(f"  ('{code}', {item['opcode']})" for code, item in catalog.items())
        + "\n) AS v(code, opcode)\n"
        "WHERE e.deleted = false AND e.tenant_id = 1 AND e.code = v.code;"
    )
    SQL_OUT.write_text("\n".join(lines) + "\n", encoding="utf-8")


def apply_identity_schema() -> None:
    psql(
        """
        SET search_path TO dynamicbusiness;
        INSERT INTO dynamic_field (
          code, name, type, unit, description, source, status, max_relations,
          index_strategy, options, provider_code, semantic_type, tenant_id, creator
        ) VALUES
          ('command_direction', '方向', 'ENUM', NULL, '这条指令谁发给谁：下发 / 上报 / 回执',
           'SYSTEM', 1, NULL, 'BTREE',
           '[{"label":"下发","value":"downlink"},{"label":"上报","value":"uplink"},{"label":"回执","value":"ack"}]',
           NULL, 'command_direction', 1, 'seed'),
          ('exchange_mode', '交互方式', 'ENUM', NULL, '这条指令自己怎么收发。回执和只定义动作内容的指令可不填。2 开头塞进任务包是算步骤时的事，不在这里选。',
           'SYSTEM', 1, NULL, 'BTREE',
           '[{"label":"同步要结果","value":"sync_wait_result"},{"label":"异步出结果","value":"async_result"},{"label":"主动上报","value":"active_uplink"},{"label":"心跳对回","value":"heartbeat"}]',
           NULL, 'exchange_mode', 1, 'seed'),
          ('protocol_version', '协议版本', 'MULTI_SELECT', NULL, '这条指令适用哪几份对接协议。生成指令时按设备选中的协议版本来找。',
           'SYSTEM', 1, NULL, 'BTREE',
           '[{"label":"机器人对接协议","value":"robot-ws"},{"label":"无人机对接协议","value":"uav-ws"}]',
           NULL, 'protocol_version', 1, 'seed')
        ON CONFLICT (code, tenant_id) WHERE deleted = false
        DO UPDATE SET
          name = EXCLUDED.name, type = EXCLUDED.type, description = EXCLUDED.description,
          options = EXCLUDED.options, semantic_type = EXCLUDED.semantic_type,
          source = EXCLUDED.source, status = 1, deleted = false,
          updater = 'seed', update_time = CURRENT_TIMESTAMP;
        INSERT INTO dynamic_model_field_assignment (
          model_id, field_id, model_code, field_code, required, is_searchable, is_filterable, is_sortable,
          sort, field_source, tenant_id, creator
        )
        SELECT m.id, f.id, m.code, f.code, false, true, true, false, mapping.sort_order, 'CUSTOM', 1, 'seed'
        FROM (VALUES
          ('exchange_mode', 14),
          ('protocol_version', 16)
        ) AS mapping(field_code, sort_order)
        JOIN dynamic_model m ON m.deleted = false AND m.tenant_id = 1
          AND m.code IN ('data_protocol_ws', 'data_protocol_robot')
        JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1 AND f.code = mapping.field_code
        ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
        DO UPDATE SET
          field_id = EXCLUDED.field_id, sort = EXCLUDED.sort, field_source = EXCLUDED.field_source,
          deleted = false, updater = 'seed', update_time = CURRENT_TIMESTAMP;
        """
    )


def apply_live(catalog: dict[str, dict]) -> int:
    apply_identity_schema()
    psql(
        "UPDATE dynamicbusiness.dynamic_category "
        "SET name = '巡检', description = '巡检共用外层指令', "
        "updater = 'seed', update_time = CURRENT_TIMESTAMP "
        "WHERE deleted = false AND tenant_id = 1 AND code = 'cat-protocol-gs' "
        "AND name IS DISTINCT FROM '巡检';"
    )
    raw = psql(
        "SELECT json_agg(json_build_object('id', id, 'code', code, 'name', name, "
        "'custom_fields', custom_fields)) "
        "FROM dynamicbusiness.ent_data_protocol_t1 "
        "WHERE deleted = false AND tenant_id = 1;"
    ).strip()
    rows = json.loads(raw or "[]")
    by_code = {str(row.get("code") or ""): row for row in rows}
    updated = 0
    for code, complete in catalog.items():
        row = by_code.get(code)
        if not row:
            insert_missing_command(code, complete)
            upsert_identity_index(code, complete)
            updated += 1
            print(f"inserted {code} roles={list(complete['command_json'])}")
            continue
        nxt = merge_custom_fields(row.get("custom_fields") or {}, complete)
        name = complete["name"]
        same_fields = nxt == row.get("custom_fields")
        same_name = row.get("name") == name
        if same_fields and same_name:
            continue
        payload = json.dumps(nxt, ensure_ascii=False).replace("'", "''")
        name_sql = name.replace("'", "''")
        direction = str(complete["command_direction"]).replace("'", "''")
        psql(
            "UPDATE dynamicbusiness.ent_data_protocol_t1 "
            f"SET custom_fields = '{payload}'::jsonb, name = '{name_sql}', "
            f"command_direction = '{direction}', "
            "updater = 'seed', update_time = CURRENT_TIMESTAMP "
            f"WHERE id = {int(row['id'])};"
        )
        upsert_identity_index(code, complete)
        updated += 1
        print(f"updated {code} name={name} roles={list(complete['command_json'])}")
    print(f"done {updated}/{len(catalog)}")
    return updated


def upsert_identity_index(code: str, complete: dict) -> None:
    opcode = int(complete["opcode"])
    pairs = (
        ("opcode", None, opcode),
        ("exchange_mode", complete["exchange_mode"] or None, None),
        ("protocol_version", protocol_version_index(complete["protocol_version"]), None),
    )
    for field_code, value_string, value_number in pairs:
        number_sql = "NULL" if value_number is None else str(value_number)
        string_sql = "NULL" if value_string is None else "'" + str(value_string).replace("'", "''") + "'"
        psql(
            "UPDATE dynamicbusiness.dynamic_entity_field_index_t1 idx "
            f"SET value_string = {string_sql}, value_number = {number_sql}, "
            "deleted = false, updater = 'seed', update_time = CURRENT_TIMESTAMP "
            "FROM dynamicbusiness.ent_data_protocol_t1 e "
            f"WHERE e.code = '{code}' AND e.deleted = false AND e.tenant_id = 1 "
            "AND idx.entity_id = e.id AND idx.deleted = false AND idx.tenant_id = 1 "
            f"AND idx.field_code = '{field_code}';"
        )
        psql(
            "INSERT INTO dynamicbusiness.dynamic_entity_field_index_t1 "
            "(entity_id, model_id, field_code, value_string, value_number, creator, tenant_id, deleted) "
            f"SELECT e.id, e.model_id, '{field_code}', {string_sql}, {number_sql}, 'seed', 1, false "
            "FROM dynamicbusiness.ent_data_protocol_t1 e "
            f"WHERE e.deleted = false AND e.tenant_id = 1 AND e.code = '{code}' "
            "AND NOT EXISTS ("
            "  SELECT 1 FROM dynamicbusiness.dynamic_entity_field_index_t1 idx "
            "  WHERE idx.tenant_id = 1 AND idx.entity_id = e.id "
            f"    AND idx.field_code = '{field_code}' AND idx.deleted = false"
            ");"
        )


def insert_missing_command(code: str, complete: dict) -> None:
    payload = json.dumps(merge_custom_fields({}, complete), ensure_ascii=False).replace("'", "''")
    name = str(complete["name"]).replace("'", "''")
    opcode = int(complete["opcode"])
    direction = str(complete["command_direction"]).replace("'", "''")
    psql(
        "INSERT INTO dynamicbusiness.ent_data_protocol_t1 "
        "(tenant_id, entity_type_code, model_id, name, code, status, custom_fields, command_direction, creator, deleted) "
        "SELECT 1, 'data_protocol', m.id, "
        f"'{name}', '{code}', 1, '{payload}'::jsonb, '{direction}', 'seed', false "
        "FROM dynamicbusiness.dynamic_model m "
        "WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'data_protocol_ws' "
        "AND NOT EXISTS ("
        "  SELECT 1 FROM dynamicbusiness.ent_data_protocol_t1 e "
        f"  WHERE e.deleted = false AND e.tenant_id = 1 AND e.code = '{code}'"
        ");"
    )
    psql(
        "INSERT INTO dynamicbusiness.dynamic_entity_category_relation_t1 "
        "(tenant_id, category_id, entity_type_code, entity_id, domain, sort, creator, deleted) "
        "SELECT 1, c.id, 'data_protocol', e.id, NULL, 0, 'seed', false "
        "FROM dynamicbusiness.ent_data_protocol_t1 e "
        "JOIN dynamicbusiness.dynamic_category c "
        "  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'cat-protocol-gs' "
        f"WHERE e.deleted = false AND e.tenant_id = 1 AND e.code = '{code}' "
        "AND NOT EXISTS ("
        "  SELECT 1 FROM dynamicbusiness.dynamic_entity_category_relation_t1 r "
        "  WHERE r.deleted = false AND r.tenant_id = 1 AND r.entity_type_code = 'data_protocol' "
        "    AND r.entity_id = e.id AND r.category_id = c.id"
        ");"
    )
    for field_code, value_string, value_number in (
        ("opcode", None, opcode),
        ("exchange_mode", complete["exchange_mode"] or None, None),
        ("protocol_version", protocol_version_index(complete["protocol_version"]), None),
    ):
        number_sql = "NULL" if value_number is None else str(value_number)
        string_sql = "NULL" if value_string is None else "'" + str(value_string).replace("'", "''") + "'"
        psql(
            "INSERT INTO dynamicbusiness.dynamic_entity_field_index_t1 "
            "(entity_id, model_id, field_code, value_string, value_number, creator, tenant_id, deleted) "
            f"SELECT e.id, e.model_id, '{field_code}', {string_sql}, {number_sql}, 'seed', 1, false "
            "FROM dynamicbusiness.ent_data_protocol_t1 e "
            f"WHERE e.deleted = false AND e.tenant_id = 1 AND e.code = '{code}' "
            "AND NOT EXISTS ("
            "  SELECT 1 FROM dynamicbusiness.dynamic_entity_field_index_t1 idx "
            f"  WHERE idx.deleted = false AND idx.tenant_id = 1 AND idx.entity_id = e.id "
            f"    AND idx.field_code = '{field_code}'"
            ");"
        )


def assert_ack_semantics(catalog: dict[str, dict]) -> None:
    envelope = ("opcode", "msgId", "code", "msg")
    for code, item in catalog.items():
        packets = item["command_json"]
        success = packets.get("inboundSuccess")
        if isinstance(success, dict):
            missing = [key for key in envelope if key not in success]
            if missing:
                raise AssertionError(f"{code} inboundSuccess 缺外壳 {missing}")
            if "data" not in success:
                raise AssertionError(f"{code} inboundSuccess 缺 data")
            if success["code"] != SUCCESS_ACK_CODE:
                raise AssertionError(f"{code} inboundSuccess.code={success['code']}")
            if "resultCode" in success:
                raise AssertionError(f"{code} inboundSuccess 不得用根上 resultCode 顶替外壳")
        failure = packets.get("inboundFailure")
        if isinstance(failure, dict):
            missing = [key for key in envelope if key not in failure]
            if missing:
                raise AssertionError(f"{code} inboundFailure 缺外壳 {missing}")
            if failure["code"] == SUCCESS_ACK_CODE:
                raise AssertionError(f"{code} inboundFailure.code 不能是成功码")
            if not str(failure.get("msg") or "").strip():
                raise AssertionError(f"{code} inboundFailure.msg 不能空")
            if "resultCode" in failure:
                raise AssertionError(f"{code} inboundFailure 不得用根上 resultCode 顶替外壳")


def request_keys(catalog: dict[str, dict], code: str) -> set[str]:
    outbound = catalog[code]["command_json"]["outbound"]
    request = outbound.get("request")
    if not isinstance(request, dict):
        raise AssertionError(f"{code} 下发空包没有 request")
    return set(request)


def assert_protocol_request_keys(catalog: dict[str, dict]) -> None:
    expected = {
        "proto-robot-200102-move": {"pointId"},
        "proto-robot-200301-photo": {
            "lat",
            "lng",
            "height",
            "angleLevel",
            "angleVertical",
            "number",
            "focuses",
            "parking",
            "time",
        },
        "proto-uav-200102-move": {"lng", "lat", "height", "pointId"},
        "proto-uav-200201-record-on": {
            "rstp",
            "angleLevel",
            "angleVertical",
            "focuses",
            "parking",
            "time",
            "rotate",
        },
        "proto-uav-200202-record-off": {"blank", "parking", "time"},
        "proto-uav-200301-photo": {
            "lat",
            "lng",
            "height",
            "angleLevel",
            "angleVertical",
            "number",
            "focuses",
            "parking",
            "time",
        },
        "proto-uav-200402-gas-off": {"blank", "parking", "time"},
    }
    forbidden_on_close_record = {"angleLevel", "angleVertical", "focuses"}
    for code, keys in expected.items():
        actual = request_keys(catalog, code)
        if actual != keys:
            raise AssertionError(f"{code} 下发 request 键与对端协议不符 actual={sorted(actual)} expected={sorted(keys)}")
    close_keys = request_keys(catalog, "proto-uav-200202-record-off")
    leaked = close_keys & forbidden_on_close_record
    if leaked:
        raise AssertionError(f"200202 不得带对准字段 {sorted(leaked)}")


def assert_step_result_uplink(catalog: dict[str, dict]) -> None:
    item = catalog["proto-gs-500202-action-result"]
    outbound = item["command_json"]["outbound"]
    if "inboundSuccess" in item["command_json"] or "inboundFailure" in item["command_json"]:
        raise AssertionError("500202 是上报，不得再写外层成功/失败卷")
    if not isinstance(outbound.get("packages"), dict):
        raise AssertionError("500202 packages 必须是一条结果对象，不能是数组")
    step = outbound["packages"]
    missing = [key for key in ("opcode", "sequence", "result") if key not in step]
    if missing:
        raise AssertionError(f"500202 packages 缺步身份字段 {missing}")
    if outbound.get("opcode") != 500202:
        raise AssertionError("500202 外层 opcode 必须是 500202")
    if step.get("opcode") == 500202:
        raise AssertionError("500202 packages.opcode 必须是内层动作码，不能再写 500202")


def main() -> int:
    catalog = apply_identities(build_catalog())
    assert_ack_semantics(catalog)
    assert_protocol_request_keys(catalog)
    assert_step_result_uplink(catalog)
    write_seed_sql(catalog)
    apply_live(catalog)
    print(f"wrote {SQL_OUT}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
