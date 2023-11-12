package bg.sofia.uni.fmi.mjt.simcity.utility;

import bg.sofia.uni.fmi.mjt.simcity.property.billable.Billable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class UtilityService implements UtilityServiceAPI {
    private final Map<UtilityType, Double> taxRates;

    public UtilityService(Map<UtilityType, Double> taxRates) {
        this.taxRates = taxRates;
    }

    @Override
    public <T extends Billable> double getUtilityCosts(UtilityType utilityType, T billable) {
        if (utilityType == null) {
            throw new IllegalArgumentException("Utility type cannot be null!");
        }
        if (billable == null) {
            throw new IllegalArgumentException("Billable cannot be null!");
        }
        double taxRate = this.taxRates.get(utilityType);
        switch (utilityType) {
            case WATER -> taxRate *= billable.getWaterConsumption();
            case ELECTRICITY -> taxRate *= billable.getElectricityConsumption();
            case NATURAL_GAS -> taxRate *= billable.getNaturalGasConsumption();
            default -> {
                return 0.0;
            }
        }
        return taxRate;
    }

    @Override
    public <T extends Billable> double getTotalUtilityCosts(T billable) {
        if (billable == null) {
            throw new IllegalArgumentException("The billable cannot be null.");
        }
        double total = 0;
        total += this.taxRates.get(UtilityType.WATER) * billable.getWaterConsumption();
        total += this.taxRates.get(UtilityType.ELECTRICITY) * billable.getElectricityConsumption();
        total += this.taxRates.get(UtilityType.NATURAL_GAS) * billable.getNaturalGasConsumption();
        return total;
    }

    @Override
    public <T extends Billable> Map<UtilityType, Double> computeCostsDifference(T firstBillable, T secondBillable) {
        if (firstBillable == null || secondBillable == null) {
            throw new IllegalArgumentException("Billable cannot be null!");
        }
        Map<UtilityType, Double> result = new HashMap<>();

        double firstBillableWater = getUtilityCosts(UtilityType.WATER, firstBillable);
        double secondBillableWater = getUtilityCosts(UtilityType.WATER, secondBillable);
        double waterDiff = Math.abs(firstBillableWater - secondBillableWater);
        result.put(UtilityType.WATER, waterDiff);

        double firstBillableEl = getUtilityCosts(UtilityType.ELECTRICITY, firstBillable);
        double secondBillableEl = getUtilityCosts(UtilityType.ELECTRICITY, secondBillable);
        double elDiff = Math.abs(firstBillableEl - secondBillableEl);
        result.put(UtilityType.ELECTRICITY, elDiff);

        double firstBillableGas = getUtilityCosts(UtilityType.NATURAL_GAS, firstBillable);
        double secondBillableGas = getUtilityCosts(UtilityType.NATURAL_GAS, secondBillable);
        double gasDiff = Math.abs(firstBillableGas - secondBillableGas);
        result.put(UtilityType.NATURAL_GAS, gasDiff);

        return Collections.unmodifiableMap(result);
    }
}
