package me.lunafy.skyfall.arena;

import org.bukkit.*;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ArenaManager {
    private World arenaWorld;
    private final List<Location> pillars = new ArrayList<>();

    public void generateArena(int playerCount)
    {
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
        return Math.max(5, playerCount * 3);
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
}
