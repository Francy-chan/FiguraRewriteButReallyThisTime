package net.blancworks.figura.version;

import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.api.VersionParsingException;

public class FiguraVersion {

    private final SemanticVersion version;

    public FiguraVersion(String version) throws VersionParsingException {
        this(SemanticVersion.parse(version));
    }

    public FiguraVersion(SemanticVersion version) {
        this.version = version;
    }

    public SemanticVersion getVersion() {
        return this.version;
    }

    public CompatibiltiyLevel compareTo(SemanticVersion other) {
        //If other MAJOR is not the same as this version, it's incompatible.
        if (other.getVersionComponent(0) != this.version.getVersionComponent(0))
            return CompatibiltiyLevel.IncompatibleMajor;

        //If other MINOR is lower than this version, it's incompatible.
        if (other.getVersionComponent(1) < this.version.getVersionComponent(1))
            return CompatibiltiyLevel.IncompatibleMinor;

        //If other patch is higher than this patch, it should be compatible, but warn about it just in case.
        if (other.getVersionComponent(3) > this.version.getVersionComponent(3))
            return CompatibiltiyLevel.WarningPatch;

        return CompatibiltiyLevel.Compatible;
    }

    public enum CompatibiltiyLevel {
        IncompatibleMajor,
        IncompatibleMinor,
        WarningPatch,
        Compatible
    }
}
