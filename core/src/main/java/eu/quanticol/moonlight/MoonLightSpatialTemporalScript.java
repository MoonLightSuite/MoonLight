/**
 *
 */
package eu.quanticol.moonlight;

import eu.quanticol.moonlight.formula.SignalDomain;
import eu.quanticol.moonlight.signal.DataHandler;

import java.lang.reflect.InvocationTargetException;

/**
 * @author loreti
 *
 */
public abstract class MoonLightSpatialTemporalScript implements MoonLightScript {

    private final String[] spatialTemporalMonitors;
    private SignalDomain<?> domain;

    public MoonLightSpatialTemporalScript(String[] spatialTemporalMonitors) {
        this.spatialTemporalMonitors = spatialTemporalMonitors;
    }

    public abstract SpatialTemporalScriptComponent<?> selectSpatialTemporalComponent(String name);

    public abstract SpatialTemporalScriptComponent<?> selectDefaultSpatialTemporalComponent();

    public void setMonitoringDomain(SignalDomain<?> domain) {
        this.domain = domain;
    }

    public SignalDomain<?> getMonitoringDomain() {
        return this.domain;
    }


    public String[] getSpatialTemporalMonitors() {
        return spatialTemporalMonitors;
    }

    public String getInfoSpatialTemporalMonitor(String name) {
        SpatialTemporalScriptComponent<?> c = selectSpatialTemporalComponent(name);
        if (c != null) {
            return c.getInfo();
        } else {
            return "Spatial-temporal monitor " + name + " is unknown!";
        }
    }

    public String getInfoDefaultSpatialTemporalMonitor() {
        SpatialTemporalScriptComponent<?> c = selectDefaultSpatialTemporalComponent();
        if (c != null) {
            return c.getInfo();
        } else {
            return "There it not any default spatial temporal monitor!";
        }
    }

    public static MoonLightSpatialTemporalScript forName(String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        Class<?> scriptClass = MoonLightSpatialTemporalScript.class.getClassLoader().loadClass(className);
        return (MoonLightSpatialTemporalScript) scriptClass.getDeclaredConstructor().newInstance();
    }

	@Override
	public boolean isTemporal() {
		return false;
	}

	@Override
	public boolean isSpatialTemporal() {
		return true;
	}

	@Override
	public MoonLightSpatialTemporalScript spatialTemporal() {
		return this;
	}


}
