/**
 * @file TagSetTest.java
 * @brief Classe di test per la validazione della logica interna di TagSet.
 *
 * @details La seguente classe di test unitari verifica il comportamento isolato della classe {@link TagSet}:
 * <li>L'inizializzazione corretta di un TagSet vuoto.</li>
 * <li>Le operazioni di aggiunta e rimozione dei tag utente (Favourite).</li>
 * <li>La prevenzione di duplicati all'interno dell'insieme.</li>
 * <li>L'invariante di protezione: il blocco di addTag/removeTag su tag di sistema (Explicit, NewRelease).</li>
 * <li>L'assegnazione corretta dei tag di sistema tramite setSystemTag.</li>
 * <li>Il meccanismo di copia difensiva del metodo getTags().</li>
 * <li>Il corretto svuotamento tramite il metodo clear().</li>
 * </ul>
 *
 * @see Tag
 * @see TagSet
 *
 * @author ChiaraCrisci
 */
package it.unisa.diem.sad_gruppo6.controllers; 

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unisa.diem.sad_gruppo6.model.domain.Tag;
import it.unisa.diem.sad_gruppo6.model.domain.TagSet;

public class TagSetTest {

    // Oggetto sotto test ri-inizializzato prima di ogni scenario.
    private TagSet tagSet;

    /**
     * @brief Setup eseguito prima di ogni test.
     */
    @BeforeEach
    public void setUp() {
        tagSet = new TagSet();
    }

    /**
     * @brief Un TagSet appena creato non contiene alcun tag ed è vuoto.
     */
    @Test
    public void testNewTagSetIsEmpty() {
        assertFalse(tagSet.hasTag(Tag.Favourite));
        assertFalse(tagSet.hasTag(Tag.Explicit));
        assertFalse(tagSet.hasTag(Tag.NewRelease));
        assertTrue(tagSet.getTags().isEmpty(),
                "Un TagSet appena inizializzato deve essere vuoto");
    }

    /**
     * @brief addTag(Favourite) aggiunge correttamente il tag e hasTag() ritorna true.
     */
    @Test
    public void testAddFavouriteTagSuccess() {
        tagSet.addTag(Tag.Favourite);

        assertTrue(tagSet.hasTag(Tag.Favourite),
                "Dopo addTag(Favourite) il set deve contenere il tag");
        assertEquals(1, tagSet.getTags().size());
    }

    /**
     * @brief Aggiungere lo stesso tag utente due volte non produce duplicati.
     */
    @Test
    public void testAddDuplicateTagDoesNotCreateDuplicate() {
        tagSet.addTag(Tag.Favourite);
        tagSet.addTag(Tag.Favourite); 

        assertEquals(1, tagSet.getTags().size(),
                "Il set non deve contenere duplicati per lo stesso tag");
    }

    /**
     * @brief removeTag(Favourite) rimuove il tag e hasTag() ritorna false.
     */
    @Test
    public void testRemoveFavouriteTagSuccess() {
        tagSet.addTag(Tag.Favourite);
        tagSet.removeTag(Tag.Favourite);

        assertFalse(tagSet.hasTag(Tag.Favourite),
                "Dopo removeTag(Favourite) il set non deve più contenere il tag");
        assertTrue(tagSet.getTags().isEmpty());
    }

    /**
     * @brief removeTag su tag utente assente non lancia eccezioni (operazione idempotente).
     */
    @Test
    public void testRemoveAbsentTagDoesNotThrow() {
        assertDoesNotThrow(() -> tagSet.removeTag(Tag.Favourite),
                "Rimuovere un tag utente non presente non deve lanciare eccezioni");
    }

    /**
     * @brief addTag(Explicit) deve lanciare IllegalArgumentException.
     * @details I tag di sistema non sono modificabili tramite addTag().
     */
    @Test
    public void testAddSystemTagExplicitThrows() {
        assertThrows(IllegalArgumentException.class, () -> tagSet.addTag(Tag.Explicit),
                "addTag(Explicit) deve lanciare IllegalArgumentException: è un tag di sistema");
    }

    /**
     * @brief addTag(NewRelease) deve lanciare IllegalArgumentException.
     */
    @Test
    public void testAddSystemTagNewReleaseThrows() {
        assertThrows(IllegalArgumentException.class, () -> tagSet.addTag(Tag.NewRelease),
                "addTag(NewRelease) deve lanciare IllegalArgumentException: è un tag di sistema");
    }

    /**
     * @brief removeTag(Explicit) deve lanciare IllegalArgumentException.
     * @details I tag di sistema non sono rimovibili tramite removeTag().
     */
    @Test
    public void testRemoveSystemTagExplicitThrows() {
        tagSet.setSystemTag(Tag.Explicit);
        assertThrows(IllegalArgumentException.class, () -> tagSet.removeTag(Tag.Explicit),
                "removeTag(Explicit) deve lanciare IllegalArgumentException: è un tag di sistema");
    }

    /**
     * @brief removeTag(NewRelease) deve lanciare IllegalArgumentException.
     */
    @Test
    public void testRemoveSystemTagNewReleaseThrows() {
        tagSet.setSystemTag(Tag.NewRelease);
        assertThrows(IllegalArgumentException.class, () -> tagSet.removeTag(Tag.NewRelease),
                "removeTag(NewRelease) deve lanciare IllegalArgumentException: è un tag di sistema");
    }

    /**
     * @brief addTag(null) deve lanciare IllegalArgumentException.
     */
    @Test
    public void testAddNullTagThrows() {
        assertThrows(IllegalArgumentException.class, () -> tagSet.addTag(null),
                "addTag(null) deve lanciare IllegalArgumentException");
    }

    /**
     * @brief removeTag(null) deve lanciare IllegalArgumentException.
     */
    @Test
    public void testRemoveNullTagThrows() {
        assertThrows(IllegalArgumentException.class, () -> tagSet.removeTag(null),
                "removeTag(null) deve lanciare IllegalArgumentException");
    }

    /**
     * @brief setSystemTag(Explicit) inserisce correttamente il tag nell'insieme.
     */
    @Test
    public void testSetSystemTagExplicitSuccess() {
        tagSet.setSystemTag(Tag.Explicit);
        assertTrue(tagSet.hasTag(Tag.Explicit),
                "Dopo setSystemTag(Explicit) il set deve contenere il tag");
    }

    /**
     * @brief setSystemTag(NewRelease) inserisce correttamente il tag nell'insieme.
     */
    @Test
    public void testSetSystemTagNewReleaseSuccess() {
        tagSet.setSystemTag(Tag.NewRelease);
        assertTrue(tagSet.hasTag(Tag.NewRelease),
                "Dopo setSystemTag(NewRelease) il set deve contenere il tag");
    }

    /**
     * @brief setSystemTag(Favourite) deve lanciare IllegalArgumentException.
     * @details Favourite non è un tag contrassegnato come di sistema.
     */
    @Test
    public void testSetSystemTagOnUserTagThrows() {
        assertThrows(IllegalArgumentException.class, () -> tagSet.setSystemTag(Tag.Favourite),
                "setSystemTag(Favourite) deve fallire: Favourite non è un tag di sistema");
    }

    /**
     * @brief setSystemTag(null) deve lanciare IllegalArgumentException.
     */
    @Test
    public void testSetSystemTagNullThrows() {
        assertThrows(IllegalArgumentException.class, () -> tagSet.setSystemTag(null),
                "setSystemTag(null) deve lanciare IllegalArgumentException");
    }


    /**
     * @brief getTags() restituisce una copia difensiva: modificarla non altera il set originale.
     */
    @Test
    public void testGetTagsReturnsDefensiveCopy() {
        tagSet.addTag(Tag.Favourite);
        
        Set<Tag> returnedTags = tagSet.getTags();

        returnedTags.clear();

        assertTrue(tagSet.hasTag(Tag.Favourite),
                "getTags() deve restituire una copia: svuotare il set restituito non deve alterare l'originale");
    }

    /**
     * @brief clear() svuota completamente il TagSet eliminando sia i tag utente sia quelli di sistema.
     */
    @Test
    public void testClearRemovesAllTags() {
        tagSet.addTag(Tag.Favourite);
        tagSet.setSystemTag(Tag.Explicit);

        tagSet.clear();

        assertFalse(tagSet.hasTag(Tag.Favourite), "Dopo clear() il tag Favourite deve essere rimosso");
        assertFalse(tagSet.hasTag(Tag.Explicit), "Dopo clear() il tag Explicit deve essere rimosso");
        assertTrue(tagSet.getTags().isEmpty(), "Dopo clear() l'insieme complessivo dei tag deve essere vuoto");
    }

    /**
     * @brief Flusso completo di toggle sequenziale (add -> has -> remove -> has).
     */
    @Test
    public void testFavouriteTagToggle() {
        tagSet.addTag(Tag.Favourite);
        assertTrue(tagSet.hasTag(Tag.Favourite), "Verifica presenza dopo inserimento");

        tagSet.removeTag(Tag.Favourite);
        assertFalse(tagSet.hasTag(Tag.Favourite), "Verifica assenza dopo rimozione");
    }
}