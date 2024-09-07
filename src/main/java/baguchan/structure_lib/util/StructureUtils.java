package baguchan.structure_lib.util;

import baguchan.structure_lib.StructureLib;
import com.mojang.nbt.CompoundTag;
import com.mojang.nbt.ListTag;
import com.mojang.nbt.NbtIo;
import net.minecraft.client.Minecraft;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.World;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class StructureUtils {
	public static boolean saveStructure(World world, String modid, String name, int originX, int originY, int originZ, int maxX, int maxY, int maxZ) {
		CompoundTag compoundTag = new CompoundTag();
		int temp;
		if (originX > maxX) {
			temp = originX;
			originX = maxX;
			maxX = temp;
		}
		if (originY > maxY) {
			temp = originY;
			originY = maxY;
			maxY = temp;
		}
		if (originZ > maxZ) {
			temp = originZ;
			originZ = maxZ;
			maxZ = temp;
		}

		Vec3i origin = new Vec3i(originX, originY, originZ);
		Vec3i originMax = new Vec3i(maxX, maxY, maxZ);
		if (MathHelper.abs(originMax.x - origin.x) > 40) {
			return false;
		}
		if (MathHelper.abs(originMax.y - origin.y) > 40) {
			return false;
		}
		if (MathHelper.abs(originMax.z - origin.z) > 40) {
			return false;
		}


		List<BlockInstance> blocks = addBlocks(world, origin, originMax);
		ListTag blocksTag = new ListTag();
		ListTag tileTag = new ListTag();
		ListTag decorationTag = new ListTag();
		blocks.forEach(blockInstance -> {
			if (blockInstance.tile != null) {
				CompoundTag compoundTag1 = new CompoundTag();
				CompoundTag posTag = new CompoundTag();
				CompoundTag tileData = new CompoundTag();
				blockInstance.tile.writeToNBT(tileData);
				blockInstance.pos.writeToNBT(posTag);
				compoundTag1.putString("id", blockInstance.block.getKey());
				compoundTag1.putInt("meta", blockInstance.meta);
				compoundTag1.put("pos", posTag);
				compoundTag1.put("tile_data", tileData);
				tileTag.addTag(compoundTag1);
			} else if (blockInstance.block.blockMaterial == Material.decoration) {
				CompoundTag compoundTag1 = new CompoundTag();
				CompoundTag posTag = new CompoundTag();
				CompoundTag tileData = new CompoundTag();
				blockInstance.tile.writeToNBT(tileData);
				blockInstance.pos.writeToNBT(posTag);
				compoundTag1.putString("id", blockInstance.block.getKey());
				compoundTag1.putInt("meta", blockInstance.meta);
				compoundTag1.put("pos", posTag);
				compoundTag1.put("tile_data", tileData);
				decorationTag.addTag(compoundTag1);
			} else {
				CompoundTag compoundTag1 = new CompoundTag();
				CompoundTag posTag = new CompoundTag();
				blockInstance.pos.writeToNBT(posTag);
				compoundTag1.putString("id", blockInstance.block.getKey());
				compoundTag1.putInt("meta", blockInstance.meta);
				compoundTag1.put("pos", posTag);
				blocksTag.addTag(compoundTag1);
			}
		});
		compoundTag.put("Blocks", blocksTag);
		compoundTag.put("TileEntities", tileTag);
		compoundTag.put("Decorations", decorationTag);
		compoundTag.putInt("SizeX", (int) MathHelper.abs(originMax.x - origin.x) + 1);
		compoundTag.putInt("SizeY", (int) MathHelper.abs(originMax.y - origin.y) + 1);
		compoundTag.putInt("SizeZ", (int) MathHelper.abs(originMax.z - origin.z) + 1);
		RevampStructure structure = new RevampStructure(modid, name, compoundTag, false, true);
		saveToNbt(Minecraft.getMinecraft(Minecraft.class).getMinecraftDir(), name, structure);
		return true;
	}

	private static List<BlockInstance> addBlocks(World world, Vec3i origin, Vec3i originMax) {
		List<BlockInstance> list = new ArrayList<>();
		for (int x = origin.x; x <= originMax.x; x++) {
			for (int y = origin.y; y <= originMax.y; y++) {
				for (int z = origin.z; z <= originMax.z; z++) {
					Vec3i vec3i = new Vec3i(x - origin.x, y - origin.y, z - origin.z);
					Block block = world.getBlock(x, y, z);
					if (block != null) {
						BlockInstance blockInstance = new BlockInstance(block, vec3i, world.getBlockMetadata(x, y, z), world.getBlockTileEntity(x, y, z));
						list.add(blockInstance);
					}
				}
			}
		}
		return list;
	}

	public static void saveToNbt(Minecraft minecraft, String name, RevampStructure structure) {
		saveToNbt(minecraft.getMinecraftDir(), name, structure);
	}

	public static void saveToNbt(File directory, String name, RevampStructure structure) {
		try {

			File dir = new File(directory, name + ".nbt");
			OutputStream resource = Files.newOutputStream(dir.toPath());

			try {
				if (resource != null) {
					NbtIo.writeCompressed(structure.data, resource);
					StructureLib.LOGGER.info(String.format("Structure '%s' saved.", name));
				}
			} catch (Throwable var6) {
				if (resource != null) {
					try {
						resource.close();
					} catch (Throwable var5) {
						var6.addSuppressed(var5);
					}
				}

				throw var6;
			}

			if (resource != null) {
				resource.close();
			}
		} catch (IOException var7) {
			IOException e = var7;
			e.printStackTrace();
		}

	}


}
