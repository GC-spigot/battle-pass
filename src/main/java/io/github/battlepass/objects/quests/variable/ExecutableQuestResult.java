package io.github.battlepass.objects.quests.variable;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ExecutableQuestResult implements QuestResult {
    private String root;

    // Item / Block
    private String material;
    private byte data;

    @Override
    public ExecutableQuestResult root(String root) {
        if (root == null) {
            return this;
        }
        this.root = root;
        return this;
    }

    @Override
    public ExecutableQuestResult root(Block rootBlock) {
        if (rootBlock == null) {
            return this;
        }
        this.material = rootBlock.getType().toString();
        this.data = rootBlock.getData();
        return this;
    }

    @Override
    public ExecutableQuestResult root(ItemStack rootItem) {
        if (rootItem == null) {
            return this;
        }
        this.material = rootItem.getType().toString();
        this.data = rootItem.getData().getData();
        return this;
    }

    @Override
    public QuestResult none() {
        this.root = "none";
        return this;
    }

    @Override
    public String getRoot() {
        return this.material == null ? this.root : this.material + ":" + this.data;
    }

    @Override
    public boolean isEligible(Player player, Variable variable) {
        boolean noVariableRoot = variable.getRoot().equalsIgnoreCase("none");
        if (noVariableRoot) {
            return true;
        }
        if (this.material == null) {
            return this.root.equalsIgnoreCase(variable.getRoot());
        }
        return variable.supplyMaterial((material, data) -> {
            return this.material.equals(material) && (data < 0 || this.data == data);
        });
    }
}