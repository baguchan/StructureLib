package baguchan.structure_lib;

import baguchan.structure_lib.command.StructureSaveCommand;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.helper.CommandHelper;
import turniplabs.halplibe.util.GameStartEntrypoint;
import turniplabs.halplibe.util.RecipeEntrypoint;


public class StructureLib implements ModInitializer, GameStartEntrypoint, RecipeEntrypoint {
	public static final String MOD_ID = "structure_lib";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);


	@Override
    public void onInitialize() {
		LOGGER.info("Structure Lib initialized.");
		CommandHelper.createCommand(new StructureSaveCommand("structure_lib"));
    }

	@Override
	public void beforeGameStart() {

	}

	@Override
	public void afterGameStart() {

	}

	@Override
	public void onRecipesReady() {

	}

	@Override
	public void initNamespaces() {

	}
}
