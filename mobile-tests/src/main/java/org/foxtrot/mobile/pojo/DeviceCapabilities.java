package org.foxtrot.mobile.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceCapabilities {

    private boolean enabled;
    private String platform;
    private String deviceName;
    private String platformVersion;
    private String language;
    private String locale;
    private String driverName;
}
