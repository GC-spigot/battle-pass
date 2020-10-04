package io.github.battlepass.objects.reward;

import com.google.common.collect.Multiset;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class Reward<T> {
    private final String id;
    private final String name;
    private final List<String> loreAddon;
    protected Multiset<T> set;

    public Reward(String id, String name, List<String> loreAddon, Multiset<T> set) {
        this.id = id;
        this.name = name;
        this.loreAddon = loreAddon;
        this.set = set;
    }

    public abstract void reward(Player player, int tier);

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public List<String> getLoreAddon() {
        return this.loreAddon;
    }

    public Multiset<T> getSet() {
        return this.set;
    }

    public void setSet(Multiset<T> set) {
        this.set = set;
    }

    public void addElement(T element) {
        this.set.add(element);
    }

    public void removeElement(T element) {
        this.set.remove(element);
    }
}
