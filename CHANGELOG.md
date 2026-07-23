# Changelog

## Release 1.0

The first stable release. Grape Expectations now follows a whole fermenting session — including
wine left in your bank — with a redesigned, self-updating overlay.

### The overlay
- **Counts:** grapes, jugs of water, and fermenting wine, each with its icon. Wine left
  fermenting in your **bank** is counted too, so a banked batch isn't forgotten.
- **Level progress:** a projected level bar labelled with the percent into the level and,
  optionally, how many more wines you need to reach the next one — counting down live as you
  make wine, not only once the batch ferments.
- **Estimated XP:** the Cooking XP the batch will bank, shown as an *expected* value that
  accounts for the chance of bad wine below level 68. Once fermenting can no longer fail, it
  shows the exact figure instead.
- **Ferment timer:** a smooth countdown that shades from green through yellow to red as the
  batch finishes, landing right as the wine ferments.

### Changes since 0.1
- **Bank fermenting** now counts toward every figure.
- **Redesigned overlay:** reordered rows in a fixed-width box that no longer jumps between
  sizes, a colour-gradient timer, and compact/grouped number formatting.
- Depositing or withdrawing wine mid-ferment no longer disturbs the countdown.
- New **"Show wines to next level"** setting.
- Simplified to a single, honest expected-XP figure — the banked-XP mode and the standalone
  GE cost estimate were removed.

### Settings
- Toggle each of the four rows and the wines-to-next-level figure.
- Recolour the estimated-XP text and the level bar.

## Release 0.1

The first release — a compact on-screen overlay that tracks wine fermenting at a glance.

### Quick overview
- See your **grapes, jugs of water, and fermenting wine** counts without opening your inventory.
- Know the **Cooking XP banked** in the current batch before it ferments.
- Watch a **projected level bar** roll toward your next level as the batch lands.
- Follow a **countdown timer** to fermentation that restarts each time you make a fresh wine.

### The overlay
- **Row 1 — counts:** grape, jug of water, and fermenting-wine icons with live quantities.
- **Row 2 — banked XP:** the Cooking XP the current batch will grant. By default this is an
  *expected* value that accounts for the chance of bad wine below level 68; you can switch it
  to an optimistic (no-fail) total in the settings.
- **Row 3 — level progress:** a bar from your projected level to the next, filling as the banked
  XP is added, and rolling forward automatically when it would take you past a level.
- **Row 4 — ferment timer:** a decreasing countdown of seconds until the batch ferments.

The overlay hides itself when you aren't holding any wine ingredients, and you can drag it
anywhere on screen.

### Settings
- Toggle each of the four rows on or off.
- Choose the banked-XP mode: **Expected** (fail-adjusted) or **Optimistic** (no-fail).
- Recolour the banked-XP text and the level and timer bars.
