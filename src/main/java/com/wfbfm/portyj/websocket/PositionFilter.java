package com.wfbfm.portyj.websocket;

import com.wfbfm.portyj.positions.Position;
import com.wfbfm.portyj.positions.SubscriptionMsg;

import java.util.Set;
import java.util.stream.Collectors;

public record PositionFilter(Set<Long> ids, Set<String> accounts, Set<String> isins)
{
    public PositionFilter(final SubscriptionMsg subscriptionMsg)
    {
        this(
                subscriptionMsg.getIdsList().stream().collect(Collectors.toSet()),
                subscriptionMsg.getAccountTypesList().stream().collect(Collectors.toSet()),
                subscriptionMsg.getIsinsList().stream().collect(Collectors.toSet()));
    }

    public boolean matches(final Position p)
    {
        return (ids.isEmpty() || ids.contains(p.getId()))
                && (accounts.isEmpty() || accounts.contains(p.getAccountType()))
                && (isins.isEmpty() || isins.contains(p.getIsin()));
    }
}