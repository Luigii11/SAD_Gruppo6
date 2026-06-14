package it.unisa.diem.sad_gruppo6.model.command;

import it.unisa.diem.sad_gruppo6.model.domain.Playlist;
import it.unisa.diem.sad_gruppo6.model.domain.Track;
import it.unisa.diem.sad_gruppo6.model.library.PlaylistLibrary;

public class ReorderTrackCommand implements AppCommand {

    private Playlist playlist;
    private Track track;
    private int newIndex;
    private int oldIndex;

    public ReorderTrackCommand(Playlist playlist, Track track, int newIndex) {
        this.playlist = playlist;
        this.track = track;
        this.newIndex = newIndex;
    }

    @Override
    public void execute() {
        this.oldIndex = playlist.getTracks().indexOf(track);
        playlist.reorder(track, newIndex);
        PlaylistLibrary.getInstance().updatePlaylist(playlist);
    }

    @Override
    public void undo() {
        playlist.reorder(track, oldIndex);
        PlaylistLibrary.getInstance().updatePlaylist(playlist);
    }
}