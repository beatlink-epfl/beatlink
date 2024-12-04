package com.epfl.beatlink.repository.library

import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.epfl.beatlink.model.library.Playlist
import com.epfl.beatlink.model.library.PlaylistRepository
import com.epfl.beatlink.model.library.PlaylistTrack
import com.epfl.beatlink.model.spotify.objects.SpotifyTrack
import com.epfl.beatlink.model.spotify.objects.State
import com.epfl.beatlink.utils.ImageUtils.base64ToBitmap
import com.epfl.beatlink.utils.ImageUtils.resizeAndCompressImageFromUri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

@Suppress("UNCHECKED_CAST")
class PlaylistRepositoryFirestore(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : PlaylistRepository {

  private val collectionPath = "playlists"
  private var userID = ""

  /** Helper function to convert DocumentSnapshot to Playlist */
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
    val playlistTracks: List<PlaylistTrack> =
        (doc.get("playlistTracks") as? List<Map<String, Any>>)?.mapNotNull { trackData ->
          try {
            PlaylistTrack(
                track =
                    SpotifyTrack(
                        name = trackData["name"] as? String ?: "",
                        artist = trackData["artist"] as? String ?: "",
                        trackId = trackData["trackId"] as? String ?: "",
                        cover = trackData["cover"] as? String ?: "",
                        duration = (trackData["duration"] as? Long)?.toInt() ?: 0,
                        popularity = (trackData["popularity"] as? Long)?.toInt() ?: 0,
                        state =
                            State.valueOf(trackData["state"] as? String ?: State.PAUSE.toString())),
                likes = (trackData["likes"] as? Long)?.toInt() ?: 0,
                likedBy =
                    (trackData["likedBy"] as? List<String>)?.toMutableList() ?: mutableListOf())
          } catch (e: Exception) {
            Log.e("documentToPlaylist", "Error mapping PlaylistTrack: $trackData", e)
            null
          }
        } ?: emptyList()

    return Playlist(
        playlistID = playlistID,
        playlistCover = playlistCover,
        playlistName = playlistName,
        playlistDescription = playlistDescription,
        playlistPublic = playlistPublic,
        userId = userId,
        playlistOwner = playlistOwner,
        playlistCollaborators = playlistCollaborators,
        playlistTracks = playlistTracks,
        nbTracks = playlistTracks.size)
  }

  /** Helper function: Storing PlaylistTrack in Firestore */
  fun playlistTrackToMap(track: PlaylistTrack): Map<String, Any> {
    return mapOf(
        "name" to track.track.name,
        "artist" to track.track.artist,
        "trackId" to track.track.trackId,
        "cover" to track.track.cover,
        "duration" to track.track.duration,
        "popularity" to track.track.popularity,
        "state" to track.track.state.name, // Convert enum to string
        "likes" to track.likes,
        "likedBy" to track.likedBy)
  }

  /** Helper function: Convert Playlist to a Map for Firestore */
  fun playlistToMap(playlist: Playlist): Map<String, Any?> {
    return mapOf(
        "playlistID" to playlist.playlistID,
        "playlistCover" to playlist.playlistCover,
        "playlistName" to playlist.playlistName,
        "playlistDescription" to playlist.playlistDescription,
        "playlistPublic" to playlist.playlistPublic,
        "userId" to playlist.userId,
        "playlistOwner" to playlist.playlistOwner,
        "playlistCollaborators" to playlist.playlistCollaborators,
        "playlistTracks" to playlist.playlistTracks.map { playlistTrackToMap(it) },
        "nbTracks" to playlist.nbTracks)
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

  override fun getUserId(): String? {
    val userId = auth.currentUser?.uid
    Log.d("AUTH", "Current user ID: $userId")
    userID = userId ?: ""
    return userId
  }

  /** Retrieves a list of playlists of the user from Firestore */
  override fun getOwnedPlaylists(
      onSuccess: (List<Playlist>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
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

  /** Retrieve a list of playlists of playlists that are shared with the user from Firestore */
  override fun getSharedPlaylists(
      onSuccess: (List<Playlist>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    val docRef =
        db.collection(collectionPath)
            .whereArrayContains("playlistCollaborators", userID)
            .whereNotEqualTo("userId", userID)
    docRef
        .get()
        .addOnSuccessListener { res ->
          val playlistList =
              res?.documents?.mapNotNull { doc -> documentToPlaylist(doc) }
                  ?: emptyList() // If no documents, return an empty list
          onSuccess(playlistList)
        }
        .addOnFailureListener { exception ->
          Log.e(TAG, "Error retrieving shared playlists", exception)
          onFailure(exception)
        }
  }

  /** Retrieves a list of public playlists from Firestore */
  override fun getPublicPlaylists(
      onSuccess: (List<Playlist>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    val docRef =
        db.collection(collectionPath)
            .whereEqualTo("playlistPublic", true)
            .whereNotEqualTo("userId", userID)
    docRef
        .get()
        .addOnSuccessListener { res ->
          val playlistList =
              res?.documents?.mapNotNull { doc -> documentToPlaylist(doc) }
                  ?: emptyList() // If no documents, return an empty list
          onSuccess(playlistList)
        }
        .addOnFailureListener { exception ->
          Log.e(TAG, "Error retrieving public playlists", exception)
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
      val playlistData = playlistToMap(playlist)
      val docRef = db.collection(collectionPath).document(playlist.playlistID)
      docRef
          .set(playlistData)
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
      val playlistData = playlistToMap(playlist)
      db.collection(collectionPath)
          .document(playlist.playlistID)
          .set(playlistData)
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

  override fun updatePlaylistCollaborators(
      playlist: Playlist,
      newCollabList: List<String>,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    db.collection(collectionPath)
        .document(playlist.playlistID)
        .update("playlistCollaborators", newCollabList)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { err ->
          Log.e(TAG, "Error updating document CollabList field", err)
          onFailure(err)
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

  /** Updates only the list of tracks contained in the playlist in Firestore */
  override fun updatePlaylistTracks(
      playlist: Playlist,
      newListTracks: List<PlaylistTrack>,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    // Convert the list of PlaylistTrack objects to a list of maps
    val playlistTracksMap = newListTracks.map { playlistTrackToMap(it) }
    db.collection(collectionPath)
        .document(playlist.playlistID)
        .update("playlistTracks", playlistTracksMap)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { err ->
          Log.e(TAG, "Error updating playlistTracks field", err)
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

  /** Uploads the playlist cover image to Firestore */
  override fun uploadPlaylistCover(imageUri: Uri, context: Context, playlist: Playlist) {
    val base64Image = resizeAndCompressImageFromUri(imageUri, context)
    if (base64Image != null) {
      savePlaylistCoverBase64(playlist, base64Image)
    } else {
      Log.e("UPLOAD_PLAYLIST_COVER_ERROR", "Failed to convert image to Base64")
    }
  }

  /** Saves the playlist cover image as a Base64 string in Firestore */
  fun savePlaylistCoverBase64(playlist: Playlist, base64Image: String) {
    val playlistDoc = db.collection(collectionPath).document(playlist.playlistID)
    val playlistData = mapOf("playlistCover" to base64Image)

    playlistDoc.set(playlistData, SetOptions.merge())
  }

  /** Loads the playlist cover image from Firestore */
  override fun loadPlaylistCover(playlist: Playlist, onBitmapLoaded: (Bitmap?) -> Unit) {
    val playlistDoc = db.collection(collectionPath).document(playlist.playlistID)

    playlistDoc
        .get()
        .addOnSuccessListener { document ->
          val cover = document.getString("playlistCover")
          val bitmap = cover?.let { base64ToBitmap(it) }
          onBitmapLoaded(bitmap)
        }
        .addOnFailureListener { e ->
          Log.e("LOAD_PLAYLIST_COVER_ERROR", "Error loading playlist cover", e)
          onBitmapLoaded(null)
        }
  }
}
