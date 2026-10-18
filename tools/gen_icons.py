"""Draw the 16x16 item icons for the weapon-class mechanisms and technology modules.

Style follows the mod's existing component icons: shaded metal, dark outline.

usage: gen_icons.py [PREVIEW.png]
"""
import math
import sys
from pathlib import Path

from PIL import Image

OUT = Path(__file__).resolve().parent.parent / "src/main/resources/assets/newgunrizons/textures/items"
OUTLINE = (22, 22, 30, 255)

STEEL = [(70, 74, 84), (112, 118, 130), (160, 168, 180), (210, 216, 226)]  # dark -> light
GUNMETAL = [(40, 42, 50), (64, 68, 78), (92, 96, 108), (128, 132, 144)]
BRASS = [(120, 84, 30), (176, 128, 46), (222, 178, 76), (250, 222, 140)]
COPPER = [(110, 50, 24), (170, 86, 40), (222, 128, 64), (250, 180, 120)]
POLYMER = [(28, 28, 32), (44, 44, 50), (62, 62, 70), (84, 84, 94)]


def _ramp(glow, core):
    dark = tuple(int(c * 0.45) for c in glow)
    light = tuple((g + c) // 2 for g, c in zip(glow, core))
    return [dark, glow, light, core]


GLOW = {  # same tier accents as the weapon models (tools/gunforge.py)
    "EV": _ramp((255, 176, 60), (255, 236, 200)),
    "IV": _ramp((40, 200, 255), (210, 245, 255)),
    "LuV": _ramp((255, 70, 190), (255, 215, 240)),
    "ZPM": _ramp((255, 120, 30), (255, 225, 190)),
    "UV": _ramp((110, 160, 255), (225, 235, 255)),
    "UHV": _ramp((180, 70, 255), (240, 215, 255)),
    "UEV": _ramp((80, 90, 255), (215, 220, 255)),
    "UIV": _ramp((40, 230, 200), (205, 255, 245)),
    "UMV": _ramp((90, 255, 110), (225, 255, 225)),
    "UXV": _ramp((220, 80, 255), (250, 220, 255)),
    "MAX": _ramp((255, 205, 70), (255, 250, 225)),
}


class Icon:
    def __init__(self):
        self.img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
        self.p = self.img.load()

    def px(self, x, y, c):
        if 0 <= x < 16 and 0 <= y < 16:
            self.p[x, y] = tuple(c) + (255,) if len(c) == 3 else tuple(c)

    def rect(self, x0, y0, x1, y1, ramp, shade=True):
        """Filled rect, lit from the top-left: top row light, bottom row dark."""
        for y in range(y0, y1 + 1):
            for x in range(x0, x1 + 1):
                if not shade:
                    c = ramp[2]
                elif y == y0 or x == x0:
                    c = ramp[3]
                elif y == y1 or x == x1:
                    c = ramp[0]
                else:
                    c = ramp[2] if (y - y0) < (y1 - y0) / 2 else ramp[1]
                self.px(x, y, c)

    def line(self, x0, y0, x1, y1, c, width=1):
        n = max(abs(x1 - x0), abs(y1 - y0), 1)
        for i in range(n + 1):
            x = round(x0 + (x1 - x0) * i / n)
            y = round(y0 + (y1 - y0) * i / n)
            for dx in range(width):
                for dy in range(width):
                    self.px(x + dx, y + dy, c)

    def disc(self, cx, cy, r, ramp):
        for y in range(16):
            for x in range(16):
                d = math.hypot(x - cx, y - cy)
                if d <= r + 0.3:
                    # light from top-left
                    k = ((x - cx) + (y - cy)) / (2 * r + 1e-6)
                    idx = 3 if k < -0.45 else 2 if k < 0 else 1 if k < 0.45 else 0
                    self.px(x, y, ramp[idx])

    def ring(self, cx, cy, r_out, r_in, ramp):
        for y in range(16):
            for x in range(16):
                d = math.hypot(x - cx, y - cy)
                if r_in - 0.2 <= d <= r_out + 0.3:
                    k = ((x - cx) + (y - cy)) / (2 * r_out)
                    self.px(x, y, ramp[3] if k < -0.4 else ramp[2] if k < 0 else ramp[1] if k < 0.4 else ramp[0])

    def outline(self):
        a = [[self.p[x, y][3] > 0 for x in range(16)] for y in range(16)]
        for y in range(16):
            for x in range(16):
                if a[y][x]:
                    continue
                if any(0 <= x + dx < 16 and 0 <= y + dy < 16 and a[y + dy][x + dx]
                       for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1))):
                    self.p[x, y] = OUTLINE
        return self


def pistol_slide():
    i = Icon()
    i.rect(2, 5, 13, 8, STEEL)
    i.rect(6, 5, 8, 6, POLYMER, shade=False)  # ejection port
    for x in (11, 12, 13):
        i.line(x, 6, x, 8, STEEL[0])  # rear serrations
    i.px(3, 4, STEEL[3])  # front sight
    i.px(12, 4, STEEL[3])  # rear sight
    i.rect(3, 9, 7, 10, GUNMETAL)  # recoil spring guide
    return i.outline()


def blowback_bolt():
    i = Icon()
    i.rect(3, 4, 12, 8, STEEL)
    i.rect(10, 5, 11, 7, GUNMETAL, shade=False)  # firing pin hole
    for k, x in enumerate(range(3, 13, 2)):  # recoil spring
        i.line(x, 10 + (k % 2), x + 1, 11 - (k % 2), STEEL[2])
    i.rect(2, 9, 2, 12, GUNMETAL)
    i.rect(13, 9, 13, 12, GUNMETAL)
    return i.outline()


def gas_piston_system():
    i = Icon()
    i.rect(1, 7, 14, 8, STEEL)  # gas tube
    i.rect(9, 4, 11, 10, GUNMETAL)  # gas block
    i.rect(2, 9, 8, 10, STEEL)  # piston rod
    i.px(10, 6, (255, 140, 60))  # port
    return i.outline()


def precision_bolt_action():
    i = Icon()
    i.rect(2, 5, 11, 8, STEEL)
    i.line(10, 8, 13, 11, STEEL[2], width=2)  # bolt handle
    i.disc(13.5, 12.5, 1.6, GUNMETAL)  # knob
    i.rect(4, 6, 7, 6, STEEL[0:1] * 4, shade=False)  # locking lug groove
    i.px(3, 4, BRASS[2])
    return i.outline()


def pump_action_assembly():
    i = Icon()
    i.rect(1, 4, 14, 6, STEEL)  # magazine tube
    i.rect(3, 7, 11, 11, POLYMER)  # forend
    for x in range(4, 11, 2):
        i.line(x, 8, x, 10, POLYMER[3])  # grip grooves
    i.rect(12, 7, 13, 8, STEEL)  # action bars
    return i.outline()


def belt_feed_mechanism():
    i = Icon()
    i.rect(5, 2, 13, 7, GUNMETAL)  # feed tray cover
    i.rect(6, 3, 8, 4, STEEL, shade=False)
    for k in range(5):  # cartridge belt hanging down-left
        x, y = 9 - 2 * k, 8 + k
        i.rect(x, y, x + 1, y + 1, BRASS)
        i.px(x, y - 1 if k else y, BRASS[3])
    return i.outline()


def smart_targeting_module():
    i = Icon()
    g = GLOW["EV"]
    i.rect(2, 3, 13, 12, POLYMER)
    i.disc(7.5, 7.5, 3.2, g)
    i.px(7, 7, g[3])
    for x in (3, 12):
        i.line(x, 5, x, 10, g[1])  # side indicator strips
    i.line(5, 13, 10, 13, STEEL[1])  # connector
    return i.outline()


def electromagnetic_coil():
    i = Icon()
    i.rect(2, 4, 13, 11, COPPER)
    for x in range(3, 13, 2):
        i.line(x, 4, x, 11, COPPER[0])  # windings
    i.rect(0, 6, 1, 9, STEEL)  # core ends
    i.rect(14, 6, 15, 9, STEEL)
    i.px(7, 3, GLOW["IV"][1])
    i.px(8, 3, GLOW["IV"][1])
    return i.outline()


def superconducting_rail():
    i = Icon()
    g = GLOW["IV"]
    i.line(1, 12, 12, 1, STEEL[1], width=3)
    i.line(1, 12, 12, 1, STEEL[3])
    i.line(3, 12, 14, 1, g[1])  # glowing superconducting strip
    i.line(2, 14, 4, 12, GUNMETAL[2], width=2)
    return i.outline()


def focusing_lens_array():
    i = Icon()
    g = GLOW["LuV"]
    i.rect(1, 5, 14, 10, GUNMETAL)
    for cx in (3.5, 7.5, 11.5):
        i.disc(cx, 7.5, 1.8, g)
    i.px(3, 7, g[3])
    i.px(7, 7, g[3])
    i.px(11, 7, g[3])
    return i.outline()


def plasma_containment_chamber():
    i = Icon()
    g = GLOW["ZPM"]
    i.rect(4, 1, 11, 2, STEEL)  # caps
    i.rect(4, 13, 11, 14, STEEL)
    i.rect(3, 3, 12, 12, GUNMETAL)
    i.disc(7.5, 7.5, 3.6, g)
    i.ring(7.5, 7.5, 4.6, 4.0, STEEL)
    i.px(6, 6, g[3])
    return i.outline()


def particle_accelerator_segment():
    i = Icon()
    g = GLOW["UV"]
    i.ring(7.5, 7.5, 6.5, 4.2, STEEL)
    i.ring(7.5, 7.5, 5.8, 4.6, g)
    i.rect(6, 0, 9, 2, GUNMETAL)  # beam pipe flange
    i.rect(6, 13, 9, 15, GUNMETAL)
    return i.outline()


def neutron_emitter():
    i = Icon()
    g = GLOW["UV"]
    i.rect(2, 5, 9, 10, STEEL)
    i.rect(3, 6, 4, 9, GUNMETAL, shade=False)  # moderator band
    i.rect(10, 4, 11, 11, GUNMETAL)  # emitter dish
    i.rect(12, 5, 13, 10, g)
    i.rect(14, 6, 14, 9, g, shade=False)
    i.px(13, 7, g[3])
    return i.outline()


def antimatter_trap():
    i = Icon()
    g = GLOW["UHV"]
    i.disc(7.5, 7.5, 4.5, GUNMETAL)
    i.disc(7.5, 7.5, 1.6, g)
    for a in range(0, 360, 12):  # tilted containment ring
        x = 7.5 + 6.6 * math.cos(math.radians(a))
        y = 7.5 + 2.4 * math.sin(math.radians(a))
        if not (60 < a < 120):
            i.px(round(x), round(y), g[1] if a > 180 else g[2])
    return i.outline()


def exotic_matter_stabilizer():
    i = Icon()
    g = GLOW["UHV"]
    pts = [(7.5, 1), (14, 7.5), (7.5, 14), (1, 7.5)]
    for y in range(16):
        for x in range(16):
            if abs(x - 7.5) + abs(y - 7.5) <= 6.6:
                d = abs(x - 7.5) + abs(y - 7.5)
                c = (60, 30, 90) if d > 4 else (90, 40, 130) if d > 2 else g[1]
                i.px(x, y, c)
    for x, y in ((5, 5), (10, 6), (6, 10), (9, 9)):
        i.px(x, y, (255, 255, 255))
    i.px(7, 7, g[3])
    i.px(8, 8, g[3])
    _ = pts
    return i.outline()


def quantum_entanglement_core():
    i = Icon()
    g = GLOW["UEV"]
    i.rect(3, 3, 12, 12, GUNMETAL)
    i.disc(5.5, 7.5, 2.2, g)  # entangled pair
    i.disc(10.5, 7.5, 2.2, g)
    i.line(7, 7, 9, 7, g[3])
    i.line(7, 8, 9, 8, g[2])
    i.px(5, 7, g[3])
    i.px(10, 7, g[3])
    for x in (1, 14):
        i.rect(x, 6, x, 9, STEEL)
    return i.outline()


def graviton_field_emitter():
    i = Icon()
    g = GLOW["UIV"]
    i.rect(1, 5, 7, 10, GUNMETAL)  # emitter body
    i.rect(8, 3, 9, 12, STEEL)  # dish
    for k, r in enumerate((2.0, 3.5, 5.0)):  # field arcs
        for a in range(-60, 61, 12):
            x = 9 + r * math.cos(math.radians(a))
            y = 7.5 + r * math.sin(math.radians(a))
            i.px(round(x), round(y), g[3 - k])
    i.px(4, 7, g[2])
    return i.outline()


def chrono_stabilizer():
    i = Icon()
    g = GLOW["UMV"]
    i.ring(7.5, 7.5, 6.5, 5.2, BRASS)
    i.disc(7.5, 7.5, 4.6, [(20, 40, 24), (30, 60, 34), (40, 80, 44), (60, 110, 64)])
    for a in range(0, 360, 30):
        i.px(round(7.5 + 4 * math.cos(math.radians(a))), round(7.5 + 4 * math.sin(math.radians(a))), g[2])
    i.line(7, 7, 7, 3, g[3])  # hands
    i.line(8, 8, 11, 9, g[1])
    i.rect(6, 0, 9, 1, BRASS)  # crown
    return i.outline()


def singularity_containment_unit():
    i = Icon()
    g = GLOW["UMV"]
    i.rect(2, 1, 13, 2, STEEL)
    i.rect(2, 13, 13, 14, STEEL)
    i.rect(2, 3, 2, 12, GUNMETAL)
    i.rect(13, 3, 13, 12, GUNMETAL)
    for a in range(0, 360, 10):  # accretion disc
        x = 7.5 + 5.0 * math.cos(math.radians(a))
        y = 7.5 + 1.8 * math.sin(math.radians(a))
        i.px(round(x), round(y), g[2] if a < 180 else g[1])
    i.disc(7.5, 7.5, 2.2, [(4, 4, 6), (8, 8, 10), (12, 12, 16), (20, 20, 26)])
    i.px(7, 5, g[3])
    return i.outline()


def dimensional_rift_emitter():
    i = Icon()
    g = GLOW["UXV"]
    i.rect(1, 9, 9, 13, GUNMETAL)  # emitter
    i.rect(2, 10, 3, 12, g, shade=False)
    pts = [(9, 10), (11, 7), (10, 5), (13, 3), (12, 1), (15, 0)]  # rift tear
    for (x0, y0), (x1, y1) in zip(pts, pts[1:]):
        i.line(x0, y0, x1, y1, g[1], width=2)
        i.line(x0, y0, x1, y1, g[3])
    i.px(14, 6, g[2])
    i.px(8, 4, g[2])
    return i.outline()


def reality_engine():
    i = Icon()
    g = GLOW["MAX"]
    for y in range(16):
        for x in range(16):
            d = abs(x - 7.5) + abs(y - 7.5)
            if d <= 7:
                i.px(x, y, g[1] if d > 5.5 else (236, 232, 220) if d > 3 else g[2])
    for a in range(0, 360, 45):
        i.px(round(7.5 + 5 * math.cos(math.radians(a))), round(7.5 + 5 * math.sin(math.radians(a))), g[0])
    i.disc(7.5, 7.5, 1.8, [(150, 190, 255), (190, 215, 255), (225, 240, 255), (255, 255, 255)])
    return i.outline()


def _chassis(ramp, accent, sparkle=None, rainbow=False):
    """Gun-frame silhouette: receiver rail, open body frame, grip and stock, in an Avaritia material."""
    i = Icon()
    i.rect(1, 4, 14, 5, ramp)  # top rail
    i.rect(1, 6, 2, 9, ramp)  # front post
    i.rect(13, 6, 14, 10, ramp)  # stock post
    i.rect(1, 9, 9, 10, ramp)  # lower rail
    i.rect(7, 11, 9, 14, ramp)  # grip
    i.rect(10, 9, 14, 10, ramp)
    for x in range(4, 12, 2):  # lightening cuts glow with the tier accent
        i.px(x, 7, accent[1])
    i.px(5, 7, accent[3])
    if rainbow:
        cols = [(255, 90, 90), (255, 200, 80), (120, 255, 120), (90, 200, 255), (200, 120, 255)]
        for k, x in enumerate(range(2, 14, 3)):
            i.px(x, 4, cols[k % len(cols)])
    if sparkle:
        for x, y in ((3, 5), (11, 4), (13, 8), (8, 12)):
            i.px(x, y, sparkle)
    return i.outline()


CRYSTAL = [(70, 130, 150), (120, 200, 220), (180, 240, 255), (235, 255, 255)]
NEUTRONIUM = [(30, 30, 34), (52, 52, 58), (74, 74, 82), (110, 110, 120)]
SPACETIME = [(14, 10, 30), (28, 20, 52), (44, 32, 76), (70, 56, 110)]
ETERNAL = [(170, 160, 140), (215, 208, 190), (240, 236, 224), (255, 252, 244)]


def crystal_matrix_chassis():
    return _chassis(CRYSTAL, GLOW["UV"], sparkle=(255, 255, 255))


def neutronium_chassis():
    return _chassis(NEUTRONIUM, GLOW["UHV"], sparkle=(220, 220, 230))


def infinity_chassis():
    return _chassis(STEEL, GLOW["UEV"], rainbow=True)


def spacetime_chassis():
    return _chassis(SPACETIME, GLOW["UMV"], sparkle=(200, 255, 210))


def eternal_chassis():
    return _chassis(ETERNAL, GLOW["MAX"], sparkle=(255, 220, 120))


ICONS = {
    "pistol_slide": pistol_slide,
    "blowback_bolt": blowback_bolt,
    "gas_piston_system": gas_piston_system,
    "precision_bolt_action": precision_bolt_action,
    "pump_action_assembly": pump_action_assembly,
    "belt_feed_mechanism": belt_feed_mechanism,
    "smart_targeting_module": smart_targeting_module,
    "electromagnetic_coil": electromagnetic_coil,
    "superconducting_rail": superconducting_rail,
    "focusing_lens_array": focusing_lens_array,
    "plasma_containment_chamber": plasma_containment_chamber,
    "particle_accelerator_segment": particle_accelerator_segment,
    "neutron_emitter": neutron_emitter,
    "antimatter_trap": antimatter_trap,
    "exotic_matter_stabilizer": exotic_matter_stabilizer,
    "quantum_entanglement_core": quantum_entanglement_core,
    "graviton_field_emitter": graviton_field_emitter,
    "chrono_stabilizer": chrono_stabilizer,
    "singularity_containment_unit": singularity_containment_unit,
    "dimensional_rift_emitter": dimensional_rift_emitter,
    "reality_engine": reality_engine,
    "crystal_matrix_chassis": crystal_matrix_chassis,
    "neutronium_chassis": neutronium_chassis,
    "infinity_chassis": infinity_chassis,
    "spacetime_chassis": spacetime_chassis,
    "eternal_chassis": eternal_chassis,
}


def main():
    imgs = []
    for name, fn in ICONS.items():
        img = fn().img
        img.save(OUT / f"{name}.png")
        imgs.append(img)
    if len(sys.argv) > 1:
        cell = 112
        sheet = Image.new("RGBA", (cell * 7, cell * 4), (40, 44, 52, 255))
        for k, img in enumerate(imgs):
            big = img.resize((96, 96), Image.NEAREST)
            sheet.paste(big, ((k % 7) * cell + 8, (k // 7) * cell + 8), big)
        sheet.save(sys.argv[1])


if __name__ == "__main__":
    main()
