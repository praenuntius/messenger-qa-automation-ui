package org.foxtrot.mobile.core.application.devices;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aeonbits.owner.ConfigFactory;
import org.foxtrot.mobile.config.MobileConfig;
import org.foxtrot.mobile.pojo.DeviceCapabilities;
import org.foxtrot.mobile.pojo.Devices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class DeviceManager {

    private static final Logger logger = LoggerFactory.getLogger(DeviceManager.class);
    private static final MobileConfig config = ConfigFactory.create(MobileConfig.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static DeviceCapabilities enabledDevice;

    private DeviceManager() {
    }

    public static DeviceCapabilities getEnabledDevice() {
        if (enabledDevice == null) {
            enabledDevice = loadEnabledDevice();
        }
        return enabledDevice;
    }

    private static DeviceCapabilities loadEnabledDevice() {
        Devices devices = getDevicesFromJSON();
        DeviceCapabilities[] deviceCapabilities = devices.devices();
        String platformName = System.getProperty("platform.name");
        if (!platformName.isBlank()) {
            return getDeviceByPlatform(deviceCapabilities, platformName);
        }
        return getEnabledDeviceFromJson(deviceCapabilities);
    }

    private static DeviceCapabilities getDeviceByPlatform(DeviceCapabilities[] devices, String platformName) {
        List<DeviceCapabilities> matchedDevices = Arrays.stream(devices)
                .filter(device -> device.getPlatform().equalsIgnoreCase(platformName))
                .toList();
        if (matchedDevices.isEmpty()) {
            throw new RuntimeException("В devices.json нет устройства для платформы: " + platformName);
        }
        if (matchedDevices.size() > 1) {
            throw new RuntimeException("В devices.json больше одного устройства для платформы: " + platformName);
        }
        DeviceCapabilities device = matchedDevices.get(0);
        logger.info("Выбран девайс по platform.name={}: {} {}", platformName, device.getPlatform(),
                device.getDeviceName());
        return device;
    }

    private static DeviceCapabilities getEnabledDeviceFromJson(DeviceCapabilities[] devices) {
        List<DeviceCapabilities> enabledDevices = Arrays.stream(devices)
                .filter(DeviceCapabilities::isEnabled)
                .toList();
        if (enabledDevices.isEmpty()) {
            throw new RuntimeException("В devices.json нет enabled=true девайса");
        }
        if (enabledDevices.size() > 1) {
            throw new RuntimeException("В devices.json больше одного enabled=true девайса");
        }
        DeviceCapabilities device = enabledDevices.get(0);
        logger.info("Выбран девайс из devices.json: {} {}", device.getPlatform(), device.getDeviceName());
        return device;
    }

    private static Devices getDevicesFromJSON() {
        try {
            Path path = Path.of(config.relativeDevicesPath()).toAbsolutePath().normalize();
            logger.info("Читаю devices.json: {}", path);
            return objectMapper.readValue(path.toFile(), Devices.class);
        } catch (Exception e) {
            throw new RuntimeException("Не удалось прочитать devices.json: " + e.getMessage(), e);
        }
    }
}
