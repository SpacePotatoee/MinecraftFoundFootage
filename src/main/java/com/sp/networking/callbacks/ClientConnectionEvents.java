package com.sp.networking.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.MinecraftClient;

public class ClientConnectionEvents {

    public static final Event<Disconnect> DISCONNECT = EventFactory.createArrayBacked(Disconnect.class, listeners -> (client) -> {
        for (Disconnect listener : listeners) {
            listener.onLoginDisconnect(client);
        }
    });

    @FunctionalInterface
    public interface Disconnect {
        void onLoginDisconnect(MinecraftClient client);
    }

}
