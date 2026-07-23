# Changelog

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
