#!/usr/bin/env python3
"""Unit tests for import_legacy_path_sites.build_network (stdlib unittest)."""

from __future__ import annotations

import unittest

from import_legacy_path_sites import SiteMap, build_network, map_zone_id

SITE = SiteMap(legacy_site_id="121240", facility_id=45, facility_code="FAC-L")


class BuildNetworkTests(unittest.TestCase):
    def test_map_zone_id_stable(self):
        a = map_zone_id("TG1")
        b = map_zone_id("TG1")
        self.assertIsNotNone(a)
        self.assertEqual(a, b)
        self.assertNotEqual(a, map_zone_id("TG2"))

    def test_gate_kind_becomes_door(self):
        transits = [
            {
                "navigation_waypoint_id": "GATE1",
                "waypoint_name": "东门",
                "navigation_waypoint_kind": 3,
                "tank_group_id": "TG1",
                "enabled": 1,
                "coordinate_json": '{"sceneX":1,"sceneY":0,"sceneZ":2}',
            }
        ]
        net = build_network(SITE, [], transits, [])
        node = next(n for n in net["nodes"] if n["nodeId"] == "GATE1")
        self.assertEqual(node["nodeType"], "DOOR")
        self.assertEqual(node["zoneId"], map_zone_id("TG1"))
        self.assertEqual(node["payload"]["legacyWaypointKind"], 3)

    def test_station_gets_zone_from_tank_group(self):
        visits = [
            {
                "inspection_point_id": "P1",
                "waypoint_name": "p",
                "tank_group_id": "TG1",
                "enabled": 1,
                "coordinate_json": '{"sceneX":0,"sceneY":0,"sceneZ":0}',
            }
        ]
        net = build_network(SITE, visits, [], [])
        self.assertEqual(net["nodes"][0]["nodeType"], "STATION")
        self.assertEqual(net["nodes"][0]["zoneId"], map_zone_id("TG1"))

    def test_weight_minus_one_is_blocked(self):
        visits = [
            {
                "inspection_point_id": "A",
                "enabled": 1,
                "tank_group_id": "T",
                "coordinate_json": '{"sceneX":0,"sceneY":0,"sceneZ":0}',
            },
            {
                "inspection_point_id": "B",
                "enabled": 1,
                "tank_group_id": "T",
                "coordinate_json": '{"sceneX":1,"sceneY":0,"sceneZ":0}',
            },
        ]
        edges = [{"start_waypoint_id": "A", "end_waypoint_id": "B", "weight": -1, "distance": 10}]
        net = build_network(SITE, visits, [], edges)
        self.assertEqual(len(net["edges"]), 1)
        e = net["edges"][0]
        self.assertEqual(e["traversability"], "BLOCKED")
        self.assertEqual(e.get("impedanceByProfile"), {})

    def test_positive_distance_impedance(self):
        visits = [
            {
                "inspection_point_id": "A",
                "enabled": 1,
                "coordinate_json": '{"sceneX":0,"sceneY":0,"sceneZ":0}',
            },
            {
                "inspection_point_id": "B",
                "enabled": 1,
                "coordinate_json": '{"sceneX":3,"sceneY":0,"sceneZ":0}',
            },
        ]
        edges = [{"start_waypoint_id": "A", "end_waypoint_id": "B", "distance": 12, "weight": 5}]
        net = build_network(SITE, visits, [], edges)
        self.assertEqual(len(net["edges"]), 1)
        self.assertEqual(net["edges"][0]["traversability"], "TRAVERSABLE")
        self.assertEqual(net["edges"][0]["impedanceByProfile"]["person_walk"], 12.0)


if __name__ == "__main__":
    unittest.main()
