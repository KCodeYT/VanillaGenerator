/*
 * Copyright 2022 KCodeYT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.kcodeyt.vanilla.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.level.Level;
import cn.nukkit.utils.TextFormat;

/**
 * @author Kevims KCodeYT
 * @version 1.0-SNAPSHOT
 */
public class WorldCommand extends Command {

    public WorldCommand() {
        super("world", "Teleports to a specific world");
        this.setPermission("vanilla.command.world");
        this.commandParameters.clear();
        this.commandParameters.put("World", new CommandParameter[]{
                CommandParameter.newType("world", false, CommandParamType.STRING)
        });
        this.commandParameters.put("Player2World", new CommandParameter[]{
                CommandParameter.newType("player", false, CommandParamType.TARGET),
                CommandParameter.newType("world", false, CommandParamType.STRING)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if(!this.testPermission(sender))
            return false;
        if(args.length == 0) {
            sender.sendMessage("§7Usage§8: §7/world <world>");
            sender.sendMessage("§7Usage§8: §7/world <player> <world>");
            return false;
        }

        final String playerName = TextFormat.clean(args.length < 2 ? sender.getName() : args[0]);
        final String levelName = args.length < 2 ? args[0] : args[1];

        final Level level = sender.getServer().getLevelByName(levelName);
        if(level == null) {
            sender.sendMessage("§cThe specific level \"" + levelName + "§r§c\" does not exists!");
            return false;
        }

        if(args.length == 1 && !(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players!");
            sender.sendMessage("§7Usage§8: §7/world <player> <world>");
            return false;
        }

        final Player player = args.length < 2 ? (Player) sender : sender.getServer().getPlayer(playerName);
        if(player == null) {
            sender.sendMessage("§cCan't find player " + playerName + "§r§c!");
            return false;
        }

        player.teleport(level.getSafeSpawn());
        sender.sendMessage("§aSuccessfully teleported §6" + player.getName() + "§r§a to §6" + levelName + "§r§a!");
        return true;
    }

}
