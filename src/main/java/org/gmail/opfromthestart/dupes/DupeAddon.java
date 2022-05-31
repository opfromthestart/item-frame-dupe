package org.gmail.opfromthestart.dupes;

import org.gmail.opfromthestart.dupes.modules.AnotherExample;
import org.gmail.opfromthestart.dupes.modules.ItemFrameDupe;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

public class DupeAddon extends MeteorAddon {
	public static final Logger LOG = LoggerFactory.getLogger(DupeAddon.class);
	public static final Category CATEGORY = new Category("Dupes");

	@Override
	public void onInitialize() {
		LOG.info("Initializing Meteor Addon Template");

		// Required when using @EventHandler
		MeteorClient.EVENT_BUS.registerLambdaFactory("org.gmail.opfromthestart.dupes", (lookupInMethod, klass) -> (MethodHandles.Lookup) lookupInMethod.invoke(null, klass, MethodHandles.lookup()));

		// Modules
		Modules.get().add(new ItemFrameDupe());


		// Modules.get().add(new AnotherExample());

		// Commands
		// Commands.get().add(new ExampleCommand());

		// HUD
        // HUD.get().elements.add(new HudExample());
	}

	@Override
	public void onRegisterCategories() {
		Modules.registerCategory(CATEGORY);
	}
}
