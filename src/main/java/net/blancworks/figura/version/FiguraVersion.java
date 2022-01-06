package net.blancworks.figura.version;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIntArray;

public class FiguraVersion {
    public int MAJOR = 0;
    public int MINOR = 0;
    public int PATCH = 0;

    public CompatibiltiyLevel lastCompatibilityLevel;
    public boolean lastWasCompatible;

    public FiguraVersion(int MAJOR, int MINOR, int PATCH) {
        this.MAJOR = MAJOR;
        this.MINOR = MINOR;
        this.PATCH = PATCH;
    }

    public CompatibiltiyLevel compatibilityLevel(FiguraVersion otherVersion) {
        lastCompatibilityLevel = getCompat(otherVersion);
        return lastCompatibilityLevel;
    }

    private CompatibiltiyLevel getCompat(FiguraVersion otherVersion) {
        //If other MAJOR is not the same as this version, it's incompatible.
        if (otherVersion.MAJOR != MAJOR)
            return CompatibiltiyLevel.IncompatibleMajor;

        //If other MINOR is lower than this version, it's incompatible.
        if (otherVersion.MINOR < MINOR)
            return CompatibiltiyLevel.IncompatibleMinor;

        //Either case is compatible at this point, so, flag!
        lastWasCompatible = true;

        //If other patch is higher than this patch, it should be compatible, but warn about it just in case.
        if (otherVersion.PATCH > PATCH)
            return CompatibiltiyLevel.WarningPatch;

        return CompatibiltiyLevel.Compatible;
    }

    public static FiguraVersion fromNBT(NbtIntArray numbers) {
        return new FiguraVersion(numbers.get(0).intValue(), numbers.get(1).intValue(), numbers.get(2).intValue());
    }

    public static NbtIntArray toNBT(FiguraVersion version) {
        return new NbtIntArray(new int[]{version.MAJOR, version.MINOR, version.PATCH});
    }

    public static FiguraVersion checkVersionInNBT(NbtCompound compound, FiguraVersion compare) {
        //Check avatar version number
        NbtIntArray element = (NbtIntArray) compound.get("ver");

        //If there's enough integers
        if (element != null && element.size() >= 3) {
            FiguraVersion ver = new FiguraVersion(element.get(0).intValue(), element.get(1).intValue(), element.get(2).intValue());
            ver.compatibilityLevel(compare);
            return ver;
        }
        return null;
    }

    public static void writeVersionToNbt(NbtCompound compound, FiguraVersion version) {
        compound.put("ver", toNBT(version));
    }

    public enum CompatibiltiyLevel {
        IncompatibleMajor,
        IncompatibleMinor,
        WarningPatch,
        Compatible
    }
}
