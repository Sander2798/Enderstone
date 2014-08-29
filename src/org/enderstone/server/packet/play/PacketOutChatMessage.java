package org.enderstone.server.packet.play;

import io.netty.buffer.ByteBuf;
import org.enderstone.server.chat.Message;
import org.enderstone.server.packet.JSONStringBuilder;
import org.enderstone.server.packet.Packet;

public class PacketOutChatMessage extends Packet {

	private String jsonChat;

	public PacketOutChatMessage(String chatMessage, boolean json) {
		if (json) {
			this.jsonChat = chatMessage;
		} else {
			this.jsonChat = JSONStringBuilder.build(chatMessage);
		}
	}
	
	public PacketOutChatMessage(Message message)
	{
		this(message.toMessageJson(), true);
	}

	@Override
	public void write(ByteBuf buf) throws Exception {
		if (getStringSize(jsonChat) > 32767) {
			throw new IllegalArgumentException("The chat messages can't be any longer than 32767 bytes!");
		}
		writeString(this.jsonChat, buf);
	}

	@Override
	public int getSize() throws Exception {
		return getStringSize(this.jsonChat) + getVarIntSize(getId());
	}

	@Override
	public byte getId() {
		return 0x02;
	}

	@Override
	public void read(ByteBuf buf) throws Exception {
		throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " with ID 0x" + Integer.toHexString(getId()) + " cannot be read.");
	}
}
