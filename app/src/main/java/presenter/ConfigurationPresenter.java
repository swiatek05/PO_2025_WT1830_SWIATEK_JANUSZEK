package presenter;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import simulation.Simulation;
import simulation.SimulationPresenter;
import simulation.SimulationPreset;
import simulation.SimulationVariant;
import simulation.configs.*;
import simulation.presets.ConfigIO;

import java.io.File;
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
    @FXML private Label genomeLabel;
    @FXML private Spinner<Integer> genomeNumber;
    @FXML private Label genomeNumberLabel;
    @FXML private CheckBox csvCheckBox;

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

                genome.setVisible(false);
                genome.setManaged(false);
                genomeLabel.setVisible(false);
                genomeLabel.setManaged(false);

                genomeNumber.setVisible(true);
                genomeNumber.setManaged(true);
                genomeNumberLabel.setVisible(true);
                genomeNumberLabel.setManaged(true);
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

                genome.setVisible(true);
                genome.setManaged(true);
                genomeLabel.setVisible(true);
                genomeLabel.setManaged(true);

                genomeNumber.setVisible(false);
                genomeNumber.setManaged(false);
                genomeNumberLabel.setVisible(false);
                genomeNumberLabel.setManaged(false);
            }
        }
    }

    @FXML
    public void onStartSimulation() {
        if (minMutationsSpinner.getValue()>maxMutationsSpinner.getValue()) {
            showError("Minimalna liczba mutacji nie może przekraczać maksymalnej!!!");
            return;
        }

        List<Integer> immunityGenesList = Collections.emptyList();
        try {
            String rawInput = genome.getText().trim();
            immunityGenesList = Arrays.stream(rawInput.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Integer::parseInt)
                    .toList();
        } catch (NumberFormatException e) {
            showError("Geny muszą być liczbami rozdzielonymi przecinkami!");
        }

        SimulationConfig config = readFromUI();
        openSimulation(config);

    }

    private void openSimulation(SimulationConfig config) {
        try{
            FXMLLoader loader=new FXMLLoader(getClass().getResource("/simulation.fxml"));
            javafx.scene.Parent root = loader.load();

            simulationId++;
            SimulationPresenter presenter=loader.getController();
            presenter.setSaveToCsv(csvCheckBox.isSelected());
            presenter.setWorldConfig(config);
            presenter.setId(simulationId);

            Stage simulationStage=new Stage();
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

    private SimulationConfig readFromUI() {
        List<Integer> immunityGenesList = Collections.emptyList();
        Integer genesNumber = 1;

        switch (variantComboBox.getValue()) {
            case STANDARD -> {
                genesNumber = genomeNumber.getValue();
            }
            case POISONOUS_PLANTS -> {
                String rawInput = genome.getText().trim();
                immunityGenesList = Arrays.stream(rawInput.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .map(Integer::parseInt)
                        .toList();
                genesNumber = immunityGenesList.size();
            }
        }

        AnimalConfig animalConfig = new AnimalConfig(
                startEnergySpinner.getValue(),
                dayEndEnergyLossSpinner.getValue(),
                ableToMateEnergyNeededSpinner.getValue(),
                energyUsedToMateSpinner.getValue()
        );
        GenomeConfig genomeConfig = new GenomeConfig(
                genesNumber,
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
        return new SimulationConfig(
                animalConfig,
                genomeConfig,
                plantConfig,
                mapConfig,
                variantComboBox.getValue(),
                startPlantsCountSpinner.getValue(),
                startAnimalsCountSpinner.getValue(),
                dailyGrowthSpinner.getValue()
        );
    }

    private File ensureJsonExtension(File file) {
        if (file.getName().toLowerCase().endsWith(".json")) {
            return file;
        }
        return new File(file.getParentFile(), file.getName() + ".json");
    }

    public void onSaveConfig() {
        SimulationConfig config = readFromUI();

        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files",".json"));

        File file = fc.showSaveDialog(null);
        if (file == null) return;

        file = ensureJsonExtension(file);

        try {
            ConfigIO.savePresetToFile(file.toPath(),config);
        } catch (Exception e) {
            showError("Nie udało się zapisać konfiguracji "+e);
        }
    }
    public void onLoadConfig() {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files",".json"));

        File file = fc.showOpenDialog(null);
        if (file == null) return;

        presetComboBox.getSelectionModel().clearSelection();

        try {
            SimulationConfig config = ConfigIO.readPresetFromFile(file.toPath());
            applyPreset(config);
        } catch (Exception e) {
            showError("Nie udało się wczytać konfiguracji "+e);
        }
    }
}