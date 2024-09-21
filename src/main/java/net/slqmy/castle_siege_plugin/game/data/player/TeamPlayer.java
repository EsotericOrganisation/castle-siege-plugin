package net.slqmy.castle_siege_plugin.game.data.player;

import io.papermc.paper.entity.TeleportFlag;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.slqmy.castle_siege_plugin.api.gui.BaseGUI;
import net.slqmy.castle_siege_plugin.api.scoreboard.FastBoard;
import net.slqmy.castle_siege_plugin.api.scoreboard.ScoreboardField;
import net.slqmy.castle_siege_plugin.game.Game;
import net.slqmy.castle_siege_plugin.game.classes.King;
import net.slqmy.castle_siege_plugin.game.classes.abs.GameClass;
import net.slqmy.castle_siege_plugin.game.data.team.Team;
import net.slqmy.castle_siege_plugin.game.data.team.TeamBaseData;
import net.slqmy.castle_siege_plugin.game.phase.GamePhase;
import net.slqmy.castle_siege_plugin.util.ListUtil;
import net.slqmy.castle_siege_plugin.util.NMSUtil;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Transformation;

import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static net.kyori.adventure.text.Component.empty;

@Getter
public final class TeamPlayer {
    private static final Map<UUID, TeamPlayer> allTeamPlayers = new HashMap<>();

    private static final Random random = new Random();
    private static final MiniMessage MM = MiniMessage.miniMessage();

    private final Game game;
    private final Player player;
    private final Team team;
    private PlayerScoreboard scoreboard;

    private net.minecraft.world.entity.Display.TextDisplay nameTag;
    private net.minecraft.world.entity.Display.TextDisplay healthDisplay;

    @Setter
    private BaseGUI openGui;

    @Setter
    private int kills;
    @Setter
    private int deaths;

    @Setter
    private GameClass gameClass;

    public TeamPlayer(Player player, Team team) {
        this.game = team.getGame();

        this.player = player;
        this.team = team;

        this.kills = 0;
        this.deaths = 0;

        allTeamPlayers.put(player.getUniqueId(), this);
    }

    @Nullable
    public static TeamPlayer getFrom(@Nullable Player player) {
        if (player == null) return null;

        return allTeamPlayers.get(player.getUniqueId());
    }

    public void unregister() {
        scoreboard.getScoreboard().delete();

        nameTag.remove(Entity.RemovalReason.DISCARDED);
        healthDisplay.remove(Entity.RemovalReason.DISCARDED);

        NMSUtil.sendEntityRemovePackets(ListUtil.except(game.getPlayers(), player), nameTag, healthDisplay);
        TeamPlayer.allTeamPlayers.remove(player.getUniqueId());
    }

    public boolean isKing() {
        return gameClass instanceof King;
    }

    public void spawn() {
        List<Location> spawnPoints = team.getBaseData().spawnPoints();
        spawnPoints.forEach(location -> location.setWorld(game.getWorld()));

        player.teleport(spawnPoints.get(random.nextInt(spawnPoints.size())));
        player.setGameMode(GameMode.SURVIVAL);
        player.setHealth(20.0);
        player.setFoodLevel(20);
        player.setSaturation(20.0F);
        player.setExperienceLevelAndProgress(0);
    }

    public void equipPlayer() {
        gameClass.equipPlayer(player);
    }

    public void sendClassInfo() {
        gameClass.sendInfo(player);
    }

    public void createNameTag() {
        this.nameTag = new net.minecraft.world.entity.Display.TextDisplay(
            net.minecraft.world.entity.EntityType.TEXT_DISPLAY, NMSUtil.toNMSWorld(player.getWorld()));

        TextDisplay bukkitNameTag = (TextDisplay) nameTag.getBukkitEntity();
        bukkitNameTag.text(Component.text(player.getName()).color(team.getBaseData().color()));

        modifyTextDisplay(bukkitNameTag, 0.42);
        nameTag.startRiding(NMSUtil.toNMSPlayer(player), true);

        NMSUtil.sendEntityAddAndDataPackets(nameTag, ListUtil.except(game.getPlayers(), player));
    }

    public void createHealthDisplay() {
        this.healthDisplay = new net.minecraft.world.entity.Display.TextDisplay(
            net.minecraft.world.entity.EntityType.TEXT_DISPLAY, NMSUtil.toNMSWorld(player.getWorld()));

        TextDisplay bukkitHealthDisplay = (TextDisplay) healthDisplay.getBukkitEntity();
        bukkitHealthDisplay.text(MM.deserialize((int) Math.round(player.getHealth()) + "<red>\u2764"));

        modifyTextDisplay(bukkitHealthDisplay, 0.15);
        healthDisplay.startRiding(NMSUtil.toNMSPlayer(player), true);

        NMSUtil.sendEntityAddAndDataPackets(healthDisplay, ListUtil.except(game.getPlayers(), player));
    }

    public void joinScoreboardTeam() {
        team.getScoreboardTeam().addPlayer(player);
    }

    public void showScoreboard() {
        FastBoard board = new FastBoard(player);

        board.updateTitle(
            MM.deserialize("<yellow><b>Castle Siege"));

        board.updateLines(
            MM.deserialize("<dark_gray><st><b>" + "-".repeat(21)),
            empty(),
            MM.deserialize(" <dark_red>\uD83D\uDD25 <white>ɴᴇxᴛ ᴇᴠᴇɴᴛ: %"),
            MM.deserialize(" <color:#ffa500>\uD83D\uDD25 <white>ᴇᴠᴇɴᴛ: %"),
            MM.deserialize("     <white>▪ ᴛɪᴍᴇ: %"),
            empty(),
            MM.deserialize(" <color:#783f04>\uD83C\uDFF9 <white>ᴛᴇᴀᴍ: " + team.getName()),
            MM.deserialize(" <gray>\uD83D\uDDE1 <white>ᴋɪʟʟs: %"),
            MM.deserialize(" <red>\u2620 <white>ᴅᴇᴀᴛʜs: %"),
            MM.deserialize(" <dark_green>\u2693 <white>ʟᴏᴄᴀᴛɪᴏɴ: %"),
            empty(),
            MM.deserialize(" <yellow>discord.gg/7EF3VvjS4q"),
            MM.deserialize("<dark_gray><st><b>" + "-".repeat(21)));

        this.scoreboard = new PlayerScoreboard(board);
    }

    public void tickAsyncTasks() {
        updateScoreboard();
        updateHealthDisplay();
    }

    public void sendPreAttackMessages(Team attacker, String time) {
        if (attacker.getId().equals(team.getId())) {
            player.sendMessage(MM.deserialize(String.format(
                "<b><red>The attack will begin in %s. Head over to the attacking camp to prepare!", time)));
        } else {
            player.sendMessage(MM.deserialize(String.format(
                "<b><red>The enemy team will land in the camp in %s. Prepare to defend!", time)));
        }
    }

    public void sendAttackMessages(Team attacker) {
        if (attacker.getId().equals(team.getId())) {
            player.sendMessage(MM.deserialize("<b><dark_red>The attack has begun!"));
        } else {
            player.sendMessage(MM.deserialize("<b><dark_red>The enemy team has landed!"));
        }
    }

    public void sendAttackEndMessages(Team attacker) {
        if (attacker.getId().equals(team.getId())) {
            player.sendMessage(MM.deserialize("<b><dark_red>The attack has ended!"));

            if (team.getAttackingPlayers().contains(this)) {
                player.sendMessage(MM.deserialize("<red>You have failed to escape the enemy base in time! Death is now inevitable."));
                return;
            }
        } else {
            player.sendMessage(MM.deserialize("<b><dark_red>The enemy attack has been repelled!"));
        }

        player.sendMessage(MM.deserialize(String.format(
            "<yellow>%d attackers have not managed to escape.", attacker.getAttackingPlayers().size())));
    }

    public void teleportToCamp(TeamBaseData.AttackingCamp camp) {
        Location location = camp.teleporterLocation();
        location.setWorld(game.getWorld());

        double radius = camp.teleporterRadius();
        Location teleportLocation = location.clone().add(random.nextDouble() * radius, 0, random.nextDouble() * radius);

        player.teleportAsync(
            teleportLocation,
            PlayerTeleportEvent.TeleportCause.PLUGIN,
            TeleportFlag.EntityState.RETAIN_PASSENGERS);
    }

    private void modifyTextDisplay(TextDisplay display, double translationY) {
        display.setShadowed(false);
        display.setSeeThrough(false);
        display.setAlignment(TextDisplay.TextAlignment.CENTER);
        display.setBillboard(Display.Billboard.VERTICAL);
        display.setTeleportDuration(1);

        Transformation transformation = display.getTransformation();
        transformation.getTranslation().set(0.0, translationY, 0.0);
        display.setTransformation(transformation);
    }

    private void updateHealthDisplay() {
        if (healthDisplay == null) return;

        ((TextDisplay) healthDisplay.getBukkitEntity()).text(
            MM.deserialize((int) Math.round(player.getHealth()) + "<red>\u2764"));

        for (Player player : game.getPlayers()) {
            if (player.getUniqueId().equals(this.player.getUniqueId()))
                continue;

            List<SynchedEntityData.DataValue<?>> data = healthDisplay.getEntityData().packAll();
            assert data != null;

            NMSUtil.toNMSPlayer(player).connection
                .send(new ClientboundSetEntityDataPacket(healthDisplay.getId(), data));
        }
    }

    private void updateScoreboard() {
        if (scoreboard == null) return;
        if (scoreboard.getScoreboard().isDeleted()) return;

        LocalDateTime now = LocalDateTime.now();
        String date = now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        String nextPhaseName = getPhaseText(game.getGamePhase().getNextPhase());

        String phaseName = getPhaseText(game.getGamePhase().getPhase());
        String phaseTime = "<dark_aqua>" + game.getGamePhase().getFormattedTime();

        scoreboard
            .replaceField(ScoreboardField.DATE, MM.deserialize("<gray> " + date))
            .updateField(ScoreboardField.NEXT_PHASE, MM.deserialize(nextPhaseName), '%')
            .updateField(ScoreboardField.PHASE, MM.deserialize(phaseName), '%')
            .updateField(ScoreboardField.PHASE_TIME, MM.deserialize(phaseTime), '%')
            .updateField(ScoreboardField.KILLS, MM.deserialize("<aqua>" + kills), '%')
            .updateField(ScoreboardField.DEATHS, MM.deserialize("<aqua>" + deaths), '%')
            .updateField(ScoreboardField.LOCATION, MM.deserialize("<dark_aqua>Home Castle"), '%')
            .sendUpdatePackets();
    }

    private String getPhaseText(GamePhase phase) {
        if (phase == GamePhase.ATTACKING) {
            Team team = game.getData().getCurrentOrNextAttackingTeam();
            return team.getName() + " " + MM.serialize(Component.text(phase.getName()).color(team.getBaseData().color()));
        }

        return phase.getName();
    }
}
