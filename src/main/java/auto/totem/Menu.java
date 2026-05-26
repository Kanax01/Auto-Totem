package auto.totem;

import auto.totem.config.TotemConfig;
import auto.totem.stuff.NoTotemAnimation;
import auto.totem.stuff.NoTotemParticles;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.network.chat.Component;

public class Menu implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Component.literal("Auto Totem"));
            builder.setSavingRunnable(TotemConfig::save);

            ConfigEntryBuilder entryBuilder = builder.entryBuilder();

            ConfigCategory general = builder.getOrCreateCategory(Component.literal("General"));

            general.addEntry(entryBuilder
                    .startBooleanToggle(Component.literal("Enabled"), TotemConfig.enabled)
                    .setDefaultValue(true)
                    .setTooltip(Component.literal("Master toggle for all Auto Totem features"))
                    .setSaveConsumer(val -> TotemConfig.enabled = val)
                    .build()
            );

            general.addEntry(entryBuilder
                    .startEnumSelector(Component.literal("Mode"), Mode.class, TotemConfig.mode)
                    .setDefaultValue(Mode.REGULAR)
                    .setTooltip(Component.literal("Regular: replaces offhand totem only\nLegit: swaps from hotbar when low and replenishes"))
                    .setSaveConsumer(val -> TotemConfig.mode = val)
                    .build()
            );

            general.addEntry(entryBuilder
                    .startIntSlider(Component.literal("Legit Health Threshold"), (int) TotemConfig.legitHealthThreshold, 1, 20)
                    .setDefaultValue(10)
                    .setTooltip(Component.literal("In Legit mode, keep hotbar totems stocked at or below this health"))
                    .setSaveConsumer(val -> TotemConfig.legitHealthThreshold = val)
                    .build()
            );

            general.addEntry(entryBuilder
                    .startEnumSelector(Component.literal("AC Mode"), ACMode.class, TotemConfig.acMode)
                    .setDefaultValue(ACMode.NONE)
                    .setTooltip(Component.literal("None: fastest swaps, for vanilla/anarchy\nNCP: moderate randomized delay\nGrim: stricter randomized delay"))
                    .setSaveConsumer(val -> TotemConfig.acMode = val)
                    .build()
            );

            general.addEntry(entryBuilder
                    .startIntSlider(Component.literal("Swap Delay (ms)"), TotemConfig.swapDelay, 0, 500)
                    .setDefaultValue(15)
                    .setTooltip(Component.literal("Base delay between swaps. AC Mode sets a minimum floor above this and adds jitter."))
                    .setSaveConsumer(val -> TotemConfig.swapDelay = val)
                    .build()
            );

            ConfigCategory utils = builder.getOrCreateCategory(Component.literal("Utils"));

            utils.addEntry(entryBuilder
                    .startBooleanToggle(Component.literal("Damage Predict"), TotemConfig.damagePredict)
                    .setDefaultValue(false)
                    .setTooltip(Component.literal("Equips totem in dangerous situations"))
                    .setSaveConsumer(val -> TotemConfig.damagePredict = val)
                    .build()
            );

            utils.addEntry(entryBuilder
                    .startIntSlider(Component.literal("Danger Threshold"), (int) TotemConfig.damagePredictThreshold, 1, 20)
                    .setDefaultValue(10)
                    .setTooltip(Component.literal("Effective health threshold for danger checks"))
                    .setSaveConsumer(val -> TotemConfig.damagePredictThreshold = val)
                    .build()
            );

            utils.addEntry(entryBuilder
                    .startBooleanToggle(Component.literal("Gapple Bind"), TotemConfig.gappleBind)
                    .setDefaultValue(false)
                    .setTooltip(Component.literal("Swaps offhand with gapple when holding selected item"))
                    .setSaveConsumer(val -> TotemConfig.gappleBind = val)
                    .build()
            );

            utils.addEntry(entryBuilder
                    .startBooleanToggle(Component.literal("Gapple Bind Main"), TotemConfig.gappleBindMain)
                    .setDefaultValue(false)
                    .setTooltip(Component.literal("Swaps main hand item with gapple for quick use"))
                    .setSaveConsumer(val -> TotemConfig.gappleBindMain = val)
                    .build()
            );

            utils.addEntry(entryBuilder
                    .startEnumSelector(Component.literal("Gapple Trigger"), GappleBindTrigger.class, TotemConfig.gappleBindTrigger)
                    .setDefaultValue(GappleBindTrigger.SWORD)
                    .setTooltip(Component.literal("Mainhand item type that activates offhand gapple bind"))
                    .setSaveConsumer(val -> TotemConfig.gappleBindTrigger = val)
                    .build()
            );

            ConfigCategory misc = builder.getOrCreateCategory(Component.literal("Misc"));

            misc.addEntry(entryBuilder
                    .startBooleanToggle(Component.literal("Hide Totem Animation"), TotemConfig.hideTotemAnimation)
                    .setDefaultValue(false)
                    .setTooltip(Component.literal("Hides the totem pop animation"))
                    .setSaveConsumer(val -> {
                        TotemConfig.hideTotemAnimation = val;
                        NoTotemAnimation.AnimationToggled = val;
                    })
                    .build()
            );

            misc.addEntry(entryBuilder
                    .startBooleanToggle(Component.literal("Hide Totem Particles"), TotemConfig.hideTotemParticles)
                    .setDefaultValue(false)
                    .setTooltip(Component.literal("Hides the totem pop particles"))
                    .setSaveConsumer(val -> {
                        TotemConfig.hideTotemParticles = val;
                        NoTotemParticles.ParticlesToggled = val;
                    })
                    .build()
            );

            return builder.build();
        };
    }

    public enum Mode {
        REGULAR, LEGIT;

        @Override
        public String toString() {
            return name().charAt(0) + name().substring(1).toLowerCase();
        }
    }

    public enum GappleBindTrigger {
        SWORD, TOTEM, CRYSTAL, PICKAXE, AXE;

        @Override
        public String toString() {
            return name().charAt(0) + name().substring(1).toLowerCase();
        }
    }

    public enum ACMode {
        NONE, NCP, GRIM;

        @Override
        public String toString() {
            return switch (this) {
                case NONE -> "None";
                case NCP  -> "NCP";
                case GRIM -> "Grim";
            };
        }
    }
}