package baguchan.structure_lib.command;

import baguchan.structure_lib.util.StructureUtils;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;
import net.minecraft.core.net.command.PlayerCommandSender;

public class StructureSaveCommand extends Command {
	public StructureSaveCommand(String name, String... alts) {
		super(name, alts);
	}

	public boolean execute(CommandHandler commandHandler, CommandSender commandSender, String[] args) {
		if (commandHandler.isServer()) {
			commandSender.sendMessage("This command can only be used in singleplayer!");
			return true;
		}
		if (commandSender instanceof PlayerCommandSender) {
			EntityPlayer player = commandSender.getPlayer();
			if (args.length > 0) {


				if (args.length == 9 && args[0].equals("save")) {

					try {
						if (StructureUtils.saveStructure(player.world, args[1], args[2], Integer.parseInt(args[3]), Integer.parseInt(args[4]), Integer.parseInt(args[5]), Integer.parseInt(args[6]), Integer.parseInt(args[7]), Integer.parseInt(args[8]))) {
							commandSender.sendMessage(String.format("Structure '%s' saved!", args[2]));
						} else {
							commandSender.sendMessage(String.format("Structure '%s' is Too big than 40 range", args[2]));
						}


						return true;
					} catch (NumberFormatException var10) {
						commandSender.sendMessage("Invalid coordinates provided!");
						return true;
					}
				}
			}
		}

		return false;
	}

	public boolean opRequired(String[] args) {
		return true;
	}

	public void sendCommandSyntax(CommandHandler commandHandler, CommandSender commandSender) {
		if (commandSender instanceof PlayerCommandSender) {
			commandSender.sendMessage("/structure_lib save <modid> <name> <x> <y> <z> <maxX> <maxY> <maxZ>");
		}
	}
}
