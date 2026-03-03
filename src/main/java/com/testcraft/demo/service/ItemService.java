package com.testcraft.demo.service;

import com.testcraft.demo.model.Item;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ItemService {

    private final List<Item> items = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public ItemService() {
        items.add(new Item(idGenerator.getAndIncrement(), "Sample Item", "A sample item for TestCraft AI demo"));
    }

    public List<Item> findAll() {
        return new ArrayList<>(items);
    }

    public Optional<Item> findById(Long id) {
        return items.stream()
                .filter(item -> item.getId().equals(id))
                .findFirst();
    }

    public Item create(Item item) {
        item.setId(idGenerator.getAndIncrement());
        items.add(item);
        return item;
    }

    public Optional<Item> update(Long id, Item updated) {
        return findById(id)
                .map(existing -> {
                    existing.setName(updated.getName());
                    existing.setDescription(updated.getDescription());
                    return existing;
                });
    }

    public boolean delete(Long id) {
        return items.removeIf(item -> item.getId().equals(id));
    }
}
