package io.github.floatingpointmc.banmanager.core.cache;

import io.github.floatingpointmc.banmanager.api.punishment.Punishment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import redis.clients.jedis.RedisClient;
import redis.clients.jedis.params.SetParams;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

public class RedisPunishmentCache implements PunishmentCache {
    private final RedisClient redisClient;
    private final PunishmentSerializer serializer;

    public RedisPunishmentCache(@NotNull RedisClient redisClient, @NotNull PunishmentSerializer serializer) {
        this.redisClient = redisClient;
        this.serializer = serializer;
    }

    @Override
    public @Nullable Punishment findById(int id) {
        String data = redisClient.get(keyById(id));
        if (data == null) return null;
        return serializer.deserialize(data);
    }

    @Override
    public @NotNull Collection<Punishment> findActiveByTarget(@NotNull UUID target) {
        Collection<String> ids = redisClient.smembers(keyByTarget(target));
        if (ids.isEmpty()) return Collections.emptyList();
        Collection<Punishment> result = new ArrayList<>();
        for (String idStr : ids) {
            String data = redisClient.get(keyById(Integer.parseInt(idStr)));
            if (data != null) {
                result.add(serializer.deserialize(data));
            }
        }
        return result;
    }

    @Override
    public void put(@NotNull Punishment punishment) {
        String data = serializer.serialize(punishment);
        String idKey = keyById(punishment.getId());
        redisClient.set(idKey, data, SetParams.setParams().ex(3600));
        redisClient.sadd(keyByTarget(punishment.getTarget()), String.valueOf(punishment.getId()));
        redisClient.expire(keyByTarget(punishment.getTarget()), 3600);
    }

    @Override
    public void invalidate(int id) {
        String idKey = keyById(id);
        String data = redisClient.get(idKey);
        redisClient.del(idKey);
        if (data != null) {
            Punishment p = serializer.deserialize(data);
            if (p != null) {
                redisClient.srem(keyByTarget(p.getTarget()), String.valueOf(id));
            }
        }
    }

    @Override
    public void invalidateByTarget(@NotNull UUID target) {
        String setKey = keyByTarget(target);
        Collection<String> ids = redisClient.smembers(setKey);
        for (String idStr : ids) {
            redisClient.del(keyById(Integer.parseInt(idStr)));
        }
        redisClient.del(setKey);
    }

    private static String keyById(int id) {
        return "banmanager:punishment:id:" + id;
    }

    private static String keyByTarget(UUID target) {
        return "banmanager:punishment:target:" + target;
    }
}