/**
 * @file HomeController.java
 * Controller della vista Home.
 * Coordina la visualizzazione delle playlist e risponde agli eventi dell'interfaccia utente.
 * * @see PlaylistController
 * @author EmanuelaGraziuso, LuigiAutorino
 */
package it.unisa.diem.sad_gruppo6.controller.ui.home;

import it.unisa.diem.sad_gruppo6.App;
import it.unisa.diem.sad_gruppo6.controller.business.playlist.PlaylistController;
import it.unisa.diem.sad_gruppo6.controller.ui.playlist.PlaylistCreationDialogController;
import it.unisa.diem.sad_gruppo6.controller.ui.playlist.PlaylistDetailsController;
import it.unisa.diem.sad_gruppo6.controller.ui.utils.DialogUtils;
import it.unisa.diem.sad_gruppo6.model.command.CommandManager;
import it.unisa.diem.sad_gruppo6.model.domain.Playlist;
import it.unisa.diem.sad_gruppo6.model.library.PlaylistLibrary;
import it.unisa.diem.sad_gruppo6.model.library.PlaylistLibraryObserver;
import it.unisa.diem.sad_gruppo6.model.library.TrackLibrary;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Optional;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.geometry.Side;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;

/**
 * @class HomeController
 * @brief Controller architetturale accoppiato al file Home.fxml.
 * @details Implementa il pattern Observer tramite l'interfaccia {@link PlaylistLibraryObserver} 
 * per garantire il refresh reattivo dell'interfaccia grafica a fronte di modifiche
 * nel sottostante strato di business logic.
 */
public class HomeController implements PlaylistLibraryObserver {

    @FXML private TilePane playlistTilePane;
    @FXML private Button playPauseButton;

    private PlaylistLibrary playlistLibrary;
    private PlaylistController playlistController;
    private Playlist selectedPlaylist = null;
    private boolean isPlaying = false;

    /**
     * @brief Inizializza automaticamente il controller dopo il caricamento del file FXML.
     * @details Recupera le istanze dei Singleton del model, istanzia il controller di business,
     * effettua la registrazione dell'observer sul catalogo e avvia il primo rendering.
     */
    @FXML
    public void initialize() {
        this.playlistLibrary = PlaylistLibrary.getInstance();
        TrackLibrary trackLibrary = TrackLibrary.getInstance();
        CommandManager commandManager = new CommandManager();

        this.playlistController = new PlaylistController(trackLibrary, this.playlistLibrary, commandManager);
        this.playlistLibrary.registerObserver(this);
        refresh();
    }

    /**
     * @brief Esegue lo switch di scena per mostrare i dettagli di una specifica playlist.
     * @param[in] playlist L'oggetto Playlist di cui visualizzare il contenuto informativo e le tracce.
     * @see PlaylistDetailsController
     */
    private void openPlaylistDetails(Playlist playlist) {
        try {
            PlaylistDetailsController controller = App.setRootAndGetController("playlist/PlaylistDetails");
            controller.init(playlist, this.playlistController, TrackLibrary.getInstance(), this.playlistLibrary);
        } catch (IOException e) {
            System.err.println("Errore nel caricamento di PlaylistDetails.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    } 

    /**
     * @brief Callback invocata dal model quando si verifica un cambiamento nella libreria delle playlist.
     * @details Soddisfa il contratto dell'interfaccia {@link PlaylistLibraryObserver} forzando il refresh visivo.
     */
    @Override
    public void onPlaylistLibraryChanged() {
        refresh();
    }

    /**
     * @brief Rigenera la griglia visiva delle playlist svuotando i vecchi componenti.
     * @details Pulisce il TilePane, azzera la selezione corrente e cicla la lista aggiornata
     * delle playlist per ricostruire le card programmaticamente.
     */
    private void refresh() {
        playlistTilePane.getChildren().clear();
        this.selectedPlaylist = null;

        for (Playlist playlist : playlistLibrary.getPlaylists()) {
            VBox card = creaCardPlaylist(playlist);
            playlistTilePane.getChildren().add(card);
        }
    }

    /**
     * @brief Fabbrica programmaticamente il contenitore grafico (VBox) di una singola playlist.
     * @details Configura le classi CSS esterne, la barra superiore con i tre pallini per il 
     * menu contestuale ("Rinomina" ed "Elimina"), l'icona centrale e l'handler di selezione al click.
     * @param[in] playlist L'entità di dominio Playlist da mappare all'interno del componente grafico.
     * @return VBox Il nodo grafico preconfigurato pronto per essere inserito nel TilePane.
     */
    private VBox creaCardPlaylist(Playlist playlist) {
        VBox card = new VBox();
        card.setPrefSize(200, 150);
        card.setAlignment(Pos.TOP_CENTER);
        card.getStyleClass().add("playlist-card");

        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_RIGHT);
        topBar.setPadding(new Insets(5, 5, 0, 0));

        Button menuButton = new Button("⋮");
        menuButton.getStyleClass().add("card-menu-btn");

        ContextMenu contextMenu = new ContextMenu();

        MenuItem renameItem = new MenuItem("Rinomina ✎");
        renameItem.setOnAction(e -> {
            this.selectedPlaylist = playlist;
            handleRenamePlaylist(e);
        });

        MenuItem deleteItem = new MenuItem("Elimina 🗑");
        deleteItem.getStyleClass().add("delete-menu-item"); 
        deleteItem.setOnAction(e -> {
            this.selectedPlaylist = playlist;
            handleDeletePlaylist(e);
        });

        contextMenu.getItems().addAll(renameItem, deleteItem);

        menuButton.setOnAction(event -> {
            contextMenu.show(menuButton, Side.BOTTOM, 0, 0);
            event.consume(); 
        });

        topBar.getChildren().add(menuButton);

        VBox iconBox = new VBox();
        iconBox.setAlignment(Pos.CENTER);
        VBox.setVgrow(iconBox, Priority.ALWAYS);
        
        Label iconLabel = new Label("♫");
        iconLabel.getStyleClass().add("card-icon");
        iconBox.getChildren().add(iconLabel);

        VBox textBox = new VBox();
        textBox.getStyleClass().add("card-text-box");
        
        Label nameLabel = new Label(playlist.getName());
        nameLabel.getStyleClass().add("card-name-label");
        textBox.getChildren().add(nameLabel);

        card.getChildren().addAll(topBar, iconBox, textBox);

        card.setOnMouseClicked(event -> {
            playlistTilePane.getChildren().forEach(node -> 
                node.getStyleClass().remove("selected")
            );
            card.getStyleClass().add("selected");
            this.selectedPlaylist = playlist;

            if (event.getClickCount() == 2) {
                openPlaylistDetails(playlist);
            }
        });

        return card;
    }

    /**
     * @brief Gestisce l'evento di pressione del pulsante centrale Play/Pausa.
     * @details Effettua il toggle dello stato logico 'isPlaying' e aggiorna l'icona
     * del pulsante usando i caratteri multimediali unificati coerenti.
     * @param[in] event L'evento di azione generato dal click sul pulsante.
     */
    @FXML
    private void handlePlayPause(ActionEvent event) {
        this.isPlaying = !this.isPlaying;
        if (this.isPlaying) {
            playPauseButton.setText("⏸");
        } else {
            playPauseButton.setText("⏵");
        }
    }

    /**
     * @brief Gestisce il flusso per la ridenominazione di una playlist selezionata.
     * @details Apre un {@link TextInputDialog}, eredita dinamicamente lo stile scuro tramite
     * {@link DialogUtils} e, se confermato, invoca il controller di business.
     * @param[in] event L'evento di azione generato dal click sulla voce di menu.
     */
    @FXML
    private void handleRenamePlaylist(ActionEvent event) {
        Playlist selected = this.selectedPlaylist;
        if (selected == null) return;

        TextInputDialog dialog = new TextInputDialog(selected.getName());
        dialog.setTitle("Rinomina Playlist");
        dialog.setHeaderText("Modifica il nome per \"" + selected.getName() + "\"");
        dialog.setContentText("Nuovo nome:");
        
        DialogUtils.personalizza(dialog, playlistTilePane, "✎", "#5E27BF");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newName -> {
            try {
                playlistController.renamePlaylist(selected, newName);
                refresh(); 
            } catch (IllegalArgumentException e) {
                mostraAlertErrore("Impossibile rinominare la playlist", e.getMessage());
            }
        });
    }

    /**
     * @brief Gestisce il flusso per la cancellazione definitiva di una playlist.
     * @details Mostra un alert di conferma personalizzato. In caso di esito positivo,
     * demanda la rimozione logico-fisica al controller di business.
     * @param[in] event L'evento di azione generato dal click sul comando Elimina.
     */
    @FXML
    private void handleDeletePlaylist(ActionEvent event) {
        Playlist selected = this.selectedPlaylist;

        if (selected == null) {
            mostraAlertWarning("Nessuna playlist selezionata", "Seleziona una playlist cliccando sulla card prima di eliminarla.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Elimina playlist");
        confirm.setHeaderText("Sei sicuro?");
        confirm.setContentText("Vuoi eliminare la playlist \"" + selected.getName() + "\"?");
        
        DialogUtils.personalizza(confirm, playlistTilePane, "🗑", "#FF4C30");
        
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                playlistController.deletePlaylist(selected);
                refresh(); 
            } catch (IllegalArgumentException e) {
                mostraAlertErrore("Impossibile eliminare la playlist", e.getMessage());
            }
        }
    }

    /**
     * @brief Reindirizza l'utente verso la vista contenente la libreria di tutte le tracce.
     * @param[in] event L'evento di azione associato al click sul pulsante (☰) della barra laterale.
     */
    @FXML
    private void handleGoToAllTracks(ActionEvent event) {
        try {
            App.setRoot("library/TrackLibraryView");
        } catch (IOException e) {
            System.err.println("Errore nella navigazione a TrackLibraryView: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * @brief Carica e mostra in modalità modale il dialogo per la creazione di una nuova playlist.
     * @param[in] event L'evento di azione associato al click sul pulsante (+) della sidebar.
     * @see PlaylistCreationDialogController
     */
    @FXML
    private void handleGoToCreatePlaylist(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/unisa/diem/sad_gruppo6/view/playlist/PlaylistCreationDialog.fxml"));
            Parent root = loader.load();
            
            PlaylistCreationDialogController dialogController = loader.getController();
            dialogController.setPlaylistController(this.playlistController); 
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Crea nuova playlist");
            dialogStage.setScene(new Scene(root));
            Stage owner = (Stage) ((Node) event.getSource()).getScene().getWindow();
            dialogStage.initOwner(owner);
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            
            dialogStage.showAndWait(); 
            refresh(); 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @brief Helper privato per l'istanza e la visualizzazione di alert di errore bloccante.
     * @details Genera un pop-up di tipo ERROR, applicando la sintonizzazione scura e l'icona personalizzata.
     * @param[in] titoloHeader Il testo breve da inserire nell'intestazione del popup.
     * @param[in] messaggioContenuto Il testo descrittivo dettagliato dell'eccezione catturata.
     */
    private void mostraAlertErrore(String titoloHeader, String messaggioContenuto) {
        Alert error = new Alert(Alert.AlertType.ERROR, messaggioContenuto, ButtonType.OK);
        error.setTitle("Operazione non consentita");
        error.setHeaderText(titoloHeader);
        
        DialogUtils.personalizza(error, playlistTilePane, "❌", "#FF4C30");
        
        error.showAndWait();
    }

    /**
     * @brief Helper privato per l'istanza e la visualizzazione di alert di avviso non bloccante.
     * @details Genera un pop-up di tipo WARNING, applicando la sintonizzazione scura e l'icona personalizzata.
     * @param[in] titoloHeader Il testo breve da inserire nell'intestazione del popup.
     * @param[in] messaggioContenuto Il testo descrittivo dell'avviso.
     */
    private void mostraAlertWarning(String titoloHeader, String messaggioContenuto) {
        Alert alert = new Alert(Alert.AlertType.WARNING, messaggioContenuto, ButtonType.OK);
        alert.setTitle(titoloHeader);
        alert.setHeaderText(titoloHeader);
        
        DialogUtils.personalizza(alert, playlistTilePane, "⚠", "#FF6E57");
        
        alert.showAndWait();
    }
}