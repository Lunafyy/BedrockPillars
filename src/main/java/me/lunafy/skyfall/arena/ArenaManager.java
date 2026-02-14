package me.lunafy.skyfall.arena;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class ArenaManager {
    private World arenaWorld;
    private final List<Location> pillars = new ArrayList<>();

    public void evacuateWorld(World world)
    {
        if(world == null) return;

        World lobby = getDefaultWorld();

        for(Player player : world.getPlayers())
        {
            player.teleport(lobby.getSpawnLocation());
        }
    }

    public World getDefaultWorld()
    {
        return Bukkit.getWorld("world");
    }

    public void generateArena(int playerCount)
    {
        File worldFolder = new File(Bukkit.getWorldContainer(), "SkyfallArena");

        // Delete the existing world
        if(worldFolder.exists())
        {
            World skyfallArena = Bukkit.getWorld("SkyfallArena");

            if(skyfallArena != null)
            {
                evacuateWorld(skyfallArena);
                Bukkit.unloadWorld(skyfallArena, false);
            }

            try
            {
                Files.walk(worldFolder.toPath())
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        WorldCreator wc = new WorldCreator("SkyfallArena");
        wc.environment(World.Environment.NORMAL);
        wc.generator(new VoidWorldGenerator());
        arenaWorld = Bukkit.createWorld(wc);

        if(arenaWorld == null) {
            throw new IllegalStateException("Failed to create the SkyfallArena world!");
        }

        arenaWorld.setAutoSave(false);

        arenaWorld.setGameRule(GameRules.ADVANCE_TIME, false);
        arenaWorld.setGameRule(GameRules.ADVANCE_WEATHER, false);

        arenaWorld.setDifficulty(Difficulty.HARD);

        arenaWorld.setTime(1000);

        double radius = calculateRadius(playerCount);

        for(int i = 0; i < playerCount; i++)
        {
            Location pillarLoc = calculatePillarLocation(i, playerCount, radius);
            generatePillar(pillarLoc);
            pillars.add(pillarLoc); // Get the top block
        }
    }

    private Location calculatePillarLocation(int index, int total, double radius)
    {
        double angle = 2 * Math.PI * index / total;
        double x = radius * Math.cos(angle);
        double z = radius * Math.sin(angle);
        return new Location(arenaWorld, x, 64, z);
    }

    private double calculateRadius(int playerCount)
    {
        return Math.max(5, playerCount * 3) * 2;
    }

    private void generatePillar(Location loc)
    {
        for(int y = 0; y < 10; y++)
        {
            loc.getBlock().getRelative(0, y, 0).setType(Material.BEDROCK);
        }
    }

    public List<Location> getPillars()
    {
        return new ArrayList<>(pillars);
    }

    public World getArenaWorld()
    {
        return arenaWorld;
    }

    public void resetArena()
    {
        evacuateWorld(arenaWorld);
        pillars.clear();
        arenaWorld = null;
    }
}
