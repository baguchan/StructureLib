# Structure Lib

This Library make Structure Easier in Better Than Adventure(Minecraft Beta 1.7.3 mod)

when structure you want save. you should use those comand

/structure_lib save <modid> <name> <x> <y> <z> <maxX> <maxY> <maxZ>

once saved place /assets/<modid>/structure

and

use like this
`

    RevampeStructure revampeStructure = new RevampeStructure(StructureLib.MOD_ID, new Class[]{}, "test", "test", true, true);
    revampeStructure.placeStructure(world, x, y, z);

`

## Required Mod

halplibe(https://modrinth.com/mod/halplibe)

catalyst(https://modrinth.com/mod/catalyst)
