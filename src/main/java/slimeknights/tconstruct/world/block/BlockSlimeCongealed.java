package slimeknights.tconstruct.world.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.world.block.BlockSlime.SlimeType;

public class BlockSlimeCongealed extends Block {

  private static final AxisAlignedBB AABB = new AxisAlignedBB(0, 0, 0, 1, 0.625D, 1.0D);

  public BlockSlimeCongealed() {
    super(Material.SPONGE);
    this.setCreativeTab(TinkerRegistry.tabWorld);
    this.setHardness(0.5f);
    this.slipperiness = 0.5f;
    this.disableStats();
    this.setSoundType(SoundType.SLIME);
  }


  @SideOnly(Side.CLIENT)
  @Override
  public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
    for(SlimeType type : SlimeType.values()) {
      list.add(new ItemStack(this, 1, type.meta));
    }
  }

  @Override
  protected BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, BlockSlime.TYPE);
  }

  @Override
  public IBlockState getStateFromMeta(int meta) {
    return this.getDefaultState().withProperty(BlockSlime.TYPE, SlimeType.fromMeta(meta));
  }

  @Override
  public int getMetaFromState(IBlockState state) {
    return state.getValue(BlockSlime.TYPE).meta;
  }

  @Override
  public int damageDropped(IBlockState state) {
    return getMetaFromState(state);
  }

  @Override
  public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, World worldIn, BlockPos pos) {
    return AABB;
  }

  @Override
  public void onLanded(World world, Entity entity) {
    if(!(entity instanceof EntityLivingBase) && !(entity instanceof EntityItem)) {
      // this is mostly needed to prevent XP orbs from bouncing. which completely breaks the game.
      return;
    }
    if (entity.motionY < 0)
    {
      entity.motionY *= -1.2F;
      if(entity instanceof EntityItem) {
        entity.onGround = false;
      }
    }
  }

  @Override
  public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance) {
    // no fall damage on congealed slime
    entityIn.fall(fallDistance, 0.0F);
  }

  /* Log behaviour for slimetrees */

  @Override
  public boolean canSustainLeaves(IBlockState state, IBlockAccess world, BlockPos pos) {
    return true;
  }

  // this causes leaves to decay when you break the block
  @Override
  public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
  {
    byte b0 = 4;
    int i = b0 + 1;

    if (worldIn.isAreaLoaded(pos.add(-i, -i, -i), pos.add(i, i, i)))
    {

      for(BlockPos blockpos1 : BlockPos.getAllInBox(pos.add(-b0, -b0, -b0), pos.add(b0, b0, b0))) {
        IBlockState iblockstate1 = worldIn.getBlockState(blockpos1);

        if(iblockstate1.getBlock().isLeaves(iblockstate1, worldIn, blockpos1)) {
          iblockstate1.getBlock().beginLeavesDecay(iblockstate1, worldIn, blockpos1);
        }
      }
    }
  }
}
