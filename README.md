# Grape Expectations

A RuneLite plugin that tracks wine fermenting at a glance. It draws a compact
four-row overlay while you make wine:

1. **Inventory counts** — grapes, jugs of water, and fermenting (unfermented) wine.
2. **Banked XP** — the Cooking XP that will be realized once the current batch ferments.
3. **Level progress** — a projected level bar showing where that banked XP lands you,
   rolling to the next level bracket when the XP would take you past a threshold.
4. **Ferment timer** — a decreasing countdown of seconds until the batch ferments,
   reset each time you start a fresh wine.

> **Status:** early scaffold (`Release 0.1` in progress). The overlay, XP/level
> projection, fail-chance model, and fermentation timer are tracked as milestone issues.

## Building

```bash
./gradlew build     # compile, tests, style check, javadoc lint
./gradlew run       # launch a dev RuneLite client with the plugin loaded
```

Requires JDK 11.

## Scope

Standard wine only: **Grapes + Jug of water → Unfermented wine → Jug of wine**
(200 Cooking XP each). Banked XP is an *expected* value that accounts for the
level-based failure chance below level 68.

## License

BSD 2-Clause — see [LICENSE](LICENSE).
