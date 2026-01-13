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
import simulation.SimulationVariant;
import simulation.configs.*;

import java.io.IOException;
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

    private int simulationId=0;

    @FXML
    public void initialize() {
        variantComboBox.setItems(FXCollections.observableArrayList(SimulationVariant.values()));
        variantComboBox.getSelectionModel().selectFirst();
        SpinnerValueFactory<Double> poisonFactory =
                new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 1.0, 0.1, 0.05);
        plantPoisonChangeSpinner.setValueFactory(poisonFactory);

        SpinnerValueFactory<Integer> damageFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(Integer.MIN_VALUE, 0, -10);
        plantPoisonDamageSpinner.setValueFactory(damageFactory);
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