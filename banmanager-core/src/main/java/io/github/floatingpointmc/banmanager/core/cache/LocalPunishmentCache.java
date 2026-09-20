package io.github.floatingpointmc.banmanager.core.cache;

import io.github.floatingpointmc.banmanager.api.punishment.Punishment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class LocalPunishmentCache implements PunishmentCache {
    private final Map<Integer, Punishment> idCache = new ConcurrentHashMap<>();
    private final Map<UUID, Collection<Punishment>> targetCache = new ConcurrentHashMap<>();

    @Override
    public @Nullable Punishment findById(int id) {
        return idCache.get(id);
    }

    @Override
    public @NotNull Collection<Punishment> findActiveByTarget(@NotNull UUID target) {
        return targetCache.getOrDefault(target, Collections.emptyList());
    }

    @Override
    public void put(@NotNull Punishment punishment) {
        idCache.put(punishment.getId(), punishment);
        targetCache.computeIfAbsent(punishment.getTarget(), k -> new CopyOnWriteArrayList<>()).add(punishment);
    }

    @Override
    public void invalidate(int id) {
        Punishment removed = idCache.remove(id);
        if (removed != null) {
            Collection<Punishment> list = targetCache.get(removed.getTarget());
            if (list != null) {
                list.removeIf(p -> p.getId() == id);
                if (list.isEmpty()) {
                    targetCache.remove(removed.getTarget());
                }
            }
        }
    }

    @Override
    public void invalidateByTarget(@NotNull UUID target) {
        Collection<Punishment> removed = targetCache.remove(target);
        if (removed != null) {
            for (Punishment p : removed) {
                idCache.remove(p.getId());
            }
        }
    }
}