package io.github.implicitsaber.mod.note_block_radio.item;

import io.github.implicitsaber.mod.note_block_radio.entity.TransmitterMinecartEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class TransmitterMinecartItem extends Item {

    private static final DispenseItemBehavior DISPENSER_BEHAVIOR = new DispenseItemBehavior() {

        private final DefaultDispenseItemBehavior def = new DefaultDispenseItemBehavior();

        @Override
        public @NotNull ItemStack dispense(BlockSource src, @NotNull ItemStack stack) {
            Direction facing = src.state().getValue(DispenserBlock.FACING);
            ServerLevel level = src.level();
            Vec3 center = src.center();
            double x = center.x() + facing.getStepX() * 1.125;
            double y = Math.floor(center.y()) + facing.getStepY();
            double z = center.z() + facing.getStepZ() * 1.125;
            BlockPos pos = src.pos().relative(facing);
            BlockState state = level.getBlockState(pos);
            RailShape shape = shapeFromState(level, pos, state);
            double yOffset;
            if(state.is(BlockTags.RAILS)) {
                if(shape.isAscending()) yOffset = 0.6;
                else yOffset = 0.1;
            } else {
                BlockPos belowPos = pos.below();
                if(!state.isAir() || !level.getBlockState(belowPos).is(BlockTags.RAILS)) return this.def.dispense(src, stack);
                BlockState belowState =  level.getBlockState(belowPos);
                RailShape belowShape = shapeFromState(level, belowPos, belowState);
                if(facing != Direction.DOWN && belowShape.isAscending()) yOffset = -0.4;
                else yOffset = -0.9;
            }
            TransmitterMinecartEntity e = new TransmitterMinecartEntity(level, x, y + yOffset, z);
            EntityType.createDefaultStackConfig(level, stack, null).accept(e);
            level.addFreshEntity(e);
            stack.shrink(1);
            return stack;
        }

    };

    public TransmitterMinecartItem(Properties properties) {
        super(properties);
        DispenserBlock.registerBehavior(this, DISPENSER_BEHAVIOR);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext ctx) {
        Level level = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        BlockState state = level.getBlockState(pos);
        if(!state.is(BlockTags.RAILS)) return InteractionResult.FAIL;
        ItemStack stack = ctx.getItemInHand();
        if(level instanceof ServerLevel sl) {
            RailShape shape = shapeFromState(sl, pos, state);
            double yOffset = 0.0625;
            if(shape.isAscending()) yOffset += 0.5;
            TransmitterMinecartEntity e = new TransmitterMinecartEntity(sl, pos.getX() + 0.5, pos.getY() + yOffset, pos.getZ() + 0.5);
            EntityType.createDefaultStackConfig(sl, stack, ctx.getPlayer()).accept(e);
            sl.addFreshEntity(e);
            sl.gameEvent(GameEvent.ENTITY_PLACE, pos, GameEvent.Context.of(ctx.getPlayer(), sl.getBlockState(pos.below())));
        }
        stack.shrink(1);
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    private static RailShape shapeFromState(Level level, BlockPos pos, BlockState state) {
        return state.getBlock() instanceof BaseRailBlock r ? r.getRailDirection(state, level, pos, null) : RailShape.NORTH_SOUTH;
    }

}
