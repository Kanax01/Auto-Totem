package auto.totem;

import auto.totem.config.TotemConfig;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public class AutoTotemClient implements ClientModInitializer {

    private static KeyMapping toggleModKey;

    @Override
    public void onInitializeClient() {
        AutoTotem.init();

        toggleModKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.auto-totem.toggle_mod",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_H,
                KeyMapping.Category.MISC
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleModKey.consumeClick()) {
                TotemConfig.enabled = !TotemConfig.enabled;
                TotemConfig.save();
                if (client.player != null) {
                    client.player.sendSystemMessage(
                            Component.literal("Auto Totem " + (TotemConfig.enabled ? "enabled" : "disabled"))
                    );
                }
            }
        });
    }
}
