package com.rdvdev2.TimeTravelMod.common.timemachine.upgrade;

import com.rdvdev2.TimeTravelMod.ModRegistries;
import com.rdvdev2.TimeTravelMod.api.timemachine.TimeMachine;
import com.rdvdev2.TimeTravelMod.api.timemachine.upgrade.TimeMachineHook;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class TimeMachineUpgrade implements com.rdvdev2.TimeTravelMod.api.timemachine.upgrade.TimeMachineUpgrade {

    private HashMap<TimeMachineHook<?>, Boolean> hooks;
    private TimeMachine[] compatibleTMs;

    public TimeMachineUpgrade() {
        this.hooks = new HashMap<>(0);
    }

    public com.rdvdev2.TimeTravelMod.api.timemachine.upgrade.TimeMachineUpgrade addHook(TimeMachineHook<?> hook) {
        addHook(hook, false);
        return this;
    }

    public com.rdvdev2.TimeTravelMod.api.timemachine.upgrade.TimeMachineUpgrade addHook(TimeMachineHook<?> hook, boolean exclusiveMode) {
        this.hooks.put(hook, exclusiveMode);
        return this;
    }

    public com.rdvdev2.TimeTravelMod.api.timemachine.upgrade.TimeMachineUpgrade addAllHooks(TimeMachineHook<?>... hooks) {
        for (TimeMachineHook<?> hook:hooks) this.addHook(hook);
        return this;
    }

    public <T> T runHook(Optional<T> result, Class<? extends TimeMachineHook<?>> clazz, TimeMachine tm, Object... args) {
        Set<TimeMachineHook<T>> hooks = this.hooks.keySet().stream()
                .filter(clazz::isInstance)
                .map(h -> (TimeMachineHook<T>) h)
                .collect(Collectors.toSet());
        for (TimeMachineHook<T> timeMachineHook: hooks) result = Optional.of(timeMachineHook.run(result, tm, args));
        return result.orElseThrow(RuntimeException::new);
    }

    public boolean runVoidHook(Class<? extends TimeMachineHook<Void>> clazz, TimeMachine tm, Object... args) {
        Optional<TimeMachineHook<?>> hook = this.hooks.keySet().stream().filter(clazz::isInstance).findFirst();
        if (hook.isPresent()) {
            hook.get().run(Optional.empty(), tm, args);
            return true;
        } else return false;
    }

    public TimeMachine[] getCompatibleTMs() {
        return compatibleTMs;
    }

    public com.rdvdev2.TimeTravelMod.api.timemachine.upgrade.TimeMachineUpgrade setCompatibleTMs(TimeMachine... compatibleTMs) {
        this.compatibleTMs = compatibleTMs;
        return this;
    }

    public boolean isExclusiveHook(Class<? extends TimeMachineHook<?>> hook) {
        return hooks.entrySet().stream().anyMatch(e -> hook.isAssignableFrom(e.getKey().getClass()));
    }

    public final TranslatableText getName() {
        Identifier id = ModRegistries.UPGRADES.getId(this);
        return new TranslatableText(String.format("tmupgrade.%s.%s.name", id.getNamespace(), id.getPath()));
    }

    public final TranslatableText getDescription() {
        Identifier id = ModRegistries.UPGRADES.getId(this);
        return new TranslatableText(String.format("tmupgrade.%s.%s.description", id.getNamespace(), id.getPath()));
    }
}
