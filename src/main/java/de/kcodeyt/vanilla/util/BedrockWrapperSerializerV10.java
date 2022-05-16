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

package de.kcodeyt.vanilla.util;

import com.nukkitx.network.VarInts;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.BedrockPacketCodec;
import com.nukkitx.protocol.bedrock.BedrockSession;
import com.nukkitx.protocol.bedrock.exception.PacketSerializeException;
import com.nukkitx.protocol.bedrock.wrapper.BedrockWrapperSerializer;
import com.nukkitx.protocol.util.Zlib;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;

import java.util.Collection;
import java.util.zip.DataFormatException;

/**
 * @author Kevims KCodeYT
 * @version 1.0-SNAPSHOT
 */
public class BedrockWrapperSerializerV10 extends BedrockWrapperSerializer {

    public static final BedrockWrapperSerializerV10 DEFAULT = new BedrockWrapperSerializerV10(Zlib.RAW);

    private final Zlib zlib;

    private BedrockWrapperSerializerV10(Zlib zlib) {
        this.zlib = zlib;
    }

    @Override
    public void serialize(ByteBuf buffer, BedrockPacketCodec codec, Collection<BedrockPacket> packets, int level, BedrockSession session) {
        final ByteBuf uncompressed = ByteBufAllocator.DEFAULT.ioBuffer(packets.size() << 3);

        try {
            for(BedrockPacket packet : packets) {
                final ByteBuf packetBuffer = ByteBufAllocator.DEFAULT.ioBuffer();
                try {
                    final int id = codec.getId(packet);
                    int header = 0;
                    header |= (id & 0x3ff);
                    header |= (packet.getSenderId() & 3) << 10;
                    header |= (packet.getClientId() & 3) << 12;
                    VarInts.writeUnsignedInt(packetBuffer, header);
                    codec.tryEncode(packetBuffer, packet, session);

                    VarInts.writeUnsignedInt(uncompressed, packetBuffer.readableBytes());
                    uncompressed.writeBytes(packetBuffer);
                } catch(PacketSerializeException e) {
                    log.debug("Error occurred whilst encoding " + packet.getClass().getSimpleName(), e);
                } finally {
                    packetBuffer.release();
                }
            }

            this.zlib.deflate(uncompressed, buffer, level);
        } catch(DataFormatException e) {
            throw new RuntimeException("Unable to deflate buffer data", e);
        } finally {
            uncompressed.release();
        }
    }

    @Override
    public void deserialize(ByteBuf compressed, BedrockPacketCodec codec, Collection<BedrockPacket> packets, BedrockSession session) {
        final ByteBuf decompressed = ByteBufAllocator.DEFAULT.ioBuffer();
        try {
            this.zlib.inflate(compressed, decompressed, 1024 * 1024 * 1024); // 12MBs

            while(decompressed.isReadable()) {
                final int length = VarInts.readUnsignedInt(decompressed);
                if(length == 0) continue;

                final ByteBuf packetBuffer = decompressed.readSlice(length);

                try {
                    final int header = VarInts.readUnsignedInt(packetBuffer);
                    final int packetId = header & 0x3ff;

                    BedrockPacket packet = codec.tryDecode(packetBuffer, packetId, session);
                    packet.setPacketId(packetId);
                    packet.setSenderId((header >>> 10) & 3);
                    packet.setClientId((header >>> 12) & 3);
                    packets.add(packet);
                } catch(Exception ignored) {
                }
            }
        } catch(DataFormatException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to inflate buffer data", e);
        } finally {
            if(decompressed != null) {
                decompressed.release();
            }
        }
    }
}