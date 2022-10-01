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
        this.commandParameters.put("LocateArgs0", new CommandParameter[]{
                CommandParameter.newEnum("feature", false, new CommandEnum("Structure", LocateSubcommandStructureEnum.names())),
                CommandParameter.newEnum("useNewChunksOnly", true, new CommandEnum("Boolean", "true", "false"))
        });
        this.commandParameters.put("LocateArgs1", new CommandParameter[]{
                CommandParameter.newEnum("subcommand", false, new CommandEnum("LocateSubcommandStructure", "structure")),
                CommandParameter.newEnum("feature", false, new CommandEnum("Structure", LocateSubcommandStructureEnum.names())),
                CommandParameter.newEnum("useNewChunksOnly", true, new CommandEnum("Boolean", "true", "false"))
        });
        this.commandParameters.put("LocateArgs2", new CommandParameter[]{
                CommandParameter.newEnum("subcommand", false, new CommandEnum("LocateSubcommandBiome", "biome")),
                CommandParameter.newEnum("feature", false, new CommandEnum("Structure", SubcommandBiomeEnum.names())),
                CommandParameter.newEnum("useNewChunksOnly", true, new CommandEnum("Boolean", "true", "false"))
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

    private enum LocateSubcommandStructureEnum {
        ANCIENT_CITY,
        BASTION_REMNANT,
        BURIED_TREASURE,
        END_CITY,
        FORTRESS,
        MANSION,
        MINESHAFT,
        MONUMENT,
        PILLAGER_OUTPOST,
        RUINED_PORTAL,
        RUINS,
        SHIPWRECK,
        STRONGHOLD,
        TEMPLE,
        VILLAGE;

        static String[] names() {
            final LocateSubcommandStructureEnum[] values = values();
            final String[] names = new String[values.length];
            for(int i = 0; i < values.length; i++)
                names[i] = values[i].name().toLowerCase();
            return names;
        }
    }

    private enum SubcommandBiomeEnum {
        BAMBOO_JUNGLE,
        BAMBOO_JUNGLE_HILLS,
        BASALT_DELTAS,
        BEACH,
        BIRCH_FOREST,
        BIRCH_FOREST_HILLS,
        BIRCH_FOREST_HILLS_MUTATED,
        BIRCH_FOREST_MUTATED,
        COLD_BEACH,
        COLD_OCEAN,
        COLD_TAIGA,
        COLD_TAIGA_HILLS,
        COLD_TAIGA_MUTATED,
        CRIMSON_FOREST,
        DEEP_COLD_OCEAN,
        DEEP_DARK,
        DEEP_FROZEN_OCEAN,
        DEEP_LUKEWARM_OCEAN,
        DEEP_OCEAN,
        DESERT,
        DESERT_HILLS,
        DESERT_MUTATED,
        DRIPSTONE_CAVES,
        EXTREME_HILLS,
        EXTREME_HILLS_EDGE,
        EXTREME_HILLS_MUTATED,
        EXTREME_HILLS_PLUS_TREES,
        EXTREME_HILLS_PLUS_TREES_MUTATED,
        FLOWER_FOREST,
        FOREST,
        FOREST_HILLS,
        FROZEN_OCEAN,
        FROZEN_PEAKS,
        FROZEN_RIVER,
        GROVE,
        HELL,
        ICE_MOUNTAINS,
        ICE_PLAINS,
        ICE_PLAINS_SPIKES,
        JAGGED_PEAKS,
        JUNGLE,
        JUNGLE_EDGE,
        JUNGLE_EDGE_MUTATED,
        JUNGLE_HILLS,
        JUNGLE_MUTATED,
        LEGACY_FROZEN_OCEAN,
        LUKEWARM_OCEAN,
        LUSH_CAVES,
        MANGROVE_SWAMP,
        MEADOW,
        MEGA_TAIGA,
        MEGA_TAIGA_HILLS,
        MESA,
        MESA_BRYCE,
        MESA_PLATEAU,
        MESA_PLATEAU_MUTATED,
        MESA_PLATEAU_STONE,
        MESA_PLATEAU_STONE_MUTATED,
        MUSHROOM_ISLAND,
        MUSHROOM_ISLAND_SHORE,
        OCEAN,
        PLAINS,
        REDWOOD_TAIGA_HILLS_MUTATED,
        REDWOOD_TAIGA_MUTATED,
        RIVER,
        ROOFED_FOREST,
        ROOFED_FOREST_MUTATED,
        SAVANNA,
        SAVANNA_MUTATED,
        SAVANNA_PLATEAU,
        SAVANNA_PLATEAU_MUTATED,
        SNOWY_SLOPES,
        SOULSAND_VALLEY,
        STONE_BEACH,
        STONY_PEAKS,
        SUNFLOWER_PLAINS,
        SWAMPLAND,
        SWAMPLAND_MUTATED,
        TAIGA,
        TAIGA_HILLS,
        TAIGA_MUTATED,
        THE_END,
        WARM_OCEAN,
        WARPED_FOREST;

        static String[] names() {
            final SubcommandBiomeEnum[] values = values();
            final String[] names = new String[values.length];
            for(int i = 0; i < values.length; i++)
                names[i] = values[i].name().toLowerCase();
            return names;
        }
    }

}
