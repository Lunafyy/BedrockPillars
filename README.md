# This project is not finished
This project is still in active development and does not currently have a playable version.

Suggestions for gameplay ideas are encouraged via pull requests

-Lunafy

## AI Review

### Findings
- **Medium – Pillar cache grows across regenerations.** [src/main/java/me/lunafy/skyfall/arena/ArenaManager.java](src/main/java/me/lunafy/skyfall/arena/ArenaManager.java#L75-L80) keeps appending to pillars without clearing it first. If cleanup is skipped or the generate command is spammed, players reuse outdated spawn points. Clear the list before filling it for the next arena.

### Suggestions
- Trim unused imports such as org.jetbrains.annotations.Contract in [src/main/java/me/lunafy/skyfall/events/PlayerDisconnect.java](src/main/java/me/lunafy/skyfall/events/PlayerDisconnect.java#L13) and org.bukkit.util.Vector in [src/main/java/me/lunafy/skyfall/arena/ArenaManager.java](src/main/java/me/lunafy/skyfall/arena/ArenaManager.java#L6) to keep the build clean.

### Architecture
- Consolidate countdown logic into a dedicated helper instead of inlining it inside [src/main/java/me/lunafy/skyfall/game/GameManager.java](src/main/java/me/lunafy/skyfall/game/GameManager.java#L60-L120); keeping GameManager focused on orchestration simplifies long-term maintenance and testing.
- Model match phases as explicit objects or a state map rather than raw enum checks spread across listeners such as [src/main/java/me/lunafy/skyfall/events/PlayerMove.java](src/main/java/me/lunafy/skyfall/events/PlayerMove.java#L20-L32) and [src/main/java/me/lunafy/skyfall/events/PlayerDamageEvent.java](src/main/java/me/lunafy/skyfall/events/PlayerDamageEvent.java#L23-L44); centralised transitions reduce guard code and race conditions.
- Expose immutable views from [src/main/java/me/lunafy/skyfall/player/PlayerManager.java](src/main/java/me/lunafy/skyfall/player/PlayerManager.java#L27-L70) to prevent external code from holding stale collections; returning Optional for lookups also communicates that results might be absent.
- Wire commands directly with the managers they use instead of reaching through the plugin singleton each time (e.g., pass GameManager into [src/main/java/me/lunafy/skyfall/commands/StartGame.java](src/main/java/me/lunafy/skyfall/commands/StartGame.java#L14-L43)); this clarifies dependencies and makes command handlers easier to unit test.
- Separate arena lifecycle (load/unload) from spawn layout bookkeeping in [src/main/java/me/lunafy/skyfall/arena/ArenaManager.java](src/main/java/me/lunafy/skyfall/arena/ArenaManager.java#L21-L103); a future proof structure would let you swap arena types or run multiple arenas concurrently with minimal changes.
