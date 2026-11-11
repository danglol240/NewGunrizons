"""Render the vehicle meshes with three.js in headless Chromium: preview sheet and 32x32 item icons.

usage: render_vehicles.py THREE_DIR OUT_DIR [--icons] [NAME ...]
  THREE_DIR  an npm `three` package folder (three@0.160)
"""
import functools
import http.server
import shutil
import sys
import tempfile
import threading
from pathlib import Path

from PIL import Image, ImageDraw
from playwright.sync_api import sync_playwright

TOOLS = Path(__file__).resolve().parent
ASSETS = TOOLS.parent / "src/main/resources/assets/newgunrizons"
ORDER = ["FieldJeep", "OffroadTruck", "ArmoredAPC", "BattleTank", "HoverJeep", "TiltrotorVTOL", "PlasmaFighter",
         "ParticleCruiser", "AntimatterFrigate", "QuantumRingship", "GravityTank", "SpacetimeSkimmer",
         "DimensionalLeviathan", "GenesisArk"]
TIERS = ["LV", "MV", "HV", "EV", "IV", "LuV", "ZPM", "UV", "UHV", "UEV", "UIV", "UMV", "UXV", "MAX"]


def serve(three):
    root = Path(tempfile.mkdtemp())
    (root / "assets").symlink_to(ASSETS)
    (root / "three").symlink_to(Path(three).resolve())
    shutil.copy(TOOLS / "vehicle_preview.html", root / "index.html")
    class Quiet(http.server.SimpleHTTPRequestHandler):
        def log_message(self, *a):
            pass
    handler = functools.partial(Quiet, directory=str(root))
    srv = http.server.ThreadingHTTPServer(("127.0.0.1", 0), handler)
    threading.Thread(target=srv.serve_forever, daemon=True).start()
    return srv


def main(argv):
    three, out = argv[0], Path(argv[1])
    icons = "--icons" in argv
    names = [a for a in argv[2:] if not a.startswith("--")] or [n for n in ORDER if
                                                                (ASSETS / f"models/vehicle/vehicle_{n.lower()}.obj").exists()]
    out.mkdir(parents=True, exist_ok=True)
    srv = serve(three)
    port = srv.server_address[1]
    shots = {}
    with sync_playwright() as pw:
        b = pw.chromium.launch(args=["--use-angle=swiftshader", "--enable-unsafe-swiftshader", "--ignore-gpu-blocklist"])
        page = b.new_page(viewport={"width": 700, "height": 700})
        page.on("console", lambda msg: print("  [page]", msg.text) if msg.type == "error" else None)
        page.on("pageerror", lambda e: print("  [page error]", e))
        for n in names:
            key = n.lower()
            for view in ("3q", "rear", "side"):
                page.goto(f"http://127.0.0.1:{port}/index.html?name={key}&view={view}&size=640")
                page.wait_for_function("document.title === 'done'", timeout=60000)
                path = out / f"{key}_{view}.png"
                page.locator("canvas").screenshot(path=str(path))
                shots[(n, view)] = path
            if icons:
                page.goto(f"http://127.0.0.1:{port}/index.html?name={key}&view=3q&size=256&bg=0")
                page.wait_for_function("document.title === 'done'", timeout=60000)
                tmp = out / f"{key}_icon_big.png"
                page.locator("canvas").screenshot(path=str(tmp), omit_background=True)
                im = Image.open(tmp).convert("RGBA")
                im = im.crop(im.getbbox())
                side = max(im.size)
                sq = Image.new("RGBA", (side, side), (0, 0, 0, 0))
                sq.paste(im, ((side - im.width) // 2, (side - im.height) // 2))
                sq.resize((32, 32), Image.LANCZOS).save(ASSETS / "textures/items" / f"vehicle_{key}.png")
            print("rendered", n)
        b.close()
    srv.shutdown()
    # sheet: one row per vehicle, three views
    w = 640
    sheet = Image.new("RGB", (w * 3, w * len(names)), (35, 40, 51))
    d = ImageDraw.Draw(sheet)
    for i, n in enumerate(names):
        for j, view in enumerate(("3q", "rear", "side")):
            sheet.paste(Image.open(shots[(n, view)]).convert("RGB"), (j * w, i * w))
        d.text((10, i * w + 8), f"{TIERS[ORDER.index(n)]}  {n}", fill=(255, 220, 120))
    sheet.save(out / "sheet.png")


if __name__ == "__main__":
    main(sys.argv[1:])
