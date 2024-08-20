package baguchan.structure_lib.util;

import net.minecraft.core.util.helper.Axis;

public enum Rotation {
	NONE("none"),
	CLOCKWISE_90("clockwise_90"),
	CLOCKWISE_180("180"),
	COUNTERCLOCKWISE_90("counterclockwise_90");

	private final String id;

	private Rotation(String p_221988_) {
		this.id = p_221988_;
	}

	public Rotation getRotated(Rotation p_55953_) {
		switch (p_55953_) {
			case CLOCKWISE_180:
				switch (this) {
					case NONE:
						return CLOCKWISE_180;
					case CLOCKWISE_90:
						return COUNTERCLOCKWISE_90;
					case CLOCKWISE_180:
						return NONE;
					case COUNTERCLOCKWISE_90:
						return CLOCKWISE_90;
				}
			case COUNTERCLOCKWISE_90:
				switch (this) {
					case NONE:
						return COUNTERCLOCKWISE_90;
					case CLOCKWISE_90:
						return NONE;
					case CLOCKWISE_180:
						return CLOCKWISE_90;
					case COUNTERCLOCKWISE_90:
						return CLOCKWISE_180;
				}
			case CLOCKWISE_90:
				switch (this) {
					case NONE:
						return CLOCKWISE_90;
					case CLOCKWISE_90:
						return CLOCKWISE_180;
					case CLOCKWISE_180:
						return COUNTERCLOCKWISE_90;
					case COUNTERCLOCKWISE_90:
						return NONE;
				}
			default:
				return this;
		}
	}

	public Direction rotate(Direction p_55955_) {
		if (p_55955_.getAxis() == Axis.Y) {
			return p_55955_;
		} else {
			switch (this) {
				case CLOCKWISE_90:
					return p_55955_.rotate(1);
				case CLOCKWISE_180:
					return p_55955_.getOpposite();
				case COUNTERCLOCKWISE_90:
					return p_55955_.rotate(1).getOpposite();
				default:
					return p_55955_;
			}
		}
	}

	public int rotate(int p_55950_, int p_55951_) {
		switch (this) {
			case CLOCKWISE_90:
				return (p_55950_ + p_55951_ / 4) % p_55951_;
			case CLOCKWISE_180:
				return (p_55950_ + p_55951_ / 2) % p_55951_;
			case COUNTERCLOCKWISE_90:
				return (p_55950_ + p_55951_ * 3 / 4) % p_55951_;
			default:
				return p_55950_;
		}
	}
}
