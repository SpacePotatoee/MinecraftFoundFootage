# Rendering API

The SP-Backrooms mod provides a comprehensive rendering system with PBR materials, custom shaders, and visual effects. This guide covers how to integrate with and extend the rendering pipeline.

## Overview

The rendering system includes:
- **PBR (Physically Based Rendering)** materials for blocks
- **Custom shader integration** with Veil Renderer
- **Visual effects** (glitch, static, distortion)
- **Dynamic lighting** and shadows
- **Post-processing pipeline**

## PBR Material System

### Registering PBR Materials

```java
// In your client initialization
public void onInitializeClient() {
    // Register PBR material for your block
    PbrRegistry.registerPBR(MyBlocks.CUSTOM_BLOCK, 
        new PbrMaterial(
            true,    // Enable height mapping (parallax occlusion)
            0.35f,   // Depth multiplier (0.2-0.6 recommended)
            1.0f,    // Texture zoom (1.0 = normal scale)
            512      // Texture resolution (must be square)
        ));
}
```

### PBR Texture Setup

Create your PBR textures in: `assets/your_mod/textures/pbr/block/your_block/`

Required textures:
- `your_block_color.png` - Albedo/diffuse map
- `your_block_normal.png` - Normal map
- `your_block_height.png` - Height/displacement map

```java
// Example PBR material configurations
public class MyPbrMaterials {
    
    // High-detail surface with parallax
    public static final PbrMaterial DETAILED_SURFACE = 
        new PbrMaterial(true, 0.4f, 1.0f, 1024);
    
    // Simple surface without height
    public static final PbrMaterial SIMPLE_SURFACE = 
        new PbrMaterial(false, 0.0f, 1.0f, 512);
    
    // Large-scale texture
    public static final PbrMaterial LARGE_TEXTURE = 
        new PbrMaterial(true, 0.3f, 2.0f, 512);
}
```

### Dynamic PBR Registration

```java
// Register with resource reload support
ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES)
    .registerReloadListener(new SimpleSynchronousResourceReloadListener() {
        @Override
        public Identifier getFabricId() {
            return new Identifier("my_mod", "pbr_reload");
        }
        
        @Override
        public void reload(ResourceManager manager) {
            // Re-register PBR materials on resource reload
            registerAllPbrMaterials();
        }
    });
```

## Custom Shaders

### Shader Integration

```java
// Register custom shaders
public class MyShaders {
    public static final Identifier MY_SHADER = 
        new Identifier("my_mod", "my_custom_shader");
    
    public static void registerShaders() {
        // Shaders are automatically loaded from:
        // assets/my_mod/pinwheel/shaders/program/my_custom_shader.vsh
        // assets/my_mod/pinwheel/shaders/program/my_custom_shader.fsh
    }
}
```

### Using Shaders in Rendering

```java
// In your renderer
public void render(MatrixStack matrices, VertexConsumerProvider provider) {
    ShaderProgram shader = VeilRenderSystem.setShader(MY_SHADER);
    if (shader != null) {
        // Set shader uniforms
        shader.setFloat("time", RenderSystem.getShaderGameTime());
        shader.setVector("playerPos", playerPosition);
        shader.setInt("effectStrength", effectLevel);
        
        // Render with shader
        renderGeometry(matrices, provider);
    }
}
```

### Shader Uniform Management

```java
public class ShaderUniforms {
    
    public static void setCommonUniforms(ShaderProgram shader, 
                                        PlayerComponent player) {
        // Time-based effects
        shader.setFloat("GameTime", RenderSystem.getShaderGameTime());
        
        // Player state
        shader.setInt("IsInBackrooms", player.isInBackrooms() ? 1 : 0);
        shader.setInt("ShouldGlitch", player.shouldGlitch() ? 1 : 0);
        shader.setFloat("GlitchIntensity", getGlitchIntensity(player));
        
        // Environmental
        shader.setVector("FogColor", getCurrentFogColor());
        shader.setFloat("FogDensity", getCurrentFogDensity());
    }
}
```

## Visual Effects System

### Screen Effects

```java
// Apply visual effects to players
public class VisualEffects {
    
    public static void applyGlitchEffect(PlayerComponent player, 
                                        float intensity) {
        player.setShouldGlitch(true);
        // Glitch intensity is handled automatically
    }
    
    public static void applyStaticEffect(PlayerComponent player, 
                                        boolean enable) {
        player.setShouldDoStatic(enable);
    }
    
    public static void triggerScreenShake(PlayerEntity player, 
                                         float intensity, int duration) {
        // Send screen shake packet
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeFloat(intensity);
        buf.writeInt(duration);
        
        ServerPlayNetworking.send((ServerPlayerEntity) player,
                                 InitializePackets.SCREEN_SHAKE, buf);
    }
}
```

### Custom Render Layers

```java
public class MyRenderLayers extends RenderLayer {
    
    private static final RenderPhase.ShaderProgram MY_SHADER = 
        VeilRenderBridge.shaderState(new Identifier("my_mod", "my_shader"));
    
    private static final RenderLayer MY_LAYER = RenderLayer.of(
        "my_custom_layer",
        VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
        VertexFormat.DrawMode.QUADS,
        256,
        true,
        false,
        RenderLayer.MultiPhaseParameters.builder()
            .program(MY_SHADER)
            .texture(new RenderPhase.Texture(MY_TEXTURE, false, false))
            .transparency(RenderPhase.TRANSLUCENT_TRANSPARENCY)
            .lightmap(ENABLE_LIGHTMAP)
            .overlay(ENABLE_OVERLAY_COLOR)
            .build(true)
    );
    
    public static RenderLayer getMyLayer() {
        return MY_LAYER;
    }
}
```

## Lighting System

### Custom Light Sources

```java
// Create custom light-emitting blocks
public class MyLightBlock extends Block {
    
    @Override
    public int getLuminance(BlockState state) {
        return 15; // Light level 0-15
    }
    
    // For dynamic lighting
    public void updateLighting(World world, BlockPos pos, boolean isOn) {
        // Trigger light update
        world.updateNeighbors(pos, this);
        world.getLightingProvider().checkBlock(pos);
    }
}
```

### Shadow Mapping

```java
// Custom shadow rendering
public class MyShadowRenderer {
    
    public static void renderWithShadows(MatrixStack matrices, 
                                        VertexConsumerProvider provider) {
        if (ShadowMapRenderer.isRenderingShadowMap()) {
            // Rendering to shadow map
            renderShadowGeometry(matrices, provider);
        } else {
            // Normal rendering with shadows
            renderNormalGeometry(matrices, provider);
        }
    }
}
```

## Post-Processing Effects

### Custom Post-Processing

```java
public class MyPostProcessor {
    
    public static void applyCustomEffect(AdvancedFbo framebuffer) {
        ShaderProgram postShader = VeilRenderSystem.renderer()
            .getShaderManager().getShader(MY_POST_SHADER);
        
        if (postShader != null) {
            postShader.bind();
            postShader.setFloat("effectStrength", getEffectStrength());
            
            // Apply post-processing
            framebuffer.draw();
        }
    }
}
```

## Entity Rendering

### Custom Entity Renderers

```java
public class MyEntityRenderer extends LivingEntityRenderer<MyEntity, MyEntityModel> {
    
    @Override
    public void render(MyEntity entity, float yaw, float tickDelta,
                      MatrixStack matrices, VertexConsumerProvider provider, 
                      int light) {
        
        // Apply custom shader effects
        PlayerComponent nearestPlayer = getNearestPlayerComponent(entity);
        if (nearestPlayer != null && nearestPlayer.shouldGlitch()) {
            // Render with distortion effect
            RenderLayer layer = RenderLayers.getDistortedEntity(getTexture(entity));
            VertexConsumer buffer = provider.getBuffer(layer);
            
            // Custom rendering logic
            renderWithDistortion(entity, matrices, buffer, light);
        } else {
            // Normal rendering
            super.render(entity, yaw, tickDelta, matrices, provider, light);
        }
    }
}
```

## Performance Optimization

### LOD (Level of Detail) System

```java
public class RenderOptimization {
    
    public static boolean shouldRenderHighDetail(Entity entity, Camera camera) {
        double distance = entity.squaredDistanceTo(camera.getPos());
        return distance < 256.0; // 16 blocks
    }
    
    public static int getLODLevel(double distance) {
        if (distance < 64.0) return 0;      // High detail
        if (distance < 256.0) return 1;     // Medium detail
        return 2;                           // Low detail
    }
}
```

### Conditional Rendering

```java
public class ConditionalRendering {
    
    public static boolean shouldRenderEffect(PlayerComponent player) {
        // Skip expensive effects if not needed
        return player.isInBackrooms() && 
               ConfigStuff.enableVisualEffects &&
               !MinecraftClient.getInstance().isPaused();
    }
}
```

## Example: Complete Custom Effect

```java
public class CustomGlitchEffect {
    private static final Identifier GLITCH_SHADER = 
        new Identifier("my_mod", "custom_glitch");
    
    public static void renderGlitchEffect(PlayerComponent player, 
                                         MatrixStack matrices,
                                         VertexConsumerProvider provider) {
        if (!shouldRenderGlitch(player)) return;
        
        ShaderProgram shader = VeilRenderSystem.setShader(GLITCH_SHADER);
        if (shader == null) return;
        
        // Calculate glitch intensity
        float intensity = calculateGlitchIntensity(player);
        
        // Set shader uniforms
        shader.setFloat("glitchIntensity", intensity);
        shader.setFloat("time", RenderSystem.getShaderGameTime());
        shader.setVector("screenSize", getScreenSize());
        
        // Render fullscreen quad with glitch effect
        renderFullscreenQuad(matrices, provider);
    }
    
    private static boolean shouldRenderGlitch(PlayerComponent player) {
        return player.shouldGlitch() && 
               player.isInBackrooms() &&
               ConfigStuff.enableGlitchEffects;
    }
    
    private static float calculateGlitchIntensity(PlayerComponent player) {
        // Base intensity from player state
        float base = player.shouldInflictGlitchDamage() ? 0.8f : 0.3f;
        
        // Add time-based variation
        float time = RenderSystem.getShaderGameTime();
        float variation = (float) Math.sin(time * 10.0) * 0.2f;
        
        return Math.max(0.0f, Math.min(1.0f, base + variation));
    }
}
```

## Best Practices

1. **Check shader availability** before using custom shaders
2. **Use LOD systems** for performance-critical rendering
3. **Cache shader uniforms** when possible
4. **Respect user settings** for visual effects
5. **Test on different hardware** configurations
6. **Profile rendering performance** regularly
7. **Use appropriate render layers** for different effects
8. **Handle resource reloading** gracefully

## Troubleshooting

### Common Issues

1. **Shader not loading**: Check file paths and syntax
2. **PBR textures not appearing**: Verify texture resolution and format
3. **Performance issues**: Implement LOD and conditional rendering
4. **Z-fighting**: Adjust render order and depth testing
5. **Memory leaks**: Properly dispose of framebuffers and resources
