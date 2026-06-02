/**
 * @file TrackController.java
 * Classe di definizione di un oggetto di tipo 'TrackController', controller per la gestione delle tracce musicali.
 * 
 * @author EmanuelChirico
 */

package it.unisa.diem.sad_gruppo6.controllers;

import it.unisa.diem.sad_gruppo6.models.*;
import it.unisa.diem.sad_gruppo6.App;
import it.unisa.diem.sad_gruppo6.commands.*;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;

import java.io.IOException;

public class TrackController 
{
    private TrackLibrary library;
    private CommandManager commandManager;

    @FXML private TextField titleField;
    @FXML private TextField authorField;
    @FXML private TextField durationField;
    @FXML private TextField genreField;
    @FXML private TextField yearField;
    @FXML private Label feedbackLabel;
    /**
     * Costruttore del TrackController, inizializza la libreria delle tracce e il gestore dei comandi.
     * 
     * @param library la libreria delle tracce da gestire.
     * @param commandManager il gestore dei comandi per eseguire azioni sulla libreria delle tracce.
     */
    public TrackController() 
    {
        this.library = TrackLibrary.getInstance();
        this.commandManager = new CommandManager();
    }

    /**
     * Metodo per creare una nuova traccia e aggiungerla alla libreria, utilizzando un comando per incapsulare l'azione.
     * 
     * @param title Il titolo della traccia da creare.
     * @param author L'autore della traccia da creare.
     * @param duration La durata della traccia da creare in secondi.
     * @param genre Il genere musicale della traccia da creare.
     * @param year L'anno di pubblicazione della traccia da creare.
     */

    public void createTrack(String title, String author, int duration, String genre, int year) 
    {
        Track track = new Track(title, author, duration, genre, year);
        AddTrackToLibraryCommand command = new AddTrackToLibraryCommand(library, track);
        commandManager.execute(command);
    }

    @FXML
    private void handleAddTrack() 
    {
        try 
        {
            createTrack
            (
                titleField.getText(),
                authorField.getText(),
                Integer.parseInt(durationField.getText()),
                genreField.getText(),
                Integer.parseInt(yearField.getText())
            );
            App.setRoot("TrackLibraryView");   // torna alla libreria, sulla STESSA scena
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



}