# Engineering Review & Suggested PR Backlog

This review is based on the current source tree and `README.md`. The goal is to give you a concrete, prioritized list you can execute as one-PR-per-change.

## High-priority bug fixes

### PR 1: Clear stale pillar cache before generation
**Why**: `ArenaManager.generateArena` appends to `pillars` each run and does not clear first. If cleanup isn't reached (crash/reload/manual commands), stale spawn locations can leak into the next match.

**Scope**
- Add `pillars.clear();` at the start of arena generation.
- Add a defensive check that spawn count equals active player count.

**Files**
- `src/main/java/me/lunafy/skyfall/arena/ArenaManager.java`
- `src/main/java/me/lunafy/skyfall/game/GameManager.java`

---

### PR 2: Fix `endMatch` early-return bug when any player is offline
**Why**: In `GameManager.endMatch`, loop currently uses `return` when a player entry is null/offline, aborting end-match flow for everyone.

**Scope**
- Replace `return` with `continue` in the per-player cleanup loop.
- Add null-safe handling for winner name retrieval.

**Files**
- `src/main/java/me/lunafy/skyfall/game/GameManager.java`

---

### PR 3: Remove dead command declaration (`/generate`) from `plugin.yml`
**Why**: `plugin.yml` declares `generate` but there is no command executor registered. This creates a misleading API surface and maintenance confusion.

**Scope**
- Remove command from YAML OR implement/ register it.
- Keep command list minimal and accurate.

**Files**
- `src/main/resources/plugin.yml`
- (optional) `src/main/java/me/lunafy/skyfall/Skyfall.java` if implemented

---

## Stability & correctness improvements

### PR 4: Make shutdown cleanup null-safe
**Why**: `Skyfall.onDisable` calls `gameManager.cleanup()` unconditionally. Defensive null checks are safer during partial startup failures.

**Scope**
- Null guard in `onDisable`.
- Consider try/finally-style best-effort cleanup logging.

**Files**
- `src/main/java/me/lunafy/skyfall/Skyfall.java`

---

### PR 5: Make arena reset fully unload world
**Why**: `resetArena()` only teleports players, clears list, and nulls reference, but does not unload/delete world. This can leave stale world state around across plugin lifecycles.

**Scope**
- Ensure world unload in reset path.
- Keep generate/reset lifecycle symmetric.

**Files**
- `src/main/java/me/lunafy/skyfall/arena/ArenaManager.java`

---

### PR 6: Harden broadcast/countdown message formatting
**Why**: Start countdown line currently has malformed MiniMessage closing tags (`<red>` not closed properly).

**Scope**
- Correct tags and extract message formatting to helper constants.

**Files**
- `src/main/java/me/lunafy/skyfall/game/GameManager.java`

---

## Architecture cleanup (medium priority)

### PR 7: Decouple command classes from plugin singleton reach-through
**Why**: Commands currently depend on `Skyfall` and pull managers via getters. Constructor-injecting manager dependencies improves testability and readability.

**Scope**
- Inject `GameManager`/`PlayerManager` directly into command executors.
- Keep plugin class as composition root.

**Files**
- `src/main/java/me/lunafy/skyfall/Skyfall.java`
- `src/main/java/me/lunafy/skyfall/commands/StartGame.java`
- `src/main/java/me/lunafy/skyfall/commands/Join.java`

---

### PR 8: Make `PlayerManager` API safer to consume
**Why**: Methods expose mutable collections and nullable return values, increasing accidental misuse.

**Scope**
- Return unmodifiable views where appropriate.
- Convert UUID/player lookups to `Optional<SkyfallPlayer>`.

**Files**
- `src/main/java/me/lunafy/skyfall/player/PlayerManager.java`
- call sites in commands/events/game manager

---

### PR 9: Separate match countdown/state machine from orchestration
**Why**: `GameManager` owns lobby countdown, match countdown, and lifecycle cleanup in one class. Splitting state-machine logic simplifies future features and testing.

**Scope**
- Introduce `MatchFlowService` or similar for transitions/timers.
- Keep `GameManager` as facade for high-level game actions.

**Files**
- `src/main/java/me/lunafy/skyfall/game/GameManager.java`
- new classes under `src/main/java/me/lunafy/skyfall/game/`

---

## Build and developer experience

### PR 10: Remove machine-specific shaded jar output path
**Why**: `pom.xml` writes output to a local Windows absolute path (`D:\...`), which breaks portability for other contributors/CI.

**Scope**
- Output shaded jar to `${project.build.directory}` (or default).
- Optionally document deployment copy step separately.

**Files**
- `pom.xml`

---

### PR 11: Remove unused imports and tighten compiler hygiene
**Why**: Unused imports and broad wildcard imports increase noise and hide real warnings.

**Scope**
- Remove unused imports (e.g. `Vector`, `Optional` if not used).
- Prefer explicit imports over broad ones where practical.

**Files**
- `src/main/java/me/lunafy/skyfall/arena/ArenaManager.java`
- `src/main/java/me/lunafy/skyfall/game/GameManager.java`
- `src/main/java/me/lunafy/skyfall/events/PlayerDisconnect.java`

---

## Feature roadmap suggestions (after core bugfixes)

### PR 12: Config-driven game settings
- Move countdown durations, pillar height/radius multipliers, and loot interval into `config.yml`.

### PR 13: Deterministic loot pool
- Replace "any Material item" with weighted curated pools for game quality.

### PR 14: Match stats and post-game summary
- Track eliminations/survival time and display end-screen breakdown.

### PR 15: Rejoin policy
- Decide whether disconnected players can rejoin during lobby/start/in-game and implement clearly.

## Overall opinion

The project has a solid early skeleton (managers, events, command flow) and is very close to becoming a playable MVP, but reliability boundaries need tightening before adding new gameplay systems. The best sequence is: **critical correctness bugs → lifecycle hardening → portability/build cleanup → architecture refactor → feature expansion**.
