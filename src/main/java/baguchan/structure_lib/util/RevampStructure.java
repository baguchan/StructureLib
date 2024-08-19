package baguchan.structure_lib.util;

import com.mojang.nbt.*;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.world.World;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

public class RevampStructure {
	public String modId;
	public String translateKey;
	public String filePath;
	public CompoundTag data;
	public boolean placeAir;
	public boolean replaceBlocks;

	public RevampStructure(String modId, String translateKey, CompoundTag data, boolean placeAir, boolean replaceBlocks) {
		this.modId = modId;
		this.translateKey = "structure." + modId + "." + translateKey + ".name";
		this.data = data;
		this.filePath = null;
		this.placeAir = placeAir;
		this.replaceBlocks = replaceBlocks;
	}

	public RevampStructure(String modId, String translateKey, String filePath, boolean placeAir, boolean replaceBlocks) {
		this.modId = modId;
		this.translateKey = "structure." + modId + "." + translateKey + ".name";
		this.placeAir = placeAir;
		this.replaceBlocks = replaceBlocks;
		this.loadFromNBT(filePath);
	}

	public boolean placeStructure(World world, int originX, int originY, int originZ) {
		Vec3i origin = new Vec3i(originX, originY, originZ);
		Vec3i originSize = new Vec3i(originX + this.getSizeX(), originY + this.getSizeY(), originZ + this.getSizeZ());
		ArrayList<BlockInstance> blocks = this.getBlocks(origin);
		Iterator var7 = blocks.iterator();

		BlockInstance block;

		if (this.replaceBlocks) {
			makeEmptySpaceWithRotate(world, origin, originSize);
		}

		while (var7.hasNext()) {
			block = (BlockInstance) var7.next();
			world.setBlockAndMetadataWithNotify(block.pos.x, block.pos.y, block.pos.z, block.block.id, block.meta);
		}

		ArrayList<BlockInstance> tiles = this.getTileEntities(origin);

		Iterator var8 = tiles.iterator();

		BlockInstance tileBlocks;

		int i = 0;

		while (var8.hasNext()) {
			tileBlocks = (BlockInstance) var8.next();
			world.setBlockAndMetadataWithNotify(tileBlocks.pos.x, tileBlocks.pos.y, tileBlocks.pos.z, tileBlocks.block.id, tileBlocks.meta);
			TileEntity tileentity = world.getBlockTileEntity(tileBlocks.pos.x, tileBlocks.pos.y, tileBlocks.pos.z);

			tileentity.readFromNBT(getTileEntitiesData(i));
			i++;
		}

		return true;
	}

	public boolean placeStructure(World world, int originX, int originY, int originZ, String direction) {
		Direction dir = Direction.getFromName(direction);
		if (dir == null) {
			return false;
		} else {
			Vec3i origin = new Vec3i(originX, originY, originZ);
			ArrayList<BlockInstance> blocks = this.getBlocks(origin, dir);
			Iterator var7 = blocks.iterator();
			Vec3i rotate = (new Vec3i(this.getSizeX(), this.getSizeY(), this.getSizeZ()).rotate(origin, dir));
			if (this.replaceBlocks) {
				this.makeEmptySpaceWithRotate(world, origin, rotate);
			}

			BlockInstance block;
			while (var7.hasNext()) {
				block = (BlockInstance) var7.next();
				world.setBlockAndMetadataWithNotify(block.pos.x, block.pos.y, block.pos.z, block.block.id, block.meta);
			}

			ArrayList<BlockInstance> tiles = this.getTileEntities(world, origin, dir);

			Iterator var8 = tiles.iterator();

			BlockInstance tileBlocks;

			while (var8.hasNext()) {
				tileBlocks = (BlockInstance) var8.next();
				world.setBlockAndMetadataWithNotify(tileBlocks.pos.x, tileBlocks.pos.y, tileBlocks.pos.z, tileBlocks.block.id, tileBlocks.meta);

				TileEntity tileentity = TileEntity.createAndLoadEntity(getTileEntitiesData(tiles.indexOf(tileBlocks)));
				world.setBlockTileEntity(tileBlocks.pos.x, tileBlocks.pos.y, tileBlocks.pos.z, tileentity);

			}
			return false;
		}
	}


	private void makeEmptySpaceWithRotate(World world, Vec3i origin, Vec3i rotate) {
		Vec3i origin2 = origin.copy();
		Vec3i rotate2 = rotate.copy();

		int temp;
		if (origin2.x > rotate2.x) {
			temp = origin2.x;
			origin2.x = rotate2.x;
			rotate2.x = temp;
		}
		if (origin2.y > rotate2.y) {
			temp = origin2.y;
			origin2.y = rotate2.y;
			rotate2.y = temp;
		}
		if (origin2.z > rotate2.z) {
			temp = origin2.z;
			origin2.z = rotate2.z;
			rotate2.z = temp;
		}
		for (int x = origin2.x; x <= rotate2.x; x++) {
			for (int y = origin2.y; y <= rotate2.y; y++) {
				for (int z = origin2.z; z <= rotate2.z; z++) {
					world.setBlock(x, y, z, 0);
				}
			}
		}

	}

	public BlockInstance getOrigin() {
		CompoundTag blockTag = this.data.getCompound("Origin");
		int meta = blockTag.getInteger("meta");
		int id = getBlockId(blockTag);
		Block block = Block.getBlock(id);
		return new BlockInstance(block, new Vec3i(), meta, (TileEntity) null);
	}

	public BlockInstance getOrigin(Vec3i origin) {
		CompoundTag blockTag = this.data.getCompound("Origin");
		int meta = blockTag.getInteger("meta");
		int id = getBlockId(blockTag);
		Block block = Block.getBlock(id);
		return new BlockInstance(block, origin, meta, (TileEntity) null);
	}

	public BlockInstance getOrigin(World world, Vec3i origin) {
		CompoundTag blockTag = this.data.getCompound("Origin");
		Vec3i pos = new Vec3i(blockTag.getCompound("pos"));
		int meta = blockTag.getInteger("meta");
		int id = getBlockId(blockTag);
		Block block = Block.getBlock(id);
		return new BlockInstance(block, pos, meta, world.getBlockTileEntity(pos.x, pos.y, pos.z));
	}

	public CompoundTag getTileEntitiesData(int index) {
		ListTag tag = this.data.getList("TileEntities");

		if (tag.tagCount() > 0 && tag.tagAt(index) != null) {
			CompoundTag nbttagcompound = (CompoundTag) tag.tagAt(index);
			return nbttagcompound.getCompound("tile_data");
		}

		return new CompoundTag();
	}

	public ArrayList<BlockInstance> getTileEntities() {
		ArrayList<BlockInstance> tiles = new ArrayList();
		Iterator var2 = this.data.getList("TileEntities").iterator();

		while (var2.hasNext()) {
			Tag<?> tag = (Tag) var2.next();
			CompoundTag tileEntity = (CompoundTag) tag;
			Vec3i pos = new Vec3i(tileEntity.getCompound("pos"));
			int meta = tileEntity.getInteger("meta");
			int id = getBlockId(tileEntity);
			Block block = Block.getBlock(id);
			BlockInstance blockInstance = new BlockInstance(block, pos, meta, (TileEntity) null);
			tiles.add(blockInstance);
		}

		return tiles;
	}

	public ArrayList<BlockInstance> getTileEntities(Vec3i origin) {
		ArrayList<BlockInstance> tiles = new ArrayList();
		Iterator var3 = this.data.getList("TileEntities").iterator();

		while (var3.hasNext()) {
			Tag<?> tag = (Tag) var3.next();
			CompoundTag tileEntity = (CompoundTag) tag;
			Vec3i pos = (new Vec3i(tileEntity.getCompound("pos"))).add(origin);
			int meta = tileEntity.getInteger("meta");
			int id = getBlockId(tileEntity);
			Block block = Block.getBlock(id);
			BlockInstance blockInstance = new BlockInstance(block, pos, meta, (TileEntity) null);
			tiles.add(blockInstance);
		}

		return tiles;
	}

	public ArrayList<BlockInstance> getTileEntities(World world, Vec3i origin) {
		ArrayList<BlockInstance> tiles = new ArrayList();
		Iterator var4 = this.data.getList("TileEntities").iterator();

		while (var4.hasNext()) {
			Tag<?> tag = (Tag) var4.next();
			CompoundTag tileEntity = (CompoundTag) tag;
			Vec3i pos = (new Vec3i(tileEntity.getCompound("pos"))).add(origin);
			int meta = tileEntity.getInteger("meta");
			int id = getBlockId(tileEntity);
			Block block = Block.getBlock(id);
			BlockInstance blockInstance = new BlockInstance(block, pos, meta, world.getBlockTileEntity(pos.x, pos.y, pos.z));
			tiles.add(blockInstance);
		}

		return tiles;
	}

	public ArrayList<BlockInstance> getTileEntities(World world, Vec3i origin, Direction dir) {
		ArrayList<BlockInstance> tiles = new ArrayList();
		Iterator var5 = this.data.getList("Blocks").iterator();

		while (var5.hasNext()) {
			Tag<?> tag = (Tag) var5.next();
			CompoundTag blockTag = (CompoundTag) tag;
			Vec3i pos = (new Vec3i(blockTag.getCompound("pos"))).rotate(origin, dir);
			int meta = blockTag.getInteger("meta");
			if (meta != -1) {
				if (dir == Direction.Z_NEG) {
					meta = Direction.getDirectionFromSide(meta).getOpposite().getSideNumber();
				} else if (dir == Direction.X_NEG || dir == Direction.X_POS) {
					Direction blockDir = Direction.getDirectionFromSide(meta);
					blockDir = blockDir != Direction.X_NEG && blockDir != Direction.X_POS ? blockDir.rotate(1) : blockDir.rotate(1).getOpposite();
					meta = dir == Direction.X_NEG ? blockDir.getSideNumber() : blockDir.getOpposite().getSideNumber();
				}
			}

			int id = getBlockId(blockTag);
			Block block = Block.getBlock(id);
			BlockInstance blockInstance = new BlockInstance(block, pos, meta, world.getBlockTileEntity(pos.x, pos.y, pos.z));
			tiles.add(blockInstance);
		}

		return tiles;
	}

	public ArrayList<BlockInstance> getBlocks() {
		ArrayList<BlockInstance> tiles = new ArrayList();
		Iterator var2 = this.data.getList("Blocks").iterator();

		while (var2.hasNext()) {
			Tag<?> tag = (Tag) var2.next();
			CompoundTag blockTag = (CompoundTag) tag;
			Vec3i pos = new Vec3i(blockTag.getCompound("pos"));
			int meta = blockTag.getInteger("meta");
			int id = getBlockId(blockTag);
			Block block = Block.getBlock(id);
			BlockInstance blockInstance = new BlockInstance(block, pos, meta, (TileEntity) null);
			tiles.add(blockInstance);
		}

		return tiles;
	}

	public ArrayList<BlockInstance> getBlocks(Vec3i origin) {
		ArrayList<BlockInstance> tiles = new ArrayList();
		Iterator var3 = this.data.getList("Blocks").iterator();

		while (var3.hasNext()) {
			Tag<?> tag = (Tag) var3.next();
			CompoundTag blockTag = (CompoundTag) tag;
			Vec3i pos = (new Vec3i(blockTag.getCompound("pos"))).add(origin);
			int meta = blockTag.getInteger("meta");
			int id = getBlockId(blockTag);
			Block block = Block.getBlock(id);
			BlockInstance blockInstance = new BlockInstance(block, pos, meta, (TileEntity) null);
			tiles.add(blockInstance);
		}

		return tiles;
	}

	public ArrayList<BlockInstance> getBlocks(Vec3i origin, Direction dir) {
		ArrayList<BlockInstance> tiles = new ArrayList();
		Iterator var4 = this.data.getList("Blocks").iterator();

		while (var4.hasNext()) {
			Tag<?> tag = (Tag) var4.next();
			CompoundTag blockTag = (CompoundTag) tag;
			Vec3i pos = (new Vec3i(blockTag.getCompound("pos"))).rotate(origin, dir);
			int meta = blockTag.getInteger("meta");
			if (meta != -1) {
				if (dir == Direction.Z_NEG) {
					meta = Direction.getDirectionFromSide(meta).getOpposite().getSideNumber();
				} else if (dir == Direction.X_NEG || dir == Direction.X_POS) {
					Direction blockDir = Direction.getDirectionFromSide(meta);
					blockDir = blockDir != Direction.X_NEG && blockDir != Direction.X_POS ? blockDir.rotate(1) : blockDir.rotate(1).getOpposite();
					meta = dir == Direction.X_NEG ? blockDir.getSideNumber() : blockDir.getOpposite().getSideNumber();
				}
			}

			int id = getBlockId(blockTag);
			Block block = Block.getBlock(id);
			BlockInstance blockInstance = new BlockInstance(block, pos, meta, (TileEntity) null);
			tiles.add(blockInstance);
		}

		return tiles;
	}

	public int getSizeX() {
		if (this.data.containsKey("SizeX")) {
			return this.data.getInteger("SizeX");
		}

		return 0;
	}

	public int getSizeY() {
		if (this.data.containsKey("SizeY")) {
			return this.data.getInteger("SizeY");
		}

		return 0;
	}

	public int getSizeZ() {
		if (this.data.containsKey("SizeZ")) {
			return this.data.getInteger("SizeZ");
		}

		return 0;
	}

	public ArrayList<BlockInstance> getSubstitutions() {
		ArrayList<BlockInstance> tiles = new ArrayList();
		Iterator var2 = this.data.getCompound("Substitutions").getValues().iterator();

		while (var2.hasNext()) {
			Tag<?> tag = (Tag) var2.next();
			CompoundTag sub = (CompoundTag) tag;
			Vec3i pos = new Vec3i(sub.getCompound("pos"));
			int meta = sub.getInteger("meta");
			int id = getBlockId(sub);
			Block block = Block.getBlock(id);
			BlockInstance blockInstance = new BlockInstance(block, pos, meta, (TileEntity) null);
			tiles.add(blockInstance);
		}

		return tiles;
	}

	public ArrayList<BlockInstance> getSubstitutions(Vec3i origin) {
		ArrayList<BlockInstance> tiles = new ArrayList();
		Iterator var3 = this.data.getCompound("Substitutions").getValues().iterator();

		while (var3.hasNext()) {
			Tag<?> tag = (Tag) var3.next();
			CompoundTag sub = (CompoundTag) tag;
			Vec3i pos = (new Vec3i(sub.getCompound("pos"))).add(origin);
			int meta = sub.getInteger("meta");
			int id = getBlockId(sub);
			Block block = Block.getBlock(id);
			BlockInstance blockInstance = new BlockInstance(block, pos, meta, (TileEntity) null);
			tiles.add(blockInstance);
		}

		return tiles;
	}

	public ArrayList<BlockInstance> getSubstitutions(Vec3i origin, Direction dir) {
		ArrayList<BlockInstance> tiles = new ArrayList();
		Iterator var4 = this.data.getCompound("Substitutions").getValues().iterator();

		while (var4.hasNext()) {
			Tag<?> tag = (Tag) var4.next();
			CompoundTag tileEntity = (CompoundTag) tag;
			Vec3i pos = (new Vec3i(tileEntity.getCompound("pos"))).rotate(origin, dir);
			int meta = tileEntity.getInteger("meta");
			if (meta != -1) {
				if (dir == Direction.Z_NEG) {
					meta = Direction.getDirectionFromSide(meta).getOpposite().getSideNumber();
				} else if (dir == Direction.X_NEG || dir == Direction.X_POS) {
					Direction blockDir = Direction.getDirectionFromSide(meta);
					blockDir = blockDir != Direction.X_NEG && blockDir != Direction.X_POS ? blockDir.rotate(1) : blockDir.rotate(1).getOpposite();
					meta = dir == Direction.X_NEG ? blockDir.getSideNumber() : blockDir.getOpposite().getSideNumber();
				}
			}

			int id = getBlockId(tileEntity);
			Block block = Block.getBlock(id);
			BlockInstance blockInstance = new BlockInstance(block, pos, meta, (TileEntity) null);
			tiles.add(blockInstance);
		}

		return tiles;
	}

	public static int getBlockId(CompoundTag block) {
		Tag<?> nbt = block.getTag("id");
		if (nbt instanceof IntTag) {
			return (Integer) ((IntTag) nbt).getValue();
		} else if (nbt instanceof StringTag) {
			String args = ((String) ((StringTag) nbt).getValue());
			Block b = Block.getBlockByName(args);
			return b.id;
		} else {
			return 0;
		}
	}

	protected void loadFromNBT(String name) {
		try {
			InputStream resource = this.getClass().getResourceAsStream("/assets/" + this.modId + "/structures/" + name + ".nbt");

			try {
				if (resource != null) {
					this.data = NbtIo.readCompressed(resource);
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
