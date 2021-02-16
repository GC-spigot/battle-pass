package io.github.battlepass.objects.quests.variable;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.hyfe.simplespigot.config.Config;
import me.hyfe.simplespigot.tuple.ImmutablePair;
import me.hyfe.simplespigot.version.MultiMaterial;
import org.bukkit.configuration.MemorySection;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class Variable {
    private final String[] roots;
    private final Set<ImmutablePair<String, Byte>> materialRoots = Sets.newHashSet();
    private Map<String, List<String>> subRoots = Maps.newHashMap();

    public Variable(String root) {
        this.roots = root.split(" OR ");
        this.parseRoots();
    }

    public Variable(String root, Map<String, List<String>> subRoots) {
        this.roots = root.split(" OR ");
        this.subRoots = subRoots;
        this.parseRoots();
    }

    public String[] getRoots() {
        return this.roots;
    }

    public Map<String, List<String>> getSubRoots() {
        return this.subRoots;
    }

    public Set<ImmutablePair<String, Byte>> getMaterialRoots() {
        return this.materialRoots;
    }

    private void parseRoots() {
        for (int i = 0; i < this.roots.length; i++) {
            String[] materialSplit = this.roots[i].split(":");
            ItemStack itemStack = MultiMaterial.parseItem(this.roots[i].toUpperCase().replace(":ALL", ":0"));
            byte data = materialSplit.length > 1 ? materialSplit[1].equalsIgnoreCase("all") ? -1 : Byte.parseByte(materialSplit[1]) : 0;
            this.materialRoots.add(ImmutablePair.of(itemStack.getType().toString(), data));
            this.roots[i] = materialSplit[0];
        }
    }

    public static Variable of(Config config, String section) {
        Map<String, List<String>> subRoots = Maps.newHashMap();
        BiConsumer<String, Supplier<String>> consumer = (path, supplier) -> subRoots.put(path, Arrays.asList(supplier.get().split(" OR ")));
        if (config.get(section.concat("variable")) instanceof MemorySection) {
            String root = config.has(section.concat("variable.root")) ? config.string(section.concat("variable.root")) : "none";
            String variableSection = section.concat("variable.");
            if (config.has(variableSection.concat("holding"))) {
                apply(consumer, config, variableSection, "holding.item");
                apply(consumer, config, variableSection, "holding.name");
                apply(consumer, config, variableSection, "holding.amount");
            }
            for (String subRoot : config.keys(section.concat("variable"), false)) {
                if (!subRoot.equalsIgnoreCase("root") && !subRoot.equalsIgnoreCase("holding")) {
                    consumer.accept(subRoot, () -> config.forcedString(section + "variable." + subRoot));
                }
            }
            return new Variable(root, subRoots);
        } else if (config.has(section.concat("variable"))) {
            return new Variable(config.string(section.concat("variable")));
        } else {
            return new Variable("none");
        }
    }

    private static void apply(BiConsumer<String, Supplier<String>> consumer, Config config, String section, String addon) {
        if (config.has(section.concat(addon))) {
            consumer.accept(addon, () -> config.string(section.concat(addon)));
        }
    }
}
