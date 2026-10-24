package com.gtnewhorizon.newgunrizons.registry;

import java.lang.reflect.Method;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;

/**
 * 9x9 recipes in Avaritia's Extreme Crafting Table: the weapon chassis (UV-MAX) and the UEV-MAX technology modules.
 * These components can only be made there; the Munitions Factory then assembles them into weapons. Avaritia is
 * reached through reflection so it stays an optional dependency: without it (or if its API changes) nothing is
 * registered here and the factory makes the components instead, so the mod remains playable.
 */
final class ExtremeRecipes {

    private static final Logger LOG = LogManager.getLogger(ExtremeRecipes.class);
    private static final String MOD = "Avaritia";
    private static final String MANAGER = "fox.spiteful.avaritia.crafting.ExtremeCraftingManager";

    /** Avaritia "Resource" metadata. */
    static final int DIAMOND_LATTICE = 0, CRYSTAL_MATRIX_INGOT = 1, NEUTRONIUM_INGOT = 4, INFINITY_CATALYST = 5,
        INFINITY_INGOT = 6, NEUTRONIUM_GEAR = 9;

    /**
     * Technology module: P plates, C circuits, F field generators, E emitters, S sensors, M the previous module, K the
     * module's key ingredient, X its superconductor or exotic plate.
     */
    private static final String[] MODULE_PATTERN = { "C  PPP  C", "  F X F  ", " P E S P ", "P   M   P", "PX  K  XP",
        "P   M   P", " P S E P ", "  F X F  ", "C  PPP  C" };

    /**
     * Weapon chassis: O tier plates around the edge, C circuits, F field generators, G secondary metal, I Avaritia
     * ingots around the K core.
     */
    private static final String[] CHASSIS_PATTERN = { "OOO   OOO", "O  CFC  O", "O G   G O", " G  I  G ", " F IKI F ",
        " G  I  G ", "O G   G O", "O  CFC  O", "OOO   OOO" };

    private static Object manager;
    private static Method addShaped;
    private static boolean looked;

    private ExtremeRecipes() {}

    /** Whether Avaritia's Extreme Crafting Table can take recipes. */
    static boolean available() {
        if (looked) return addShaped != null;
        looked = true;
        if (!Loader.isModLoaded(MOD)) return false;
        try {
            Class<?> cls = Class.forName(MANAGER);
            manager = cls.getMethod("getInstance")
                .invoke(null);
            try {
                addShaped = cls.getMethod("addExtremeShapedOreRecipe", ItemStack.class, Object[].class);
            } catch (NoSuchMethodException e) {
                addShaped = cls.getMethod("addRecipe", ItemStack.class, Object[].class);
            }
        } catch (Throwable t) {
            LOG.warn("Avaritia found but its Extreme Crafting API is not usable: {}", t.toString());
            addShaped = null;
        }
        return addShaped != null;
    }

    /** One of Avaritia's resources (ingots, catalyst...), or null without Avaritia. */
    static ItemStack resource(int meta, int count) {
        if (!Loader.isModLoaded(MOD)) return null;
        Item item = GameRegistry.findItem(MOD, "Resource");
        return item == null ? null : new ItemStack(item, count, meta);
    }

    /** An Avaritia resource block (0 neutronium, 1 infinity), or null. */
    static ItemStack resourceBlock(int meta, int count) {
        if (!Loader.isModLoaded(MOD)) return null;
        Item item = Item.getItemFromBlock(GameRegistry.findBlock(MOD, "Resource_Block"));
        return item == null ? null : new ItemStack(item, count, meta);
    }

    static boolean addModule(HighTierRecipes.EndgameModule m) {
        return add(
            new ItemStack(m.output),
            MODULE_PATTERN,
            'P',
            m.plate,
            'C',
            m.circuit,
            'F',
            m.field,
            'E',
            m.emitter,
            'S',
            m.sensor,
            'M',
            m.previous,
            'K',
            m.key,
            'X',
            m.extra);
    }

    static boolean addChassis(Item output, ItemStack edge, ItemStack circuit, ItemStack field, ItemStack secondary,
        ItemStack ingot, ItemStack core) {
        return add(
            new ItemStack(output),
            CHASSIS_PATTERN,
            'O',
            edge,
            'C',
            circuit,
            'F',
            field,
            'G',
            secondary,
            'I',
            ingot,
            'K',
            core);
    }

    /** Registers a 9x9 shaped recipe; keys alternate character and ingredient. False if it could not be added. */
    private static boolean add(ItemStack output, String[] pattern, Object... keys) {
        if (!available()) return false;
        Object[] recipe = new Object[pattern.length + keys.length];
        System.arraycopy(pattern, 0, recipe, 0, pattern.length);
        for (int i = 0; i < keys.length; i++) {
            Object k = keys[i];
            if (k instanceof ItemStack) {
                ItemStack c = ((ItemStack) k).copy();
                c.stackSize = 1;
                k = c;
            } else if (k == null) {
                return false;
            }
            recipe[pattern.length + i] = k;
        }
        try {
            addShaped.invoke(manager, output, recipe);
            return true;
        } catch (Throwable t) {
            LOG.warn("Could not add Extreme Crafting recipe for {}: {}", output, t.toString());
            return false;
        }
    }
}
