package com.simplecustomnpc.item;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
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

    // 1.21.x: appendTooltip uses Item.TooltipContext
    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("item.simplecustomnpc.npc_spawn_block.tooltip"));
    }
}
