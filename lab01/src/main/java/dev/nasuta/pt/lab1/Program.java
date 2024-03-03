package dev.nasuta.pt.lab1;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class Program {
    public static void main(String[] args) {
        SortType sortType = SortType.NATURAL_SORT;
        if(args.length > 0){
            sortType = switch (args[0]) {
                case "none" -> SortType.NO_SORT;
                case "alternative" -> SortType.ALTERNATIVE_SORT;
                default -> sortType;
            };
        }
        System.out.println("Sort type: " + sortType);
        Mage.setSortType(sortType);

        final int minMageCount = 3, maxMageCount = 7, maxApprenticesCount = 3;

        List<Mage> mages = new ArrayList<>();
        int magesCount = ThreadLocalRandom.current().nextInt(minMageCount, maxMageCount + 1);
        for(int i = 0; i < magesCount; i++) {
            mages.add(generateMage("Mage#" + i, maxApprenticesCount));
        }

        // add mages to test the sorting
        Mage abcMage =  new Mage("ABC mage", 2, 1);
        Mage testMage =  new Mage("Test mage", 1, 1);
        mages.get(0).addApprentice(abcMage);
        mages.get(0).addApprentice(testMage);

        for(Mage m : mages) {
            System.out.println(m.recursiveRepresentation());
        }

        for(var entry : mages.get(0).getApprenticeStats().entrySet()) {
            System.out.println(entry.getKey() + " has " + entry.getValue() + " apprentice");
        }
    }

    private static Mage generateMage(String name, int maxApprenticesCount) {
        int level = ThreadLocalRandom.current().nextInt(1, 99 + 1);
        double power = ThreadLocalRandom.current().nextDouble(0, 999 + 1);
        int apprenticesCount = ThreadLocalRandom.current().nextInt(0, maxApprenticesCount + 1);
        Mage mage = new Mage(name, level, power);
        for(int i = 0; i < apprenticesCount; i++) {
            mage.addApprentice(generateMage(name + "#" + i, maxApprenticesCount - 1));
        }
        return mage;
    }

}
