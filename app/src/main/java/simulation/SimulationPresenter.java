package simulation;

import com.opencsv.CSVWriter;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class SimulationPresenter {
    @FXML
    private Canvas mapCanvas;
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

    @FXML
    private Label trackedGenome;
    @FXML
    private Label trackedActiveGene;
    @FXML
    private Label trackedEnergy;
    @FXML
    private Label trackedPlantsEaten;
    @FXML
    private Label trackedChildrenCount;
    @FXML
    private Label trackedDescendantsCount;
    @FXML
    private Label trackedAge;
    @FXML
    private Label trackedStatus;
    @FXML
    private ComboBox<String> statsComboBox;
    @FXML
    private LineChart<Number, Number> statsChart;
    @FXML
    private GraphicsContext gc;
    public NumberAxis xAxis;
    private Animal trackedAnimal=null;
    private SimulationConfig config;


    private GlobeWorldMap map;
    private Simulation engine;
    private boolean isPaused = false;
    private double cellSize;
    private boolean saveToCsv = false;
    private int id = 0;
    private CSVWriter csvWriter;

    private int currentDayDisplay=0;
    private int currentMinEnergy;
    private int currentMaxEnergy;

    private final String[] statOptions = {"Liczba zwierzaków", "Liczba roślin", "Wolne pola", "Średnia energia", "Średnia długość życia", "Średnia liczba dzieci"};
    private XYChart.Series<Number, Number> chartDataSeries = new XYChart.Series<>();

    public void setWorldConfig(SimulationConfig config) {
        this.config = config;
        this.map = new GlobeWorldMap(config.mapConfig(),config.plantConfig());

        this.engine=new Simulation(map,config,this);

        double initialCanvasWidth = mapCanvas.getWidth();
        double initialCanvasHeight = mapCanvas.getHeight();

        double sizeX=initialCanvasWidth/config.mapConfig().MapWidth();
        double sizeY=initialCanvasHeight/config.mapConfig().MapHeight();
        this.cellSize=Math.min(sizeX,sizeY);

        mapCanvas.setWidth(this.cellSize*config.mapConfig().MapWidth());
        mapCanvas.setHeight(this.cellSize*config.mapConfig().MapHeight());

        this.gc=mapCanvas.getGraphicsContext2D();
        if (saveToCsv) {
            try{
                int randomId = ThreadLocalRandom.current().nextInt(0,99999);
                FileWriter w = new FileWriter("simulationdata_id"+id+"_"+randomId+".csv",true);
                csvWriter = new CSVWriter(w,
                                CSVWriter.DEFAULT_SEPARATOR,
                                CSVWriter.DEFAULT_QUOTE_CHARACTER,
                                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                                CSVWriter.DEFAULT_LINE_END);
                String[] headers = {"Dzień", "Liczba zwierzaków", "Liczba roślin", "Liczba wolnych pól", "Średnia energia", "Średnia długość życia", "Średnia liczba dzieci"};

                csvWriter.writeNext(headers);
                csvWriter.flush();
            } catch (IOException e) {
                throw new RuntimeException("Nie udało się otworzyć pliku csv");
            }
        }

        statsComboBox.setItems(FXCollections.observableArrayList(statOptions));
        statsComboBox.setValue("Liczba zwierzaków");
        statsChart.getData().add(chartDataSeries);

        Thread simulationThread = new Thread(engine);
        simulationThread.setDaemon(true);
        simulationThread.start();
    }

    public void setSaveToCsv(boolean saveToCsv) {
        this.saveToCsv = saveToCsv;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void mapChanged(int currentDay,int minEnergy,int maxEnergy){
        this.currentDayDisplay=currentDay;
        this.currentMinEnergy=minEnergy;
        this.currentMaxEnergy=maxEnergy;
        if (isPaused){
            updateTrackedAnimal();
            return;
        }
        Platform.runLater(() -> {
            drawMap(currentDay,minEnergy,maxEnergy);
            updateStatistics(currentDay);
            updateChart(currentDay);
            if (saveToCsv) {
                writeToCsv(currentDay);
            }
            updateTrackedAnimal();
        });
    }

    private void writeToCsv(int currentDay) {
            SimulationStatistics stats = engine.getStatistics();
            String[] data = {String.valueOf(currentDay),String.valueOf(stats.getNumberOfAnimals()),String.valueOf(stats.getNumberOfPlants()),String.valueOf(stats.getFreeFields()),
                             String.format("%.2f",stats.getAverageEnergy()),String.format("%.2f",stats.getAverageAgeForDead()),String.format("%.2f",stats.getAverageChildrenCount())};
            csvWriter.writeNext(data);

        try {
            csvWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateChart(int currentDay) {
        SimulationStatistics stats = engine.getStatistics();
        double value = getValue(stats);

        chartDataSeries.getData().add(new XYChart.Data<>(currentDay, value));

//        if (chartDataSeries.getData().size() > 100) {
//            chartDataSeries.getData().removeFirst();
//        }
    }

    private double getValue(SimulationStatistics stats) {
        double value = 0;

        switch (statsComboBox.getValue()) {
            case "Liczba zwierzaków" -> value = stats.getNumberOfAnimals();
            case "Liczba roślin" -> value = stats.getNumberOfPlants();
            case "Wolne pola" -> value = stats.getFreeFields();
            case "Średnia energia" -> value = stats.getAverageEnergy();
            case "Średnia długość życia" -> value = stats.getAverageAgeForDead();
            case "Średnia liczba dzieci" -> value = stats.getAverageChildrenCount();
        }
        return value;
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
    private void drawMap(int currentDay,int minEnergy,int maxEnergy){
        gc.clearRect(0,0,mapCanvas.getWidth(),mapCanvas.getHeight());
        gc.setFill(Color.BEIGE);
        gc.fillRect(0,0,mapCanvas.getWidth(),mapCanvas.getHeight());
        int width=config.mapConfig().MapWidth();
        int height=config.mapConfig().MapHeight();

        for(int y=0;y<height;y++){
            for(int x=0;x<width;x++){
                Vector2d position=new Vector2d(x,y);
                Object object=map.objectAt(position,currentDay);
                if (object instanceof AbstractPlant) {
                    AbstractPlant plant = (AbstractPlant) object;
                    gc.setFill(calculatePlantColor(plant));
                    gc.fillRect(x*cellSize,y*cellSize,cellSize,cellSize);
                }
                if (object instanceof Animal){
                    Animal animal=(Animal) object;
                    drawAnimal(animal,x,y,minEnergy,maxEnergy);
                }
            }
        }
    }

    private void selectAnimal(Vector2d position) {
        List<Animal> animalsOnPosition=map.animalsAt(position);
        if(animalsOnPosition.isEmpty()){return;}

        Animal selectedAnimal;
        if (animalsOnPosition.size()==1){
            selectedAnimal=animalsOnPosition.get(0);
        }
        else{
            javafx.scene.control.ChoiceDialog<Animal> dialog = new javafx.scene.control.ChoiceDialog<>(animalsOnPosition.get(0),animalsOnPosition);
            dialog.setTitle("Wybierz zwierzaka");
            dialog.setHeaderText("Na wybranym polu znajduje się zwierząt: "+animalsOnPosition.size());
            dialog.setContentText("Wybierz zwierze do śledzenie: ");
            selectedAnimal=dialog.showAndWait().orElse(null);
        }
        if (selectedAnimal!=null){
            this.trackedAnimal=selectedAnimal;
            updateTrackedAnimal();
            drawMap(this.currentDayDisplay,this.currentMinEnergy,this.currentMaxEnergy);
        }
    }
    private void updateTrackedAnimal(){
        if(trackedAnimal==null) return;

        Platform.runLater(()->{
            trackedGenome.setText(trackedAnimal.getGenome().getGenes().toString());
            trackedActiveGene.setText(String.valueOf(trackedAnimal.getGenome().getActiveGene()));
            trackedEnergy.setText(String.valueOf(trackedAnimal.getEnergy()));
            trackedPlantsEaten.setText(String.valueOf(trackedAnimal.getCountEatenPlants()));
            trackedChildrenCount.setText(String.valueOf(trackedAnimal.getChildrenCount()));
            trackedDescendantsCount.setText(String.valueOf(trackedAnimal.getDescendantsCount()));
            if(trackedAnimal.isDead()){
                trackedStatus.setText("Zmarł w dniu: "+trackedAnimal.getDayOfDeath());
                trackedAge.setText("Żył przez: "+trackedAnimal.getAgeForDead());
                trackedStatus.setTextFill(Color.RED);
            }
            else{
                trackedStatus.setText("Żyje");
                trackedAge.setText(String.valueOf(trackedAnimal.getAge()));
                trackedStatus.setTextFill(Color.GREEN);
            }
        });
    }

    private void drawAnimal(Animal animal, int x, int y,int minEnergy,int maxEnergy) {
        double startEnergy=config.animalConfig().startEnergy();
        double currentEnergy=(double) animal.getEnergy();

        double denominator = (double) (maxEnergy-minEnergy);
        double energyStan;

        if (denominator == 0) {
            energyStan = 1.0;
        } else {
            energyStan=(currentEnergy-minEnergy)/denominator;
        }
        energyStan=Math.max(0.2,Math.min(1.0,energyStan));
        gc.setFill(Color.WHITE.interpolate(Color.BLUE, energyStan));

        double margin=cellSize*0.20;
        double size=cellSize-(2*margin);
        double posX=x*cellSize+margin;
        double posY=y*cellSize+margin;
        gc.fillOval(posX,posY,size,size);
        if (animal==trackedAnimal){
            gc.setStroke(Color.GOLD);
            gc.setLineWidth(cellSize*0.15);
            gc.strokeOval(posX,posY,size,size);
        }
        else if (currentEnergy>startEnergy*3){ //dla poteznego zwierzaka
           gc.setStroke(Color.BLACK);
           gc.setLineWidth(cellSize*0.15);
           gc.strokeOval(posX,posY,size,size);
        }
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
    public void onPauseButton() {
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

    @FXML
    public void onStatSelectionChanged() {
        chartDataSeries.getData().clear();

        xAxis.setLowerBound(currentDayDisplay);
        xAxis.setAutoRanging(true);
    }
    @FXML
    private void onMapClick(MouseEvent event){
        if (!isPaused){return;}

        int x=(int) (event.getX()/cellSize);
        int y=(int) (event.getY()/cellSize);

        if (x>=0 && x<config.mapConfig().MapWidth() && y>=0 && y<config.mapConfig().MapHeight()){
            selectAnimal(new Vector2d(x,y));
        }


    }
}
