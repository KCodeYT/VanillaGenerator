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
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.level.Level;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.network.protocol.TextPacket;
import com.nukkitx.protocol.bedrock.data.command.CommandOutputMessage;
import de.kcodeyt.vanilla.VanillaGeneratorPlugin;
import de.kcodeyt.vanilla.generator.Vanilla;

/**
 * @author Kevims KCodeYT
 * @version 1.0-SNAPSHOT
 */
public class LocateCommand extends Command {

    public LocateCommand() {
        super("locate", "Locates a specific structure");
        this.setPermission("vanilla.command.locate");
        this.commandParameters.clear();
        this.commandParameters.put("LocateArgs", new CommandParameter[]{
                CommandParameter.newEnum("feature", false, new CommandEnum("Feature", StructureFeatureType.names()))
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if(!(sender instanceof final Player player)) {
            sender.sendMessage("You must be online to use this command!");
            return true;
        }

        final Level level = player.getLevel();
        final Generator generator = level.getGenerator();
        if(!(generator instanceof Vanilla)) {
            sender.sendMessage("§cThis command can be executed only in a vanilla generator world");
            return true;
        }

        final String locateCommand = "locate" + (args.length > 0 ? " " + String.join(" ", args) : "");
        VanillaGeneratorPlugin.getVanillaServer(level).whenComplete((vanillaServer, throwable) -> {
            if(vanillaServer == null) return;

            vanillaServer.getClient().sendCommand(locateCommand, commandOutputPacket -> {
                for(CommandOutputMessage message : commandOutputPacket.getMessages()) {
                    final TextPacket textPacket = new TextPacket();
                    textPacket.type = TextPacket.TYPE_TRANSLATION;
                    textPacket.message = message.getMessageId();
                    textPacket.parameters = message.getParameters();
                    player.dataPacket(textPacket);
                }
            });
        });

        return true;
    }

    @SuppressWarnings("SpellCheckingInspection")
    private enum StructureFeatureType {

        ANCIENTCITY,
        BASTIONREMNANT,
        BURIEDTREASURE,
        ENDCITY,
        FORTRESS,
        MANSION,
        MINESHAFT,
        MONUMENT,
        RUINS,
        PILLAGEROUTPOST,
        RUINEDPORTAL,
        SHIPWRECK,
        STRONGHOLD,
        TEMPLE,
        VILLAGE;

        static String[] names() {
            final StructureFeatureType[] values = values();
            final String[] names = new String[values.length];
            for(int i = 0; i < values.length; i++)
                names[i] = values[i].name().toLowerCase();
            return names;
        }

    }

}
