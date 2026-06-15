package it.unisa.diem.sad_gruppo6.model.library;

/**
 * Interfaccia Subject del pattern Observer per la libreria delle playlist.
 * Definisce il contratto per la registrazione, rimozione e notifica degli observer.
 *
 * @pattern Observer (Subject)
 */
public interface PlaylistLibrarySubject {
    void registerObserver(PlaylistLibraryObserver observer);
    void removeObserver(PlaylistLibraryObserver observer);
    void notifyObservers();
}
