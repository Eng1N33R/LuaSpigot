package net.engin33r.luaspigot.lua.lib;

import net.engin33r.luaspigot.lua.Library;
import net.engin33r.luaspigot.lua.TypeValidator;
import net.engin33r.luaspigot.lua.annotation.LibFunctionDef;
import net.engin33r.luaspigot.lua.type.LuaItem;
import net.engin33r.luaspigot.lua.type.LuaPlayer;
import net.engin33r.luaspigot.lua.type.util.LuaColor;
import net.engin33r.luaspigot.lua.type.util.LuaUUID;
import net.engin33r.luaspigot.lua.type.util.LuaVector;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.UUID;

/**
 * General-purpose utility library for creating and discovering Spigot objects
 */
public class BukkitUtilsLibrary extends Library {
    @Override
    public String getName() {
        return "bukkit";
    }

    @LibFunctionDef(name = "new", module = "color")
    public Varargs newColor(Varargs args) {
        return new LuaColor(args.checkint(1), args.checkint(2),
                args.checkint(3));
    }

    @LibFunctionDef(name = "new", module = "vector")
    public Varargs newVector(Varargs args) {
        return new LuaVector(args.checkdouble(1), args.checkdouble(2),
                args.checkdouble(3));
    }

    @LibFunctionDef(name = "new", module = "item")
    public Varargs newItem(Varargs args) {
        return new LuaItem(args.checkjstring(1), args.optint(2, 1));
    }

    @LibFunctionDef(name = "getByName", module = "player")
    @SuppressWarnings("deprecation")
    public Varargs getPlayerByName(Varargs args) {
        String name = args.checkjstring(1);
        if (args.optboolean(2, false)) {
            return new LuaPlayer(Bukkit.getOfflinePlayer(name));
        } else {
            return new LuaPlayer(Bukkit.getPlayer(name));
        }
    }

    @LibFunctionDef(name = "getByUUID", module = "player")
    public Varargs getPlayerByUUID(Varargs args) {
        UUID uuid;
        if (args.isstring(1)) {
            uuid = UUID.fromString(args.tojstring(1));
        } else if (args.istable(1) &&
                TypeValidator.is(args.checktable(1), "uuid")) {
            uuid = ((LuaUUID) args.checktable(1)).getHandle();
        } else {
            LuaValue.error("string or table expected, got "
                    + args.arg(1).type());
            return LuaValue.NIL;
        }

        return new LuaPlayer(Bukkit.getOfflinePlayer(uuid));
    }

    @LibFunctionDef(name = "broadcast", module = "chat")
    public Varargs broadcast(Varargs args) {
        int n = args.narg();
        if (n == 0) return LuaValue.NIL;

        String str = "";
        for (int i = 1; i <= args.narg(); i++) {
            if (i > 1) str += "\t";
            str += args.checkjstring(i);
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(str);
        }
        return LuaValue.NIL;
    }
}