/* 
 * Enderstone
 * Copyright (C) 2014 Sander Gielisse and Fernando van Loenhout
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
package org.enderstone.server.packet.play;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import org.enderstone.server.Main;
import org.enderstone.server.entity.EnderPlayer;
import org.enderstone.server.packet.NetworkManager;
import org.enderstone.server.packet.Packet;

public class PacketInClientSettings extends Packet {

	private String locale;
	private byte renderDistance;
	private byte chatFlags;
	private boolean chatColors;
	private int displayedSkinParts;

	@Override
	public void read(ByteBuf buf) throws IOException {
		this.locale = readString(buf);
		this.renderDistance = buf.readByte();
		this.chatFlags = buf.readByte();
		this.chatColors = buf.readBoolean();
		this.displayedSkinParts = buf.readUnsignedByte();
	}

	@Override
	public void write(ByteBuf buf) throws IOException {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be written.");
	}

	@Override
	public int getSize() throws IOException {
		return getStringSize(locale) + 4 + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x15;
	}

	@Override
	public void onRecieve(final NetworkManager networkManager) {
		Main.getInstance().sendToMainThread(new Runnable(){

			@Override
			public void run() {
				EnderPlayer player;
				if((player=networkManager.player) == null) return;
				player.clientSettings.setChatColors(chatColors);
				player.clientSettings.setChatFlags(chatFlags);
				player.clientSettings.setLocale(locale);
				player.clientSettings.setRenderDistance(renderDistance);
				player.clientSettings.setDisplayedSkinParts(displayedSkinParts);
			}
		});
	}

	public String getLocale() {
		return locale;
	}

	public byte getRenderDistance() {
		return renderDistance;
	}

	public byte getChatFlags() {
		return chatFlags;
	}

	public boolean getChatColors() {
		return chatColors;
	}
}
