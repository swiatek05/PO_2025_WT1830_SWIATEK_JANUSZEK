package presenter;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import simulation.SimulationVariant;
import simulation.configs.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ConfigurationPresenter {
    @FXML private Spinner<Integer> widthSpinner;
    @FXML private Spinner<Integer> heightSpinner;
    @FXML private ComboBox<SimulationVariant> variantComboBox;
    @FXML private Spinner<Integer> startPlantsCountSpinner;
    @FXML private Spinner<Integer> dailyGrowthSpinner;
    @FXML private Spinner<Integer> plantEnergySpinner;
    @FXML private Spinner<Integer> plantPoisonDamageSpinner;
    @FXML private Spinner<Double> plantPoisonChangeSpinner;
    @FXML private Spinner<Integer> startAnimalsCountSpinner;
    @FXML private Spinner<Integer> startEnergySpinner;
    @FXML private Spinner<Integer> dayEndEnergyLossSpinner;
    @FXML private Spinner<Integer> ableToMateEnergyNeededSpinner;
    @FXML private Spinner<Integer> energyUsedToMateSpinner;
    @FXML private Spinner<Integer> minMutationsSpinner;
    @FXML private Spinner<Integer> maxMutationsSpinner;
    @FXML private TextField genome;

    @FXML
    public void initialize() {
        variantComboBox.setItems(FXCollections.observableArrayList(SimulationVariant.values()));
        variantComboBox.getSelectionModel().selectFirst();
        javafx.scene.control.SpinnerValueFactory<Double> poisonFactory =
                new javafx.scene.control.SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 1.0, 0.1, 0.05);
        plantPoisonChangeSpinner.setValueFactory(poisonFactory);
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

        System.out.println("--- NOWA SYMULACJA ---");
        //System.out.println("Wariant: " + config.variant());
        //System.out.println("Rozmiar mapy: " + mapConfig.width() + "x" + mapConfig.height());
        System.out.println("Geny odporności: " + genomeConfig.immunityGenes());
        System.out.println("Pełna konfiguracja: " + config);
        //here initializate simulation

    }
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Błąd konfiguracji");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}