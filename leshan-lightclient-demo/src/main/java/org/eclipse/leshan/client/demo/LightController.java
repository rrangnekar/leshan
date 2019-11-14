package org.eclipse.leshan.client.demo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.eclipse.leshan.client.request.ServerIdentity;
import org.eclipse.leshan.client.resource.BaseInstanceEnabler;
import org.eclipse.leshan.core.model.ObjectModel;
import org.eclipse.leshan.core.response.ExecuteResponse;
import org.eclipse.leshan.core.response.ReadResponse;
import org.eclipse.leshan.core.response.WriteResponse;
import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.util.NamedThreadFactory;

public class LightController extends BaseInstanceEnabler {

    // private static final String UNIT_CELSIUS = "cel";
    // private static final int SENSOR_VALUE = 5700;
    // private static final int UNITS = 5701;
    // private static final int MAX_MEASURED_VALUE = 5602;
    // private static final int MIN_MEASURED_VALUE = 5601;
    // private static final int RESET_MIN_MAX_MEASURED_VALUES = 5605;
    private static final int ON_OFF = 5850; //RW //true or false
    private static final int DIMMER = 5851; //RW //0-100
    private static final int ON_TIME = 5852; //RW //in seconds
    private static final int CUMULATIVE_ACTIVE_POWER = 5805; //R //Wh
    private static final int POWER_FACTOR = 5820; //R
    private static final int COLOR = 5706; //RW
    private static final int SENSOR_UNITS = 5701; //R
    private static final int APPLICATION_TYPE = 5750; //RW

    private static final List<Integer> supportedResources = Arrays.asList(ON_OFF, DIMMER, ON_TIME,
            CUMULATIVE_ACTIVE_POWER, POWER_FACTOR, COLOR, SENSOR_UNITS, APPLICATION_TYPE);
    // private final Random rng = new Random();
    // private double currentTemp = 20d;
    // private double minMeasuredValue = currentTemp;
    // private double maxMeasuredValue = currentTemp;

    private boolean state_on_off = false;
    private long state_dimmer = 50;
    private long state_on_time = 100;
    private float state_cumulative_active_power = 10;
    private float state_power_factor = 0.5f;
    private String state_color = "red";
    private String state_sensor_units = "cel";
    private String state_application_type = "Itron Streetlight";

    public LightController() {
    }

    @Override
    public synchronized ReadResponse read(ServerIdentity identity, int resourceId) {
        switch (resourceId) {
        case ON_OFF:
            return ReadResponse.success(resourceId, state_on_off);
        case DIMMER:
            return ReadResponse.success(resourceId, state_dimmer);
        case ON_TIME:
            return ReadResponse.success(resourceId, state_on_time);
        case CUMULATIVE_ACTIVE_POWER:
            return ReadResponse.success(resourceId, state_cumulative_active_power);
        case POWER_FACTOR:
            return ReadResponse.success(resourceId, state_power_factor);
        case COLOR:
            return ReadResponse.success(resourceId, state_color);
        case SENSOR_UNITS:
            return ReadResponse.success(resourceId, state_sensor_units);
        case APPLICATION_TYPE:
            return ReadResponse.success(resourceId, state_application_type);
        default:
            return super.read(identity, resourceId);
        }
    }

    @Override
    public WriteResponse write(ServerIdentity identity, int resourceid, LwM2mResource value) {
        switch (resourceid) {
        case ON_OFF:
            state_on_off = (boolean) value.getValue();
            System.out.println("Setting on_off state to: " + state_on_off);
            return WriteResponse.success();
        case DIMMER:
            state_dimmer = (long) value.getValue();
            System.out.println("Setting dimmer value to: " + state_dimmer);
            return WriteResponse.success();
        case ON_TIME:
            long on_time_val = (long) value.getValue();
            if (on_time_val == 0) {
                System.out.println("Resetting on time counter to 0");
                state_on_time = 0;
            } else {
                System.out.println("Unable to reset on_time counter");
            }
            return WriteResponse.success();
        case COLOR:
            state_color = (String) value.getValue();
            System.out.println("Setting light color to: " + state_color);
            return WriteResponse.success();
        case APPLICATION_TYPE:
            state_application_type = (String) value.getValue();
            System.out.println("Setting application_type to: " + state_application_type);
            return WriteResponse.success();
        default:
            return super.write(identity, resourceid, value);
        }
    }

    @Override
    public synchronized ExecuteResponse execute(ServerIdentity identity, int resourceId, String params) {
        return super.execute(identity, resourceId, params);
    }

    // private double getTwoDigitValue(double value) {
    //     BigDecimal toBeTruncated = BigDecimal.valueOf(value);
    //     return toBeTruncated.setScale(2, RoundingMode.HALF_UP).doubleValue();
    // }

    // private void adjustTemperature() {
    //     float delta = (rng.nextInt(20) - 10) / 10f;
    //     currentTemp += delta;
    //     Integer changedResource = adjustMinMaxMeasuredValue(currentTemp);
    //     if (changedResource != null) {
    //         fireResourcesChange(SENSOR_VALUE, changedResource);
    //     } else {
    //         fireResourcesChange(SENSOR_VALUE);
    //     }
    // }

    // private synchronized Integer adjustMinMaxMeasuredValue(double newTemperature) {
    //     if (newTemperature > maxMeasuredValue) {
    //         maxMeasuredValue = newTemperature;
    //         return MAX_MEASURED_VALUE;
    //     } else if (newTemperature < minMeasuredValue) {
    //         minMeasuredValue = newTemperature;
    //         return MIN_MEASURED_VALUE;
    //     } else {
    //         return null;
    //     }
    // }

    // private void resetMinMaxMeasuredValues() {
    //     minMeasuredValue = currentTemp;
    //     maxMeasuredValue = currentTemp;
    // }

    @Override
    public List<Integer> getAvailableResourceIds(ObjectModel model) {
        return supportedResources;
    }
}
