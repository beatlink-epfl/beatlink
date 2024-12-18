package com.epfl.beatlink.ui.library

import com.epfl.beatlink.model.library.Playlist
import com.epfl.beatlink.repository.library.PlaylistRepository
import com.epfl.beatlink.viewmodel.library.PlaylistViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.mockito.Mockito.mock

class FakePlaylistViewModel(
    playlistRepository: PlaylistRepository = mock(PlaylistRepository::class.java)
) : PlaylistViewModel(playlistRepository) {

  override fun updateTemporallyCollaborators(collaborators: List<String>) {
    (tempPlaylistCollaborators as MutableStateFlow).value = collaborators
  }

  override fun updateTemporallyIsPublic(isPublic: Boolean) {
    (tempPlaylistIsPublic as MutableStateFlow).value = isPublic
  }

  override fun selectPlaylist(playlist: Playlist) {
    (selectedPlaylist as MutableStateFlow).value = playlist
  }
}
