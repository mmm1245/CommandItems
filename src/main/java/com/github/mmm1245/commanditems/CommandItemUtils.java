package com.github.mmm1245.commanditems;

import com.mojang.brigadier.ParseResults;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class CommandItemUtils {
    public static boolean addCommand(ServerPlayerEntity player, String command){
        ItemStack mainStack = player.getMainHandStack();
        if(mainStack == null || mainStack == ItemStack.EMPTY)
            return false;
        mainStack.setSubNbt("BoundCommand", NbtString.of(command));
        return true;
    }
    public static boolean removeCommand(ServerPlayerEntity player){
        ItemStack mainStack = player.getMainHandStack();
        if(mainStack == null || mainStack == ItemStack.EMPTY)
            return false;
        NbtCompound nbt = mainStack.getNbt();
        if(nbt == null)
            return false;
        if(nbt.getString("BoundCommand").isEmpty())
            return false;
        mainStack.removeSubNbt("BoundCommand");
        return true;
    }
    public static String getCommand(ServerPlayerEntity player){
        ItemStack mainStack = player.getMainHandStack();
        if(mainStack == null || mainStack == ItemStack.EMPTY)
            return null;
        NbtCompound nbt = mainStack.getNbt();
        if(nbt == null)
            return null;
        if(nbt.getString("BoundCommand").isEmpty())
            return null;
        return nbt.getString("BoundCommand");
    }
    public static boolean onUse(ItemStack stack, PlayerEntity playerEntity, @Nullable BlockPos block, @Nullable LivingEntity entity){
        NbtCompound nbt = stack.getNbt();
        if(nbt == null)
            return false;
        String command = nbt.getString("BoundCommand");
        if(command.isBlank())
            return false;
        command = command.replace("$x", ""+playerEntity.getX())
                         .replace("$y", ""+playerEntity.getY())
                         .replace("$z", ""+playerEntity.getZ());
        if(command.contains("$bx") || command.contains("$by") || command.contains("$bz")){
            if(block == null)
                return true;
            command = command.replace("$bx", ""+block.getX())
                             .replace("$by", ""+block.getY())
                             .replace("$bz", ""+block.getZ());
        }
        if(command.contains("$ex") || command.contains("$ey") || command.contains("$ez") || command.contains("$eid")){
            if(entity == null)
                return true;
            command = command.replace("$ex", ""+entity.getX())
                             .replace("$ey", ""+entity.getY())
                             .replace("$ez", ""+entity.getZ())
                             .replace("$eid", ""+entity.getUuidAsString());
        }
        playerEntity.getServer().getCommandManager().executeWithPrefix(playerEntity.getCommandSource(), command);
        return true;
    }
}
