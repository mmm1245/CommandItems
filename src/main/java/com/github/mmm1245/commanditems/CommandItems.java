package com.github.mmm1245.commanditems;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;

public class CommandItems implements ModInitializer {
    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                    LiteralArgumentBuilder.<ServerCommandSource>literal("itembind")
                            .then(LiteralArgumentBuilder.<ServerCommandSource>literal("bind").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2)).then(
                                    RequiredArgumentBuilder.<ServerCommandSource,String>argument("command", StringArgumentType.greedyString())
                                    .executes(context -> {
                                ServerPlayerEntity playerEntity = context.getSource().getPlayer();
                                if(playerEntity == null){
                                    context.getSource().sendMessage(Text.literal("Player not found").styled(style -> style.withColor(Formatting.RED)));
                                    return 1;
                                }
                                String command = StringArgumentType.getString(context, "command");
                                if(command.isBlank()){
                                    context.getSource().sendMessage(Text.literal("Command cannot be blank, use clear to remove bound command").styled(style -> style.withColor(Formatting.RED)));
                                    return 1;
                                }
                                if(CommandItemUtils.addCommand(playerEntity, command)){
                                    context.getSource().sendMessage(Text.literal("Command bound successfully").styled(style -> style.withColor(Formatting.GREEN)));
                                } else {
                                    context.getSource().sendMessage(Text.literal("No item found").styled(style -> style.withColor(Formatting.RED)));
                                }
                                return 1;
                            })))
                            .then(LiteralArgumentBuilder.<ServerCommandSource>literal("clear").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2)).executes(context -> {
                                ServerPlayerEntity playerEntity = context.getSource().getPlayer();
                                if(playerEntity == null){
                                    context.getSource().sendMessage(Text.literal("Player not found").styled(style -> style.withColor(Formatting.RED)));
                                    return 1;
                                }
                                if(CommandItemUtils.removeCommand(playerEntity)){
                                    context.getSource().sendMessage(Text.literal("Bound command removed").styled(style -> style.withColor(Formatting.GREEN)));
                                } else {
                                    context.getSource().sendMessage(Text.literal("No command was bound").styled(style -> style.withColor(Formatting.RED)));
                                }
                                return 1;
                            }))
                            .then(LiteralArgumentBuilder.<ServerCommandSource>literal("get").executes(context -> {
                                ServerPlayerEntity playerEntity = context.getSource().getPlayer();
                                if(playerEntity == null){
                                    context.getSource().sendMessage(Text.literal("Player not found").styled(style -> style.withColor(Formatting.RED)));
                                    return 1;
                                }
                                String command = CommandItemUtils.getCommand(playerEntity);
                                if(command != null){
                                    context.getSource().sendMessage(Text.literal("Commands bound: ").append(Text.literal(command).styled(style -> style.withBold(true))));
                                } else {
                                    context.getSource().sendMessage(Text.literal("No command bound").styled(style -> style.withColor(Formatting.RED)));
                                }
                                return 1;
                            }))
            );
        });
    }
}
