package net.engin33r.luaspigot.lua.type;

import net.engin33r.luaspigot.lua.TableUtils;
import net.engin33r.luaspigot.lua.TypeUtils;
import net.engin33r.luaspigot.lua.WrapperType;
import net.engin33r.luaspigot.lua.annotation.LinkedFieldAccessorDefinition;
import net.engin33r.luaspigot.lua.annotation.LinkedFieldMutatorDefinition;
import net.engin33r.luaspigot.lua.annotation.MethodDefinition;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.luaj.vm2.*;

/**
 * Wrapper type that represents an item stack.
 */
public class LuaItem extends WrapperType<ItemStack> {
    private static final LuaValue typeMetatable = LuaValue.tableOf();

    public static class LuaItemMeta extends WrapperType<ItemMeta> {
        private static final LuaValue typeMetatable = LuaValue.tableOf();

        public LuaItemMeta(ItemStack item, ItemMeta handle) {
            super(handle);

            registerLinkedField("value",
                    val -> {
                        handle.setDisplayName(val.checkjstring());
                        item.setItemMeta(handle);
                    },
                    () -> LuaString.valueOf(handle.getDisplayName()));
            registerLinkedField("lore",
                    val -> {
                        handle.setLore(TableUtils.listFrom(
                                val.checktable(), String::valueOf));
                        item.setItemMeta(handle);
                    },
                    () -> TableUtils.tableFrom(handle.getLore(),
                            LuaString::valueOf));
        }

        @MethodDefinition("addEnchantment")
        public Varargs addEnchantment(Varargs arg) {
            getHandle().addEnchant(Enchantment.getByName(arg.checkjstring(1)),
                    arg.checkint(2), arg.checkboolean(3));
            return NIL;
        }

        @MethodDefinition("hasEnchantment")
        public Varargs hasEnchantment(Varargs arg) {
            return LuaBoolean.valueOf(getHandle()
                    .hasEnchant(Enchantment.getByName(arg.checkjstring(1))));
        }

        @MethodDefinition("removeEnchantment")
        public Varargs removeEnchantment(Varargs arg) {
            getHandle().removeEnchant(
                    Enchantment.getByName(arg.checkjstring(1)));
            return NIL;
        }

        @Override
        protected LuaValue getMetatable() {
            return typeMetatable;
        }

        @Override
        public String getName() {
            return "itemmeta";
        }
    }

    public LuaItem(ItemStack item) {
        super(item);

        registerField("meta", new LuaItemMeta(item, item.getItemMeta()));
    }

    public LuaItem(Item item) {
        super(item.getItemStack());
    }

    public LuaItem(String name, int n) {
        this(new ItemStack(Material.getMaterial(name.toUpperCase()), n));
    }

    @Override
    protected LuaValue getMetatable() {
        return typeMetatable;
    }

    @Override
    public String getName() {
        return "item";
    }

    @LinkedFieldMutatorDefinition("material")
    public void setType(LuaValue val) {
        getHandle().setType(TypeUtils.getEnum(val, Material.class));
    }

    @LinkedFieldAccessorDefinition("material")
    public LuaValue getType() {
        ItemStack item = getHandle();
        return (item == null || item.getType() == null) ? NIL :
                LuaString.valueOf(item.getType().toString());
    }

    @LinkedFieldMutatorDefinition("durability")
    public void setDurability(LuaValue val) {
        getHandle().setDurability(val.checknumber().toshort());
    }

    @LinkedFieldAccessorDefinition("durability")
    public LuaValue getDurability() {
        return LuaNumber.valueOf(getHandle().getDurability());
    }

    @LinkedFieldMutatorDefinition("amount")
    public void setAmount(LuaValue val) {
        getHandle().setAmount(val.checkint());
    }

    @LinkedFieldAccessorDefinition("amount")
    public LuaValue getAmount() {
        return LuaNumber.valueOf(getHandle().getAmount());
    }
}
