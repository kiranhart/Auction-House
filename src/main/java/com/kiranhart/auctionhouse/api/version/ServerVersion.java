package com.kiranhart.auctionhouse.api.version;
/*
    The current file was created by Kiran Hart
    Date: August 04 2019
    Time: 2:28 PM
    
    Code within this class is not to be redistributed without proper permission.
*/

public enum ServerVersion {

    NOT_SUPPORTED("unknown_server_version"),
    V1_7("org.bukkit.craftbukkit.v1_7"),
    V1_8("org.bukkit.craftbukkit.v1_8"),
    V1_9("org.bukkit.craftbukkit.v1_9"),
    V1_10("org.bukkit.craftbukkit.v1_10"),
    V1_11("org.bukkit.craftbukkit.v1_11"),
    V1_12("org.bukkit.craftbukkit.v1_12"),
    V1_13("org.bukkit.craftbukkit.v1_13"),
    V1_14("org.bukkit.craftbukkit.v1_14"),
    V1_15("org.bukkit.craftbukkit.v1_15"),
    V1_16("org.bukkit.craftbukkit.v1_16");

    private String versionName;

    ServerVersion(String version) {
        this.versionName = version;
    }

    public static ServerVersion fromPackageName(String packageName) {
        for (ServerVersion version : values())
            if (packageName.startsWith(version.versionName)) return version;
        return ServerVersion.NOT_SUPPORTED;
    }

}
