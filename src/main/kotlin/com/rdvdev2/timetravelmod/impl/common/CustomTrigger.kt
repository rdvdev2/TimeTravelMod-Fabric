package com.rdvdev2.timetravelmod.impl.common

import com.google.common.collect.Lists
import com.google.common.collect.Maps
import com.google.common.collect.Sets
import com.google.gson.JsonObject
import net.minecraft.advancement.PlayerAdvancementTracker
import net.minecraft.advancement.criterion.AbstractCriterion
import net.minecraft.advancement.criterion.AbstractCriterionConditions
import net.minecraft.advancement.criterion.Criterion.ConditionsContainer
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer
import net.minecraft.predicate.entity.EntityPredicate.Extended
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

class CustomTrigger(private val identifier: Identifier) : AbstractCriterion<CustomTrigger.Instance>() {

    private val listeners: Map<PlayerAdvancementTracker, Listeners> = Maps.newHashMap()

    override fun getId(): Identifier {
        return identifier
    }

    override fun conditionsFromJson(obj: JsonObject, playerPredicate: Extended, predicateDeserializer: AdvancementEntityPredicateDeserializer): Instance {
        return Instance(id, playerPredicate)
    }

    fun trigger(player: ServerPlayerEntity) {
        val triggerListeners = listeners[player.advancementTracker]
        triggerListeners?.trigger(player)
    }

    class Instance(identifier: Identifier?, playerPredicate: Extended?) : AbstractCriterionConditions(identifier, playerPredicate) {
        fun test(): Boolean {
            return true
        }
    }

    class Listeners(private val playerAdvancements: PlayerAdvancementTracker) {

        private val listeners: MutableSet<ConditionsContainer<*>> = Sets.newHashSet()

        val empty: Boolean
            get() = listeners.isEmpty()

        fun add(listener: ConditionsContainer<*>) {
            listeners.add(listener)
        }

        fun remove(listener: ConditionsContainer<*>) {
            listeners.remove(listener)
        }

        fun trigger(player: ServerPlayerEntity?) {
            var list: ArrayList<ConditionsContainer<*>>? = null
            for (listener in listeners) {
                if ((listener.conditions as Instance).test()) {
                    if (list == null) {
                        list = Lists.newArrayList()
                    }
                    list!!.add(listener)
                }
            }
            if (list != null) {
                for (listener in list) listener.grant(playerAdvancements)
            }
        }
    }
}