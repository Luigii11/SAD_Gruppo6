/**
 * @file TrackControllerView.java
 * Classe di definizione di un oggetto di tipo 'TrackControllerView', controller 
 * delegato alla sola gestione della UI.
 * 
 * 
 * @author EmanuelChirico
 */

package it.unisa.diem.sad_gruppo6.controller.ui.track;

import java.io.IOException;

import it.unisa.diem.sad_gruppo6.App;
import it.unisa.diem.sad_gruppo6.controller.business.track.TrackController;
import it.unisa.diem.sad_gruppo6.model.domain.Track;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import java.io.File;

public class TrackControllerView {

    @FXML private TextField titleField;
    @FXML private TextField authorField;
    @FXML private TextField durationField;
    @FXML private TextField genreField;
    @FXML private TextField yearField;
    @FXML private TextField pathField;  
    @FXML private Label feedbackLabel;

    private TrackController trackController;
    private Track trackToEdit;
    

    public TrackControllerView() 
    {
        this.trackController = new TrackController();
    }


    @FXML
    private void handleAddTrack() 
    {
        try 
        {
            trackController.createTrack(
                titleField.getText(),
                authorField.getText(),
                genreField.getText(),
                Integer.parseInt(yearField.getText()),
                pathField.getText()
            );
            App.setRoot("library/TrackLibraryView");
        } 
        catch (NumberFormatException e) 
        {
            feedbackLabel.setStyle("-fx-text-fill: red;");
            feedbackLabel.setText("Durata e anno devono essere numeri.");
        } 
        catch (IllegalArgumentException e)
        {
            feedbackLabel.setStyle("-fx-text-fill: red;");
            feedbackLabel.setText(e.getMessage());
        } 
        catch (IOException e) 
        { 
            feedbackLabel.setStyle("-fx-text-fill: red;");
            feedbackLabel.setText("Errore nel caricamento della libreria.");
        }
    }

    public void setTrackToEdit(Track track)
    {
        this.trackToEdit = track;
        titleField.setText(track.getTitle());
        authorField.setText(track.getAuthor());
        durationField.setText(String.valueOf(track.getDuration()));
        genreField.setText(track.getGenre());
        yearField.setText(String.valueOf(track.getYear()));
        pathField.setText(track.getPath());
    }   

    public void handleBrowseFile(ActionEvent event) 
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleziona file audio");
        File file = fileChooser.showOpenDialog(pathField.getScene().getWindow());
        if (file != null) {
            pathField.setText(file.getAbsolutePath());
        }
    }


    @FXML
    private void handleEditTrack()
    { 
        if (trackToEdit == null) return;

        try
        {
            trackController.editTrack
            (
                trackToEdit,
                titleField.getText(),
                authorField.getText(),
                
                genreField.getText(),
                Integer.parseInt(yearField.getText()),
                pathField.getText()
            );
            App.setRoot("library/TrackLibraryView");
        }
        catch (NumberFormatException e)
        {
            feedbackLabel.setStyle("-fx-text-fill: red;");
            feedbackLabel.setText("Durata e anno devono essere numeri.");
        }
        catch (IllegalArgumentException e)
        {
            feedbackLabel.setStyle("-fx-text-fill: red;");
            feedbackLabel.setText(e.getMessage());
        }
        catch (IOException e) 
        { 
            feedbackLabel.setStyle("-fx-text-fill: red;");
            feedbackLabel.setText("Errore nel tornare alla libreria.");
        }
    }

    @FXML
    private void handleBack(ActionEvent event)
    {
        try
        {
            App.setRoot("library/TrackLibraryView");
        }
        catch (IOException e)
        {
            feedbackLabel.setStyle("-fx-text-fill: red;");
            feedbackLabel.setText("Errore nel tornare alla libreria.");
        }
    }

    
}
