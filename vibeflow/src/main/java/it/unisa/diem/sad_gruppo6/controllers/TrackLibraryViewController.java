/**
 * @file TrackLibraryViewController.java
 * 
 * Controller della vista 'TrackLibraryView.fxml'
 * Gestisce esclusovamente la UI.
 * 
 * @author EmanuelGraziuso
 */

package it.unisa.diem.sad_gruppo6.controllers;

import it.unisa.diem.sad_gruppo6.models.*;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import it.unisa.diem.sad_gruppo6.App;
public class TrackLibraryViewController implements Initializable, TrackLibraryObserver {

    private static final double TITLE= 200;
    private static final double GENRE = 160;
    private static final double AUTHOR = 160;
    private static final double DURATION = 80;

    private TrackLibrary library;
    private PlaybackController playbackController = new PlaybackController();

    @FXML private ListView<Track> trackListView;
    @FXML private Label emptyLabel;
    @FXML private Button addTrackButton;


    /**
     *Costruttore: ottiene il singleton della libreria.
     */
    public TrackLibraryViewController() {
        this.library = TrackLibrary.getInstance();
    }

    /**
     * Inizializzazione JavaFX: registra la vista come osservatore della libreria e
     *  configura la ListView.
     */

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        library.registerObserver(this);
        
        if(trackListView !=null){
            trackListView.skinProperty().addListener((obs, oldSkin, newSkin) -> 
            {
                javafx.scene.control.ScrollBar hBar = (javafx.scene.control.ScrollBar) trackListView.lookup(".scroll-bar:horizontal");
                if (hBar != null) {
                    hBar.setVisible(false);
                    hBar.setManaged(false);
                }
            });

            trackListView.setCellFactory(lv -> new ListCell<Track>() {
                private final Label lblTitle = makeCellLabel(TITLE);
                private final Label lblGenre = makeCellLabel(GENRE);
                private final Label lblAuthor = makeCellLabel(AUTHOR);
                private final Label lblDuration = makeCellLabel(DURATION);
                private final HBox content = new HBox(lblTitle, lblGenre, lblAuthor, lblDuration);
                {
                    setStyle("-fx-padding: 6 16 6 16;");
                    setText(null);
                }
                @Override
                protected void updateItem(Track track, boolean empty) {
                    super.updateItem(track, empty);
                    if (empty || track == null) {
                        setGraphic(null);
                    } else {
                        int min = track.getDuration() / 60;
                        int sec = track.getDuration() % 60;
                        lblTitle.setText(track.getTitle());
                        lblGenre.setText(track.getGenre());
                        lblAuthor.setText(track.getAuthor());
                        lblDuration.setText(String.format("%d:%02d", min, sec));
                        setGraphic(content);
                        
                    }
                }
            });

            onLibraryChanged();
            trackListView.setOnMouseClicked(event -> 
            {
            System.out.println("CLICK rilevato, conteggio = " + event.getClickCount());
            if (event.getClickCount() == 2) {
                Track selected = trackListView.getSelectionModel().getSelectedItem();
                System.out.println("doppio click, selected = " + selected);
                if (selected != null) {
                   try {
                        playbackController.play(selected);
                        System.out.println("play eseguito, prima di setRoot");
                        App.setRoot("MediaPlayer");
                        System.out.println("dopo setRoot");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            });
            
        }
}

        /**
         * Gestione pulsante "+": naviga verso la vista di creazione traccia.
         */

        @FXML 
        private void handleAddTrack(ActionEvent event) {
            try {
                App.setRoot("createTrack");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onTrackAdded(Track track) {
            onLibraryChanged();
        }

        @Override
        public void onLibraryChanged() {
            if(trackListView == null) return;
            var tracks = library.getTracks();
            if (tracks.isEmpty()) {
                emptyLabel.setVisible(true);
                trackListView.setItems(FXCollections.emptyObservableList());
            } else {
                emptyLabel.setVisible(false);
                trackListView.setItems(FXCollections.observableArrayList(tracks));
            
            }
        }


        /**
         * Crea una Label con larghezza fissa per le celle della lista.
         * 
         * @param width La larghezza fissa della Label.
         * 
        */  

        private Label makeCellLabel(double width) {
            Label label = new Label();
            label.setPrefWidth(width);
            label.setMinWidth(width);
            label.setMaxWidth(width);
            label.setStyle("-fx-font-size: 13px;");
            return label;
        }
}
