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
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.level.Level;
import cn.nukkit.network.protocol.TextPacket;
import com.nukkitx.protocol.bedrock.data.command.CommandOutputMessage;
import de.kcodeyt.vanilla.VanillaGeneratorPlugin;
import de.kcodeyt.vanilla.generator.Vanilla;

import java.util.Arrays;

/**
 * @author Kevims KCodeYT
 * @version 1.0-SNAPSHOT
 */
public class SudoCommand extends Command {

    public SudoCommand() {
        super("sudo", "Executes a command on a background server");
        this.setPermission("vanilla.command.sudo");
        this.commandParameters.clear();
        this.commandParameters.put("SudoArgs", new CommandParameter[]{
                CommandParameter.newType("server", false, CommandParamType.STRING),
                CommandParameter.newType("command", false, CommandParamType.TEXT)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        final String server = args.length > 0 ? args[0] : null;
        final String command = args.length > 1 ? String.join(" ", Arrays.copyOfRange(args, 1, args.length)) : null;

        if(server == null) {
            sender.sendMessage("§cPlease specify a world name!");
            return false;
        }

        if(command == null) {
            sender.sendMessage("§cPlease specify a command!");
            return false;
        }

        final Level level = Server.getInstance().getLevelByName(server);
        if(level == null || !(level.getGenerator() instanceof Vanilla)) {
            sender.sendMessage("§cThe world does not exists or does now has a background server!");
            return false;
        }

        VanillaGeneratorPlugin.getVanillaServer(level).whenComplete((vanillaServer, throwable) -> {
            if(vanillaServer == null) {
                sender.sendMessage("§cCould not find the background server for " + level.getFolderName() + "!");
                return;
            }

            vanillaServer.getClient().sendCommand(command, commandOutputPacket -> {
                sender.sendMessage("§aGot command response:");

                for(CommandOutputMessage message : commandOutputPacket.getMessages()) {
                    if(sender instanceof Player) {
                        final TextPacket textPacket = new TextPacket();
                        textPacket.type = TextPacket.TYPE_TRANSLATION;
                        textPacket.message = message.getMessageId();
                        textPacket.parameters = message.getParameters();

                        ((Player) sender).dataPacket(textPacket);
                    } else {
                        sender.sendMessage(message.getMessageId() + " [" + Arrays.toString(message.getParameters()) + "]");
                    }
                }
            });
        });
        return true;
    }

}
