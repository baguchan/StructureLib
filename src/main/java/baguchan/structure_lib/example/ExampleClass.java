package baguchan.structure_lib.example;

import baguchan.structure_lib.StructureLib;
import baguchan.structure_lib.util.RevampeStructure;
import net.minecraft.core.world.World;

public class ExampleClass {
	public void place(World world, int x, int y, int z) {
		RevampeStructure revampeStructure = new RevampeStructure(StructureLib.MOD_ID, new Class[]{}, "test", "test", true, true);
		revampeStructure.placeStructure(world, x, y, z);
	}
}
