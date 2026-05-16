package auto.totem;

import auto.totem.config.TotemConfig;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AutoTotemMain implements ModInitializer {
	public static final String MOD_ID = "auto-totem";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		LOGGER.info("Auto Totem Loaded...");
		TotemConfig.load();

	}

}