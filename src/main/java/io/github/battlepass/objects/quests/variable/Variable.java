package io.github.battlepass.objects.quests.variable;

import me.hyfe.simplespigot.version.MultiMaterial;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiFunction;

public class Variable {
    private String root;

    private String material;
    private byte data;

    public Variable(String root) {
        this.root = root;
        this.parseRoot();
    }

    public String getRoot() {
        return this.root;
    }

    public boolean supplyMaterial(BiFunction<String, Byte, Boolean> function) {
        return function.apply(this.material, this.data);
    }

    private void parseRoot() {
        String[] materialSplit = this.root.split(":");
        ItemStack itemStack = MultiMaterial.parseItem(this.root.toUpperCase().replace(":ALL", ":0"));
        this.material = itemStack.getType().toString();
        this.data = materialSplit.length > 1 ? materialSplit[1].equalsIgnoreCase("all") ? -1 : Byte.parseByte(materialSplit[1]) : 0;
        this.root = materialSplit[0];
    }
}
