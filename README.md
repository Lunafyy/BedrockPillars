# This project is not finished
This project is still in active development and does not currently have a playable version.

Suggestions for gameplay ideas are encouraged via pull requests

-Lunafy

## AI Review

### Findings
- **High – Countdown crash when a queued player disconnects during the start sequence.** [src/main/java/me/lunafy/skyfall/game/GameManager.java](src/main/java/me/lunafy/skyfall/game/GameManager.java#L86-L90) retrieves a Bukkit player and immediately applies blindness. If someone leaves during the lobby countdown, the reference becomes null, the runnable throws, and the match never starts. Guard against null, drop the player from the queue, and continue the countdown.
- **High – Match cleanup fails when the surviving player disconnects or nobody is left alive.** [src/main/java/me/lunafy/skyfall/game/GameManager.java](src/main/java/me/lunafy/skyfall/game/GameManager.java#L118-L134) expects the winner to stay online and only schedules cleanup when a winner exists. An offline winner causes a null pointer, and a null winner skips cleanup entirely, leaving spectators, loot tasks, and arena state running. Always call cleanup (and handle a missing winner title) before returning to IDLE.
- **High – Logout eliminations never finish the round.** [src/main/java/me/lunafy/skyfall/events/PlayerDisconnect.java](src/main/java/me/lunafy/skyfall/events/PlayerDisconnect.java#L31-L41) marks the player dead but never rechecks win conditions, so the game keeps running if the last opponent logs out and item drops continue forever. Invoke checkWinConditions() after setting the player inactive.
- **Medium – Game state flips to IDLE before cleanup completes.** [src/main/java/me/lunafy/skyfall/game/GameManager.java](src/main/java/me/lunafy/skyfall/game/GameManager.java#L132-L134) resets the state immediately even though cleanup runs five seconds later. New lobbies can start while the previous match still owns the arena and player map, causing stale spectators and incorrect counts. Move the state transition into cleanup() once all teardown is done.
- **Medium – Pillar cache grows across regenerations.** [src/main/java/me/lunafy/skyfall/arena/ArenaManager.java](src/main/java/me/lunafy/skyfall/arena/ArenaManager.java#L75-L80) keeps appending to pillars without clearing it first. If cleanup is skipped or the generate command is spammed, players reuse outdated spawn points. Clear the list before filling it for the next arena.

### Suggestions
- Remove lobby leavers from the queue (e.g., handle PlayerQuit during LOBBY_COUNTDOWN) so PlayerManager reflects the actual roster and generateArena sizes pillars correctly.
- Call gameManager.cleanup() inside [src/main/java/me/lunafy/skyfall/Skyfall.java](src/main/java/me/lunafy/skyfall/Skyfall.java#L33-L41) during plugin disable to stop schedulers and unload the arena consistently.
- Trim unused imports such as org.jetbrains.annotations.Contract in [src/main/java/me/lunafy/skyfall/events/PlayerDisconnect.java](src/main/java/me/lunafy/skyfall/events/PlayerDisconnect.java#L13) and org.bukkit.util.Vector in [src/main/java/me/lunafy/skyfall/arena/ArenaManager.java](src/main/java/me/lunafy/skyfall/arena/ArenaManager.java#L6) to keep the build clean.
- Consider surfacing the start signal by using TitleHelpers.goTitle() in [src/main/java/me/lunafy/skyfall/game/GameManager.java](src/main/java/me/lunafy/skyfall/game/GameManager.java#L102-L109) so players see an explicit GO banner when the match begins.

### Architecture
- Consolidate countdown logic into a dedicated helper instead of inlining it inside [src/main/java/me/lunafy/skyfall/game/GameManager.java](src/main/java/me/lunafy/skyfall/game/GameManager.java#L60-L120); keeping GameManager focused on orchestration simplifies long-term maintenance and testing.
- Model match phases as explicit objects or a state map rather than raw enum checks spread across listeners such as [src/main/java/me/lunafy/skyfall/events/PlayerMove.java](src/main/java/me/lunafy/skyfall/events/PlayerMove.java#L20-L32) and [src/main/java/me/lunafy/skyfall/events/PlayerDamageEvent.java](src/main/java/me/lunafy/skyfall/events/PlayerDamageEvent.java#L23-L44); centralised transitions reduce guard code and race conditions.
- Expose immutable views from [src/main/java/me/lunafy/skyfall/player/PlayerManager.java](src/main/java/me/lunafy/skyfall/player/PlayerManager.java#L27-L70) to prevent external code from holding stale collections; returning Optional for lookups also communicates that results might be absent.
- Wire commands directly with the managers they use instead of reaching through the plugin singleton each time (e.g., pass GameManager into [src/main/java/me/lunafy/skyfall/commands/StartGame.java](src/main/java/me/lunafy/skyfall/commands/StartGame.java#L14-L43)); this clarifies dependencies and makes command handlers easier to unit test.
- Separate arena lifecycle (load/unload) from spawn layout bookkeeping in [src/main/java/me/lunafy/skyfall/arena/ArenaManager.java](src/main/java/me/lunafy/skyfall/arena/ArenaManager.java#L21-L103); a future proof structure would let you swap arena types or run multiple arenas concurrently with minimal changes.
