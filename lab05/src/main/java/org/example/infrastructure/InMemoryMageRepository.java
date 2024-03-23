package org.example.infrastructure;

import org.example.core.Mage;
import org.example.core.MageRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class InMemoryMageRepository implements MageRepository {
    private final Collection<Mage> collection = new ArrayList<>();

    public InMemoryMageRepository() {}
    public InMemoryMageRepository(Collection<Mage> collection) {
        this.collection.addAll(collection);
    }

    @Override
    public Optional<Mage> findByName(String name) {
        return collection.stream()
                .filter(mage -> mage.getName().equals(name))
                .findFirst();
    }

    @Override
    public void delete(String name) {
        var mage = findByName(name);

        if (mage.isEmpty()) {
            throw new IllegalArgumentException("Mage not found");
        }

        collection.remove(mage.get());
    }

    @Override
    public void save(Mage mage) {
        if (collection.stream().anyMatch(m -> m.getName().equals(mage.getName()))) {
            throw new IllegalArgumentException("Mage already exists");
        }

        collection.add(mage);
    }
}
