package io.github.battlepass.commands.bpadmin.materialsub;

import io.github.battlepass.service.Services;
import me.hyfe.simplespigot.command.command.SubCommand;
import me.hyfe.simplespigot.plugin.SimplePlugin;
import me.hyfe.simplespigot.text.Text;
import me.hyfe.simplespigot.version.ServerVersion;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MaterialItemSub extends SubCommand<Player> {

    public MaterialItemSub(SimplePlugin plugin) {
        super(plugin);

        this.inheritPermission();
        this.addFlats("material", "item");
    }

    @Override
    public void onExecute(Player sender, String[] args) {
        ItemStack itemInHand = ServerVersion.isOver_V1_12() ? sender.getInventory().getItemInMainHand() : sender.getItemInHand();
        Text.sendMessage(sender, "&eName of item in hand: ".concat(Services.getItemAsConfigString(itemInHand)));
    }
}