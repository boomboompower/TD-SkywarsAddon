package me.boomboompower.textdisplayer.skywarsaddon;

import com.google.gson.JsonObject;

import net.hypixel.api.HypixelAPI;
import net.hypixel.api.reply.PlayerReply;
import net.hypixel.api.request.Request;
import net.hypixel.api.request.RequestBuilder;
import net.hypixel.api.request.RequestParam;
import net.hypixel.api.request.RequestType;
import net.hypixel.api.util.Callback;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import org.apache.commons.lang3.text.WordUtils;

import java.util.UUID;

@Mod(modid = SkywarsAddon.MOD_ID, name = SkywarsAddon.MOD_NAME, version = SkywarsAddon.VERSION)
public class SkywarsAddon {

    public static final String MOD_ID = "td_skywars";
    public static final String MOD_NAME = "TD-SkywarsAddon";
    public static final String VERSION = "1.0-SNAPSHOT";

    private int currentTick = 0;
    private boolean enabled = true;
    private boolean isInWorld = false;

    /* General numbers */
    private Object blocks_placed;
    private Object blocks_broken;

    /* Projectiles */
    private Object enderpearls_thrown;
    private Object eggs_thrown;
    private Object arrows_shot;
    private Object arrows_hit;

    /* Currency */
    private Object souls;
    private Object coins;

    /* Stat numbers */
    private Object ranked_score;
    private Object win_streak;
    private Object assists;
    private Object deaths;
    private Object losses;
    private Object quits;
    private Object kills;
    private Object wins;

    /* Kits */
    private Object ranked_kit;
    private Object teams_kit;
    private Object mega_kit;
    private Object solo_kit;

    /* Other */
    private Object last_mode;
    private Object cage;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            try {
                Class.forName("me.boomboompower.textdisplayer.loading.Placeholder");
            } catch (Exception ex) {
                enabled = false;
                return;
            }

            blocks_placed = new me.boomboompower.textdisplayer.loading.Placeholder("sw_blocks_placed", 0);
            blocks_broken = new me.boomboompower.textdisplayer.loading.Placeholder("sw_blocks_broken", 0);

            enderpearls_thrown = new me.boomboompower.textdisplayer.loading.Placeholder("sw_enderpearls_thrown", 0);
            eggs_thrown = new me.boomboompower.textdisplayer.loading.Placeholder("sw_eggs_thrown", 0);
            arrows_shot = new me.boomboompower.textdisplayer.loading.Placeholder("sw_arrows_shot", 0);
            arrows_hit = new me.boomboompower.textdisplayer.loading.Placeholder("sw_arrows_hit", 0);

            souls = new me.boomboompower.textdisplayer.loading.Placeholder("sw_souls", 0);
            coins = new me.boomboompower.textdisplayer.loading.Placeholder("sw_coins", 0);

            ranked_score = new me.boomboompower.textdisplayer.loading.Placeholder("sw_ranked_score", 0);
            win_streak = new me.boomboompower.textdisplayer.loading.Placeholder("sw_streak", 0);
            assists = new me.boomboompower.textdisplayer.loading.Placeholder("sw_assists", 0);
            deaths = new me.boomboompower.textdisplayer.loading.Placeholder("sw_deaths", 0);
            losses = new me.boomboompower.textdisplayer.loading.Placeholder("sw_losses", 0);
            quits = new me.boomboompower.textdisplayer.loading.Placeholder("sw_quits", 0);
            kills = new me.boomboompower.textdisplayer.loading.Placeholder("sw_kills", 0);
            wins = new me.boomboompower.textdisplayer.loading.Placeholder("sw_wins", 0);

            ranked_kit = new me.boomboompower.textdisplayer.loading.Placeholder("sw_ranked_kit", "Default");
            teams_kit = new me.boomboompower.textdisplayer.loading.Placeholder("sw_teams_kit", "Default");
            mega_kit = new me.boomboompower.textdisplayer.loading.Placeholder("sw_mega_kit", "Default");
            solo_kit = new me.boomboompower.textdisplayer.loading.Placeholder("sw_solo_kit", "Default");

            last_mode = new me.boomboompower.textdisplayer.loading.Placeholder("sw_last_mode", "Solo");
            cage = new me.boomboompower.textdisplayer.loading.Placeholder("sw_cage", "Default");
        });
        HypixelAPI.getInstance().setApiKey(UUID.fromString(""));
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (!isInWorld || !enabled) return;

        if (currentTick > 1200) {
            currentTick = 0;
            update();
        } else {
            currentTick++;
            //if (currentTick % 10 == 0) System.out.println(String.format("i @ %s", currentTick));
        }
    }

    @SubscribeEvent
    public void onJoin(PlayerEvent.PlayerLoggedInEvent event) {
        isInWorld = true;

        update();
    }

    @SubscribeEvent
    public void onQuit(PlayerEvent.PlayerLoggedOutEvent event) {
        isInWorld = false;
    }

    private void update() {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            Request request = RequestBuilder.newBuilder(RequestType.PLAYER).addParam(RequestParam.PLAYER_BY_UUID, Minecraft.getMinecraft().getSession().getProfile().getId()).createRequest();

            System.out.println(request.getURL(HypixelAPI.getInstance()));
            System.out.println(request.getParams());

            HypixelAPI.getInstance().getAsync(request, (Callback<PlayerReply>) (failcause, result) -> {
                try {
                    update(result.getPlayer().
                            getAsJsonObject("stats").
                            getAsJsonObject("SkyWars")
                    );
                    System.out.println("Updated!");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.out.print("Failed to update");
                }
            });
        });
    }

    private void update(JsonObject array) {
        ((me.boomboompower.textdisplayer.loading.Placeholder) ranked_score).setReplacement(0); //ranked_score.setReplacement(optInt(array, ""));
        ((me.boomboompower.textdisplayer.loading.Placeholder) win_streak).setReplacement(Utils.optInt(array, "win_streak"));
        ((me.boomboompower.textdisplayer.loading.Placeholder) assists).setReplacement(Utils.optInt(array, "deaths"));
        ((me.boomboompower.textdisplayer.loading.Placeholder) deaths).setReplacement(Utils.optInt(array, "deaths"));
        ((me.boomboompower.textdisplayer.loading.Placeholder) souls).setReplacement(Utils.optInt(array, "souls"));
        ((me.boomboompower.textdisplayer.loading.Placeholder) coins).setReplacement(Utils.optInt(array, "coins"));
        ((me.boomboompower.textdisplayer.loading.Placeholder) quits).setReplacement(Utils.optInt(array, "quits"));
        ((me.boomboompower.textdisplayer.loading.Placeholder) kills).setReplacement(Utils.optInt(array, "kills"));
        ((me.boomboompower.textdisplayer.loading.Placeholder) wins).setReplacement(Utils.optInt(array, "wins"));

        ((me.boomboompower.textdisplayer.loading.Placeholder) enderpearls_thrown).setReplacement(Utils.optInt(array, "enderpearls_thrown"));
        ((me.boomboompower.textdisplayer.loading.Placeholder) eggs_thrown).setReplacement(Utils.optInt(array, "egg_thrown"));
        ((me.boomboompower.textdisplayer.loading.Placeholder) arrows_shot).setReplacement(Utils.optInt(array, "arrows_shot"));
        ((me.boomboompower.textdisplayer.loading.Placeholder) arrows_hit).setReplacement(Utils.optInt(array, "arrows_hit"));

        ((me.boomboompower.textdisplayer.loading.Placeholder) blocks_placed).setReplacement(Utils.optInt(array, "blocks_placed"));
        ((me.boomboompower.textdisplayer.loading.Placeholder) blocks_broken).setReplacement(Utils.optInt(array, "blocks_broken"));

        ((me.boomboompower.textdisplayer.loading.Placeholder) last_mode).setReplacement(WordUtils.capitalizeFully(Utils.optString(array, "lastMode", "Solo")));
        ((me.boomboompower.textdisplayer.loading.Placeholder) cage).setReplacement(WordUtils.capitalizeFully(Utils.remove(Utils.optString(array, "activeCage", "Normal"), "cage_")));

        ((me.boomboompower.textdisplayer.loading.Placeholder) ranked_kit).setReplacement(WordUtils.capitalizeFully(Utils.remove(Utils.optString(array, "activeKit_RANKED", "Default"), "kit_ranked_ranked_")));
        ((me.boomboompower.textdisplayer.loading.Placeholder) teams_kit).setReplacement(WordUtils.capitalizeFully(Utils.remove(Utils.optString(array, "activeKit_TEAM", "Default"), "kit_attacking_team_")));
        ((me.boomboompower.textdisplayer.loading.Placeholder) mega_kit).setReplacement(WordUtils.capitalizeFully(Utils.remove(Utils.optString(array, "activeKit_MEGA", "Default"), "kit_mega_mega_")));
        ((me.boomboompower.textdisplayer.loading.Placeholder) solo_kit).setReplacement(WordUtils.capitalizeFully(Utils.remove(Utils.optString(array, "activeKit_SOLO", "Default"), "kit_advanced_solo_")));
    }
}
