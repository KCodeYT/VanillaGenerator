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

package de.kcodeyt.vanilla.generator.client.clientdata;

import com.nimbusds.jose.shaded.json.JSONObject;
import de.kcodeyt.vanilla.generator.client.Network;
import lombok.Value;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author Kevims KCodeYT
 * @version 1.0-SNAPSHOT
 */
@Value
public class LoginData {

    String name;
    String xuid;
    UUID uniqueId;
    Skin skin;
    DeviceOS deviceOS;
    UIProfile uiProfile;

    public JSONObject buildSkinData(Random random, InetSocketAddress serverAddress) {
        final UUID deviceId = UUID.randomUUID();
        final Map<String, Object> map = new LinkedHashMap<>();
        map.put("AnimatedImageData", new ArrayList<>());
        map.put("ArmSize", "");
        map.put("CapeData", "");
        map.put("CapeId", "");
        map.put("CapeImageHeight", 0);
        map.put("CapeImageWidth", 0);
        map.put("CapeOnClassicSkin", false);
        map.put("ClientRandomId", random.nextInt());
        map.put("CurrentInputMode", 1);
        map.put("DefaultInputMode", 1);
        map.put("DeviceId", deviceId.toString());
        map.put("DeviceModel", "");
        map.put("DeviceOS", this.deviceOS.ordinal());
        map.put("GameVersion", Network.CODEC.getMinecraftVersion());
        map.put("GuiScale", 0);
        map.put("LanguageCode", "en_US");
        map.put("PersonaPieces", new ArrayList<>());
        map.put("PersonaSkin", false);
        map.put("PieceTintColors", new ArrayList<>());
        map.put("PlatformOfflineId", "");
        map.put("PlatformOnlineId", "");
        map.put("PlayFabId", UUID.randomUUID().toString());
        map.put("PremiumSkin", false);
        map.put("SelfSignedId", this.uniqueId.toString());
        map.put("ServerAddress", serverAddress.getAddress().getHostAddress() + ":" + serverAddress.getPort());
        map.put("SkinAnimationData", "");
        map.put("SkinColor", "#0");
        map.put("SkinData", new String(Base64.getEncoder().encode(this.skin.getSkinData())));
        map.put("SkinGeometryData", new String(Base64.getEncoder().encode(this.skin.getGeometryData().getBytes(StandardCharsets.UTF_8))));
        map.put("SkinGeometryDataEngineVersion", new String(Base64.getEncoder().encode(this.skin.getGeometryDataEngineVersion().getBytes(StandardCharsets.UTF_8))));
        map.put("SkinId", UUID.randomUUID() + ".Custom" + deviceId);
        map.put("SkinImageHeight", this.skin.getImage().getHeight());
        map.put("SkinImageWidth", this.skin.getImage().getWidth());
        map.put("SkinResourcePatch", new String(Base64.getEncoder().encode(("{\"geometry\":{\"default\":\"" + this.skin.getGeometryName() + "\"}}").getBytes())));
        map.put("ThirdPartyName", this.name);
        map.put("ThirdPartyNameOnly", false);
        map.put("UIProfile", this.uiProfile.ordinal());
        return new JSONObject(map);
    }

}
