package com.gtnewhorizon.newgunrizons.machines;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlocksTiered;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.onElementPass;
import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.InputHatch;
import static gregtech.api.enums.HatchElement.Maintenance;
import static gregtech.api.enums.HatchElement.OutputBus;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.commons.lang3.tuple.Pair;

import com.gtnewhorizon.newgunrizons.registry.MunitionsRecipeMap;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;

import gregtech.api.GregTechAPI;
import gregtech.api.casing.Casings;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.ICasingTextureProvider;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEExtendedPowerMultiBlockBase;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.misc.GTStructureChannels;

/**
 * Munitions Factory: the only machine that makes NewGunrizons weapons, components, magazines and ammunition.
 * <p>
 * The factory grows with the player's progression:
 * <ul>
 * <li>a 5x3 head with the controller between two Steel Gear Box Casings;</li>
 * <li>1-7 production line segments behind it, each a 5x3 ring of Solid Steel Machine Casings around three tiered
 * Machine Casings (LV..UHV);</li>
 * <li>the Machine Casing tier (all segments must match) is the factory's era: it caps the voltage tier of the recipes
 * the factory accepts;</li>
 * <li>every segment adds 4 parallels.</li>
 * </ul>
 */
public class MTEMunitionsFactory extends MTEExtendedPowerMultiBlockBase<MTEMunitionsFactory>
    implements ISurvivalConstructable, ICasingTextureProvider {

    private static final String HEAD = "head";
    private static final String SEGMENT = "segment";
    private static final int OFFSET_X = 2;
    private static final int OFFSET_Y = 1;
    private static final int MAX_SEGMENTS = 7;
    private static final int PARALLEL_PER_SEGMENT = 4;
    private static final int MIN_CASINGS = 16;
    /** Machine Casing meta in sBlockCasings1 equals its voltage tier: 1 = LV ... 9 = UHV. */
    private static final int MIN_TIER = 1;
    private static final int MAX_TIER = 9;
    private static final int TIER_NOT_SET = -1;

    private int casingAmount;
    private int casingTier = TIER_NOT_SET;
    private int segments;

    private static final IStructureDefinition<MTEMunitionsFactory> STRUCTURE = StructureDefinition
        .<MTEMunitionsFactory>builder()
        .addShape(HEAD, new String[][] { { "CCCCC", "CG~GC", "CCCCC" } })
        .addShape(SEGMENT, new String[][] { { "CCCCC", "CTTTC", "CCCCC" } })
        .addElement('G', Casings.SteelGearBoxCasing.asElement())
        .addElement(
            'T',
            GTStructureChannels.TIER_MACHINE_CASING.use(
                ofBlocksTiered(
                    (block, meta) -> block == GregTechAPI.sBlockCasings1 && meta >= MIN_TIER && meta <= MAX_TIER ? meta
                        : null,
                    IntStream.rangeClosed(MIN_TIER, MAX_TIER)
                        .mapToObj(i -> Pair.of(GregTechAPI.sBlockCasings1, i))
                        .collect(Collectors.toList()),
                    TIER_NOT_SET,
                    (MTEMunitionsFactory m, Integer tier) -> m.casingTier = tier,
                    (MTEMunitionsFactory m) -> m.casingTier)))
        .addElement(
            'C',
            buildHatchAdder(MTEMunitionsFactory.class).atLeast(InputBus, InputHatch, OutputBus, Maintenance, Energy)
                .casingIndex(Casings.SolidSteelMachineCasing.textureId)
                .hint(1)
                .buildAndChain(onElementPass(m -> ++m.casingAmount, Casings.SolidSteelMachineCasing.asElement())))
        .build();

    public MTEMunitionsFactory(int id, String name, String nameRegional) {
        super(id, name, nameRegional);
    }

    public MTEMunitionsFactory(String name) {
        super(name);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity tileEntity) {
        return new MTEMunitionsFactory(this.mName);
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType("Munitions Factory")
            .addInfo("Manufactures every NewGunrizons weapon, part, magazine and round")
            .addInfo("Weapons and ammo cannot be made anywhere else")
            .addSeparator()
            .addInfo("Grows with your progression:")
            .addInfo("Add production line segments behind the head, up to " + MAX_SEGMENTS)
            .addInfo("Each segment adds " + PARALLEL_PER_SEGMENT + " parallels")
            .addInfo("The Machine Casings in the segments set the factory's era:")
            .addInfo("recipes above that voltage tier are refused (LV casings = LV era ... UHV casings = UHV era)")
            .addInfo("All segments must use the same Machine Casing tier")
            .addInfo("Runs recipes at double speed")
            .beginVariableStructureBlock(5, 5, 3, 3, 2, 1 + MAX_SEGMENTS, false)
            .addController("Front center, between the two Steel Gear Box Casings")
            .addCasingInfoMin("Solid Steel Machine Casing", MIN_CASINGS, false)
            .addCasing("2", "Steel Gear Box Casing", false)
            .addCasing("3 per segment", "Machine Casing (LV-UHV, sets the era)", true)
            .addEnergyHatch("1+", "Any Solid Steel Machine Casing", 1)
            .addMaintenanceHatch("1", "Any Solid Steel Machine Casing", 1)
            .addInputBus("1+", "Any Solid Steel Machine Casing", 1)
            .addInputHatch("0+", "Any Solid Steel Machine Casing", 1)
            .addOutputBus("1+", "Any Solid Steel Machine Casing", 1)
            .addStructureAuthors(EnumChatFormatting.GOLD + "NewGunrizons")
            .toolTipFinisher();
        return tt;
    }

    @Override
    public IStructureDefinition<MTEMunitionsFactory> getStructureDefinition() {
        return STRUCTURE;
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        buildPiece(HEAD, stackSize, hintsOnly, OFFSET_X, OFFSET_Y, 0);
        int count = GTStructureChannels.STRUCTURE_LENGTH.getValueClamped(stackSize, 1, MAX_SEGMENTS);
        for (int i = 1; i <= count; i++) {
            buildPiece(SEGMENT, stackSize, hintsOnly, OFFSET_X, OFFSET_Y, -i);
        }
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (mMachine) return -1;
        int built = survivalBuildPiece(HEAD, stackSize, OFFSET_X, OFFSET_Y, 0, elementBudget, env, false, true);
        if (built >= 0) return built;
        int count = GTStructureChannels.STRUCTURE_LENGTH.getValueClamped(stackSize, 1, MAX_SEGMENTS);
        for (int i = 1; i <= count; i++) {
            built = survivalBuildPiece(SEGMENT, stackSize, OFFSET_X, OFFSET_Y, -i, elementBudget, env, false, true);
            if (built >= 0) return built;
        }
        return -1;
    }

    @Override
    public void checkMachine(IGregTechTileEntity baseMetaTileEntity, ItemStack stack, List<StructureError> errors) {
        casingAmount = 0;
        casingTier = TIER_NOT_SET;
        segments = 0;
        if (!checkPiece(HEAD, OFFSET_X, OFFSET_Y, 0, errors)) return;

        // Count segments until the first one that is missing or incomplete.
        List<StructureError> segmentErrors = new ArrayList<>();
        for (int i = 1; i <= MAX_SEGMENTS; i++) {
            segmentErrors.clear();
            if (!checkPiece(SEGMENT, OFFSET_X, OFFSET_Y, -i, segmentErrors)) break;
            segments = i;
        }
        if (segments == 0) {
            // Report why the first segment is not recognised.
            errors.addAll(segmentErrors);
            return;
        }
        checkCasingMin(errors, casingAmount, MIN_CASINGS);
        checkHasEnergyHatch(errors);
        checkHasMaintenanceHatch(errors);
        checkHasInputBus(errors);
        checkHasOutputBus(errors);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity baseMetaTileEntity, ForgeDirection side, ForgeDirection facing,
        int colorIndex, boolean active, boolean redstoneLevel) {
        return Textures.BlockIcons.createTextureWithCasing(
            this,
            side,
            facing,
            active,
            Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE,
            Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE_GLOW,
            Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE,
            Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE_GLOW);
    }

    @Override
    public ITexture getCasingTexture() {
        return Casings.SolidSteelMachineCasing.getCasingTexture();
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return MunitionsRecipeMap.MUNITIONS;
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic() {

            @Nonnull
            @Override
            protected CheckRecipeResult validateRecipe(@Nonnull GTRecipe recipe) {
                int recipeTier = GTUtility.getTier(recipe.mEUt);
                if (recipeTier > casingTier) {
                    return CheckRecipeResultRegistry.insufficientMachineTier(recipeTier);
                }
                return CheckRecipeResultRegistry.SUCCESSFUL;
            }
        }.setSpeedBonus(1.0D / 2.0D)
            .setMaxParallelSupplier(this::getTrueParallel);
    }

    @Override
    public int getMaxParallelRecipes() {
        return PARALLEL_PER_SEGMENT * Math.max(1, segments);
    }

    @Override
    public String[] getInfoData() {
        String[] base = super.getInfoData();
        String[] info = new String[base.length + 2];
        System.arraycopy(base, 0, info, 0, base.length);
        String era = casingTier >= MIN_TIER ? GTValues.VN[casingTier] : "none";
        info[base.length] = "Era: " + EnumChatFormatting.YELLOW + era + EnumChatFormatting.RESET;
        info[base.length + 1] = "Segments: " + EnumChatFormatting.YELLOW
            + segments
            + EnumChatFormatting.RESET
            + " ("
            + getMaxParallelRecipes()
            + " parallels)";
        return info;
    }

    @Override
    public void saveNBTData(NBTTagCompound nbt) {
        super.saveNBTData(nbt);
        nbt.setInteger("casingTier", casingTier);
        nbt.setInteger("segments", segments);
    }

    @Override
    public void loadNBTData(NBTTagCompound nbt) {
        super.loadNBTData(nbt);
        casingTier = nbt.getInteger("casingTier");
        segments = nbt.getInteger("segments");
    }

    @Override
    public boolean supportsInputSeparation() {
        return true;
    }

    @Override
    public boolean supportsVoidProtection() {
        return true;
    }

    @Override
    public boolean supportsBatchMode() {
        return true;
    }
}
