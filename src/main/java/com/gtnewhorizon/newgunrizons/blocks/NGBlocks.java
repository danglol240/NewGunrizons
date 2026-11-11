package com.gtnewhorizon.newgunrizons.blocks;

import cpw.mods.fml.common.registry.GameRegistry;

/** Structure blocks of the NewGunrizons multiblocks. */
public final class NGBlocks {

    /** Tier frames: roofs of the Munitions Factory and storeys of the Drone Assembly Complex. */
    public static BlockTiered TierFrame;
    /** Landing pads: the rings of the Drone Assembly Complex. */
    public static BlockTiered LandingPad;

    private NGBlocks() {}

    public static void init() {
        TierFrame = new BlockTiered("TierFrame", "frame", false);
        GameRegistry.registerBlock(TierFrame, ItemBlockTiered.class, "TierFrame");
        LandingPad = new BlockTiered("LandingPad", "pad", true);
        GameRegistry.registerBlock(LandingPad, ItemBlockTiered.class, "LandingPad");
    }
}
