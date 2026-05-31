/**
 * @file Track.java
 * Classe di definizione di un oggetto di tipo 'Track'.
 * 
 * @author EmanuelChirico
 */

package it.unisa.diem.sad_gruppo6.models;
import java.time.LocalDate;
import java.util.Objects;

public class Track 
{
    // Attributi
    private String title;
    private String author;
    private int duration;
    private String genre;
    private int year; 
    private int playCount;

    /**
     * Costruttore classe 'Track'.
     * 
     * @param title Il titolo della traccia.
     * @param author L'autore della traccia.
     * @param duration La durata della traccia in secondi.
     * @param genre Il genere musicale della traccia.
     * @param year L'anno di pubblicazione della traccia.  
     * @param playCount Il numero di volte che la traccia è stata riprodotta.
     */

    public Track(String title, String author, int duration, String genre, int year) 
    {
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.year = year;
    }

    // Getter e Setter
    public String getTitle() 
    {
        return title;
    }

    /**
     * Setter del titolo della traccia, con controllo di validità sull'input.
     * 
     * @param title Il titolo da assegnare alla traccia.
     * @throws IllegalArgumentException Se il titolo è null o vuoto.
     */

    public void setTitle(String title) 
    {
        if (title == null || title.isBlank())
        {
            throw new IllegalArgumentException("Il titolo della traccia non può essere vuoto.");
        }
        this.title = title;
    }

    public String getAuthor() 
    {
        return author;
    }
    
    /**
     * Setter dell'autore della traccia, con controllo di validità sull'input.
     * 
     * @param author L'artista che ha composto la traccia.
     * @throws IllegalArgumentException Se la stringa dell'autore è null o vuoto.
     */

    public void setAuthor(String author) 
    {
        if (author == null || author.isBlank())
        {
            throw new IllegalArgumentException("L'autore della traccia non può essere vuoto.");
        }
        this.author = author;
    }

    public String getGenre() 
    {
        return genre;
    }

    /**
     * Setter del genere musicale della traccia, con controllo di validità sull'input.
     * 
     * @param genre Genere musicale della traccia.
     * @throws IllegalArgumentException Se la stringa del genere è null o vuoto.
     */

    public void setGenre(String genre)
    {
        if (genre == null || genre.isBlank())
        {
            throw new IllegalArgumentException("Il genere della traccia non può essere vuoto.");
        }
        this.genre = genre;
    }

    public int getDuration() 
    {
        return duration;
    }

    public int getYear() 
    {
        return year;
    }

    /**
     * Setter dell'anno di pubblicazione della traccia, con controllo di validità sull'input.
     * 
     * @param year Anno di uscita della traccia.
     * @throws IllegalArgumentException Se l'anno di pubblicazione è inferiore al 1970 o superiore all'anno corrente.
     */

    public void setYear(int year) 
    {
        int currentYear = LocalDate.now().getYear();
        if (year < 1970 || year > currentYear) 
        {
            throw new IllegalArgumentException("L'anno di pubblicazione deve essere compreso tra 1970 e " + currentYear + ".");
        }
        this.year = year;
    }

    /**
     * Getter del numero di riproduzioni della traccia
     * 
     * @return Il numero di volte che la traccia è stata riprodotta.
     */
    public int getPlayCount() 
    {
        return playCount;
    }

     /**
     * Metodo equals sovrascritto per confrontare due oggetti di tipo 'Track' basandosi su titolo e autore,
     * ignorando gli altri attributi come durata, genere, anno e playCount, in quanto due tracce con lo stesso 
     * titolo e autore possono essere considerate la stessa traccia anche se differiscono per altri attributi.
     *  
     * @return true se i titoli e gli autori delle tracce sono uguali, false altrimenti.
     */
    @Override
    public boolean equals(Object obj) 
    {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Track other = (Track) obj;
        return Objects.equals(title, other.title) && Objects.equals(author, other.author);
    }

     /**
     * Metodo hashCode sovrascritto per generare un codice hash basato su titolo e autore, in coerenza con il metodo equals
     * 
     * @return L'hash generato a partire da titolo e autore della traccia.
     */
    @Override
    public int hashCode() 
    {
        return Objects.hash(title, author);
    }

}
