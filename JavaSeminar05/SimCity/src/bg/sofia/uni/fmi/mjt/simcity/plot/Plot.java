package bg.sofia.uni.fmi.mjt.simcity.plot;

import bg.sofia.uni.fmi.mjt.simcity.exception.BuildableNotFoundException;
import bg.sofia.uni.fmi.mjt.simcity.exception.BuildableAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.simcity.exception.InsufficientPlotAreaException;
import bg.sofia.uni.fmi.mjt.simcity.property.buildable.Buildable;
import java.util.HashMap;
import java.util.Map;

public class Plot<E extends Buildable> implements PlotAPI<E> {
    private int buildableArea;
    private final int startingBuildArea;
    private Map<String, E> buildables;

    public Plot(int buildableArea) {
        this.startingBuildArea = buildableArea;
        this.buildableArea = buildableArea;
        buildables = new HashMap<>();
    }

    @Override
    public void construct(String address, E buildable) {
        if (address == null) {
            throw new IllegalArgumentException("Address cannot be null!");
        }
        if (address.isBlank()) {
            throw new IllegalArgumentException("Address cannot be empty");
        }
        if (buildable == null) {
            throw new IllegalArgumentException("Buildable cannot be null!");
        }
        if (buildables.containsKey(address)) {
            throw new BuildableAlreadyExistsException("Address is already occupied on the plot!");
        }
        if (buildable.getArea() > buildableArea) {
            throw new InsufficientPlotAreaException("The required area exceeds the remaining plot area!");
        }
        this.buildables.put(address, buildable);
        this.buildableArea -= buildable.getArea();
    }

    @Override
    public void constructAll(Map<String, E> buildables) {
        if (buildables == null || buildables.isEmpty()) {
            throw new IllegalArgumentException("Map of buildables is null, empty.");
        }
        var keySet = buildables.keySet();
        for (var key : keySet) {
            if (this.buildables.containsKey(key)) {
                throw new BuildableAlreadyExistsException("Address is already occupied on the plot!");
            }
        }
        var valuesSet = buildables.values();
        long allAreas = 0;
        for (var v : valuesSet) {
            allAreas += v.getArea();
        }
        if (allAreas > this.buildableArea) {
            throw new InsufficientPlotAreaException("The required area exceeds the remaining plot area!");
        }
        this.buildables.putAll(buildables);
        this.buildableArea -= allAreas;
    }

    @Override
    public void demolish(String address) {
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("The provided address is null or blank.");
        }
        if (!this.buildables.containsKey(address)) {
            throw new BuildableNotFoundException("Buildable with such address does not exist on the plot.");
        }
        this.buildableArea += this.buildables.get(address).getArea();
        this.buildables.remove(address);
    }

    @Override
    public void demolishAll() {
        this.buildables.clear();
        this.buildableArea = this.startingBuildArea;
    }

    @Override
    public Map<String, E> getAllBuildables() {
        return Map.copyOf(this.buildables);
    }

    @Override
    public int getRemainingBuildableArea() {
        return this.buildableArea;
    }
}
