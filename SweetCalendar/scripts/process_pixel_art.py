"""Remove solid backgrounds from pixel-art JPGs and export transparent PNGs."""
from __future__ import annotations

import os
from collections import deque
from pathlib import Path

from PIL import Image

SRC_DIR = Path(__file__).resolve().parents[2] / "pixel art"
OUT_DIR = Path(__file__).resolve().parents[1] / "app" / "src" / "main" / "res" / "drawable"

# source filename -> drawable resource name (without extension)
OUTPUT_NAMES = {
    "240_F_1851046705_l8RTkBrLSVQ8i52LXpf1tOdV7NYzxvYR.jpg": "pixel_wrapped_candy",
    "240_F_1707792970_p7lh2CaHZyOnnwMUqhX2FCqccqfnOhvB.jpg": "pixel_lollipop_swirl",
    "240_F_653155836_OYh6zqhBIs443ixZpDjVlwQT56GAGI65.jpg": "pixel_lollipop",
    "240_F_1306489105_rR8xzyazgQVYpiJmFmrr554Rn0jIcBzH.jpg": "pixel_ice_cream",
    "240_F_1949416871_sTZqVWysJ9HLoKXRxkHVjhOCBOwiBdEq.jpg": "pixel_peppermint",
    "240_F_2165878473_6QB8pd8J2alaKAp6WBnhQeIXMDlUYesH.jpg": "pixel_chocolate",
}


def is_background_pixel(r: int, g: int, b: int, tolerance: int = 28) -> bool:
    # Treat near-white and very light gray JPEG fringe as background.
    return r >= 255 - tolerance and g >= 255 - tolerance and b >= 255 - tolerance


def remove_background(image: Image.Image, tolerance: int = 28) -> Image.Image:
    rgba = image.convert("RGBA")
    width, height = rgba.size
    pixels = rgba.load()
    visited = [[False] * width for _ in range(height)]
    queue: deque[tuple[int, int]] = deque()

    for x in range(width):
        queue.append((x, 0))
        queue.append((x, height - 1))
    for y in range(height):
        queue.append((0, y))
        queue.append((width - 1, y))

    while queue:
        x, y = queue.popleft()
        if x < 0 or y < 0 or x >= width or y >= height or visited[y][x]:
            continue
        visited[y][x] = True
        r, g, b, _ = pixels[x, y]
        if not is_background_pixel(r, g, b, tolerance):
            continue
        pixels[x, y] = (r, g, b, 0)
        queue.append((x + 1, y))
        queue.append((x - 1, y))
        queue.append((x, y + 1))
        queue.append((x, y - 1))

    return rgba


def main() -> None:
    OUT_DIR.mkdir(parents=True, exist_ok=True)
    for filename, out_name in OUTPUT_NAMES.items():
        src = SRC_DIR / filename
        if not src.exists():
            raise FileNotFoundError(src)
        img = Image.open(src)
        processed = remove_background(img)
        out_path = OUT_DIR / f"{out_name}.png"
        processed.save(out_path, optimize=True)
        print(f"Wrote {out_path.name} ({processed.size[0]}x{processed.size[1]})")


if __name__ == "__main__":
    main()
