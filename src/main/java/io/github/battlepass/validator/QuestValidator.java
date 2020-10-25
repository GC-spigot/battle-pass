package io.github.battlepass.validator;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.objects.quests.Quest;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class QuestValidator {
    private final Logger logger = BattlePlugin.logger();

    public boolean checkQuest(Quest quest, AtomicInteger failCounter) {
        String prefix = "[Quest Validator] You have a broken quest. ";
        if (quest == null) {
            this.logger.log(Level.SEVERE, prefix.concat("Quest itself is null."));
            return false;
        }
        String categoryId = quest.getCategoryId();
        String id = quest.getId();
        if (categoryId == null || id == null || categoryId.isEmpty() || id.isEmpty()) {
            this.logger.log(Level.SEVERE, prefix.concat("(issue with category or quest ID.)"));
            return false;
        }
        prefix = prefix.concat(" Category: ").concat(categoryId).concat(" | ID: ").concat(id).concat(" | ");
        if (quest.getName() == null || quest.getName().isEmpty()) {
            this.log(prefix.concat("This quest has no name."));
            return false;
        }
        if (quest.getType() == null || quest.getType().isEmpty()) {
            this.log(prefix.concat("This quest has no type (e.g block-break or block-place)."));
            return false;
        }
        if (quest.getRequiredProgress() < 1) {
            this.log(prefix.concat("This quest's progress is not set or is lower than 1."));
            return false;
        }
        if (quest.getPoints() < 1) {
            this.log(prefix.concat("This quest's points reward is not set or is lower than one."));
            return false;
        }
        if (quest.getItemStack() == null) {
            this.log(prefix.concat("This quest's item is not present or is broken."));
            quest.setItemStack(new ItemStack(Material.BARRIER));
            failCounter.getAndIncrement();
        }
        return true;
    }

    private void log(String message) {
        this.logger.log(Level.WARNING, message);
    }
}
