package org.example.event.events;
import org.example.event.AppEvent;
import org.example.model.dto.DeviceMinimalDto;
import java.util.List;
public class DeviceCheckedEvent extends AppEvent {
    private final List<DeviceMinimalDto> devices;
    public DeviceCheckedEvent(Object source, List<DeviceMinimalDto> devices) {
        super(source);
        this.devices = devices;
    }
    public List<DeviceMinimalDto> getDevices() {
        return devices;
    }
    public boolean hasDevices() {
        return devices != null && !devices.isEmpty();
    }
}