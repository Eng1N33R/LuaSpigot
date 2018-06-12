package net.engin33r.luaspigot.citizens.lua;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.DespawnReason;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.engin33r.luaspigot.lua.LinkedField;
import net.engin33r.luaspigot.lua.TableUtils;
import net.engin33r.luaspigot.lua.WrapperType;
import net.engin33r.luaspigot.lua.annotation.DynamicFieldDefinition;
import net.engin33r.luaspigot.lua.annotation.MethodDefinition;
import net.engin33r.luaspigot.lua.type.LuaEntity;
import net.engin33r.luaspigot.lua.type.LuaLocation;
import net.engin33r.luaspigot.lua.type.util.LuaUUID;
import org.bukkit.entity.EntityType;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.luaj.vm2.*;

import java.util.HashMap;
import java.util.Map;

public class LuaNPC extends WrapperType<NPC> {
    private static final LuaValue typeMetatable = LuaValue.tableOf();

    private static final Map<String, Class<? extends LuaNPCTrait>>
            traitAdapters = new HashMap<>();

    public LuaNPC(NPC npc) {
        super(npc);

        registerField("uuid", new LuaUUID(npc.getUniqueId()));
        registerField("fullName", LuaString.valueOf(npc.getFullName()));
        registerLinkedField("value", new NameField(this));
        registerLinkedField("flyable", new FlyableField(this));
        registerLinkedField("protected", new ProtectedField(this));
    }

    @SuppressWarnings("unused")
    public static void registerTraitAdapter(String name, Class<? extends
            LuaNPCTrait> clazz) {
        traitAdapters.put(name, clazz);
    }

    @Override
    protected LuaValue getMetatable() {
        return typeMetatable;
    }

    public String getName() {
        return "npc";
    }

    @Override
    public String toLuaString() {
        return "npc: " + getHandle().getName() + " (" + getHandle()
                .getUniqueId() + ")";
    }

    @DynamicFieldDefinition("id")
    public LuaValue getID() {
        return LuaNumber.valueOf(getHandle().getId());
    }

    @DynamicFieldDefinition("entity")
    public LuaValue getEntity() {
        return new LuaEntity(getHandle().getEntity());
    }

    @DynamicFieldDefinition("traits")
    public LuaValue getTraits() {
        return TableUtils.tableFrom(getHandle().getTraits(), trait ->
                LuaString.valueOf(CitizensAPI.getTraitFactory().getTrait
                        (trait.getClass()).getName()));
    }

    @DynamicFieldDefinition("spawned")
    public LuaValue getSpawned() {
        return LuaBoolean.valueOf(getHandle().isSpawned());
    }

    @DynamicFieldDefinition("lastLocation")
    public LuaValue getLastLocation() {
        return new LuaLocation(getHandle().getStoredLocation());
    }

    @MethodDefinition("spawn")
    public Varargs spawn(Varargs args) {
        return LuaBoolean.valueOf(getHandle().spawn(((LuaLocation) args
                .checktable(1)).getHandle()));
    }

    @MethodDefinition("despawn")
    public Varargs despawn(Varargs args) {
        return LuaBoolean.valueOf(getHandle().despawn(DespawnReason.valueOf
                (args.optjstring(1, "PLUGIN"))));
    }

    @MethodDefinition("teleport")
    public Varargs teleport(Varargs args) {
        getHandle().teleport(((LuaLocation) args.checktable(1)).getHandle(),
                PlayerTeleportEvent.TeleportCause.valueOf(
                        args.optjstring(2, "PLUGIN")));
        return NIL;
    }

    @MethodDefinition("face")
    public Varargs face(Varargs args) {
        getHandle().faceLocation(
                ((LuaLocation) args.checktable(1)).getHandle());
        return NIL;
    }

    @MethodDefinition("setEntityType")
    public Varargs setEntityType(Varargs args) {
        getHandle().setBukkitEntityType(
                EntityType.valueOf(args.checkjstring(1)));
        return NIL;
    }

    @MethodDefinition("addTrait")
    public Varargs addTrait(Varargs args) {
        getHandle().addTrait(
                CitizensAPI.getTraitFactory().getTrait(args.checkjstring(1)));
        return NIL;
    }

    @MethodDefinition("removeTrait")
    public Varargs removeTrait(Varargs args) {
        getHandle().removeTrait(
                CitizensAPI.getTraitFactory().getTrait(args.checkjstring(1))
                        .getClass());
        return NIL;
    }

    @MethodDefinition("hasTrait")
    public Varargs hasTrait(Varargs args) {
        Trait t = CitizensAPI.getTraitFactory().getTrait(args.checkjstring(1));
        if (t == null) return FALSE;
        return LuaBoolean.valueOf(getHandle().hasTrait(t.getClass()));
    }

    @MethodDefinition("getTrait")
    public Varargs getTrait(Varargs args) {
        String name = args.checkjstring(1);

        Class<? extends Trait> clazz = CitizensAPI.getTraitFactory().getTrait
                (name).getClass();
        Trait trait = getHandle().getTrait(clazz);

        try {
            Class<? extends LuaNPCTrait> lclass = traitAdapters.get(name);
            if (lclass == null) lclass = LuaNPCTrait.class;
            return lclass.getConstructor(clazz).newInstance(trait);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return NIL;
    }

    private class NameField extends LinkedField<LuaNPC> {
        NameField(LuaNPC self) {
            super(self);
        }

        @Override
        public void update(LuaValue val) {
            getHandle().setName(val.tojstring());
        }

        @Override
        public LuaValue query() {
            return LuaString.valueOf(getHandle().getName());
        }
    }

    private class FlyableField extends LinkedField<LuaNPC> {
        FlyableField(LuaNPC self) {
            super(self);
        }

        @Override
        public void update(LuaValue val) {
            getHandle().setFlyable(val.checkboolean());
        }

        @Override
        public LuaValue query() {
            return LuaBoolean.valueOf(getHandle().isFlyable());
        }
    }

    private class ProtectedField extends LinkedField<LuaNPC> {
        ProtectedField(LuaNPC self) {
            super(self);
        }

        @Override
        public void update(LuaValue val) {
            getHandle().setProtected(val.checkboolean());
        }

        @Override
        public LuaValue query() {
            return LuaBoolean.valueOf(getHandle().isProtected());
        }
    }
}
