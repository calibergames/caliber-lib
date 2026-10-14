package com.calibermc.caliberlib.fabric;

import com.calibermc.caliberlib.compat.CompatLists;
import com.calibermc.caliberlib.compat.CompatLogin;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking;
import net.minecraft.network.FriendlyByteBuf;

/**
 * The compat list's login check on Fabric and Quilt, as a login query. Login
 * waits for every query's answer before the player joins, and the
 * registries are compared only after joining, so a different list is
 * refused with its own reason first.
 */
public final class FabricCompatLogin {

	private FabricCompatLogin() {
	}

	/** Server side; also on clients, for the integrated server's LAN guests. */
	static void server() {
		ServerLoginConnectionEvents.QUERY_START.register((handler, server, sender, synchronizer) -> {
			// The integrated server's own client shares the list.
			if (handler.connection.isMemoryConnection()) {
				return;
			}
			FriendlyByteBuf buf = PacketByteBufs.create();
			CompatLogin.write(buf);
			sender.sendPacket(CompatLogin.CHANNEL, buf);
		});
		ServerLoginNetworking.registerGlobalReceiver(CompatLogin.CHANNEL, (server, handler, understood, buf, synchronizer, sender) -> {
			if (!understood) {
				// A client without CaliberLib: refused only when there are compat shapes to agree on.
				if (CompatLists.active()) {
					handler.disconnect(CompatLogin.missing());
				}
				return;
			}
			CompatLogin.Remote client = CompatLogin.Remote.read(buf);
			if (!client.matches()) {
				String player = handler.gameProfile != null ? handler.gameProfile.getName() : handler.getUserName();
				int[] n = CompatLogin.logDifference(client, "server", "client " + player);
				handler.disconnect(CompatLogin.differs(n[0], n[1]));
			}
		});
	}
}
