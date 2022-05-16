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

package de.kcodeyt.vanilla;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.anvil.Anvil;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.plugin.PluginBase;
import com.google.common.util.concurrent.MoreExecutors;
import com.nukkitx.protocol.bedrock.wrapper.BedrockWrapperSerializer;
import com.nukkitx.protocol.bedrock.wrapper.BedrockWrapperSerializers;
import de.kcodeyt.vanilla.command.LocateCommand;
import de.kcodeyt.vanilla.command.SudoCommand;
import de.kcodeyt.vanilla.command.WorldCommand;
import de.kcodeyt.vanilla.generator.VanillaNether;
import de.kcodeyt.vanilla.generator.VanillaOverworld;
import de.kcodeyt.vanilla.generator.VanillaTheEnd;
import de.kcodeyt.vanilla.generator.client.clientdata.Skin;
import de.kcodeyt.vanilla.generator.network.EncryptionKeyFactory;
import de.kcodeyt.vanilla.generator.server.VanillaServer;
import de.kcodeyt.vanilla.util.BedrockWrapperSerializerV10;
import de.kcodeyt.vanilla.world.World;
import lombok.Getter;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author Kevims KCodeYT
 * @version 1.0-SNAPSHOT
 */
public class VanillaGeneratorPlugin extends PluginBase implements Listener {

    public static final Skin STEVE_SKIN;

    static {
        Skin skin;
        try {
            System.setProperty("http.agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.132 Safari/537.36");
            skin = new Skin(
                    ImageIO.read(new URL("http://assets.mojang.com/SkinTemplates/steve.png")),
                    "geometry.humanoid.custom",
                    "{\"format_version\":\"1.12.0\",\"minecraft:geometry\":[{\"bones\":[{\"name\":\"body\",\"parent\":\"waist\",\"pivot\":[0.0,24.0,0.0]},{\"name\":\"waist\",\"pivot\":[0.0,12.0,0.0]},{\"cubes\":[{\"origin\":[-5.0,8.0,3.0],\"size\":[10,16,1],\"uv\":[0,0]}],\"name\":\"cape\",\"parent\":\"body\",\"pivot\":[0.0,24.0,3.0],\"rotation\":[0.0,180.0,0.0]}],\"description\":{\"identifier\":\"geometry.cape\",\"texture_height\":32,\"texture_width\":64}},{\"bones\":[{\"name\":\"root\",\"pivot\":[0.0,0.0,0.0]},{\"cubes\":[{\"origin\":[-4.0,12.0,-2.0],\"size\":[8,12,4],\"uv\":[16,16]}],\"name\":\"body\",\"parent\":\"waist\",\"pivot\":[0.0,24.0,0.0]},{\"name\":\"waist\",\"parent\":\"root\",\"pivot\":[0.0,12.0,0.0]},{\"cubes\":[{\"origin\":[-4.0,24.0,-4.0],\"size\":[8,8,8],\"uv\":[0,0]}],\"name\":\"head\",\"parent\":\"body\",\"pivot\":[0.0,24.0,0.0]},{\"name\":\"cape\",\"parent\":\"body\",\"pivot\":[0.0,24,3.0]},{\"cubes\":[{\"inflate\":0.50,\"origin\":[-4.0,24.0,-4.0],\"size\":[8,8,8],\"uv\":[32,0]}],\"name\":\"hat\",\"parent\":\"head\",\"pivot\":[0.0,24.0,0.0]},{\"cubes\":[{\"origin\":[4.0,12.0,-2.0],\"size\":[4,12,4],\"uv\":[32,48]}],\"name\":\"leftArm\",\"parent\":\"body\",\"pivot\":[5.0,22.0,0.0]},{\"cubes\":[{\"inflate\":0.250,\"origin\":[4.0,12.0,-2.0],\"size\":[4,12,4],\"uv\":[48,48]}],\"name\":\"leftSleeve\",\"parent\":\"leftArm\",\"pivot\":[5.0,22.0,0.0]},{\"name\":\"leftItem\",\"parent\":\"leftArm\",\"pivot\":[6.0,15.0,1.0]},{\"cubes\":[{\"origin\":[-8.0,12.0,-2.0],\"size\":[4,12,4],\"uv\":[40,16]}],\"name\":\"rightArm\",\"parent\":\"body\",\"pivot\":[-5.0,22.0,0.0]},{\"cubes\":[{\"inflate\":0.250,\"origin\":[-8.0,12.0,-2.0],\"size\":[4,12,4],\"uv\":[40,32]}],\"name\":\"rightSleeve\",\"parent\":\"rightArm\",\"pivot\":[-5.0,22.0,0.0]},{\"locators\":{\"lead_hold\":[-6,15,1]},\"name\":\"rightItem\",\"parent\":\"rightArm\",\"pivot\":[-6,15,1]},{\"cubes\":[{\"origin\":[-0.10,0.0,-2.0],\"size\":[4,12,4],\"uv\":[16,48]}],\"name\":\"leftLeg\",\"parent\":\"root\",\"pivot\":[1.90,12.0,0.0]},{\"cubes\":[{\"inflate\":0.250,\"origin\":[-0.10,0.0,-2.0],\"size\":[4,12,4],\"uv\":[0,48]}],\"name\":\"leftPants\",\"parent\":\"leftLeg\",\"pivot\":[1.90,12.0,0.0]},{\"cubes\":[{\"origin\":[-3.90,0.0,-2.0],\"size\":[4,12,4],\"uv\":[0,16]}],\"name\":\"rightLeg\",\"parent\":\"root\",\"pivot\":[-1.90,12.0,0.0]},{\"cubes\":[{\"inflate\":0.250,\"origin\":[-3.90,0.0,-2.0],\"size\":[4,12,4],\"uv\":[0,32]}],\"name\":\"rightPants\",\"parent\":\"rightLeg\",\"pivot\":[-1.90,12.0,0.0]},{\"cubes\":[{\"inflate\":0.250,\"origin\":[-4.0,12.0,-2.0],\"size\":[8,12,4],\"uv\":[16,32]}],\"name\":\"jacket\",\"parent\":\"body\",\"pivot\":[0.0,24.0,0.0]}],\"description\":{\"identifier\":\"geometry.humanoid.custom\",\"texture_height\":64,\"texture_width\":64,\"visible_bounds_height\":2,\"visible_bounds_offset\":[0,1,0],\"visible_bounds_width\":1}},{\"bones\":[{\"name\":\"root\",\"pivot\":[0.0,0.0,0.0]},{\"name\":\"waist\",\"parent\":\"root\",\"pivot\":[0.0,12.0,0.0]},{\"cubes\":[{\"origin\":[-4.0,12.0,-2.0],\"size\":[8,12,4],\"uv\":[16,16]}],\"name\":\"body\",\"parent\":\"waist\",\"pivot\":[0.0,24.0,0.0]},{\"cubes\":[{\"origin\":[-4.0,24.0,-4.0],\"size\":[8,8,8],\"uv\":[0,0]}],\"name\":\"head\",\"parent\":\"body\",\"pivot\":[0.0,24.0,0.0]},{\"cubes\":[{\"inflate\":0.50,\"origin\":[-4.0,24.0,-4.0],\"size\":[8,8,8],\"uv\":[32,0]}],\"name\":\"hat\",\"parent\":\"head\",\"pivot\":[0.0,24.0,0.0]},{\"cubes\":[{\"origin\":[-3.90,0.0,-2.0],\"size\":[4,12,4],\"uv\":[0,16]}],\"name\":\"rightLeg\",\"parent\":\"root\",\"pivot\":[-1.90,12.0,0.0]},{\"cubes\":[{\"inflate\":0.250,\"origin\":[-3.90,0.0,-2.0],\"size\":[4,12,4],\"uv\":[0,32]}],\"name\":\"rightPants\",\"parent\":\"rightLeg\",\"pivot\":[-1.90,12.0,0.0]},{\"cubes\":[{\"origin\":[-0.10,0.0,-2.0],\"size\":[4,12,4],\"uv\":[16,48]}],\"mirror\":true,\"name\":\"leftLeg\",\"parent\":\"root\",\"pivot\":[1.90,12.0,0.0]},{\"cubes\":[{\"inflate\":0.250,\"origin\":[-0.10,0.0,-2.0],\"size\":[4,12,4],\"uv\":[0,48]}],\"name\":\"leftPants\",\"parent\":\"leftLeg\",\"pivot\":[1.90,12.0,0.0]},{\"cubes\":[{\"origin\":[4.0,11.50,-2.0],\"size\":[3,12,4],\"uv\":[32,48]}],\"name\":\"leftArm\",\"parent\":\"body\",\"pivot\":[5.0,21.50,0.0]},{\"cubes\":[{\"inflate\":0.250,\"origin\":[4.0,11.50,-2.0],\"size\":[3,12,4],\"uv\":[48,48]}],\"name\":\"leftSleeve\",\"parent\":\"leftArm\",\"pivot\":[5.0,21.50,0.0]},{\"name\":\"leftItem\",\"parent\":\"leftArm\",\"pivot\":[6,14.50,1]},{\"cubes\":[{\"origin\":[-7.0,11.50,-2.0],\"size\":[3,12,4],\"uv\":[40,16]}],\"name\":\"rightArm\",\"parent\":\"body\",\"pivot\":[-5.0,21.50,0.0]},{\"cubes\":[{\"inflate\":0.250,\"origin\":[-7.0,11.50,-2.0],\"size\":[3,12,4],\"uv\":[40,32]}],\"name\":\"rightSleeve\",\"parent\":\"rightArm\",\"pivot\":[-5.0,21.50,0.0]},{\"locators\":{\"lead_hold\":[-6,14.50,1]},\"name\":\"rightItem\",\"parent\":\"rightArm\",\"pivot\":[-6,14.50,1]},{\"cubes\":[{\"inflate\":0.250,\"origin\":[-4.0,12.0,-2.0],\"size\":[8,12,4],\"uv\":[16,32]}],\"name\":\"jacket\",\"parent\":\"body\",\"pivot\":[0.0,24.0,0.0]},{\"name\":\"cape\",\"parent\":\"body\",\"pivot\":[0.0,24,-3.0]}],\"description\":{\"identifier\":\"geometry.humanoid.customSlim\",\"texture_height\":64,\"texture_width\":64,\"visible_bounds_height\":2,\"visible_bounds_offset\":[0,1,0],\"visible_bounds_width\":1}}]}",
                    "0.0.0"
            );
        } catch(IOException e) {
            skin = null;
        }

        STEVE_SKIN = skin;
    }

    private static final List<VanillaServer> VANILLA_SERVERS = new CopyOnWriteArrayList<>();
    @Getter
    private static VanillaGeneratorPlugin instance;

    @Getter
    private ScheduledExecutorService executorService;
    @Getter
    private EncryptionKeyFactory encryptionKeyFactory;

    public static synchronized CompletableFuture<VanillaServer> getVanillaServer(Level level) {
        return VANILLA_SERVERS.stream().filter(vanillaServer -> vanillaServer.isLevel(level)).findAny().
                map(CompletableFuture::completedFuture).
                orElseGet(() -> CompletableFuture.supplyAsync(() -> {
                    final int minY = switch(level.getGenerator().getDimension()) {
                        case Level.DIMENSION_OVERWORLD -> -4;
                        case Level.DIMENSION_NETHER, Level.DIMENSION_THE_END -> 0;
                        default -> -1;
                    };
                    final int maxY = switch(level.getGenerator().getDimension()) {
                        case Level.DIMENSION_OVERWORLD -> 20;
                        case Level.DIMENSION_NETHER -> 8;
                        case Level.DIMENSION_THE_END -> 16;
                        default -> -1;
                    };
                    final int dimension = level.getGenerator().getDimension();

                    final VanillaServer vanillaServer = new VanillaServer(new World(instance, level, dimension, minY, maxY));
                    synchronized(VANILLA_SERVERS) {
                        VANILLA_SERVERS.add(vanillaServer);
                        return vanillaServer;
                    }
                }));
    }

    @Override
    public void onLoad() {
        replaceSerializer();
        instance = this;

        this.executorService = MoreExecutors.listeningDecorator(Executors.newScheduledThreadPool(4));

        this.encryptionKeyFactory = new EncryptionKeyFactory();

        Generator.addGenerator(VanillaOverworld.class, "vanilla", VanillaOverworld.TYPE);
        Generator.addGenerator(VanillaNether.class, "vanilla_nether", VanillaNether.TYPE);
        Generator.addGenerator(VanillaTheEnd.class, "vanilla_the_end", VanillaTheEnd.TYPE);

        this.setEnabled(true);

        final Server server = this.getServer();
        if(!server.isLevelGenerated("nether"))
            server.generateLevel("nether", System.currentTimeMillis(), VanillaNether.class, new HashMap<>(), Anvil.class);
        else
            server.loadLevel("nether");

        if(!server.isLevelGenerated("the_end"))
            server.generateLevel("the_end", System.currentTimeMillis(), VanillaTheEnd.class, new HashMap<>(), Anvil.class);
        else
            server.loadLevel("the_end");
    }

    @Override
    public void onEnable() {
        final Server server = this.getServer();

        server.getPluginManager().registerEvents(this, this);
        server.getCommandMap().register("world", new WorldCommand());
        server.getCommandMap().register("world", new LocateCommand());
        server.getCommandMap().register("world", new SudoCommand());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        player.setCheckMovement(false);
        this.getServer().getScheduler().scheduleRepeatingTask(null, () -> {
            if(!player.isOnline()) return;

            final float tps = this.getServer().getTicksPerSecond();
            final float avgTps = this.getServer().getTicksPerSecondAverage();
            final String tpsColor = tps < 0 ? "§5" : tps < 4 ? "§4" : tps < 8 ? "§c" : tps < 16 ? "§6" : "§a";
            final String avgTpsColor = avgTps < 0 ? "§5" : avgTps < 4 ? "§4" : avgTps < 8 ? "§c" : avgTps < 16 ? "§6" : "§a";
            player.sendActionBar("TPS: " + tpsColor + tps + "§f Average TPS: " + avgTpsColor + avgTps + "\n" +
                    "§fChunk: " + player.getChunkX() + ":" + player.getChunkZ());
        }, 5, true);
    }

    @Override
    public void onDisable() {
        VANILLA_SERVERS.forEach(VanillaServer::close);
    }

    private static void replaceSerializer() {
        try {
            final Class<BedrockWrapperSerializers> serializersClass = BedrockWrapperSerializers.class;
            final Field serializers = serializersClass.getDeclaredField("SERIALIZERS");
            serializers.setAccessible(true);
            final Map<?, ?> serializersMap_ = (Map<?, ?>) serializers.get(null);
            //noinspection unchecked
            final Map<Integer, BedrockWrapperSerializer> serializerMap = (Map<Integer, BedrockWrapperSerializer>) serializersMap_;
            serializerMap.put(10, BedrockWrapperSerializerV10.DEFAULT);
        } catch(IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

}
