package auto.totem.mixin;

import auto.totem.stuff.NoTotemParticles;
import auto.totem.config.TotemConfig;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ParticleEngine.class)
public class TotemParticlesMixin {
	@Inject(method = "createTrackingEmitter", at = @At("HEAD"), cancellable = true)
	private void onAddParticle(net.minecraft.world.entity.Entity entity, ParticleOptions options, CallbackInfo ci) {
		if (!TotemConfig.enabled) return;
		if (!NoTotemParticles.enabled()) return;
		if (options.getType() == ParticleTypes.TOTEM_OF_UNDYING) {
			ci.cancel();
		}
	}
}
