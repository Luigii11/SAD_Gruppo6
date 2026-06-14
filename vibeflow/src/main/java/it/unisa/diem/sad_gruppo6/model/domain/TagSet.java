/**
 * @file TagSet.java
 * Classe di definizione di un oggetto di tipo 'TagSet', insieme dei tag
 * visivi associati a una traccia musicale. Incapsula la collezione di
 * oggetti 'Tag' e fornisce le operazioni di base per la loro gestione,
 * facendo rispettare l'invariante secondo cui i tag di sistema (EXPLICIT,
 * NEW_RELEASE) non possono essere modificati tramite l'API pubblica
 * addTag()/removeTag(), ma solo tramite setSystemTag(), riservata
 * all'assegnazione automatica in fase di creazione della traccia.
 *
 * @author ChiaraCrisci
 */
package it.unisa.diem.sad_gruppo6.model.domain;

import java.util.EnumSet;
import java.util.Set;

public class TagSet
{
    private final Set<Tag> tags;

    /**
     * Costruttore della classe 'TagSet'.
     * Inizializza l'insieme dei tag come vuoto.
     */
    public TagSet()
    {
        this.tags = EnumSet.noneOf(Tag.class);
    }

    /**
     * Aggiunge un tag gestibile dall'utente al set, se non già presente.
     *
     * @param tag Il tag da aggiungere.
     * @throws IllegalArgumentException Se il tag è null o è un tag di sistema (EXPLICIT, NEW_RELEASE).
     */
    public void addTag(Tag tag)
    {
        validateUserEditable(tag);
        tags.add(tag);
    }

    /**
     * Rimuove un tag gestibile dall'utente dal set, se presente.
     *
     * @param tag Il tag da rimuovere.
     * @throws IllegalArgumentException Se il tag è null o è un tag di sistema (EXPLICIT, NEW_RELEASE).
     */
    public void removeTag(Tag tag)
    {
        validateUserEditable(tag);
        tags.remove(tag);
    }

    /**
     * Assegna un tag di sistema al set. Riservato all'uso interno da parte
     * del TrackController durante la creazione della traccia (AC3); non
     * deve essere invocato per modificare i tag su richiesta dell'utente.
     *
     * @param tag Il tag di sistema da assegnare (EXPLICIT o NEW_RELEASE).
     * @throws IllegalArgumentException Se il tag è null o non è un tag di sistema.
     */
    public void setSystemTag(Tag tag)
    {
        if (tag == null)
        {
            throw new IllegalArgumentException("Il tag da assegnare non può essere null.");
        }
        if (!tag.isSystemAssigned())
        {
            throw new IllegalArgumentException("Il tag " + tag + " non è un tag di sistema.");
        }
        tags.add(tag);
    }

    /**
     * Verifica se il set contiene un determinato tag.
     *
     * @param tag Il tag da verificare.
     * @return true se il tag è presente nel set, false altrimenti.
     */
    public boolean hasTag(Tag tag)
    {
        return tags.contains(tag);
    }

    /**
     * Restituisce l'insieme dei tag attualmente assegnati.
     *
     * @return Una copia dell'insieme contenente tutti i tag presenti.
     */
    public Set<Tag> getTags()
    {
        return EnumSet.copyOf(tags.isEmpty() ? EnumSet.noneOf(Tag.class) : tags);
    }

    /**
     * Rimuove tutti i tag dal set, riportandolo allo stato iniziale vuoto.
     */
    public void clear()
    {
        tags.clear();
    }

    /**
     * Verifica che il tag fornito sia valido e gestibile dall'utente.
     *
     * @param tag Il tag da validare.
     * @throws IllegalArgumentException Se il tag è null o è un tag di sistema.
     */
    private void validateUserEditable(Tag tag)
    {
        if (tag == null)
        {
            throw new IllegalArgumentException("Il tag non può essere null.");
        }
        if (tag.isSystemAssigned())
        {
            throw new IllegalArgumentException("Il tag " + tag + " è assegnato automaticamente dal sistema e non può essere modificato dall'utente.");
        }
    }
}