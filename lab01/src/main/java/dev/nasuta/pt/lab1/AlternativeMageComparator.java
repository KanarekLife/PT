package dev.nasuta.pt.lab1;

import java.util.Comparator;

public class AlternativeMageComparator implements Comparator<Mage> {
    @Override
    public int compare(Mage first, Mage second) {
        int levelComparison = Integer.compare(first.getLevel(), second.getLevel());
        if (levelComparison != 0)
            return levelComparison;
        int nameComparison = first.getName().compareTo(second.getName());
        if (nameComparison != 0)
            return nameComparison;
        return Double.compare(first.getPower(), second.getPower());
    }
}
