"""Preview the projectile styles drawn by EntityBulletRenderer.

Re-implements the renderer's geometry (camera-relative space, camera at the origin
looking down +Z, Y down as in Minecraft's eye space after projection) with additive
blending, so the shapes can be checked without starting the game.

usage: preview_projectiles.py OUT.png
"""
import math
import random
import sys

import numpy as np
from PIL import Image, ImageDraw

W, H, F = 420, 260, 300.0  # tile size and focal length (pixels)


def add(a, b): return [a[i] + b[i] for i in range(3)]
def sub(a, b): return [a[i] - b[i] for i in range(3)]
def mul(a, k): return [v * k for v in a]
def cross(a, b): return [a[1] * b[2] - a[2] * b[1], a[2] * b[0] - a[0] * b[2], a[0] * b[1] - a[1] * b[0]]
def length(a): return math.sqrt(sum(v * v for v in a))


def norm(a):
    l = length(a)
    return None if l < 1e-9 else mul(a, 1 / l)


def basis(n):
    helper = [0, 1, 0] if abs(n[1]) < 0.9 else [1, 0, 0]
    u = norm(cross(n, helper))
    return u, cross(n, u)


class Canvas:
    def __init__(self):
        self.acc = np.zeros((H, W, 3), dtype=np.float32)

    def proj(self, p):
        return (W / 2 + F * p[0] / p[2], H / 2 - F * p[1] / p[2])

    def poly(self, pts, rgb, alpha):
        layer = Image.new("L", (W, H), 0)
        ImageDraw.Draw(layer).polygon([self.proj(p) for p in pts], fill=int(255 * max(0, min(1, alpha))))
        m = np.asarray(layer, dtype=np.float32)[..., None] / 255.0
        self.acc += m * np.array(rgb, dtype=np.float32)[None, None, :] * 255

    def line(self, pts, rgb, alphas, width):
        for i in range(len(pts) - 1):
            layer = Image.new("L", (W, H), 0)
            ImageDraw.Draw(layer).line([self.proj(pts[i]), self.proj(pts[i + 1])],
                                       fill=int(255 * max(0, min(1, alphas[i]))), width=int(width))
            m = np.asarray(layer, dtype=np.float32)[..., None] / 255.0
            self.acc += m * np.array(rgb, dtype=np.float32)[None, None, :] * 255

    def image(self, label):
        img = Image.fromarray(np.clip(self.acc + np.array([16, 18, 24]), 0, 255).astype(np.uint8))
        ImageDraw.Draw(img).text((8, 6), label, fill=(230, 230, 236))
        return img


WHITE = (1, 1, 1)


def ribbon(cv, a, b, width, rgb, aa, ab, steps=10):
    d = sub(b, a)
    side = norm(cross(d, mul(add(a, b), 0.5)))
    if side is None:
        return
    o = mul(side, width / 2)
    for i in range(steps):  # approximate the alpha gradient with sub-quads
        f0, f1 = i / steps, (i + 1) / steps
        p0, p1 = add(a, mul(d, f0)), add(a, mul(d, f1))
        cv.poly([sub(p0, o), add(p0, o), add(p1, o), sub(p1, o)], rgb, aa + (ab - aa) * (f0 + f1) / 2)


def glow(cv, c, radius, rgb, alpha, rings=6):
    u, v = basis(norm(c))
    for k in range(rings, 0, -1):  # concentric discs approximate the radial fade
        rr = radius * k / rings
        pts = [add(c, add(mul(u, math.cos(a) * rr), mul(v, math.sin(a) * rr)))
               for a in [2 * math.pi * i / 16 for i in range(16)]]
        cv.poly(pts, rgb, alpha / rings)


def ring(cv, c, axis, radius, rgb, alpha, width):
    u, v = basis(axis)
    pts = [add(c, add(mul(u, math.cos(a) * radius), mul(v, math.sin(a) * radius)))
           for a in [2 * math.pi * i / 24 for i in range(25)]]
    cv.line(pts, rgb, [alpha] * 25, width)


def draw(style, rgb, s, t=7.3):
    cv = Canvas()
    p = [0.6, -0.3, 6.0]
    d = norm([0.55, 0.12, 1.0])
    if style == "TRACER":
        tail = add(p, mul(d, -2.5 * s))
        ribbon(cv, tail, p, 0.10 * s, rgb, 0, 0.55); ribbon(cv, tail, p, 0.03 * s, WHITE, 0, 0.9)
    elif style == "RAIL":
        ln = 6 * s; tail = add(p, mul(d, -ln))
        ribbon(cv, tail, p, 0.22 * s, rgb, 0, 0.45); ribbon(cv, tail, p, 0.06 * s, WHITE, 0.1, 1)
        for i in range(1, 6):
            ring(cv, add(p, mul(d, -i * ln / 6)), d, (0.14 + 0.03 * i) * s, rgb, 0.7 * (1 - i / 6), 2)
        glow(cv, p, 0.22 * s, rgb, 0.8)
    elif style == "LASER":
        start = add(p, mul(d, -8))
        fl = 0.75 + 0.25 * math.sin(t * 2.7)
        ribbon(cv, start, p, 0.14 * s, rgb, 0.35 * fl, 0.45 * fl); ribbon(cv, start, p, 0.035 * s, WHITE, 0.8, 1)
        glow(cv, p, 0.18 * s, rgb, 0.9)
    elif style == "PLASMA":
        r = (0.22 + 0.04 * math.sin(t * 1.3)) * s
        ribbon(cv, add(p, mul(d, -1.6 * s)), p, r * 1.3, rgb, 0, 0.5)
        glow(cv, p, r * 2.2, rgb, 0.45); glow(cv, p, r, WHITE, 0.9)
    elif style == "HELIX":
        ln = 4 * s
        ribbon(cv, add(p, mul(d, -ln)), p, 0.05 * s, WHITE, 0, 1)
        u, v = basis(d)
        for strand in range(2):
            pts, al = [], []
            for i in range(49):
                k = ln * i / 48; a = k * 5 + t * 0.9 + strand * math.pi
                pts.append(add(add(p, mul(d, -k)), add(mul(u, math.cos(a) * 0.16 * s), mul(v, math.sin(a) * 0.16 * s))))
                al.append(0.95 * (1 - k / ln))
            cv.line(pts, rgb, al, 2.5)
        glow(cv, p, 0.2 * s, rgb, 0.85)
    elif style == "PULSE":
        ribbon(cv, add(p, mul(d, -2 * s)), p, 0.12 * s, rgb, 0, 0.5)
        glow(cv, p, 0.26 * s, WHITE, 0.8); glow(cv, p, 0.5 * s, rgb, 0.4)
        for i in range(3):
            ph = ((t * 0.12) + i / 3) % 1
            ring(cv, add(p, mul(d, -ph * 1.5 * s)), d, (0.15 + ph * 0.6) * s, rgb, 0.8 * (1 - ph), 2)
    elif style == "ORB_RING":
        ribbon(cv, add(p, mul(d, -2.5 * s)), p, 0.3 * s, rgb, 0, 0.35)
        glow(cv, p, 0.55 * s, rgb, 0.5); glow(cv, p, 0.22 * s, WHITE, 0.9)
        u, v = basis(d); a1, a2 = t * 0.35, -t * 0.27
        ring(cv, p, norm(add(mul(d, math.cos(a1)), mul(u, math.sin(a1)))), 0.6 * s, rgb, 0.9, 3)
        ring(cv, p, norm(add(mul(u, math.cos(a2)), mul(v, math.sin(a2)))), 0.48 * s,
             (1, rgb[1] * 0.6 + 0.4, rgb[2] * 0.6 + 0.4), 0.8, 2)
    elif style == "SHARDS":
        rnd = random.Random(42 + int(t * 2)); u, v = basis(d)
        ribbon(cv, add(p, mul(d, -2 * s)), p, 0.04 * s, WHITE, 0, 0.9)
        for i in range(9):
            back = rnd.random() * 2.5 * s; off = 0.1 + rnd.random() * 0.35 * s; ang = rnd.random() * 2 * math.pi
            c = add(add(p, mul(d, -back)), add(mul(u, math.cos(ang) * off), mul(v, math.sin(ang) * off)))
            tip = add(c, mul(d, -(0.2 + rnd.random() * 0.4) * s))
            ribbon(cv, c, tip, 0.06 * s, (0.65, 0.25, 1.0) if i % 2 == 0 else rgb, 0.9, 0.2, steps=3)
        glow(cv, p, 0.3 * s, rgb, 0.7)
    return cv


def hexrgb(h):
    return ((h >> 16) & 255) / 255, ((h >> 8) & 255) / 255, (h & 255) / 255


ROWS = [
    ("XR-7 / HX-9  TRACER", "TRACER", 0x4696FF, 1.2),
    ("Railgun  RAIL", "RAIL", 0xFF40D6, 1.6),
    ("Laser Rifle  LASER", "LASER", 0x28E4F0, 1.0),
    ("Plasma Cannon  PLASMA", "PLASMA", 0x28E4F0, 2.2),
    ("Particle Beam  HELIX", "HELIX", 0x5AFF78, 1.0),
    ("Neutron Rifle  PULSE", "PULSE", 0x5AFF78, 1.0),
    ("Antimatter Cannon  ORB_RING", "ORB_RING", 0xFF4034, 1.8),
    ("Exotic Matter  SHARDS", "SHARDS", 0xFF4034, 1.0),
]

if __name__ == "__main__":
    tiles = [draw(style, hexrgb(col), sc).image(label) for label, style, col, sc in ROWS]
    sheet = Image.new("RGB", (W * 2, H * 4))
    for i, t in enumerate(tiles):
        sheet.paste(t, ((i % 2) * W, (i // 2) * H))
    sheet.save(sys.argv[1])
