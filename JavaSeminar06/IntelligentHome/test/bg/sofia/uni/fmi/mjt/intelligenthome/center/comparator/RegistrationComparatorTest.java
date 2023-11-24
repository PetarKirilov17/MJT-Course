package bg.sofia.uni.fmi.mjt.intelligenthome.center.comparator;

import bg.sofia.uni.fmi.mjt.intelligenthome.device.IoTDevice;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RegistrationComparatorTest {
    @Mock
    private IoTDevice firstDevice;
    @Mock
    private IoTDevice secondDevice;
    @InjectMocks
    private RegistrationComparator comparator;

    @Test
    void testCompareDevices() {
        when(firstDevice.getRegistration()).thenReturn(1L);
        when(secondDevice.getRegistration()).thenReturn(2L);
        assertTrue(comparator.compare(firstDevice, secondDevice) < 0,
            "Comparison by registration should work properly!");
    }
}
