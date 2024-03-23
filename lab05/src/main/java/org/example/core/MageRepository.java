package org.example.core;

import java.util.Optional;

public interface MageRepository {
    Optional<Mage> findByName(String name);
    void delete(String name);
    void save(Mage mage);
}
