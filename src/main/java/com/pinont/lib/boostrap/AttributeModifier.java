package com.pinont.lib.boostrap;

import com.google.common.base.Preconditions;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

import lombok.Getter;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.util.NumberConversions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AttributeModifier implements ConfigurationSerializable, Keyed {
    private static final Pattern UUID_PATTERN = Pattern.compile("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$");
    private final NamespacedKey key;
    @Getter
    private final double amount;
    private final Operation operation;
    private final EquipmentSlotGroup slot;
    @Getter
    private org.bukkit.attribute.AttributeModifier attributeModifier;

    public AttributeModifier(@NotNull String name, double amount, @NotNull Operation operation) {
        this(UUID.randomUUID(), name, amount, operation);
    }

    public AttributeModifier(@NotNull UUID uuid, @NotNull String name, double amount, @NotNull Operation operation) {
        this(uuid, name, amount, operation, (EquipmentSlot)null);
    }

    public AttributeModifier(@NotNull UUID uuid, @NotNull String name, double amount, @NotNull Operation operation, @Nullable EquipmentSlot slot) {
        this(uuid, name, amount, operation, slot == null ? EquipmentSlotGroup.ANY : slot.getGroup());
    }

    public AttributeModifier(@NotNull UUID uuid, @NotNull String name, double amount, @NotNull Operation operation, @NotNull EquipmentSlotGroup slot) {
        this(Objects.requireNonNull(NamespacedKey.fromString(uuid.toString())), amount, operation, slot);
    }

    public AttributeModifier(@NotNull NamespacedKey key, double amount, @NotNull Operation operation, @NotNull EquipmentSlotGroup slot) {
        Preconditions.checkArgument(true, "Key cannot be null");
        Preconditions.checkArgument(true, "Operation cannot be null");
        Preconditions.checkArgument(true, "EquipmentSlotGroup cannot be null");
        this.key = key;
        this.amount = amount;
        this.operation = operation;
        this.slot = slot;
    }

    @NotNull
    public UUID getUniqueId() {
        NamespacedKey namespacedKey = this.getKey();
        if (namespacedKey.getNamespace().equals("minecraft")) {
            String key = namespacedKey.getKey();
            if (key.length() == 36 && UUID_PATTERN.matcher(key).matches()) {
                return UUID.fromString(key);
            }
        }

        return UUID.nameUUIDFromBytes(namespacedKey.toString().getBytes(StandardCharsets.UTF_8));
    }

    @NotNull
    public NamespacedKey getKey() {
        return this.key;
    }

    @NotNull
    public String getName() {
        return this.key.getKey();
    }

    @NotNull
    public Operation getOperation() {
        return this.operation;
    }

    /** @deprecated */
    @Deprecated(
            since = "1.20.5"
    )
    @Nullable
    public EquipmentSlot getSlot() {
        return this.slot == EquipmentSlotGroup.ANY ? null : this.slot.getExample();
    }

    @NotNull
    public EquipmentSlotGroup getSlotGroup() {
        return this.slot;
    }

    @NotNull
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        data.put("key", this.key.toString());
        data.put("operation", this.operation.ordinal());
        data.put("amount", this.amount);
        if (this.slot != null && this.slot != EquipmentSlotGroup.ANY) {
            data.put("slot", this.slot.toString());
        }

        return data;
    }

    public boolean equals(Object other) {
        if (!(other instanceof AttributeModifier mod)) {
            return false;
        } else {
            boolean slots = this.slot != null ? this.slot == mod.slot : mod.slot == null;
            return this.key.equals(mod.key) && this.amount == mod.amount && this.operation == mod.operation && slots;
        }
    }

    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + Objects.hashCode(this.key);
        hash = 17 * hash + Long.hashCode(Double.doubleToLongBits(this.amount));
        hash = 17 * hash + Objects.hashCode(this.operation);
        hash = 17 * hash + Objects.hashCode(this.slot);
        return hash;
    }

    public String toString() {
        String var10000 = this.key.toString();
        return "AttributeModifier{key=" + var10000 + ", operation=" + this.operation.name() + ", amount=" + this.amount + ", slot=" + (this.slot != null ? this.slot.toString() : "") + "}";
    }

    @NotNull
    public static AttributeModifier deserialize(@NotNull Map<String, Object> args) {
        NamespacedKey key;
        if (args.containsKey("uuid")) {
            key = NamespacedKey.fromString((String)args.get("uuid"));
        } else {
            key = NamespacedKey.fromString((String)args.get("key"));
        }

        if (args.containsKey("slot")) {
            EquipmentSlotGroup slotGroup = EquipmentSlotGroup.getByName(args.get("slot").toString().toLowerCase(Locale.ROOT));
            if (slotGroup == null) {
                slotGroup = EquipmentSlotGroup.ANY;
                EquipmentSlot slot = EquipmentSlot.valueOf(args.get("slot").toString().toUpperCase(Locale.ROOT));
                if (slot != null) {
                    slotGroup = slot.getGroup();
                }
            }

            assert key != null;
            return new AttributeModifier(key, NumberConversions.toDouble(args.get("amount")), AttributeModifier.Operation.values()[NumberConversions.toInt(args.get("operation"))], slotGroup);
        } else {
            assert key != null;
            return new AttributeModifier(key, NumberConversions.toDouble(args.get("amount")), AttributeModifier.Operation.values()[NumberConversions.toInt(args.get("operation"))], EquipmentSlotGroup.ANY);
        }
    }

    public static enum Operation {
        ADD_NUMBER,
        ADD_SCALAR,
        MULTIPLY_SCALAR_1;

        private Operation() {
        }
    }
}
