package simulation;

import simulation.configs.SimulationConfig;

public record SimulationPreset(String name, SimulationConfig simulationConfig){
    @Override
    public String toString() {
        return name;
    }
}


