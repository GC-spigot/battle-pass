package io.github.battlepass.validator;

import io.github.battlepass.objects.quests.Quest;
import org.bukkit.Bukkit;

import java.util.logging.Level;
import java.util.logging.Logger;

public class QuestValidator {

    public boolean checkQuest(Quest quest) {
        Logger logger = Bukkit.getLogger();
        String categoryId = quest.getCategoryId();
        String id = quest.getId();
        String prefix = "[Quest Validator] You have a broken quest. ";
        if (categoryId == null || id == null || categoryId.isEmpty() || id.isEmpty()) {
            logger.log(Level.SEVERE, prefix.concat("(issue with category or quest ID.)"));
            return false;
        }
        prefix = prefix.concat(" Category: ").concat(categoryId).concat(" | ID: ").concat(id).concat(" ||| ");
        if (quest.getName() == null || quest.getName().isEmpty()) {
            this.log(prefix.concat("This quest has no name."));
            return false;
        }
        if (quest.getItemStack() == null) {
            this.log(prefix.concat("This quest's item is not present or is broken."));
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
        return true;
    }

    private void log(String message) {
        Logger logger = Bukkit.getLogger();
        logger.log(Level.WARNING, message);
    }
}
