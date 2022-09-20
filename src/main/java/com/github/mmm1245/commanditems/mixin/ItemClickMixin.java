package com.github.mmm1245.commanditems.mixin;

import com.github.mmm1245.commanditems.CommandItemUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemClickMixin {
    @Shadow private int count;

    @Inject(at = @At("HEAD"), method = "use", cancellable = true)
    public void onUse(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir){
        if(world.isClient)
            return;
        ItemStack stack = (ItemStack) (Object)this;
        if(CommandItemUtils.onUse(stack, user, null, null))
            cir.setReturnValue(TypedActionResult.success(stack));
    }
    @Inject(at = @At("HEAD"), method = "useOnEntity", cancellable = true)
    public void onUseEntity(PlayerEntity user, LivingEntity entity, Hand hand, CallbackInfoReturnable<ActionResult> cir){
        if(user instanceof ServerPlayerEntity) {
            ItemStack stack = (ItemStack) (Object) this;
            if (CommandItemUtils.onUse(stack, user, null, entity))
                cir.setReturnValue(ActionResult.SUCCESS);
        }
    }
    @Inject(at = @At("HEAD"), method = "useOnBlock", cancellable = true)
    public void onUseBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir){
        if(context.getWorld().isClient)
            return;
        ItemStack stack = (ItemStack) (Object)this;
        if(context.getPlayer() == null)
            return;
        if(CommandItemUtils.onUse(stack, context.getPlayer(), context.getBlockPos(), null)) {
            //todo: fix desync
            //context.getPlayer().setStackInHand(context.getHand(), stack);
            cir.setReturnValue(ActionResult.SUCCESS);
        }
    }
}
