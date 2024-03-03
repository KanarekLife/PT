package dev.nasuta.pt.lab1;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.HashMap;
import java.util.TreeMap;

public class Mage implements Comparable<Mage> {
    private static SortType sortType = SortType.NATURAL_SORT;

    private final String name;
    private final int level;
    private final double power;
    private final Set<Mage> apprentices;

    public Mage(String name, int level, double power) {
        this.name = name;
        this.level = level;
        this.power = power;
        this.apprentices = switch (sortType) {
            case NO_SORT -> new HashSet<Mage>();
            case NATURAL_SORT -> new TreeSet<Mage>();
            case ALTERNATIVE_SORT -> new TreeSet<Mage>(new AlternativeMageComparator());
            default -> throw new UnsupportedOperationException(sortType + " is not a supported sort type");
        };
    }

    public String getName() {
        return name;
    }
    public int getLevel() {
        return level;
    }
    public double getPower() {
        return power;
    }
    public Set<Mage> getApprentices() {
        return apprentices;
    }
    public void addApprentice(Mage mage) {
        apprentices.add(mage);
    }
    public static void setSortType(SortType newSortType) {
        sortType = newSortType;
    }
    public static SortType getSortType() {
        return sortType;
    }
    public String recursiveRepresentation() {
        return recursiveRepresentation("", 0);
    }
    private String recursiveRepresentation(String result, int depth) {
        result += "-".repeat(Math.max(0, depth));
        result += this.toString() + '\n';
        for (Mage mage : apprentices) {
            result = mage.recursiveRepresentation(result, depth + 1);
        }
        return result;
    }
    public Map<Mage, Integer> getApprenticeStats() {
        Map<Mage, Integer> map = switch (sortType) {
            case NO_SORT -> new HashMap<Mage, Integer>();
            case NATURAL_SORT -> new TreeMap<Mage, Integer>();
            case ALTERNATIVE_SORT -> new TreeMap<Mage, Integer>(new AlternativeMageComparator());
            default -> throw new UnsupportedOperationException(sortType + " is not a supported sort type");
        };
        populateApprenticeStats(map);
        return map;
    }
    private void populateApprenticeStats(Map<Mage, Integer> map) {
        map.put(this, getApprenticeCount());
        for (Mage mage : apprentices) {
            mage.populateApprenticeStats(map);
        }
    }
    public int getApprenticeCount() {
        int count = 0;
        for (Mage mage : apprentices) {
            count += mage.getApprenticeCount() + 1;
        }
        return count;
    }
    @Override
    public String toString() {
        return String.format("Mage{name='%s', level=%d, power=%.2f}", name, level, power);
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;

        Mage mage = (Mage) obj;
        return (this.name.equals(mage.name) && this.level == mage.level
                && this.power == mage.power && this.apprentices.equals(mage.apprentices));
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (name == null ? 0 : name.hashCode());
        result = prime * result + (int) (level ^ (level >>> 31));
        result = prime * result + Double.valueOf(power).hashCode();
        result = prime * result + apprentices.hashCode();
        return result;
    }

    @Override
    public int compareTo(Mage other) {
        int nameComparison = this.name.compareTo(other.name);
        if (nameComparison != 0)
            return nameComparison;
        int levelComparison = Integer.compare(this.level, other.level);
        if (levelComparison != 0)
            return levelComparison;
        return Double.compare(this.power, other.power);
    }
}
