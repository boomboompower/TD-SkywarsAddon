/*
 *     Copyright (C) 2017 boomboompower
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import java.util.UUID;

@Mod(modid = SkywarsAddon.MOD_ID, name = SkywarsAddon.MOD_NAME, version = SkywarsAddon.VERSION)
public class SkywarsAddon {

    public static final String PREFIX = EnumChatFormatting.GOLD + "TD-Skywars" + EnumChatFormatting.AQUA + " > " + EnumChatFormatting.GRAY;
    public static final String MOD_ID = "td_skywars";
    public static final String MOD_NAME = "TD-SkywarsAddon";
    public static final String VERSION = "1.0-SNAPSHOT";

    @Mod.Instance
    public static SkywarsAddon instance;

    protected int currentTick = 0;

    protected boolean keyUsed;
    protected boolean enabled = true;

    /* General numbers */
    protected Object blocks_placed;
    protected Object blocks_broken;

    /* Projectiles */
    protected Object enderpearls_thrown;
    protected Object eggs_thrown;
    protected Object arrows_shot;
    protected Object arrows_hit;

    /* Currency */
    protected Object souls;
    protected Object coins;

    /* Stat numbers */
    protected Object ranked_score;
    protected Object win_streak;
    protected Object assists;
    protected Object deaths;
    protected Object losses;
    protected Object quits;
    protected Object kills;
    protected Object wins;

    /* Kits */
    protected Object ranked_kit;
    protected Object teams_kit;
    protected Object mega_kit;
    protected Object solo_kit;

    /* Other */
    protected Object last_mode;
    protected Object cage;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        ClientCommandHandler.instance.registerCommand(new Command());
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
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (!enabled) return;

        if (currentTick > 1200) {
            currentTick = 0;
            update();
        } else {
            currentTick++;
            //if (currentTick % 10 == 0) log(String.format("i @ %s", currentTick));
        }
    }

    @SubscribeEvent
    public void onJoin(WorldEvent.Load event) {
        update();
    }

    public void update() {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            HypixelAPI.getInstance().setApiKey(UUID.fromString(ApiKey.getKey(true)));
            Request request = RequestBuilder.newBuilder(RequestType.PLAYER).addParam(RequestParam.PLAYER_BY_UUID, Minecraft.getMinecraft().getSession().getProfile().getId()).createRequest();

            log("Loaded: " + request.getURL(HypixelAPI.getInstance()));

            HypixelAPI.getInstance().getAsync(request, (Callback<PlayerReply>) (failcause, result) -> {
                try {
                    update(result.getPlayer().
                            getAsJsonObject("stats").
                            getAsJsonObject("SkyWars")
                    );
                    log("Updated!");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.out.print("Failed to update");
                }
            });
        });
    }

    protected void update(JsonObject array) {
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
        ((me.boomboompower.textdisplayer.loading.Placeholder) solo_kit).setReplacement(WordUtils.capitalizeFully(Utils.remove(Utils.optString(array, "activeKit_SOLO", "Default"), "kit_advanced_solo_", "kit_basic_solo_")));
    }

    protected void log(String message, Object... replace) {
        LogManager.getLogger("TD-Skywars").log(Level.INFO, String.format(message, replace));
    }
}
