package io.github.battlepass.registry.quest.object;

public class PluginVersion {
    private int major;
    private int minor;
    private int bugfix;

    public int getMajor() {
        return this.major;
    }

    public PluginVersion setMajor(int major) {
        this.major = major;
        return this;
    }

    public int getMinor() {
        return this.minor;
    }

    public PluginVersion setMinor(int minor) {
        this.minor = minor;
        return this;
    }

    public int getBugfix() {
        return this.bugfix;
    }

    public PluginVersion setBugfix(int bugfix) {
        this.bugfix = bugfix;
        return this;
    }

    @Override
    public String toString() {
        return "PluginVersion{" +
                "major=" + this.major +
                ", minor=" + this.minor +
                ", bugfix=" + this.bugfix +
                '}';
    }
}
