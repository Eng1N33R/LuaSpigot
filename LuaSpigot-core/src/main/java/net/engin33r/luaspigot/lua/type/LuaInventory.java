package net.engin33r.luaspigot.lua.type;

import net.engin33r.luaspigot.lua.WeakType;
import net.engin33r.luaspigot.lua.annotation.MethodDef;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.Map;

/**
 * Wrapper type describing an inventory (chests, players etc.)
 */
public class LuaInventory extends WeakType {
    private static LuaValue typeMetatable = LuaValue.tableOf();

    private Inventory inv;

    public LuaInventory(Inventory inv) {
        this.inv = inv;

        registerField("title", LuaValue.valueOf(inv.getTitle()));
        registerField("name", LuaValue.valueOf(inv.getName()));
        registerField("type", LuaValue.valueOf(inv.getType().name()
                .toLowerCase()));
    }

    @Override
    protected LuaValue getMetatable() {
        return typeMetatable;
    }

    public Inventory getInventory() {
        return this.inv;
    }

    public ItemStack[] getContents() {
        return this.inv.getContents();
    }

    @Override
    public String getName() {
        return "inventory";
    }

    @MethodDef(name = "get")
    public Varargs get(Varargs arg) {
        ItemStack stack = this.inv.getItem(arg.checkint(1));

        return new LuaItem(stack);
    }

    @MethodDef(name = "add")
    public Varargs add(Varargs arg) {
        Map<Integer, ItemStack> failed = this.inv.addItem(((LuaItem) arg
                .checktable(1)).getItem());
        LuaTable tbl = LuaValue.tableOf();
        for (Integer key : failed.keySet()) {
            tbl.set(LuaValue.valueOf(key), new LuaItem(failed.get(key)));
        }
        return tbl;
    }

    @MethodDef(name = "set")
    public Varargs set(Varargs arg) {
        this.inv.setItem(arg.checkint(1), ((LuaItem) arg.checktable(2))
                .getItem());
        return NIL;
    }

    @MethodDef(name = "setAll")
    public Varargs setAll(Varargs arg) {
        LuaTable tbl = arg.checktable(1);
        int size = tbl.length();
        ItemStack[] contents = new ItemStack[size];
        for (int i = 1; i <= size; i++) {
            contents[i-1] = ((LuaItem) tbl.get(i).checktable()).getItem();
        }
        this.inv.setContents(contents);
        return NIL;
    }
}