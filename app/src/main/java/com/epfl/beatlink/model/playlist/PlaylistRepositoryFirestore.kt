package com.epfl.beatlink.model.playlist

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class PlaylistRepositoryFirestore(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : PlaylistRepository {

  private val collectionPath = "playlists"
  private var userID = ""

  // helper function to convert DocumentSnapShot to Playlist
  fun documentToPlaylist(doc: DocumentSnapshot): Playlist {
    val playlistID: String = doc.getString("playlistID") ?: ""
    val playlistName: String = doc.getString("playlistName") ?: ""
    val playlistDescription: String = doc.getString("playlistDescription") ?: ""
    val playlistCover: String = doc.getString("playlistCover") ?: ""
    val playlistPublic: Boolean = doc.getBoolean("playlistPublic") ?: false
    val userId: String = doc.getString("userId") ?: ""
    val playlistOwner: String = doc.getString("playlistOwner") ?: ""
    val playlistCollaborators: List<String> =
        doc.get("playlistCollaborators") as? List<String> ?: emptyList()
    val playlistSongs: List<String> = doc.get("playlistSongs") as? List<String> ?: emptyList()

    return Playlist(
        playlistID = playlistID,
        playlistCover = playlistCover,
        playlistName = playlistName,
        playlistDescription = playlistDescription,
        playlistPublic = playlistPublic,
        userId = userId,
        playlistOwner = playlistOwner,
        playlistCollaborators = playlistCollaborators,
        playlistSongs = playlistSongs,
        nbTracks = playlistSongs.size)
  }

  /** Generates and returns a new unique ID for a playlist item */
  override fun getNewUid(): String {
    return db.collection(collectionPath).document().id
  }

  override fun init(onSuccess: () -> Unit) {
    auth.addAuthStateListener { firebaseAuth ->
      val currentUser = firebaseAuth.currentUser
      if (currentUser != null) {
        // User is authenticated, proceed with onSuccess
        userID = currentUser.uid
        onSuccess()
      } else {
        Log.d(TAG, "User not authenticated")
      }
    }
  }

  /** Retrieves a list of playlists from Firestore */
  override fun getPlaylists(onSuccess: (List<Playlist>) -> Unit, onFailure: (Exception) -> Unit) {
    val docRef = db.collection(collectionPath).whereEqualTo("userId", userID)
    docRef
        .get()
        .addOnSuccessListener { res ->
          val playlistList =
              res?.documents?.mapNotNull { doc -> documentToPlaylist(doc) }
                  ?: emptyList() // If no documents, return an empty list
          onSuccess(playlistList)
        }
        .addOnFailureListener { exception ->
          Log.e(TAG, "Error retrieving playlists", exception)
          onFailure(exception)
        }
  }

  /** Add a new playlist to Firestore */
  override fun addPlaylist(
      playlist: Playlist,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    try {
      // Create a new playlist with the `ownerId` field
      val playlistWithOwner = playlist.copy(userId = userID)
      val docRef = db.collection(collectionPath).document(playlistWithOwner.playlistID)
      docRef
          .set(playlistWithOwner)
          .addOnSuccessListener {
            Log.d(TAG, "DocumentSnapshot written with ID: ${docRef.id}")
            onSuccess()
          }
          .addOnFailureListener { err ->
            Log.e(TAG, "Error adding document", err)
            onFailure(err)
          }
    } catch (e: Exception) {
      Log.e(TAG, "Unexpected error in addPlaylist", e)
      onFailure(e)
    }
  }

  /** Updates an existing playlist in Firestore */
  override fun updatePlaylist(
      playlist: Playlist,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    try {
      db.collection(collectionPath)
          .document(playlist.playlistID)
          .set(playlist)
          .addOnSuccessListener { onSuccess() }
          .addOnFailureListener { err ->
            Log.e(TAG, "Error updating document", err)
            onFailure(err)
          }
    } catch (e: Exception) {
      Log.e(TAG, "Unexpected error in updatePlaylist", e)
      onFailure(e)
    }
  }

  /** Updates only the track count of a specific playlist in Firestore */
  override fun updatePlaylistTrackCount(
      playlist: Playlist,
      newTrackCount: Int,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    db.collection(collectionPath)
        .document(playlist.playlistID)
        .update("nbTracks", newTrackCount)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { err ->
          Log.e(TAG, "Error updating document Track Count field", err)
          onFailure(err)
        }
  }

  /** Updates only the list of songs contained in the playlist in Firestore */
  override fun updatePlaylistSongs(
      playlist: Playlist,
      newListSongs: List<String>,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {

    db.collection(collectionPath)
        .document(playlist.playlistID)
        .update("playlistSongs", newListSongs)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { err ->
          Log.e(TAG, "Error updating document Songs field", err)
          onFailure(err)
        }
  }

  /** Deletes a playlist by its ID from Firestore */
  override fun deletePlaylistById(
      id: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    try {
      db.collection(collectionPath)
          .document(id)
          .delete()
          .addOnSuccessListener { onSuccess() }
          .addOnFailureListener { err ->
            Log.e(TAG, "Error deleting document", err)
            onFailure(err)
          }
    } catch (e: Exception) {
      Log.e(TAG, "Unexpected error in deletePlaylist", e)
      onFailure(e)
    }
  }
}
