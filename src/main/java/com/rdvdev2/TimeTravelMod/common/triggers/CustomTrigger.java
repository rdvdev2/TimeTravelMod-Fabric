package com.rdvdev2.TimeTravelMod.common.triggers;

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
import java.util.Map;
import java.util.Set;

public class CustomTrigger extends AbstractCriterion<CustomTrigger.Instance> {

    private final Identifier resourceloacation;
    public final Map<PlayerAdvancementTracker, Listeners> listeners = Maps.newHashMap();

    public CustomTrigger(Identifier resourceLocation) {
        this.resourceloacation = resourceLocation;
    }

    @Override
    public Identifier getId() {
        return this.resourceloacation;
    }


    @Override
    public Instance conditionsFromJson(JsonObject obj, EntityPredicate.Extended extended, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        return new Instance(getId(), extended);
    }

    public void trigger(ServerPlayerEntity player) {
        Listeners triggerListeners = listeners.get(player.getAdvancementTracker());

        if (triggerListeners != null) {
            triggerListeners.trigger(player);
        }
    }

    public static class Instance extends AbstractCriterionConditions {

        public Instance(Identifier rl, EntityPredicate.Extended playerPredicate) {
            super(rl, playerPredicate);
        }

        public boolean test() {
            return true;
        }
    }

    static class Listeners {
        private final PlayerAdvancementTracker playerAdvancements;
        private final Set<Criterion.ConditionsContainer<?>> listeners = Sets.newHashSet();

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
            ArrayList<Criterion.ConditionsContainer<?>> list = null;

            for (Criterion.ConditionsContainer<?> listener : listeners) {
                if (((Instance)listener.getConditions()).test()) {
                    if (list == null) {
                        list = Lists.newArrayList();
                    }
                    list.add(listener);
                }
            }

            if (list != null) {
                for (Criterion.ConditionsContainer<?> listener : list) {
                    listener.grant(playerAdvancements);
                }
            }
        }
    }
}