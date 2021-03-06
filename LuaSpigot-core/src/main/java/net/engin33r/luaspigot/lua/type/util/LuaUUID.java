package net.engin33r.luaspigot.lua.type.util;

import net.engin33r.luaspigot.lua.TypeUtils;
import net.engin33r.luaspigot.lua.WrapperType;
import net.engin33r.luaspigot.lua.annotation.MetatableMethodDefinition;
import org.luaj.vm2.LuaBoolean;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.UUID;

/**
 * Weak type to definitely represent UUIDs.
 */
public class LuaUUID extends WrapperType<UUID> {
    private static final LuaValue typeMetatable = LuaValue.tableOf();

    public LuaUUID(UUID uuid) {
        super(uuid);
    }

    public LuaUUID(String uuid) {
        super(UUID.fromString(uuid));
    }

    @Override
    public String getName() {
        return "uuid";
    }

    @Override
    public String toLuaString() {
        return getHandle().toString();
    }

    @MetatableMethodDefinition("__eq")
    public Varargs equals(Varargs arg) {
        TypeUtils.validate(arg.checktable(1), "uuid");
        TypeUtils.validate(arg.checktable(2), "uuid");

        LuaUUID uuid1 = (LuaUUID) arg.checktable(1);
        LuaUUID uuid2 = (LuaUUID) arg.checktable(2);
        return LuaBoolean.valueOf(uuid1.getHandle().equals(uuid2.getHandle()));
    }

    @Override
    protected LuaValue getMetatable() {
        return typeMetatable;
    }
}
