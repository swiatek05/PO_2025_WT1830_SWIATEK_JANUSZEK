package simulation;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import model.Vector2d;
import model.animal.Animal;
import model.map.GlobeWorldMap;
import model.map.WorldMap;
import model.plant.AbstractPlant;
import model.plant.PoisonGrass;
import simulation.configs.PlantConfig;
import simulation.configs.SimulationConfig;

import java.util.List;

public class SimulationPresenter {
    @FXML
    private GridPane mapGrid;
    @FXML
    private Label day;
    @FXML
    private Label numberOfAnimals;
    @FXML
    private Label numberOfPlants;
    @FXML
    private Label freeFields;
    @FXML
    private Label mostPopularGenom;
    @FXML
    private Label averageEnergy;
    @FXML
    private Label averageAgeForDead;
    @FXML
    private Label averageChildrenCount;
    @FXML
    private Button pauseButton;

    private SimulationConfig config;

    private GlobeWorldMap map;
    private Simulation engine;
    private boolean isPaused = false;
    private double cellSize;
    public void setWorldConfig(SimulationConfig config) {
        this.config = config;

        this.map = new GlobeWorldMap(config.mapConfig(),config.plantConfig());
        this.engine = new Simulation(map, config, this);
        initializeGridConstraints();

        Thread simulationThread = new Thread(engine);
        simulationThread.setDaemon(true);
        simulationThread.start();
    }
    private void initializeGridConstraints() {
        mapGrid.getChildren().clear();
        mapGrid.getColumnConstraints().clear();
        mapGrid.getRowConstraints().clear();

        int width=config.mapConfig().MapWidth();
        int height=config.mapConfig().MapHeight();

        double sizeX=600.0/width;
        double sizeY=600.0/height;
        cellSize=Math.min(sizeX,sizeY);

        for (int i=0;i<width;i++){
            mapGrid.getColumnConstraints().add(new ColumnConstraints(cellSize));
        }
        for (int i=0;i<height;i++){
            mapGrid.getRowConstraints().add(new RowConstraints(cellSize));
        }
    }
    public void mapChanged(int currentDay){
        if (isPaused){
            return;
        }
        Platform.runLater(() -> {
            drawMap(currentDay);
            updateStatistics(currentDay);
        });
    }
    private void updateStatistics(int currentDay){
        SimulationStatistics stats=engine.getStatistics();
        day.setText(String.valueOf(currentDay));
        numberOfAnimals.setText(String.valueOf(stats.getNumberOfAnimals()));
        numberOfPlants.setText(String.valueOf(stats.getNumberOfPlants()));
        freeFields.setText(String.valueOf(stats.getFreeFields()));

        averageEnergy.setText(String.format("%.2f",stats.getAverageEnergy()));
        averageAgeForDead.setText(String.format("%.2f",stats.getAverageAgeForDead()));
        averageChildrenCount.setText(String.format("%.2f",stats.getAverageChildrenCount()));

        if (stats.getMostPopularGenom()!=null){
            mostPopularGenom.setText(stats.getMostPopularGenom().toString());

        }
        else{
            mostPopularGenom.setText("-");
        }

    }
    private void drawMap(int currentDay){
        mapGrid.getChildren().clear();
        int width=config.mapConfig().MapWidth();
        int height=config.mapConfig().MapHeight();

        for(int y=0;y<height;y++){
            for(int x=0;x<width;x++){
                Vector2d position=new Vector2d(x,y);
                Rectangle rect=new Rectangle(cellSize,cellSize);

                Object object=map.objectAt(position,currentDay);
                if (object instanceof AbstractPlant) {
                    AbstractPlant plant = (AbstractPlant) object;
                    rect.setFill(calculatePlantColor(plant));
                }
                else{
                        rect.setFill(Color.BEIGE);
                    }
                mapGrid.add(rect,x,y);

                if (object instanceof Animal){
                    Animal animal=(Animal) object;
                    drawAnimal(animal,x,y);
                }

            }
        }
    }

    private void drawAnimal(Animal animal, int x, int y) {
        Circle animalG=new Circle(cellSize*0.4);

        double startEnergy=config.animalConfig().startEnergy();

        double currentEnergy=(double) animal.getEnergy();

        double energyStan=(double) (currentEnergy-minEnergy)/ (double) (maxEnergy-minEnergy);

        energyStan=Math.min(1.0,Math.max(0.2,energyStan));

        animalG.setFill(Color.hsb(240,energyStan,1.0));

        if (currentEnergy>startEnergy*3){ //dla poteznego zwierzaka
            animalG.setStroke(Color.BLACK);
            animalG.setStrokeWidth(2);
        }

        GridPane.setHalignment(animalG, HPos.CENTER);
        GridPane.setValignment(animalG, VPos.CENTER);
        mapGrid.add(animalG,x,y);
    }

    private Paint calculatePlantColor(AbstractPlant plant) {
        int energyValue=plant.getConsumptionEnergy();
        if (energyValue>=0){
            return Color.LIME;
        }
        else{
            double maxDamage=config.plantConfig().plantPoisonConsumptionDamage(); //zakladam ze nie 0
            double poisonValue=Math.abs(energyValue)/Math.abs(maxDamage);
            return Color.LIME.interpolate(Color.RED,poisonValue);

        }
    }
    @FXML
    public void onPauseButton(){
        if (isPaused){
            engine.resume();
            pauseButton.setText("PAUZA");
        }
        else{
            engine.pause();
            pauseButton.setText("WZNÓW");
        }
        isPaused=!isPaused;
    }
}
