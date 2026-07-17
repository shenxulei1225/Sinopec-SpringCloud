#!/usr/bin/env python3
import math
import unittest

from segment_polyline import split_polyline_to_segments


class SegmentPolylineTest(unittest.TestCase):
    def test_straight_10m_makes_10_unit_segments(self):
        pts = [(0.0, 0.0), (10.0, 0.0)]
        segs = split_polyline_to_segments(pts, segment_length=1.0)
        self.assertEqual(len(segs), 10)
        self.assertTrue(all(abs(s.scale_x - 1.0) < 1e-6 for s in segs))
        self.assertAlmostEqual(segs[0].x, 0.5, places=5)
        self.assertAlmostEqual(segs[-1].x, 9.5, places=5)
        self.assertAlmostEqual(segs[0].z, 0.0, places=5)

    def test_partial_tail_scales(self):
        pts = [(0.0, 0.0), (2.5, 0.0)]
        segs = split_polyline_to_segments(pts, segment_length=1.0)
        self.assertEqual(len(segs), 3)
        self.assertAlmostEqual(segs[0].scale_x, 1.0, places=5)
        self.assertAlmostEqual(segs[1].scale_x, 1.0, places=5)
        self.assertAlmostEqual(segs[2].scale_x, 0.5, places=5)

    def test_yaw_along_plus_z(self):
        pts = [(0.0, 0.0), (0.0, 4.0)]
        segs = split_polyline_to_segments(pts, segment_length=1.0)
        self.assertEqual(len(segs), 4)
        # direction (dx,dz)=(0,1) → yaw = atan2(-1, 0) = -90°
        self.assertTrue(all(abs(s.yaw_deg - (-90.0)) < 1e-4 for s in segs))


if __name__ == "__main__":
    unittest.main()
