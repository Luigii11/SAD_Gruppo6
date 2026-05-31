package it.unisa.diem.sad_gruppo6.commands;
import java.util.Stack;
/**
 * @file AppCommand.java
 * 
 * Classe concreta per la gestione dei comandi nell'applicazione e 
 * consente di eseguire comandi e di tenere traccia della loro esecuzione.
 * Utilizza il pattern Command per incapsulare azioni che possono essere eseguite e mantenute in una cronologia.  
 * 
 * @pattern Command
 * 
 * @author EmanuelChirico
 */
public class CommandManager 
{

    private Stack<AppCommand> history;

    /**
     * Costruttore del CommandManager, inizializza la cronologia dei comandi eseguiti.
     * 
     * @param command il comando da eseguire.
     */

    public CommandManager() 
    {
        history = new Stack<>();
    }

    /**
     * Segue un comando e lo aggiunge alla cronologia dei comandi eseguiti.
     * 
     * @param command il comando da eseguire.
     */
    
    public void execute(AppCommand command) 
    {
        command.execute();
        history.push(command);
    }

}
