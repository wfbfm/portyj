package com.wfbfm.portyj.websocket;

import com.wfbfm.portyj.positions.Position;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class PositionCache
{

    final Map<Long, Position> positionsById = new ConcurrentHashMap<>();
    final Map<String, Set<Long>> idsByAccount = new ConcurrentHashMap<>();
    final Map<String, Set<Long>> idsByIsin = new ConcurrentHashMap<>();

    public void put(final Position p)
    {
        positionsById.put(p.getId(), p);
        idsByAccount.computeIfAbsent(p.getAccountType(), k -> ConcurrentHashMap.newKeySet()).add(p.getId());
        idsByIsin.computeIfAbsent(p.getIsin(), k -> ConcurrentHashMap.newKeySet()).add(p.getId());
    }

    public Set<Long> queryIds(final PositionFilter filter)
    {
        // Start from the most selective index you have
        if (!filter.ids().isEmpty())
        {
            return filter.ids();
        }

        if (filter.accounts().isEmpty() && filter.isins().isEmpty())
        {
            return positionsById.keySet();
        }

        final Set<Long> resultByAccount = new HashSet<>();
        filter.accounts().forEach(account -> {
            resultByAccount.addAll(idsByAccount.getOrDefault(account, Set.of()));
        });
        final Set<Long> resultByIsin = new HashSet<>();
        filter.isins().forEach(account -> {
            resultByIsin.addAll(idsByAccount.getOrDefault(account, Set.of()));
        });
        return intersect(resultByAccount, resultByIsin);
    }

    public Position getById(final Long id)
    {
        return positionsById.get(id);
    }

    private Set<Long> intersect(final Set<Long> a, final Set<Long> b)
    {
        if (a == null) return new HashSet<>(b);
        Set<Long> smaller = a.size() < b.size() ? a : b;
        Set<Long> larger = a.size() < b.size() ? b : a;
        return smaller.stream().filter(larger::contains)
                .collect(Collectors.toSet());
    }
}
