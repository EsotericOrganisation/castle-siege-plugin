package net.slqmy.castle_siege_plugin.game;

import com.destroystokyo.paper.ParticleBuilder;
import net.slqmy.castle_siege_plugin.CastleSiegePlugin;
import net.slqmy.castle_siege_plugin.game.data.arena.ArenaConfig;
import net.slqmy.castle_siege_plugin.game.data.player.TeamPlayer;
import net.slqmy.castle_siege_plugin.game.data.team.Team;
import net.slqmy.castle_siege_plugin.game.data.team.TeamBaseData;
import net.slqmy.castle_siege_plugin.util.NMSUtil;
import net.slqmy.castle_siege_plugin.util.Util;
import org.bukkit.*;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public final class GameEventHandler implements Listener {
    private final CastleSiegePlugin plugin;

    private final Game game;
    private final ArenaConfig arenaConfig;

    private final World gameWorld;

    public GameEventHandler(Game game) {
        this.plugin = CastleSiegePlugin.getInstance();

        this.game = game;
        this.arenaConfig = game.getArenaConfig();

        this.gameWorld = game.getWorld();
    }

    public void registerEvents() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void unregisterEvents() {
        HandlerList.unregisterAll(this);
    }

    public void onAttackStart(Team attackingTeam, Team defendingTeam) {
        TeamBaseData.AttackingCamp attackingTeamCamp = attackingTeam.getBaseData().attackingCamp();
        TeamBaseData.AttackingCamp defendingTeamCamp = defendingTeam.getBaseData().attackingCamp();

        Location attackingBaseTeleporter = attackingTeamCamp.teleporterLocation();
        attackingBaseTeleporter.setWorld(gameWorld);

        List<Player> nearbyPlayers = (List<Player>) attackingBaseTeleporter
            .getNearbyPlayers(attackingTeamCamp.teleporterRadius());

        for (Player player : nearbyPlayers) {
            TeamPlayer teamPlayer = TeamPlayer.getFrom(player);

            if (teamPlayer != null && teamPlayer.getTeam().getId().equals(attackingTeam.getId())) {
                teamPlayer.teleportToCamp(defendingTeamCamp);

                teamPlayer.getTeam().getAttackingPlayers().add(teamPlayer);
            }
        }

        game.getPlayers().forEach(player -> Util.playSound(player, Sound.ENTITY_ENDER_DRAGON_GROWL));
        game.getTeamPlayers().forEach(teamPlayer -> teamPlayer.sendAttackMessages(attackingTeam));
    }

    public void onAttackEnd(Team attackingTeam, Team defendingTeam) {
        TeamBaseData.AttackingCamp attackingTeamCamp = attackingTeam.getBaseData().attackingCamp();
        TeamBaseData.AttackingCamp defendingTeamCamp = defendingTeam.getBaseData().attackingCamp();

        Location defendingBaseTeleporter = defendingTeamCamp.teleporterLocation();
        defendingBaseTeleporter.setWorld(gameWorld);

        List<Player> nearbyPlayers = (List<Player>) defendingBaseTeleporter
            .getNearbyPlayers(defendingTeamCamp.teleporterRadius());

        for (Player player : nearbyPlayers) {
            TeamPlayer teamPlayer = TeamPlayer.getFrom(player);

            if (teamPlayer != null && teamPlayer.getTeam().getId().equals(attackingTeam.getId())) {
                teamPlayer.teleportToCamp(attackingTeamCamp);

                teamPlayer.getTeam().getAttackingPlayers().remove(teamPlayer);
            }
        }

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            DamageSource damageSource = DamageSource
                .builder(DamageType.MAGIC)
                .withDirectEntity(defendingTeam.getKing().getPlayer())
                .build();

            for (TeamPlayer teamPlayer : attackingTeam.getAttackingPlayers()) {
                Player player = teamPlayer.getPlayer();

                player.damage(1000.0, damageSource);
                gameWorld.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 1.0F, 1.0F);

                double y = gameWorld.getMaxHeight();
                Location location = player.getLocation();

                while (y > player.getY()) {
                    location.setY(y--);
                    gameWorld.spawnParticle(new ParticleBuilder(Particle.DUST).color(Color.RED).particle(), location, 1);
                }
            }
        }, 20L * 5);

        game.getPlayers().forEach(player -> Util.playSound(player, Sound.ENTITY_LIGHTNING_BOLT_THUNDER));
        game.getTeamPlayers().forEach(teamPlayer -> teamPlayer.sendAttackEndMessages(attackingTeam));
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!isValidEvent(event)) return;

        TeamPlayer teamPlayer = TeamPlayer.getFrom(event.getEntity());
        TeamPlayer killerTeamPlayer = TeamPlayer.getFrom(event.getEntity().getKiller());

        if (teamPlayer == null) return;
        if (killerTeamPlayer == null) return;

        Player player = teamPlayer.getPlayer();
        Player killer = killerTeamPlayer.getPlayer();

        teamPlayer.setDeaths(teamPlayer.getDeaths() + 1);
        killerTeamPlayer.setKills(killerTeamPlayer.getKills() + 1);

        gameWorld.dropItem(player.getLocation(), new ItemStack(Material.GOLD_INGOT, arenaConfig.goldDropOnKill()));
        Util.playSound(killer, Sound.ENTITY_SKELETON_DEATH);

        player.spigot().respawn();
        teamPlayer.spawn();

        teamPlayer.getNameTag().startRiding(NMSUtil.toNMSPlayer(player), true);
        teamPlayer.getHealthDisplay().startRiding(NMSUtil.toNMSPlayer(player), true);
    }

    public void onPlayerJoin(PlayerJoinEvent event) {

    }

    public void onPlayerLeave(PlayerQuitEvent event) {

    }

    public void onPlayerJoinWorld(PlayerChangedWorldEvent event) {

    }

    public void onPlayerLeaveWorld(PlayerChangedWorldEvent event) {

    }

    private boolean isValidEvent(PlayerDeathEvent event) {
        return event.getEntity().getWorld().equals(gameWorld);
    }

    private boolean isValidEvent(PlayerEvent event) {
        return event.getPlayer().getWorld().equals(gameWorld);
    }
}
