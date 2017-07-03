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

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.Arrays;
import java.util.List;

public class Command implements ICommand {

    @Override
    public String getCommandName() {
        return "textdisplayerskywars";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return EnumChatFormatting.RED + "Usage /" + getCommandName() + " <status, update, toggle, world>";
    }

    @Override
    public List<String> getCommandAliases() {
        return Arrays.asList("tdsw", "tdskywars");
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            sendMessage(getCommandUsage(sender));
        } else {
            switch (args[0]) {
                case "status":
                case "info":
                    sendMessage("enabled = [ %s ]", SkywarsAddon.instance.enabled);
                    sendMessage("keyUsed = [ %s ]", SkywarsAddon.instance.keyUsed);
                    sendMessage("currentTick = [ %s ]", SkywarsAddon.instance.currentTick);
                    break;
                case "toggle":
                    sendMessage("This addon is now %s!", ((SkywarsAddon.instance.enabled = !SkywarsAddon.instance.enabled) ? EnumChatFormatting.GREEN + "Enabled" + EnumChatFormatting.GRAY : EnumChatFormatting.RED + "Disabled" + EnumChatFormatting.GRAY));
                    break;
                case "update":
                case "forceupdate":
                    sendMessage("Updated, text should be fixed!");
                    SkywarsAddon.instance.update();
                    break;
                default:
                    sendMessage(getCommandUsage(sender));
                    break;
            }
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        return args.length == 1 ? CommandBase.getListOfStringsMatchingLastWord(args, "status", "update", "toggle") : null;
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    @Override
    public int compareTo(ICommand o) {
        return 0;
    }

    private void sendMessage(String message, Object... replace) {
        Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(SkywarsAddon.PREFIX + String.format(message, replace)));
    }
}
