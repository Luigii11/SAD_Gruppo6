package it.unisa.diem.sad_gruppo6.model.playback.states;

/**
 * Interfaccia Subject del pattern Observer per lo stato del playback.
 * Definisce il contratto per la registrazione, rimozione e notifica degli observer.
 *
 * @pattern Observer (Subject)
 */
public interface PlaybackSubject {
    void registerObserver(PlaybackObserver observer);
    void removeObserver(PlaybackObserver observer);
    void notifyObservers();
}
