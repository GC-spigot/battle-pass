package io.github.battlepass.objects.quests.variable;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.hyfe.simplespigot.config.ConfigLoader;
import me.hyfe.simplespigot.tuple.ImmutablePair;
import me.hyfe.simplespigot.version.MultiMaterial;
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

    public static Variable of(ConfigLoader.Reader reader) {
        Map<String, List<String>> subRoots = Maps.newHashMap();
        BiConsumer<String, Supplier<String>> consumer = (path, supplier) -> subRoots.put(path, Arrays.asList(supplier.get().split(" OR ")));
        if (reader.has("variable.root")) {
            String root = reader.string("variable.root");
            if (reader.has("variable.holding")) {
                consumer.accept("holding.item", () -> reader.string("variable.holding.item"));
                consumer.accept("holding.name", () -> reader.string("variable.holding.name"));
                consumer.accept("holding.amount", () -> Integer.toString(reader.integer("variable.holding.amount")));
            }
            reader.keyLoop(reader.getCurrentPath().concat(".variable"), subRoot -> {
                if (!subRoot.equalsIgnoreCase("root") && !subRoot.equalsIgnoreCase("holding")) {
                    consumer.accept(subRoot, reader::string);
                }
            });
            return new Variable(root, subRoots);
        } else if (reader.has("variable")) {
            return new Variable(reader.string("variable"));
        } else {
            return new Variable("none");
        }
    }
}
