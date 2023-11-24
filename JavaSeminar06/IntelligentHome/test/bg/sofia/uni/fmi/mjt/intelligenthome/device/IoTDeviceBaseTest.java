package bg.sofia.uni.fmi.mjt.intelligenthome.device;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class IoTDeviceBaseTest {
    static IoTDeviceBase deviceBase;

    @BeforeAll
    static void setup() {
        deviceBase = new AmazonAlexa("testAlexa", 10, LocalDateTime.now().minusHours(2));
        deviceBase.setRegistration(LocalDateTime.now().minusHours(5));
    }

    @Test
    void testGetRegistration() {
        assertEquals(5, deviceBase.getRegistration());
    }

    @Test
    void testGetPowerConsumptionKWh() {
        assertEquals(20, deviceBase.getPowerConsumptionKWh());
    }

    @Test
    void testGetName() {
        assertEquals("testAlexa", deviceBase.getName());
    }

    @Test
    void testGetPowerConsumption() {
        assertEquals(10, deviceBase.getPowerConsumption());
    }
}
