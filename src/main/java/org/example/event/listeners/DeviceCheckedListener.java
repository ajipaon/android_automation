package org.example.event.listeners;
import org.example.event.EventListener;
import org.example.event.events.DeviceCheckedEvent;
import org.example.model.Device;
import org.example.model.dto.DeviceMinimalDto;
import org.example.service.DeviceService;
import org.example.service.JobExecutor;
import org.example.utils.Logger;
import java.time.LocalDate;
public class DeviceCheckedListener implements EventListener<DeviceCheckedEvent> {
    private final Logger logger = Logger.getInstance();
    private final DeviceService deviceService = new DeviceService();
    @Override
    public void onEvent(DeviceCheckedEvent event) {
        if (event.hasDevices()) {
            logger.info("[DeviceCheckedListener] %d device internal terhubung: %s", event.getDevices().size(), event.getDevices());
            for (DeviceMinimalDto deviceMinimalDto : event.getDevices()){
                Device cekDevice = deviceService.getDeviceByDeviceId(deviceMinimalDto.getDeviceId());
                if(cekDevice == null){
                    Device device = new Device();
                    device.setDeviceId(deviceMinimalDto.getDeviceId());
                    device.setDeviceName(deviceMinimalDto.getDeviceName());
                    device.setModel(deviceMinimalDto.getModel());
                    device.setAndroidVersion(deviceMinimalDto.getAndroidVersion());
                    device.setInternal(true);
                    device.setCreatedAt(LocalDate.now());
                    deviceService.createDevice(device);
                }
            }
        } else {
            logger.warn("[DeviceCheckedListener] Tidak ada device terhubung pada %s",
                    event.getTimestamp());
        }
    }
    @Override
    public Class<DeviceCheckedEvent> getEventType() {
        return DeviceCheckedEvent.class;
    }
}