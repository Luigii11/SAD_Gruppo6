/**
 * @file PlaylistCreationDialogController.java
 * @brief Controller per la finestra modale di creazione di una nuova playlist.
 * @details Gestisce l'interazione dell'utente per l'inserimento del nome, invia il comando 
 * di creazione al controller di dominio e gestisce eventuali conflitti o errori di validazione.
 * @author LuigiAutorino
 */

package it.unisa.diem.sad_gruppo6.controller.ui.playlist;

import it.unisa.diem.sad_gruppo6.controller.business.playlist.PlaylistController;
import it.unisa.diem.sad_gruppo6.controller.ui.utils.DialogUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PlaylistCreationDialogController {

    /* Componenti grafici */
    @FXML private VBox rootContainer;
    @FXML private TextField playlistName;
    @FXML private Button saveButton;

    /* Attributi */    
    private PlaylistController playlistController;

    /**
     * @brief Inietta il controller di business logic.
     * @details Metodo chiamato dal controller "padre" (Home) prima di mostrare la finestra.
     * @param controller L'istanza attiva del gestore delle playlist.
     */
    public void setPlaylistController(PlaylistController controller) {
        this.playlistController = controller;
    }

    /* Eventi */

    /**
     * @brief Gestisce il tentativo di salvataggio della playlist.
     * @details Preleva il testo, lo invia al modello di dominio e chiude la finestra 
     * in caso di successo. Se il nome è invalido o duplicato, mostra un errore senza chiudere.
     */
    @FXML
    private void handleSave(ActionEvent event) {
        String userInput = playlistName.getText();
        
        try {
            // Tenta la creazione delegando la logica di business
            playlistController.createPlaylist(userInput);
            
            // Se non ci sono eccezioni, la playlist è creata. Chiudiamo il pop-up.
            close();
            
        } catch (IllegalArgumentException e) {
            // Intercetta stringhe vuote o nomi già esistenti nel sistema
            showError("Creation failed", e.getMessage());
        }
    }

    /**
     * @brief Annulla l'operazione e chiude la finestra.
     */
    @FXML
    private void handleBack(ActionEvent event) {
        close();
    }

    /* Utils */
    
    /**
     * @brief Chiude lo Stage (finestra) corrente associato a questo controller.
     */
    private void close() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    /**
     * @brief Genera un popup di errore bloccante tematizzato.
     * @param header Titolo dell'errore (es. "Creation failed").
     * @param content Dettaglio dell'eccezione (es. "Playlist already exists").
     */
    private void showError(String header, String content) {
        Alert alert = new Alert(AlertType.ERROR, content, ButtonType.OK);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        
        // Applica il tema scuro e l'icona personalizzata coerente col resto dell'app
        DialogUtils.personalizza(alert, rootContainer, "❌", "#FF4C30");
        
        alert.showAndWait();
    }
}