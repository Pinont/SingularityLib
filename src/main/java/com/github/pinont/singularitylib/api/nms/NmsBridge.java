package com.github.pinont.singularitylib.api.nms;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

/**
 * Soft-loading bridge to the optional <b>SingularityNMS</b> module.
 *
 * <p>The lib is designed to run WITHOUT SingularityNMS installed: all NMS access is
 * funneled through this class, which reflectively probes for
 * {@code com.github.pinont.singularitynms.*} at runtime. When the NMS plugin (or jar)
 * is present on the server, deep vanilla-NBT operations become available; otherwise
 * the bridge reports {@link #isAvailable()} == false and callers degrade gracefully
 * (e.g. fall back to PersistentDataContainer).
 *
 * <p>Bootstrap-model note: SingularityNMS ships as its own jar; on a server with both
 * plugins installed, consumers get NMS-backed item/entity NBT through this neutral seam
 * with no compile-time dependency.
 */
public final class NmsBridge {

    private static final String NBT_CLASS = "com.github.pinont.singularitynms.nbt.NbtCompound";

    private static Boolean available;
    private static Class<?> nbtClass;
    private static Method ofItemStack;
    private static Method applyTo;

    private NmsBridge() {
    }

    /**
     * @return true if the SingularityNMS classes are loadable on this runtime
     */
    public static synchronized boolean isAvailable() {
        if (available == null) {
            try {
                nbtClass = Class.forName(NBT_CLASS);
                ofItemStack = nbtClass.getMethod("ofItemStack", ItemStack.class);
                applyTo = nbtClass.getMethod("applyTo", ItemStack.class);
                available = true;
            } catch (ClassNotFoundException e) {
                available = false;
            } catch (LinkageError e) {
                // NbtCompound is present but its net.minecraft.* dependencies aren't
                // resolvable in this context (e.g. lib-only test env without the NMS
                // server jar). Treat as unavailable — the NMS plugin provides them at
                // runtime on a real server.
                available = false;
            } catch (NoSuchMethodException e) {
                available = false;
            }
        }
        return available;
    }

    /**
     * Reads the vanilla NBT of an item as a raw Object (actually
     * {@code singularitynms.nbt.NbtCompound}) — usable via reflection only, or
     * {@code null} when NMS is unavailable.
     */
    @Nullable
    public static Object readItemNbt(ItemStack item) {
        if (!isAvailable() || item == null) {
            return null;
        }
        try {
            return ofItemStack.invoke(null, item);
        } catch (ReflectiveOperationException e) {
            return null;
        }
    }

    /**
     * Writes processed NBT back to an item via the NMS {@code applyTo(ItemStack)};
     * returns the new ItemStack (NMS treats Bukkit stacks as value copies) or the
     * original when NMS is unavailable.
     */
    public static ItemStack applyItemNbt(ItemStack item, Object nbt) {
        if (!isAvailable() || nbt == null) {
            return item;
        }
        try {
            return (ItemStack) applyTo.invoke(nbt, item);
        } catch (ReflectiveOperationException e) {
            return item;
        }
    }

    /**
     * Convenience: whether an item currently carries vanilla custom_data (best-effort;
     * only meaningful when NMS is available).
     */
    public static boolean hasVanillaNbt(ItemStack item) {
        return readItemNbt(item) != null;
    }
}