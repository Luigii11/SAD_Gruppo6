package it.unisa.diem.sad_gruppo6.model.library;

/**
 * Interfaccia Subject del pattern Observer per la libreria delle tracce.
 * Definisce il contratto per la registrazione, rimozione e notifica degli observer.
 *
 * @pattern Observer (Subject)
 */
public interface TrackLibrarySubject {
    void registerObserver(TrackLibraryObserver observer);
    void removeObserver(TrackLibraryObserver observer);
    void notifyObservers();
}
