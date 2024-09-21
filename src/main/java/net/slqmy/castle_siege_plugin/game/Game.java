package net.slqmy.castle_siege_plugin.game;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.slqmy.castle_siege_plugin.CastleSiegePlugin;
import net.slqmy.castle_siege_plugin.game.classes.*;
import net.slqmy.castle_siege_plugin.game.classes.abs.GameClass;
import net.slqmy.castle_siege_plugin.game.data.arena.ArenaConfig;
import net.slqmy.castle_siege_plugin.game.data.player.TeamPlayer;
import net.slqmy.castle_siege_plugin.game.data.team.Team;
import net.slqmy.castle_siege_plugin.game.data.team.TeamBaseData;
import net.slqmy.castle_siege_plugin.game.map.MapData;
import net.slqmy.castle_siege_plugin.game.map.MapLoader;
import net.slqmy.castle_siege_plugin.game.phase.GamePhase;
import net.slqmy.castle_siege_plugin.game.phase.GamePhaseManager;
import net.slqmy.castle_siege_plugin.items.CustomItemManager;
import net.slqmy.castle_siege_plugin.items.bows.LongBow;
import net.slqmy.castle_siege_plugin.items.bows.RecurveBow;
import net.slqmy.castle_siege_plugin.mobs.MobManager;
import net.slqmy.castle_siege_plugin.mobs.natural.valley.skeleton.ValleySkeleton;
import net.slqmy.castle_siege_plugin.util.Util;
import net.slqmy.castle_siege_plugin.util.classes.Pair;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

@Getter
public final class Game {
    private static final Random random = new Random();
    private static final MiniMessage MM = MiniMessage.miniMessage();

    @Getter(value = AccessLevel.NONE)
    private final CastleSiegePlugin plugin;
    private final ArenaConfig arenaConfig;

    private final List<? extends Player> players;
    private final Pair<Team> teams;

    private final World world;
    private final MapData mapData;

    private final GameData data;

    private final CustomItemManager itemManager;
    private final MobManager mobManager;

    private final GamePhaseManager gamePhase;
    private final GameEventHandler eventHandler;

    private BukkitTask tickManager;
    private BukkitTask asyncTickManager;

    private boolean loaded;
    private Consumer<? super World> onLoad;

    @Getter
    private static final List<Player> allPlayers = new ArrayList<>();

    public Game(ArenaConfig arenaConfig, List<? extends Player> players) {
        this.plugin = CastleSiegePlugin.getInstance();
        this.arenaConfig = arenaConfig;

        this.players = players;
        this.teams = new Pair<>();

        this.world = Bukkit.getWorld(arenaConfig.worldName());
        this.mapData = loadMap();

        this.data = new GameData();

        this.itemManager = initItemManager();
        this.mobManager = initMobManager();

        this.gamePhase = new GamePhaseManager(this);
        this.eventHandler = new GameEventHandler(this);

        this.loaded = false;

        allPlayers.addAll(this.players);
    }

    public List<TeamPlayer> getTeamPlayers() {
        return players.stream().map(TeamPlayer::getFrom).toList();
    }

    public void markAsLoaded() {
        this.loaded = true;

        if (onLoad != null)
            onLoad.accept(world);
    }

    public void startGame(@Nullable Consumer<? super World> onLoad) {
        gamePhase.next();

        createTeams();
        createScoreboardTeams();

        assignPlayersToTeams();
        assignClasses();

        eventHandler.registerEvents();
        startTickManager();

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            spawnPlayers();
            preparePlayers();

            sendGuideMessages();
            sendClassMessages();
        }, 1L);

        this.onLoad = onLoad;
    }

    public void endGame(@Nullable Consumer<? super World> onComplete) {
        if (!loaded)
            throw new IllegalStateException("Cannot end a game whose map is currently loading!");

        eventHandler.unregisterEvents();

        tickManager.cancel();
        asyncTickManager.cancel();

        plugin.getGameManager().getGames().remove(world.getUID());
        teams.asList().forEach(Team::unregister);

        mapData.getNPCsById().forEach((id, npc) -> npc.unregister());
        mapData.getWindowBlockDisplays().forEach((id, displayList) -> displayList.forEach(Entity::remove));

        itemManager.unregisterAll();
        mobManager.getTrackedMobs().forEach(Entity::remove);

        allPlayers.removeAll(players);

        if (onComplete != null) onComplete.accept(world);
    }

    private MobManager initMobManager() {
        MobManager mobManager = new MobManager(this);
        mobManager.registerMob(new ValleySkeleton(this));
        mobManager.cacheData();

        return mobManager;
    }

    private CustomItemManager initItemManager() {
        CustomItemManager itemManager = new CustomItemManager();
        itemManager.registerCustomItem(new RecurveBow());
        itemManager.registerCustomItem(new LongBow());

        return itemManager;
    }

    private MapData loadMap() {
        return new MapLoader(this).loadConfig().loadMap();
    }

    private void createTeams() {
        List<TeamBaseData> teamBases = arenaConfig.bases();

        teams.setLeft(new Team(teamBases.get(0), this));
        teams.setRight(new Team(teamBases.get(1), this));
    }

    private void assignPlayersToTeams() {
        boolean left = random.nextBoolean();

        for (Player player : players) {
            Team team = left ? teams.getLeft() : teams.getRight();
            team.addPlayer(player);

            left = !left;
        }
    }

    private void assignClasses() {
        List<GameClass> classes = List.of(
            new King(this),
            new Berserk(this),
            new Archer(this),
            new Mage(this),
            new Knight(this)
        );

        for (Team team : teams.asList()) {
            List<TeamPlayer> players = team.getTeamPlayers();
            int teamPlayerCount = players.size();

            int index = 0;
            for (GameClass gameClass : classes) {
                int iterations = gameClass.getPlayerCount(teamPlayerCount);

                for (int i = 0; i < iterations; i++) {
                    if (index >= teamPlayerCount) break;

                    players.get(index).setGameClass(gameClass.newInstance());
                    index++;
                }
            }

            players.subList(index, teamPlayerCount)
                .forEach(player -> player.setGameClass(new Berserk(this)));
        }
    }

    private void createScoreboardTeams() {
        for (Team team : teams.asList()) {
            org.bukkit.scoreboard.Team scoreboardTeam = Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam(team.getName());

            scoreboardTeam.color(team.getBaseData().color());
            scoreboardTeam.prefix(Util.wrapInSquareBrackets(team.getName(), "dark_gray").append(Component.space()));
            scoreboardTeam.setOption(org.bukkit.scoreboard.Team.Option.NAME_TAG_VISIBILITY, org.bukkit.scoreboard.Team.OptionStatus.NEVER);

            team.setScoreboardTeam(scoreboardTeam);
        }
    }

    private void spawnPlayers() {
        teams.asList().forEach(Team::spawnPlayers);
    }

    private void preparePlayers() {
        teams.asList().forEach(Team::equipPlayers);
        teams.asList().forEach(Team::createNameAndHealthTags);
        teams.asList().forEach(Team::showScoreboardsAndRelatedInfo);
    }

    private void sendGuideMessages () {
        String text =
            "<yellow><st><b>" + "-".repeat(30) + "</b></st>" +
            "\n<b><gold>Welcome to Castle Siege!</b>" +
            "\n<gray>Collect resources by mining or hunting mobs!" +
            "\n<gray>Trade for better gear and artillery at the shop!" +
            "\n<gray>Kill the enemy king to win the game!" +
            "\n<yellow><st><b>" + "-".repeat(30);

        Component component = MM.deserialize(text);
        players.forEach(player -> player.sendMessage(component));
    }

    private void sendClassMessages() {
        teams.asList().forEach(Team::sendClassMessages);
    }

    private void startTickManager() {
        this.tickManager = new TickManager().runTaskTimer(plugin, 0L, 1L);
        this.asyncTickManager = new AsyncTickManager().runTaskTimerAsynchronously(plugin, 0L, 1L);
    }

    private class TickManager extends BukkitRunnable {
        private GamePhase lastPhase;
        private Team lastAttackingTeam;

        public TickManager() {
            this.lastPhase = gamePhase.getPhase();
            this.lastAttackingTeam = teams.getRandom();

            data.setCurrentOrNextAttackingTeam(teams.getOther(lastAttackingTeam));
            data.setTotalAttacks(0);
        }

        @Override
        public void run() {
            gamePhase.tick();
            tickNPCs();

            switch (gamePhase.getPhase()) {
                case DEVELOPMENT -> developmentPhaseTick();
                case ATTACKING -> attackingPhaseTick();
                case REST -> restPhaseTick();
                case DEATHMATCH -> deathmatchPhaseTick();
                case END ->
                    endGame((world) -> plugin.getLogger().info(
                        "[Game Completion] Castle Siege game ended in world: " + world.getName()));
            }

            this.lastPhase = gamePhase.getPhase();
        }

        private void developmentPhaseTick() {
            mobManager.tick();
        }

        private void attackingPhaseTick() {
            if (isFirstTick()) {
                eventHandler.onAttackStart(teams.getOther(lastAttackingTeam), lastAttackingTeam);
                this.lastAttackingTeam = teams.getOther(lastAttackingTeam);

                data.setCurrentOrNextAttackingTeam(lastAttackingTeam);
            }
        }

        private void restPhaseTick() {
            if (isFirstTick()) {
                eventHandler.onAttackEnd(teams.getOther(lastAttackingTeam), lastAttackingTeam);

                data.setTotalAttacks(data.getTotalAttacks() + 1);
                data.setCurrentOrNextAttackingTeam(teams.getOther(lastAttackingTeam));
            }

            mobManager.tick();
        }

        private void deathmatchPhaseTick() {
            // Deathmatch phase logic
        }

        private void tickNPCs() {
            mapData.getNPCsById().forEach((id, npc) -> npc.tick());
        }

        private boolean isFirstTick() {
            return lastPhase != gamePhase.getPhase();
        }
    }

    private class AsyncTickManager extends BukkitRunnable {
        @Override
        public void run() {
            teams.asList().forEach(Team::tickPlayersAsync);
        }
    }

    @Getter @Setter
    public static class GameData {
        private Team currentOrNextAttackingTeam;
        private int totalAttacks;
    }
}
