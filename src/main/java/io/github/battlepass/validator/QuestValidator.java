package io.github.battlepass.validator;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.objects.quests.Quest;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class QuestValidator {
    private final Logger logger = BattlePlugin.logger();

    public boolean checkQuest(Quest quest, AtomicInteger failCounter) {
        String prefix = "[Quest Validator] You have a broken quest. ";
        if (this.checkQuest(prefix, quest, failCounter)) {
            return false;
        }
        prefix += " Category: " + quest.getCategoryId() + " | ID: " + quest.getId() + " | ";
        return !(
                this.checkName(prefix, quest, failCounter)
                        || this.checkType(prefix, quest, failCounter)
                        || this.checkRequiredProgress(prefix, quest, failCounter)
                        || this.checkPoints(prefix, quest, failCounter)
                        || this.checkItemStack(prefix, quest, failCounter)
        );
    }

    private boolean checkQuest(String prefix, Quest quest, AtomicInteger counter) {
        if (quest == null) {
            this.logger.severe(prefix.concat("Quest itself is null."));
            counter.getAndIncrement();
            return true;
        }
        return false;
    }

    private boolean checkName(String prefix, Quest quest, AtomicInteger counter) {
        if (quest.getName() == null || quest.getName().replace(" ", "").isEmpty()) {
            this.log(prefix.concat("This quest has no name."));
            counter.getAndIncrement();
            return true;
        }
        return false;
    }

    private boolean checkType(String prefix, Quest quest, AtomicInteger counter) {
        if (quest.getType() == null || quest.getType().replace(" ", "").isEmpty()) {
            this.log(prefix.concat("This quest has no type (e.g block-break or block-place)."));
            counter.getAndIncrement();
            return true;
        }
        return false;
    }

    private boolean checkRequiredProgress(String prefix, Quest quest, AtomicInteger counter) {
        if (quest.getRequiredProgress().compareTo(BigInteger.ONE) < 0) {
            this.log(prefix.concat("This quest's progress is not set or is lower than 1."));
            counter.getAndIncrement();
            return true;
        }
        return false;
    }

    private boolean checkPoints(String prefix, Quest quest, AtomicInteger counter) {
        if (quest.getPoints() < 1) {
            this.log(prefix.concat("This quest's points reward is not set or is lower than one."));
            counter.getAndIncrement();
            return true;
        }
        return false;
    }

    private boolean checkItemStack(String prefix, Quest quest, AtomicInteger counter) {
        if (quest.getItemStack() == null) {
            this.log(prefix.concat("This quest's item is not present or is broken."));
            quest.setItemStack(new ItemStack(Material.BARRIER));
            counter.getAndIncrement();
            return true;
        }
        return false;
    }

    private void log(String message) {
        this.logger.warning(message);
    }
}
