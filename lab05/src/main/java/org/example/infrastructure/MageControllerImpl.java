package org.example.infrastructure;

import org.example.core.Mage;
import org.example.core.MageController;
import org.example.core.MageRepository;

public class MageControllerImpl implements MageController {
    private final MageRepository repository;

    public MageControllerImpl(MageRepository repository) {
        this.repository = repository;
    }

    @Override
    public String find(String name) {
        var result = repository.findByName(name);

        if (result.isEmpty()) {
            return "not found";
        }

        return result.get().toString();
    }

    @Override
    public String delete(String name) {
        try {
            repository.delete(name);
            return "done";
        } catch (IllegalArgumentException e) {
            return "not found";
        }
    }

    @Override
    public String save(String name, String level) {
        try {
            repository.save(new Mage(name, Integer.parseInt(level)));
            return "done";
        } catch (IllegalArgumentException e) {
            return "bad request";
        }
    }
}
