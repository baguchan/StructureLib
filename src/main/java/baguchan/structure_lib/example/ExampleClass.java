package baguchan.structure_lib.example;

import baguchan.structure_lib.StructureLib;
import baguchan.structure_lib.util.RevampStructure;
import net.minecraft.core.world.World;

public class ExampleClass {
	public void place(World world, int x, int y, int z) {
		RevampStructure revampStructure = new RevampStructure(StructureLib.MOD_ID, "test", "test", true, true);
		revampStructure.placeStructure(world, x, y, z);
	}
}
