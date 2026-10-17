package com.gtnewhorizon.newgunrizons.machines;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.onElementPass;
import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.InputHatch;
import static gregtech.api.enums.HatchElement.Maintenance;
import static gregtech.api.enums.HatchElement.OutputBus;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.newgunrizons.registry.MunitionsRecipeMap;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;

import gregtech.api.casing.Casings;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.ICasingTextureProvider;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEExtendedPowerMultiBlockBase;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;

/**
 * Munitions Factory: the only machine that makes NewGunrizons weapons, components, magazines and ammunition.
 * <p>
 * 5 wide, 3 high, 3 deep box of Solid Steel Machine Casings around a row of Steel Gear Box Casings, buildable from LV.
 * Runs 4 parallels per voltage tier of its energy input at double speed.
 */
public class MTEMunitionsFactory extends MTEExtendedPowerMultiBlockBase<MTEMunitionsFactory>
    implements ISurvivalConstructable, ICasingTextureProvider {

    private static final String MAIN = "main";
    private static final int OFFSET_X = 2;
    private static final int OFFSET_Y = 1;
    private static final int OFFSET_Z = 0;
    private static final int MIN_CASINGS = 22;
    private static final int PARALLEL_PER_TIER = 4;

    private int casingAmount;

    private static final IStructureDefinition<MTEMunitionsFactory> STRUCTURE = StructureDefinition
        .<MTEMunitionsFactory>builder()
        .addShape(
            MAIN,
            // layers front to back; rows top to bottom; '~' is the controller
            new String[][] { { "CCCCC", "CC~CC", "CCCCC" }, { "CCCCC", "CGGGC", "CCCCC" },
                { "CCCCC", "CCCCC", "CCCCC" } })
        .addElement('G', Casings.SteelGearBoxCasing.asElement())
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
            .addBulkMachineInfo(PARALLEL_PER_TIER, 2f, 1f)
            .beginStructureBlock(5, 3, 3, false)
            .addController("Front center, middle layer")
            .addCasingInfoMin("Solid Steel Machine Casing", MIN_CASINGS, false)
            .addCasing("3", "Steel Gear Box Casing", false)
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
        buildPiece(MAIN, stackSize, hintsOnly, OFFSET_X, OFFSET_Y, OFFSET_Z);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (mMachine) return -1;
        return survivalBuildPiece(MAIN, stackSize, OFFSET_X, OFFSET_Y, OFFSET_Z, elementBudget, env, false, true);
    }

    @Override
    public void checkMachine(IGregTechTileEntity baseMetaTileEntity, ItemStack stack, List<StructureError> errors) {
        casingAmount = 0;
        if (!checkPiece(MAIN, OFFSET_X, OFFSET_Y, OFFSET_Z, errors)) return;
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
        return new ProcessingLogic().setSpeedBonus(1.0D / 2.0D)
            .setMaxParallelSupplier(this::getTrueParallel);
    }

    @Override
    public int getMaxParallelRecipes() {
        return PARALLEL_PER_TIER * Math.max(1, GTUtility.getTier(this.getMaxInputVoltage()));
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
