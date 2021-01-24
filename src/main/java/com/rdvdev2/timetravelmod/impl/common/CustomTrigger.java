package com.rdvdev2.timetravelmod.impl.common;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CustomTrigger extends AbstractCriterion<CustomTrigger.Instance> {

    private final Identifier identifier;
    public final Map<PlayerAdvancementTracker, Listeners> listeners = Maps.newHashMap();

    public CustomTrigger(Identifier identifier) {
        this.identifier = identifier;
    }

    @Override
    public Identifier getId() {
        return identifier;
    }

    @Override
    protected CustomTrigger.Instance conditionsFromJson(JsonObject obj, EntityPredicate.Extended playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        return new Instance(getId(), playerPredicate);
    }

    public void trigger(ServerPlayerEntity player) {
        Listeners triggerListeners = listeners.get(player.getAdvancementTracker());

        if (triggerListeners != null) triggerListeners.trigger(player);
    }

    public static class Instance extends AbstractCriterionConditions {

        public Instance(Identifier identifier, EntityPredicate.Extended playerPredicate) {
            super(identifier, playerPredicate);
        }

        public boolean test() {
            return true;
        }
    }

    static class Listeners {
        private final PlayerAdvancementTracker playerAdvancements;
        private final Set<ConditionsContainer<?>> listeners = Sets.newHashSet();

        public Listeners(PlayerAdvancementTracker playerAdvancements) {
            this.playerAdvancements = playerAdvancements;
        }

        public boolean isEmpty() {
            return listeners.isEmpty();
        }

        public void add(Criterion.ConditionsContainer<?> listener) {
            listeners.add(listener);
        }

        public void remove(Criterion.ConditionsContainer<?> listener) {
            listeners.remove(listener);
        }

        public void trigger(ServerPlayerEntity player) {
            ArrayList<ConditionsContainer<?>> list = null;

            for (Criterion.ConditionsContainer<?> listener: listeners) {
                if ( ((Instance) listener.getConditions()).test() ) {
                    if (list == null) {
                        list = Lists.newArrayList();
                    }
                    list.add(listener);
                }
            }

            if (list != null) {
                for (Criterion.ConditionsContainer<?> listener: list)
                    listener.grant(playerAdvancements);
            }
        }
    }
}
