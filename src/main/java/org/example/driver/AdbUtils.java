package org.example.driver;
import org.example.model.dto.DeviceMinimalDto;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class AdbUtils {
    public static List<DeviceMinimalDto> getConnectedDevices() {
        List<DeviceMinimalDto> devices = new ArrayList<>();
        try {
            List<String> deviceIds = getDeviceIds();
            for (String deviceId : deviceIds) {
                DeviceMinimalDto device = new DeviceMinimalDto();
                device.setDeviceId(deviceId);
                String deviceName = getDeviceProperty(deviceId, "ro.product.model");
                device.setDeviceName(deviceName);
                device.setModel(deviceName); 
                String androidVersion = getDeviceProperty(deviceId, "ro.build.version.release");
                device.setAndroidVersion(androidVersion);
                devices.add(device);
            }
        } catch (Exception e) {
            throw new RuntimeException("Gagal membaca informasi device", e);
        }
        return devices;
    }
    private static List<String> getDeviceIds() throws Exception {
        List<String> devices = new ArrayList<>();
        ProcessBuilder pb = new ProcessBuilder("adb", "devices");
        pb.redirectErrorStream(true);
        Process process = pb.start();
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream())
        );
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.endsWith("\tdevice")) {
                String deviceId = line.split("\\t")[0];
                devices.add(deviceId);
            }
        }
        process.waitFor();
        return devices;
    }
    private static String getDeviceProperty(String deviceId, String property) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "adb", "-s", deviceId, "shell", "getprop", property
            );
            pb.redirectErrorStream(true);
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), "UTF-8")
            );
            String result = reader.readLine();
            process.waitFor();
            return result != null ? result.trim() : "";
        } catch (Exception e) {
            System.err.println("Gagal mendapatkan property " + property +
                    " untuk device " + deviceId + ": " + e.getMessage());
            return "";
        }
    }
}