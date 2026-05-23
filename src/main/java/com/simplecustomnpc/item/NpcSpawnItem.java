package com.simplecustomnpc.item;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.List;

public class NpcSpawnItem extends BlockItem {

    public NpcSpawnItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public Text getName(ItemStack stack) {
        return Text.translatable("item.simplecustomnpc.npc_spawn_block");
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, net.minecraft.item.tooltip.TooltipType type) {
        tooltip.add(Text.translatable("item.simplecustomnpc.npc_spawn_block.tooltip"));
    }
}
