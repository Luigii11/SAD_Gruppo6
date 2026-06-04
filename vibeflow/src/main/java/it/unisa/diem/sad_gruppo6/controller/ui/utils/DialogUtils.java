package it.unisa.diem.sad_gruppo6.controller.ui.utils;

import javafx.scene.Node;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;

/**
 * Classe Utility per la gestione e configurazione globale dei dialoghi.
 * Centralizza l'applicazione del tema grafico e delle icone tipografiche.
 * Introdotta per soddisfare SRP e DRY nella gestione della UI.
 */
public final class DialogUtils {

    private DialogUtils() {
        throw new UnsupportedOperationException("Questa è una classe utility e non può essere istanziata.");
    }

    /**
     * Applica lo stile grafico VibeFlow e inietta un'icona personalizzata.
     * * @param dialog    Il dialogo/alert da configurare.
     * @param ownerNode Un nodo grafico della schermata chiamante per recuperare il CSS.
     * @param symbol    Il carattere Unicode da usare come icona.
     * @param colorHex  Il colore esadecimale da applicare al simbolo.
     */
    public static void personalizza(Dialog<?> dialog, Node ownerNode, String symbol, String colorHex) {
        // Eredita lo stile dal nodo radice attivo
        if (ownerNode != null && ownerNode.getScene() != null && ownerNode.getScene().getRoot() != null) {
            dialog.getDialogPane().getStylesheets().addAll(ownerNode.getScene().getRoot().getStylesheets());
        }

        // Configura l'icona testuale
        Label customIcon = new Label(symbol);
        customIcon.setStyle("-fx-font-size: 36px; -fx-text-fill: " + colorHex + "; -fx-font-weight: bold;");
        dialog.setGraphic(customIcon);
    }
}