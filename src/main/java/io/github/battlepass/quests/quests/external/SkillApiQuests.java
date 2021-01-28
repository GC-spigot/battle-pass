package io.github.battlepass.quests.quests.external;

import com.sucy.skill.api.enums.ManaCost;
import com.sucy.skill.api.event.PlayerClassChangeEvent;
import com.sucy.skill.api.event.PlayerLevelUpEvent;
import com.sucy.skill.api.event.PlayerManaLossEvent;
import com.sucy.skill.api.event.PlayerSkillUnlockEvent;
import com.sucy.skill.api.event.PlayerSkillUpgradeEvent;
import com.sucy.skill.api.event.TrueDamageEvent;
import com.sucy.skill.api.skills.Skill;
import io.github.battlepass.BattlePlugin;
import io.github.battlepass.quests.quests.external.executor.ExternalQuestExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class SkillApiQuests extends ExternalQuestExecutor {

    public SkillApiQuests(BattlePlugin plugin) {
        super(plugin, "skillapi");
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamageDeal(TrueDamageEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getDamager();
        int damage = (int) Math.round(event.getDamage());
        Skill skill = event.getSkill();
        String skillName = skill.getName();
        int combo = skill.getCombo();

        this.execute("damage", player, damage, result -> result.root(skillName));
        this.execute("combo", player, combo, result -> result.root(skillName), replacer -> replacer, true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onSkillUpgrade(PlayerSkillUpgradeEvent event) {
        Player player = event.getPlayerData().getPlayer();
        String skillName = event.getUpgradedSkill().getData().getName();

        this.execute("upgrade", player, result -> result.root(skillName));
    }

    @EventHandler(ignoreCancelled = true)
    public void onSkillUnlock(PlayerSkillUnlockEvent event) {
        Player player = event.getPlayerData().getPlayer();
        String skillName = event.getUnlockedSkill().getData().getName();

        this.execute("unlock", player, result -> result.root(skillName));
    }

    @EventHandler(ignoreCancelled = true)
    public void onManaLoss(PlayerManaLossEvent event) {
        Player player = event.getPlayerData().getPlayer();
        int amount = (int) Math.round(event.getAmount());
        ManaCost costReason = event.getSource();

        this.execute("loose_mana", player, amount, result -> result.root(costReason.toString()));
    }

    @EventHandler(ignoreCancelled = true)
    public void onLevelUp(PlayerLevelUpEvent event) {
        Player player = event.getPlayerData().getPlayer();
        int level = event.getLevel();

        this.execute("reach_level", player, level, result -> result, replacer -> replacer, true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onClassChange(PlayerClassChangeEvent event) {
        Player player = event.getPlayerData().getPlayer();
        String newClassName = event.getNewClass().getName();

        this.execute("change_class", player, result -> result.root(newClassName));
    }
}
