package auto.totem.config;

import auto.totem.Menu;
import auto.totem.stuff.NoTotemAnimation;
import auto.totem.stuff.NoTotemParticles;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class TotemConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getGameDir().toFile(), "AutoTotem/config.json");

    public static boolean enabled = true;
    public static Menu.Mode mode = Menu.Mode.REGULAR;
    public static Menu.ACMode acMode = Menu.ACMode.NONE;
    public static int swapDelay = 15;
    public static boolean damagePredict = true;
    public static boolean gappleBind = false;
    public static boolean gappleBindMain = false;
    public static boolean hideTotemAnimation = false;
    public static boolean hideTotemParticles = false;
    public static float legitHealthThreshold = 10f;
    public static boolean legitSwapOnPop = true;
    public static float damagePredictThreshold = 10f;
    public static double crystalRange = 4.0;
    public static Menu.GappleBindTrigger gappleBindTrigger = Menu.GappleBindTrigger.SWORD;

    public static void load() {
        if (!CONFIG_FILE.exists()) {
            save();
            return;
        }
        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            ConfigData data = GSON.fromJson(reader, ConfigData.class);
            if (data == null) return;
            enabled = data.enabled != null ? data.enabled : true;
            mode = data.mode != null ? data.mode : Menu.Mode.REGULAR;
            acMode = data.acMode != null ? data.acMode : Menu.ACMode.NONE;
            swapDelay = data.swapDelay != null ? data.swapDelay : 15;
            damagePredict = data.damagePredict != null ? data.damagePredict : true;
            gappleBind = data.gappleBind != null ? data.gappleBind : false;
            gappleBindMain = data.gappleBindMain != null ? data.gappleBindMain : false;
            hideTotemAnimation = data.hideTotemAnimation != null ? data.hideTotemAnimation : false;
            hideTotemParticles = data.hideTotemParticles != null ? data.hideTotemParticles : false;
            legitHealthThreshold = data.legitHealthThreshold != null ? data.legitHealthThreshold : 10f;
            legitSwapOnPop = data.legitSwapOnPop != null ? data.legitSwapOnPop : true;
            damagePredictThreshold = data.damagePredictThreshold != null ? data.damagePredictThreshold : 10f;
            crystalRange = data.crystalRange != null ? data.crystalRange : 4.0;
            gappleBindTrigger = data.gappleBindTrigger != null ? data.gappleBindTrigger : Menu.GappleBindTrigger.SWORD;
            NoTotemAnimation.AnimationToggled = hideTotemAnimation;
            NoTotemParticles.ParticlesToggled = hideTotemParticles;
        } catch (IOException e) {
            System.err.println("[AutoTotem] Failed to load config: " + e.getMessage());
        }
    }

    public static void save() {
        try {
            CONFIG_FILE.getParentFile().mkdirs();
            try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
                GSON.toJson(new ConfigData(), writer);
            }
        } catch (IOException e) {
            System.err.println("[AutoTotem] Failed to save config: " + e.getMessage());
        }
    }

    private static class ConfigData {
        Boolean enabled = TotemConfig.enabled;
        Menu.Mode mode = TotemConfig.mode;
        Menu.ACMode acMode = TotemConfig.acMode;
        Integer swapDelay = TotemConfig.swapDelay;
        Boolean damagePredict = TotemConfig.damagePredict;
        Boolean gappleBind = TotemConfig.gappleBind;
        Boolean gappleBindMain = TotemConfig.gappleBindMain;
        Boolean hideTotemAnimation = TotemConfig.hideTotemAnimation;
        Boolean hideTotemParticles = TotemConfig.hideTotemParticles;
        Float legitHealthThreshold = TotemConfig.legitHealthThreshold;
        Boolean legitSwapOnPop = TotemConfig.legitSwapOnPop;
        Float damagePredictThreshold = TotemConfig.damagePredictThreshold;
        Double crystalRange = TotemConfig.crystalRange;
        Menu.GappleBindTrigger gappleBindTrigger = TotemConfig.gappleBindTrigger;
    }
}