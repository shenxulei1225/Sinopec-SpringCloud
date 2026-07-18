#!/usr/bin/env python3
"""Migrate Nacos YAML config keys: yudao: / ${yudao. → cheers: / ${cheers.

Usage:
  python3 script/nacos-migrate-cheers-config.py              # dry-run (default)
  python3 script/nacos-migrate-cheers-config.py --apply     # publish changes

Requires local Nacos console on :8080 and API on :8848 (nacos/nacos).
"""
from __future__ import annotations

import argparse
import json
import re
import sys
import urllib.error
import urllib.parse
import urllib.request

CONSOLE = "http://127.0.0.1:8080"
USER = "nacos"
PASSWORD = "nacos"
GROUP = "DEFAULT_GROUP"

YUDUO_ROOT = re.compile(r"(?m)^([ \t]*)yudao:[ \t]*$")
YUDUO_PLACEHOLDER = "${yudao."
CHEERS_PLACEHOLDER = "${cheers."


def http_json(method: str, url: str, data: dict | None = None, token: str | None = None) -> dict:
    body = None
    headers = {}
    if data is not None:
        body = urllib.parse.urlencode(data).encode()
        headers["Content-Type"] = "application/x-www-form-urlencoded"
    if token:
        headers["accessToken"] = token
        headers["Authorization"] = f"Bearer {token}"
    req = urllib.request.Request(url, data=body, headers=headers, method=method)
    with urllib.request.urlopen(req, timeout=30) as resp:
        return json.loads(resp.read().decode())


def login() -> str:
    for path in ("/v3/auth/user/login", "/v1/auth/login"):
        try:
            j = http_json("POST", f"{CONSOLE}{path}", {"username": USER, "password": PASSWORD})
            token = j.get("accessToken")
            if token:
                return token
        except Exception:
            continue
    raise SystemExit("Nacos login failed on :8080")


def list_namespaces(token: str) -> list[str]:
    j = http_json("GET", f"{CONSOLE}/v3/console/core/namespace/list?accessToken={urllib.parse.quote(token)}")
    out = []
    for item in j.get("data") or []:
        ns = item.get("namespace")
        if ns is None:
            continue
        out.append("" if ns == "public" else str(ns))
    if "dev" not in out:
        out.append("dev")
    if "" not in out:
        out.append("")
    return out


def list_configs(token: str, namespace_id: str) -> list[dict]:
    params = {
        "pageNo": 1,
        "pageSize": 500,
        "search": "blur",
        "dataId": "",
        "group": "",
        "namespaceId": namespace_id or "public",
        "accessToken": token,
    }
    url = f"{CONSOLE}/v3/console/cs/config/list?{urllib.parse.urlencode(params)}"
    j = http_json("GET", url, token=token)
    return (j.get("data") or {}).get("pageItems") or []


def get_config(token: str, data_id: str, namespace_id: str) -> str | None:
    params = {
        "dataId": data_id,
        "group": GROUP,
        "namespaceId": namespace_id or "public",
        "accessToken": token,
    }
    url = f"{CONSOLE}/v3/console/cs/config?{urllib.parse.urlencode(params)}"
    try:
        j = http_json("GET", url, token=token)
    except urllib.error.HTTPError:
        return None
    data = j.get("data")
    if isinstance(data, dict):
        return data.get("content")
    return None


def publish_config(token: str, data_id: str, namespace_id: str, content: str, typ: str = "yaml") -> None:
    data = {
        "dataId": data_id,
        "group": GROUP,
        "namespaceId": namespace_id or "public",
        "content": content,
        "type": typ,
        "accessToken": token,
    }
    # Nacos 3 console publish
    url = f"{CONSOLE}/v3/console/cs/config"
    http_json("POST", url, data=data, token=token)


def migrate_content(content: str) -> tuple[str, bool]:
    new = content
    new = YUDUO_ROOT.sub(r"\1cheers:", new)
    new = new.replace(YUDUO_PLACEHOLDER, CHEERS_PLACEHOLDER)
    # quoted config keys
    new = re.sub(r'(["\'])yudao\.([a-zA-Z][a-zA-Z0-9._-]*)\1', r"\1cheers.\2\1", new)
    return new, new != content


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--apply", action="store_true", help="Publish migrated content")
    args = parser.parse_args()

    try:
        token = login()
    except SystemExit as e:
        print(e)
        return 2

    namespaces = list_namespaces(token)
    print(f"Namespaces: {namespaces or ['(none)']}")

    changed = []
    scanned = 0
    for ns in namespaces:
        items = list_configs(token, ns)
        print(f"namespace={ns or 'public'!r}: {len(items)} config(s)")
        for item in items:
            data_id = item.get("dataId") or ""
            content = item.get("content")
            if content is None:
                content = get_config(token, data_id, ns)
            if not content:
                scanned += 1
                continue
            scanned += 1
            new_content, did = migrate_content(content)
            if not did:
                continue
            changed.append((ns or "public", data_id))
            print(f"  NEED {ns or 'public'}/{data_id}")
            if args.apply:
                publish_config(token, data_id, ns, new_content, item.get("type") or "yaml")
                print(f"  APPLIED {data_id}")

    print(f"Scanned={scanned}, need_migrate={len(changed)}, apply={args.apply}")
    if scanned == 0:
        print("No Nacos config entries found. Apps use optional:nacos — empty is OK.")
        print("When you publish configs later, re-run with --apply.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
