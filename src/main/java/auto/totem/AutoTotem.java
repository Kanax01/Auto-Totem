package auto.totem;

import auto.totem.config.TotemConfig;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.inventory.ContainerInput;

public class AutoTotem {

    private static final Minecraft mc = Minecraft.getInstance();
    private static long lastSwapTime = 0;
    private static final int MIN_SAFE_SWAP_DELAY_MS = 40;
    private static boolean gappleActive = false;
    private static int savedOffhandSlot = -1;
    private static int savedMainhandSlot = -1;
    private static final int OFFHAND_BUTTON = 40;

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.level == null) return;
            if (client.screen != null) return;
            tick(client.player);
        });
    }

    private static void tick(LocalPlayer player) {
        if (!TotemConfig.enabled) return;
        handleGappleBind(player);
        if (isGappleUseActive(player)) return;
        handleTotemReplace(player);
        handleDamagePredict(player);
    }

    // ================= TOTEM REPLACE =================

    private static void handleTotemReplace(LocalPlayer player) {
        if (!canSwap()) return;

        ItemStack mainhand = player.getMainHandItem();
        ItemStack offhand = player.getOffhandItem();

        // All modes: replace totem in mainhand
        if (mainhand.is(Items.TOTEM_OF_UNDYING)) {
            int slot = findTotemInInventory(player, true);
            if (slot != -1) {
                swapSlots(player, slot, player.getInventory().getSelectedSlot());
                lastSwapTime = System.currentTimeMillis();
                return;
            }
        }

        if (offhand.is(Items.TOTEM_OF_UNDYING)) return;

        int totemSlot = findTotemInInventory(player, false);
        if (totemSlot == -1) return;

        if (TotemConfig.mode == Menu.Mode.LEGIT) {
            handleLegitMode(player, totemSlot);
        } else {
            swapToOffhand(player, totemSlot);
            lastSwapTime = System.currentTimeMillis();
        }
    }

    private static void handleLegitMode(LocalPlayer player, int totemSlot) {
        if (!player.getOffhandItem().is(Items.TOTEM_OF_UNDYING)) {
            swapToOffhand(player, totemSlot);
            lastSwapTime = System.currentTimeMillis();
        }

        boolean shouldRefill = player.getHealth() <= TotemConfig.legitHealthThreshold;

        if (shouldRefill && findTotemInHotbar(player) == -1) {
            int inv = findTotemInInventory(player, false);
            if (inv != -1) {
                swapSlots(player, inv, findBestHotbarSlot(player));
                lastSwapTime = System.currentTimeMillis();
            }
        }
    }

    // ================= GAPPLE BIND =================

    private static void handleGappleBind(LocalPlayer player) {
        if (!TotemConfig.gappleBind && !TotemConfig.gappleBindMain) return;
        if (!canSwap()) return;

        boolean useKeyDown = mc.options.keyUse.isDown();
        ItemStack mainhand = player.getMainHandItem();
        boolean inDanger = isInDanger(player);

        if (useKeyDown && !inDanger) {
            if (TotemConfig.gappleBind && isGappleBindTrigger(mainhand) && !gappleActive) {
                int gappleSlot = findGappleInInventory(player);
                if (gappleSlot != -1) {
                    // After swap, the previous offhand item will be in the gapple's old slot.
                    savedOffhandSlot = gappleSlot;
                    swapToOffhand(player, gappleSlot);
                    gappleActive = true;
                    lastSwapTime = System.currentTimeMillis();
                }
            }

            if (TotemConfig.gappleBindMain && mainhand.is(Items.TOTEM_OF_UNDYING) && !gappleActive) {
                int gappleSlot = findGappleInInventory(player);
                if (gappleSlot != -1) {
                    // Remember where the displaced mainhand item ends up.
                    savedMainhandSlot = gappleSlot;
                    swapSlots(player, gappleSlot, player.getInventory().getSelectedSlot());
                    gappleActive = true;
                    lastSwapTime = System.currentTimeMillis();
                }
            }
        }

        if (gappleActive && (!useKeyDown || inDanger)) {
            if (savedOffhandSlot != -1) {
                swapToOffhand(player, savedOffhandSlot);
                savedOffhandSlot = -1;
            }
            if (savedMainhandSlot != -1) {
                swapSlots(player, savedMainhandSlot, player.getInventory().getSelectedSlot());
                savedMainhandSlot = -1;
            }
            gappleActive = false;
            lastSwapTime = System.currentTimeMillis();
        }
    }

    private static boolean isGappleBindTrigger(ItemStack stack) {
        return switch (TotemConfig.gappleBindTrigger) {
            case SWORD -> stack.is(ItemTags.SWORDS);
            case TOTEM -> stack.is(Items.TOTEM_OF_UNDYING);
            case CRYSTAL -> stack.is(Items.END_CRYSTAL);
            case PICKAXE -> stack.is(ItemTags.PICKAXES);
            case AXE -> stack.is(ItemTags.AXES);
        };
    }

    // ================= DAMAGE PREDICT =================

    private static void handleDamagePredict(LocalPlayer player) {
        if (!TotemConfig.damagePredict) return;
        if (!canSwap()) return;
        if (player.getOffhandItem().is(Items.TOTEM_OF_UNDYING)) return;

        if (isInDanger(player)) {
            int slot = findTotemInInventory(player, false);
            if (slot != -1) {
                swapToOffhand(player, slot);
                lastSwapTime = System.currentTimeMillis();
            }
        }
    }

    private static boolean isInDanger(LocalPlayer player) {
        float health = player.getHealth();
        float armor = player.getArmorValue();
        float blastProt = getBlastProtLevel(player);
        float reduction = Math.min((armor * 0.04f) + (blastProt * 0.04f), 0.8f);
        float effectiveHP = health / Math.max(1f - reduction, 0.2f);

        if (effectiveHP <= TotemConfig.damagePredictThreshold) return true;

        if (player.fallDistance > 3f) {
            float reducedFall = (float) ((player.fallDistance - 3f) * (1f - reduction));
            if (health - reducedFall <= TotemConfig.damagePredictThreshold) return true;
        }

        return isCrystalNearby(player);
    }

    private static boolean isCrystalNearby(LocalPlayer player) {
        AABB box = player.getBoundingBox().inflate(TotemConfig.crystalRange);
        return !player.level().getEntitiesOfClass(EndCrystal.class, box).isEmpty();
    }

    private static float getBlastProtLevel(LocalPlayer player) {
        float total = 0;
        for (EquipmentSlot slot : new EquipmentSlot[]{
                EquipmentSlot.HEAD, EquipmentSlot.CHEST,
                EquipmentSlot.LEGS, EquipmentSlot.FEET
        }) {
            ItemStack armor = player.getItemBySlot(slot);
            if (!armor.isEmpty()) {
                ItemEnchantments enchants = armor.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
                total += enchants.getLevel(
                        player.level().registryAccess()
                                .lookupOrThrow(Registries.ENCHANTMENT)
                                .getOrThrow(Enchantments.BLAST_PROTECTION)
                );
            }
        }
        return total;
    }

    // ================= SWAP METHODS =================

    private static void swapToOffhand(LocalPlayer player, int inventorySlot) {
        regularSwapToOffhand(player, inventorySlot);
    }

    // Regular mode: directly manipulate inventory items client-side
    private static void regularSwapToOffhand(LocalPlayer player, int inventorySlot) {
        sendSwapPacket(player, inventorySlot, OFFHAND_BUTTON);
    }

    private static void swapSlots(LocalPlayer player, int fromSlot, int toSlot) {
        sendSwapPacket(player, fromSlot, toSlot);
    }

    private static void sendSwapPacket(LocalPlayer player, int slot, int button) {
        if (mc.gameMode == null) return;
        if (slot < 0 || slot >= player.getInventory().getContainerSize()) return;
        if (button < 0 || (button > 8 && button != OFFHAND_BUTTON)) return;
        int screenSlot = toScreenSlotId(slot);
        mc.gameMode.handleContainerInput(
                player.inventoryMenu.containerId,
                screenSlot,
                button,
                ContainerInput.SWAP,
                player
        );
    }

    private static int toScreenSlotId(int inventoryIndex) {
        return (inventoryIndex >= 0 && inventoryIndex <= 8) ? 36 + inventoryIndex : inventoryIndex;
    }

    // ================= UTILITY =================

    private static boolean canSwap() {
        int minDelay = Math.max(TotemConfig.swapDelay, MIN_SAFE_SWAP_DELAY_MS);
        return System.currentTimeMillis() - lastSwapTime >= minDelay;
    }

    private static boolean isGappleUseActive(LocalPlayer player) {
        if (!gappleActive) return false;
        if (!mc.options.keyUse.isDown()) return false;
        return !isInDanger(player);
    }

    private static int findTotemInInventory(LocalPlayer player, boolean skipMainhand) {
        int mainhandSlot = player.getInventory().getSelectedSlot();
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            if (skipMainhand && i == mainhandSlot) continue;
            if (player.getInventory().getItem(i).is(Items.TOTEM_OF_UNDYING)) return i;
        }
        return -1;
    }

    private static int findTotemInHotbar(LocalPlayer player) {
        for (int i = 0; i < 9; i++) {
            if (player.getInventory().getItem(i).is(Items.TOTEM_OF_UNDYING)) return i;
        }
        return -1;
    }

    private static int findGappleInInventory(LocalPlayer player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            if (player.getInventory().getItem(i).is(Items.ENCHANTED_GOLDEN_APPLE)) return i;
        }
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            if (player.getInventory().getItem(i).is(Items.GOLDEN_APPLE)) return i;
        }
        return -1;
    }

    private static int findBestHotbarSlot(LocalPlayer player) {
        for (int i = 0; i < 9; i++) {
            if (player.getInventory().getItem(i).isEmpty()) return i;
        }
        return 8;
    }

}
