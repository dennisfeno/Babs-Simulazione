package it.polito.tdp.babs;

import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.babs.model.Model;
import it.polito.tdp.babs.model.SimulationResult;
import it.polito.tdp.babs.model.Simulazione;
import it.polito.tdp.babs.model.Statistics;
import it.polito.tdp.babs.model.Trip;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;

public class BabsController {

	private Model model;

	public void setModel(Model model) {
		this.model = model;
	}

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private DatePicker pickData;

	@FXML
	private Slider sliderK;

	@FXML
	private TextArea txtResult;

	@FXML
	void doContaTrip(ActionEvent event) {

		txtResult.clear();

		LocalDate ld = pickData.getValue(); // restituisce il valore del dataPicker
		if (ld == null) {
			txtResult.setText("Selezionare una data");
			return;
		}

		List<Statistics> stats = model.getStats(ld); // ottieni delle statistiche dalla classe di statistiche. 
		Collections.sort(stats); 

		for (Statistics stat : stats) {
			if (stat.getPick() <= 0) {
				txtResult.appendText(String.format("WARNING!! Stazione %s con 0 pick\n", stat.getStazione().getName()));
			} else {
				txtResult.appendText(String.format("%s %d %d\n", stat.getStazione().getName(), stat.getPick(), stat.getDrop()));
				
				txtResult.appendText("lat - "+stat.getStazione().getLat() +"\n") ;
			}
		}

	}

	@FXML
	void doSimula(ActionEvent event) {

		txtResult.clear();
		
		LocalDate ld = pickData.getValue(); // restituisce il valore del dataPicker
		if (ld == null || ld.getDayOfWeek() == DayOfWeek.SATURDAY || ld.getDayOfWeek() == DayOfWeek.SATURDAY) {
			txtResult.setText("Selezionare un giorno feriale");
			return;
		}		
		
		Double k = (double) sliderK.getValue() / 100.0 ;
		
		List<Trip> tripsPick = model.getTripsWithPickForDay(ld) ;
//		List<Trip> tripsDrop = model.getTripsWithDropForDay(ld) ;
		
		Simulazione simulazione = new Simulazione(model) ;
		simulazione.loadPick(tripsPick) ;
//		potrebbe falsare il risultato della simulazione
//		simulazione.loadDrop(tripsDrop) ;
		simulazione.loadStations(k, model.getStazioni()) ;
		simulazione.run() ;
		SimulationResult simulationResult = simulazione.collectResults() ;
		
		txtResult.appendText("PICK MISS: " + simulationResult.getNumberOfPickMiss() + "\n") ;
		txtResult.appendText("DROP MISS: " + simulationResult.getNumberOfDropMiss() + "\n") ;
	}

	@FXML
	void initialize() {
		assert pickData != null : "fx:id=\"pickData\" was not injected: check your FXML file 'Babs.fxml'.";
		assert sliderK != null : "fx:id=\"sliderK\" was not injected: check your FXML file 'Babs.fxml'.";
		assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Babs.fxml'.";

		pickData.setValue(LocalDate.of(2013, 9, 1));
	}
}
