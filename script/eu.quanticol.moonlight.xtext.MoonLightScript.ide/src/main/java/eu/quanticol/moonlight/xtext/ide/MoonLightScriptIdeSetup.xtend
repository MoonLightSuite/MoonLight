/*
 * generated by Xtext 2.18.0.M3
 */
package eu.quanticol.moonlight.xtext.ide

import com.google.inject.Guice
import eu.quanticol.moonlight.xtext.MoonLightScriptRuntimeModule
import eu.quanticol.moonlight.xtext.MoonLightScriptStandaloneSetup
import org.eclipse.xtext.util.Modules2

/**
 * Initialization support for running Xtext languages as language servers.
 */
class MoonLightScriptIdeSetup extends MoonLightScriptStandaloneSetup {

	override createInjector() {
		Guice.createInjector(Modules2.mixin(new MoonLightScriptRuntimeModule, new MoonLightScriptIdeModule))
	}
	
}