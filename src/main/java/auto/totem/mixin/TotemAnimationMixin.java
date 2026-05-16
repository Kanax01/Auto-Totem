package auto.totem.mixin;

import auto.totem.stuff.NoTotemAnimation;
import auto.totem.config.TotemConfig;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class TotemAnimationMixin {
    @Inject(method = "displayItemActivation", at = @At("HEAD"), cancellable = true)
    private void onDisplayItemActivation(ItemStack stack, CallbackInfo ci) {
        if (!TotemConfig.enabled) return;
        if (!NoTotemAnimation.enabled()) return;
        if (stack.is(Items.TOTEM_OF_UNDYING)) {
            ci.cancel();
        }
    }
}
