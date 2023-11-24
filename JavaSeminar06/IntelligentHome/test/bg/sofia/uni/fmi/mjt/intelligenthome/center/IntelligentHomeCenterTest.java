package bg.sofia.uni.fmi.mjt.intelligenthome.center;

import bg.sofia.uni.fmi.mjt.intelligenthome.center.comparator.KWhComparator;
import bg.sofia.uni.fmi.mjt.intelligenthome.center.comparator.RegistrationComparator;
import bg.sofia.uni.fmi.mjt.intelligenthome.center.exceptions.DeviceAlreadyRegisteredException;
import bg.sofia.uni.fmi.mjt.intelligenthome.center.exceptions.DeviceNotFoundException;
import bg.sofia.uni.fmi.mjt.intelligenthome.device.AmazonAlexa;
import bg.sofia.uni.fmi.mjt.intelligenthome.device.DeviceType;
import bg.sofia.uni.fmi.mjt.intelligenthome.device.IoTDevice;
import bg.sofia.uni.fmi.mjt.intelligenthome.device.RgbBulb;
import bg.sofia.uni.fmi.mjt.intelligenthome.device.WiFiThermostat;
import bg.sofia.uni.fmi.mjt.intelligenthome.storage.DeviceStorage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class IntelligentHomeCenterTest {
    @Mock
    private DeviceStorage storage;
    @Mock
    private IoTDevice device;
    @InjectMocks
    private IntelligentHomeCenter homeCenter;

    private static List<IoTDevice> deviceList;

    @BeforeAll
    static void setUp(){
         deviceList = List.of(
            new AmazonAlexa("testAmazon1", 10.5, LocalDateTime.now().minusHours(10)),
            new RgbBulb("testrgbbulb", 12.5, LocalDateTime.now().minusHours(20)),
            new WiFiThermostat("testWifi", 20.5, LocalDateTime.now().minusHours(50)),
            new AmazonAlexa("testAmazon2", 16.5, LocalDateTime.now().minusHours(30))
        );
         deviceList.get(0).setRegistration(LocalDateTime.now().minusHours(10));
         deviceList.get(1).setRegistration(LocalDateTime.now().minusHours(100));
         deviceList.get(2).setRegistration(LocalDateTime.now().minusHours(50));
         deviceList.get(3).setRegistration(LocalDateTime.now().minusHours(20));
    }

    @Test
    void testRegisterThrowsIllegalArgExcWhenDeviceIsNull() {
        assertThrows(IllegalArgumentException.class, () -> homeCenter.register(null),
            "register method should throw IllegalArgumentException when device is null");
    }

    @Test
    void testRegisterThrowsDeviceAlreadyExistsExc() {
        when(storage.exists(device.getId())).thenReturn(true);
        assertThrows(DeviceAlreadyRegisteredException.class, () -> homeCenter.register(device),
            "register method should throw DeviceAlreadyRegisteredException when device already exists");
    }

    @Test
    void testRegisterSuccessfully() {
        when(storage.exists(device.getId())).thenReturn(false);
        assertDoesNotThrow(() -> homeCenter.register(device),
            "register method should not throw exception");
        verify(storage).store(device.getId(), device);
    }

    @Test
    void testUnregisterThrowsIllegalArgExcWhenDeviceIsNull() {
        assertThrows(IllegalArgumentException.class, () -> homeCenter.unregister(null),
            "unregister method should throw IllegalArgumentException when device is null");
    }

    @Test
    void testUnregisterThrowsDeviceNotFoundExc() {
        when(storage.exists(device.getId())).thenReturn(false);
        assertThrows(DeviceNotFoundException.class, () -> homeCenter.unregister(device),
            "unregister method should throw DeviceNotFoundException when device cannot be found!");
    }

    @Test
    void testUnregisterSuccessfully() {
        when(storage.exists(device.getId())).thenReturn(true);
        assertDoesNotThrow(() -> homeCenter.unregister(device),
            "unregister method should not exception!");
        verify(storage).delete(device.getId());
    }

    @Test
    void testGetDeviceByIdWhenIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> homeCenter.getDeviceById(null),
            "Get device by id should throw exception when id is null");
    }

    @Test
    void testGetDeviceByIdWhenIdIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> homeCenter.getDeviceById(" "),
            "Get device by id should throw exception when id is empty");
    }

    @Test
    void testGetDeviceByIdThrowsDeviceNotFoundExc() {
        when(device.getId()).thenReturn("TestId");
        when(storage.exists(device.getId())).thenReturn(false);
        assertThrows(DeviceNotFoundException.class, () -> homeCenter.getDeviceById("TestId"),
            "Get device by id should throw exception when device is not found");
    }

    @Test
    void testGetDeviceByIdSuccessfully() throws DeviceNotFoundException {
        when(device.getId()).thenReturn("TestId");
        when(storage.exists(device.getId())).thenReturn(true);
        when(storage.get("TestId")).thenReturn(device);
        assertDoesNotThrow(() -> homeCenter.getDeviceById("TestId"));
        assertEquals(device, homeCenter.getDeviceById(device.getId()));
    }

    @Test
    void testGetDeviceQuantityPerTypeThrowsIllegalArgumentExc() {
        assertThrows(IllegalArgumentException.class, () -> homeCenter.getDeviceQuantityPerType(null),
            "Get device quantity per type should throw exception when type is null");
    }

    @Test
    void testGetDeviceQuantityPerTypeSuccessfully() {
        when(storage.listAll()).thenReturn(deviceList);
        assertDoesNotThrow(() -> homeCenter.getDeviceQuantityPerType(DeviceType.SMART_SPEAKER));
        assertEquals(2, homeCenter.getDeviceQuantityPerType(DeviceType.SMART_SPEAKER));
    }

    @Test
    void testGetTopNDevicesByPowerConsumptionThrowsExcWhenNegative(){
        assertThrows(IllegalArgumentException.class, () -> homeCenter.getTopNDevicesByPowerConsumption(-5),
            "GetTopNDevicesByPowerConsumption should throw Illegal Argument Exception!");
    }

    @Test
    void testGetTopNDevicesByPowerConsumptionSuccessfully(){
        when(storage.listAll()).thenReturn(deviceList);
        List<IoTDevice> expected = new ArrayList<>(deviceList);
        expected = expected.subList(2, 4);
        Collection<String> expectedIds = new ArrayList<>();
        for (var e: expected){
            expectedIds.add(e.getId());
        }
        assertIterableEquals(expectedIds, homeCenter.getTopNDevicesByPowerConsumption(2),
            "GetTopNDevicesByPowerConsumption should return the top 2 devices by consumption");
    }

    @Test
    void testGetTopNDevicesByPowerConsumptionSuccessfullyWithGreaterN(){
        when(storage.listAll()).thenReturn(deviceList);
        List<IoTDevice> expected = new ArrayList<>(deviceList);
        expected.sort(Collections.reverseOrder(new KWhComparator()));
        Collection<String> expectedIds = new ArrayList<>();
        for (var e: expected){
            expectedIds.add(e.getId());
        }
        assertIterableEquals(expectedIds, homeCenter.getTopNDevicesByPowerConsumption(5),
            "GetTopNDevicesByPowerConsumption should return all devices");
    }

    @Test
    void testGetFirstNDevicesByRegistrationThrowsIllegalArgumentException(){
        assertThrows(IllegalArgumentException.class, () -> homeCenter.getFirstNDevicesByRegistration(-5),
            "Get First N Devices By Registration should throw Illegal Argument Exception!");
    }

    @Test
    void testGetFirstNDevicesByRegistrationSuccessfully(){
        when(storage.listAll()).thenReturn(deviceList);
        List<IoTDevice> expected = new ArrayList<>(deviceList);
        expected = expected.subList(1, 3);
        assertIterableEquals(expected, homeCenter.getFirstNDevicesByRegistration(2),
            "Get First N Devices By Registration should return the top 2 devices");
    }

    @Test
    void testGetFirstNDevicesByRegistrationSuccessfullyWithGreaterN(){
        when(storage.listAll()).thenReturn(deviceList);
        List<IoTDevice> expected = new ArrayList<>(deviceList);
        expected.sort(Collections.reverseOrder(new RegistrationComparator()));
        assertIterableEquals(expected, homeCenter.getFirstNDevicesByRegistration(5),
            "GetTopNDevicesByPowerConsumption should return all devices");
    }
}

