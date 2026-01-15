package presenter;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import simulation.Simulation;
import simulation.SimulationPresenter;
import simulation.SimulationPreset;
import simulation.SimulationVariant;
import simulation.configs.*;
import simulation.presets.ConfigIO;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ConfigurationPresenter {
    @FXML private ComboBox<SimulationPreset> presetComboBox;
    @FXML private Spinner<Integer> widthSpinner;
    @FXML private Spinner<Integer> heightSpinner;
    @FXML private ComboBox<SimulationVariant> variantComboBox;
    @FXML private Spinner<Integer> startPlantsCountSpinner;
    @FXML private Spinner<Integer> dailyGrowthSpinner;
    @FXML private Spinner<Integer> plantEnergySpinner;
    @FXML private Spinner<Integer> plantPoisonDamageSpinner;
    @FXML private Label plantPoisonDamageSpinnerLabel;
    @FXML private Spinner<Double> plantPoisonChangeSpinner;
    @FXML private Label plantPoisonChangeSpinnerLabel;
    @FXML private Spinner<Integer> startAnimalsCountSpinner;
    @FXML private Spinner<Integer> startEnergySpinner;
    @FXML private Spinner<Integer> dayEndEnergyLossSpinner;
    @FXML private Spinner<Integer> ableToMateEnergyNeededSpinner;
    @FXML private Spinner<Integer> energyUsedToMateSpinner;
    @FXML private Spinner<Integer> minMutationsSpinner;
    @FXML private Spinner<Integer> maxMutationsSpinner;
    @FXML private TextField genome;

    private int simulationId=0;

    @FXML
    public void initialize() {
        variantComboBox.setItems(FXCollections.observableArrayList(SimulationVariant.values()));
        variantComboBox.getSelectionModel().selectFirst();
        // this is kinda bad, but javafx sucks so I don't know how to do it better
        setVisibilityOfPoison(variantComboBox.getValue());

        presetComboBox.setItems(
                FXCollections.observableArrayList(ConfigIO.loadPresets())
        );
        presetComboBox.getSelectionModel().selectFirst();

        presetComboBox.setOnAction(e -> {
                    SimulationPreset preset = presetComboBox.getValue();
                    if (preset != null) {
                        System.out.println(preset.simulationConfig());
                        applyPreset(preset.simulationConfig());
                    }
                });


        SpinnerValueFactory<Double> poisonFactory =
                new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 1.0, 0.1, 0.05);
        plantPoisonChangeSpinner.setValueFactory(poisonFactory);

        SpinnerValueFactory<Integer> damageFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(Integer.MIN_VALUE, 0, -10);
        plantPoisonDamageSpinner.setValueFactory(damageFactory);

        variantComboBox.setOnAction(e -> {
            SimulationVariant variant = variantComboBox.getValue();
            setVisibilityOfPoison(variant);
        });
    }

    private void applyPreset(SimulationConfig c) {
        widthSpinner.getValueFactory().setValue(c.mapConfig().MapWidth());
        heightSpinner.getValueFactory().setValue(c.mapConfig().MapHeight());
        variantComboBox.setValue(c.simulationVariant());
        setVisibilityOfPoison(c.simulationVariant());

        startPlantsCountSpinner.getValueFactory().setValue(c.startingPlantAmount());
        dailyGrowthSpinner.getValueFactory().setValue(c.plantCountToGrowEveryday());

        plantEnergySpinner.getValueFactory()
                .setValue(c.plantConfig().plantConsumptionEnergy());
        plantPoisonDamageSpinner.getValueFactory()
                .setValue(c.plantConfig().plantPoisonConsumptionDamage());
        plantPoisonChangeSpinner.getValueFactory()
                .setValue(c.plantConfig().poisonPlantChance());

        startAnimalsCountSpinner.getValueFactory()
                .setValue(c.startingAnimalAmount());

        startEnergySpinner.getValueFactory()
                .setValue(c.animalConfig().startEnergy());
        dayEndEnergyLossSpinner.getValueFactory()
                .setValue(c.animalConfig().dayEndEnergyLoss());
        ableToMateEnergyNeededSpinner.getValueFactory()
                .setValue(c.animalConfig().ableToMateEnergyNeeded());
        energyUsedToMateSpinner.getValueFactory()
                .setValue(c.animalConfig().energyUsedToMate());

        minMutationsSpinner.getValueFactory()
                .setValue(c.genomeConfig().minMutateCount());
        maxMutationsSpinner.getValueFactory()
                .setValue(c.genomeConfig().maxMutateCount());

        genome.setText(
                c.genomeConfig().immunityGenes().stream()
                        .map(String::valueOf)
                        .reduce((a, b) -> a + "," + b)
                        .orElse("")
        );
    }

    private void setVisibilityOfPoison(SimulationVariant variant) {
        switch (variant) {
            case STANDARD -> {
                plantPoisonChangeSpinner.setVisible(false);
                plantPoisonChangeSpinner.setManaged(false);
                plantPoisonChangeSpinnerLabel.setVisible(false);
                plantPoisonChangeSpinnerLabel.setManaged(false);

                plantPoisonDamageSpinner.setVisible(false);
                plantPoisonDamageSpinner.setManaged(false);
                plantPoisonDamageSpinnerLabel.setVisible(false);
                plantPoisonDamageSpinnerLabel.setManaged(false);
            }
            case POISONOUS_PLANTS -> {
                plantPoisonChangeSpinner.setVisible(true);
                plantPoisonChangeSpinner.setManaged(true);
                plantPoisonChangeSpinnerLabel.setVisible(true);
                plantPoisonChangeSpinnerLabel.setManaged(true);

                plantPoisonDamageSpinner.setVisible(true);
                plantPoisonDamageSpinner.setManaged(true);
                plantPoisonDamageSpinnerLabel.setVisible(true);
                plantPoisonDamageSpinnerLabel.setManaged(true);
            }
        }
    }

    @FXML
    public void onStartSimulation() {
        if (minMutationsSpinner.getValue()>maxMutationsSpinner.getValue()) {
            showError("Minimalna liczba mutacji nie może przekraczać maksymalnej!!!");
            return;
        }
        List<Integer> immunityGenesList;
        try {
            String rawInput = genome.getText().trim();
            if (rawInput.isEmpty()) {
                immunityGenesList =Collections.emptyList();
            } else {
                immunityGenesList = Arrays.stream(rawInput.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .map(Integer::parseInt)
                        .toList();
            }
        } catch (NumberFormatException e) {
            showError("Geny muszą być liczbami rozdzielonymi przecinkami!");
            return;
        }

        AnimalConfig animalConfig = new AnimalConfig(
                startEnergySpinner.getValue(),
                dayEndEnergyLossSpinner.getValue(),
                ableToMateEnergyNeededSpinner.getValue(),
                energyUsedToMateSpinner.getValue()
        );
        GenomeConfig genomeConfig = new GenomeConfig(
                immunityGenesList.size(),
                minMutationsSpinner.getValue(),
                maxMutationsSpinner.getValue(),
                immunityGenesList
        );
        PlantConfig plantConfig = new PlantConfig(
                plantEnergySpinner.getValue(),
                plantPoisonDamageSpinner.getValue(),
                plantPoisonChangeSpinner.getValue()
        );
        MapConfig mapConfig = new MapConfig(
                widthSpinner.getValue(),
                heightSpinner.getValue(),
                variantComboBox.getValue()
        );
        SimulationConfig config = new SimulationConfig(
                animalConfig,
                genomeConfig,
                plantConfig,
                mapConfig,
                variantComboBox.getValue(),
                startPlantsCountSpinner.getValue(),
                startAnimalsCountSpinner.getValue(),
                dailyGrowthSpinner.getValue()
        );

       openSimulation(config);

    }

    private void openSimulation(SimulationConfig config) {
        try{
            FXMLLoader loader=new FXMLLoader(getClass().getResource("/simulation.fxml"));
            javafx.scene.Parent root = loader.load();

            SimulationPresenter presenter=loader.getController();
            presenter.setWorldConfig(config);
            Stage simulationStage=new Stage();
            simulationId++;
            simulationStage.setTitle("Symulacja ID: "+ simulationId);
            simulationStage.setScene(new Scene(root));
            simulationStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showError("Nie udało się otworzyć symulacji"+e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Błąd konfiguracji");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}