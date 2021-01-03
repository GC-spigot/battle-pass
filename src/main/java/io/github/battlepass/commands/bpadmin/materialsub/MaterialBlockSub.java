package io.github.battlepass.commands.bpadmin.materialsub;

import me.hyfe.simplespigot.command.command.SubCommand;
import me.hyfe.simplespigot.plugin.SimplePlugin;
import me.hyfe.simplespigot.text.Text;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

public class MaterialBlockSub extends SubCommand<Player> {

    public MaterialBlockSub(SimplePlugin plugin) {
        super(plugin);

        this.inheritPermission();
        this.addFlats("material", "block");
    }

    @Override
    public void onExecute(Player sender, String[] args) {
        Block targetBlock = this.getBlockAtEyes(sender);
        Text.sendMessage(sender, "&eName of block you are looking at: " + targetBlock.getType().toString().toLowerCase() + ":" + targetBlock.getData());
    }

    private Block getBlockAtEyes(Player player) {
        BlockIterator blockIterator = new BlockIterator(player, 6);
        Block lastBlock = blockIterator.next();
        while (blockIterator.hasNext()) {
            lastBlock = blockIterator.next();
            if (!lastBlock.getType().equals(Material.AIR)) {
                return lastBlock;
            }
        }
        return lastBlock;
    }
}