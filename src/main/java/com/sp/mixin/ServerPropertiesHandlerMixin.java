package com.sp.mixin;

import com.sp.mixininterfaces.NewServerProperties;
import net.minecraft.server.dedicated.AbstractPropertiesHandler;
import net.minecraft.server.dedicated.ServerPropertiesHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Properties;

@Mixin(ServerPropertiesHandler.class)
public abstract class ServerPropertiesHandlerMixin  extends AbstractPropertiesHandler<ServerPropertiesHandler> implements NewServerProperties {
    public ServerPropertiesHandlerMixin(Properties properties) {
        super(properties);
    }

    @Unique private final int exitSpawnRadius = this.getInt("backrooms-exit-spawn-radius", 300);

    @Override
    public int getExitSpawnRadius() {
        return this.exitSpawnRadius;
    }

}
