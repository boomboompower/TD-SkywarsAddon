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
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import java.util.*;

public class StatsCommand implements ICommand {

    @Override
    public String getCommandName() {
        return "skywarsstats";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return EnumChatFormatting.RED + "Usage /" + getCommandName() + " <username>";
    }

    @Override
    public List<String> getCommandAliases() {
        return Collections.singletonList("swstats");
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            sendStats(Minecraft.getMinecraft().thePlayer.getName(), create(Minecraft.getMinecraft().thePlayer.getName()), args);
        } else {
            sendStats(args[0], create(args[0]), args);
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        return args.length == 1 ? CommandBase.getListOfStringsMatchingLastWord(args, getPlayers()) : null;
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    @Override
    public int compareTo(ICommand o) {
        return 0;
    }

    private ArrayList<String> getPlayers() {
        ArrayList<String> playerNames = new ArrayList<>();
        for (EntityPlayer player : Minecraft.getMinecraft().theWorld.playerEntities) {
            if (!player.isInvisibleToPlayer(Minecraft.getMinecraft().thePlayer) && !player.isInvisible()) {
                playerNames.add(player.getName());
            }
        }
        return playerNames;
    }

    private void sendMessage(String message, Object... replace) {
        Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(SkywarsAddon.PREFIX + String.format(message, replace)));
    }

    private JsonObject create(final String playerName) {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            HypixelAPI.getInstance().setApiKey(UUID.fromString(ApiKey.getKey(false)));
            Request request = RequestBuilder.newBuilder(RequestType.PLAYER).addParam(RequestParam.PLAYER_BY_NAME, playerName).createRequest();

            final JsonObject[] response = new JsonObject[1];

            HypixelAPI.getInstance().getAsync(request, (Callback<PlayerReply>) (failcause, result) -> {
                try {
                    response[0] = result.getPlayer().getAsJsonObject("stats").getAsJsonObject("SkyWars");
                } catch (Exception ex) {
                    response[0] = new JsonObject();
                }
            });
            return response[0];
        });
        return new JsonObject();
    }

    private void sendStats(String playerName, JsonObject array, String[] args) {
        sendMessage(EnumChatFormatting.BLUE + "-----------------------------------------------------");
        if (args.length == 1) {
            sendMessage("Showing information about %s", playerName);
            sendMessage("  - Wins", Utils.optInt(array, "wins"));
            sendMessage("  - Kills", Utils.optInt(array, "kills"));
            sendMessage("  - Assists", Utils.optInt(array, "assists"));
            sendMessage("  - Deaths", Utils.optInt(array, "deaths"));
            sendMessage("  - Win streak", Utils.optInt(array, "win_streak"));
        } else {
            switch (args[1]) {
                default:
                case "0":
                case "1":
                case "wins":
                case "kills":
                case "assists":
                case "deaths":
                    sendMessage("Showing information about %s for page 1", EnumChatFormatting.GOLD + playerName + EnumChatFormatting.GRAY);
                    sendMessage("  - Wins", Utils.optInt(array, "wins"));
                    sendMessage("  - Kills", Utils.optInt(array, "kills"));
                    sendMessage("  - Assists", Utils.optInt(array, "assists"));
                    sendMessage("  - Deaths", Utils.optInt(array, "deaths"));
                    break;
                case "2":
                case "coin":
                case "soul":
                case "coins":
                case "souls":
                    sendMessage("Showing information about %s for page 2", EnumChatFormatting.GOLD + playerName + EnumChatFormatting.GRAY);
                    sendMessage("  - Quits", Utils.optInt(array, "quits"));
                    sendMessage("  - Coins", Utils.optInt(array, "coins"));
                    sendMessage("  - Souls", Utils.optInt(array, "souls"));
                    sendMessage("  - Win streak", Utils.optInt(array, "win_streak"));
                    break;
                case "3":
                case "egg":
                case "eggs":
                case "arrow":
                case "arrows":
                case "enderpearl":
                case "enderpearls":
                    sendMessage("Showing information about %s for page 3", EnumChatFormatting.GOLD + playerName + EnumChatFormatting.GRAY);
                    sendMessage("  - Enderpearls thrown", Utils.optInt(array, "enderpearls_thrown"));
                    sendMessage("  - Eggs thrown", Utils.optInt(array, "egg_thrown"));
                    sendMessage("  - Arrows shot", Utils.optInt(array, "arrows_shot"));
                    sendMessage("  - Arrows hit", Utils.optInt(array, "arrows_hit"));
                    break;
                case "4":
                case "mode":
                case "cage":
                case "block":
                case "blocks":
                    sendMessage("Showing information about %s for page 4", EnumChatFormatting.GOLD + playerName + EnumChatFormatting.GRAY);
                    sendMessage("  - Blocks placed", Utils.optInt(array, "blocks_placed"));
                    sendMessage("  - Blocks broken", Utils.optInt(array, "blocks_broken"));
                    sendMessage("  - Last mode", WordUtils.capitalizeFully(Utils.optString(array, "lastMode", "Solo")));
                    sendMessage("  - Active cage", WordUtils.capitalizeFully(Utils.remove(Utils.optString(array, "activeCage", "Normal"), "cage_")));
                    break;
                case "5":
                case "kit":
                case "kits":
                    sendMessage("Showing information about %s for page 4", EnumChatFormatting.GOLD + playerName + EnumChatFormatting.GRAY);
                    sendMessage("  - Ranked kit", WordUtils.capitalizeFully(Utils.remove(Utils.optString(array, "activeKit_RANKED", "Default"), "kit_ranked_ranked_")));
                    sendMessage("  - Teams kit", WordUtils.capitalizeFully(Utils.remove(Utils.optString(array, "activeKit_TEAM", "Default"), "kit_attacking_team_")));
                    sendMessage("  - Mega kit", WordUtils.capitalizeFully(Utils.remove(Utils.optString(array, "activeKit_MEGA", "Default"), "kit_mega_mega_")));
                    sendMessage("  - Solo kit", WordUtils.capitalizeFully(Utils.remove(Utils.optString(array, "activeKit_SOLO", "Default"), "kit_advanced_solo_", "kit_basic_solo_")));
                    break;
            }
        }
        sendMessage(EnumChatFormatting.BLUE + "-----------------------------------------------------");
    }

    protected void log(String message, Object... replace) {
        LogManager.getLogger("TD-Skywars").log(Level.INFO, String.format(message, replace));
    }
}
