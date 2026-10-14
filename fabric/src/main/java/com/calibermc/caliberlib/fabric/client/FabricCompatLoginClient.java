package com.calibermc.caliberlib.fabric.client;

import com.calibermc.caliberlib.compat.CompatLogin;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.FriendlyByteBuf;

import java.util.concurrent.CompletableFuture;

/** The client's answer to the server's compat-list query (see {@code FabricCompatLogin}). */
public final class FabricCompatLoginClient {

	private FabricCompatLoginClient() {
	}

	public static void init() {
		ClientLoginNetworking.registerGlobalReceiver(CompatLogin.CHANNEL, (client, handler, buf, listeners) -> {
			CompatLogin.Remote server = CompatLogin.Remote.read(buf);
			if (!server.matches()) {
				CompatLogin.logDifference(server, "client", "server");
			}
			FriendlyByteBuf reply = PacketByteBufs.create();
			CompatLogin.write(reply);
			return CompletableFuture.completedFuture(reply);
		});
	}
}
