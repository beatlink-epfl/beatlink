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

  override fun updatePlaylist(
      playlist: Playlist,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    try {
      db.runTransaction { transaction ->
            val playlistDocRef = db.collection(collectionPath).document(playlist.playlistID)
            val playlistData = playlistToMap(playlist)

            // Update the playlist document
            transaction[playlistDocRef] = playlistData
          }
          .addOnSuccessListener { onSuccess() }
          .addOnFailureListener { e ->
            Log.e(TAG, "Error updating playlist", e)
            onFailure(e)
          }
    } catch (e: Exception) {
      Log.e(TAG, "Unexpected error in updatePlaylist", e)
      onFailure(e)
    }
  }

  override fun deleteOwnedPlaylists(onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    try {
      // Query for all playlists owned by the current user
      val query = db.collection(collectionPath).whereEqualTo("userId", userID)

      query
          .get()
          .addOnSuccessListener { querySnapshot ->
            val batch = db.batch()

            // Add delete operations for each document found
            for (document in querySnapshot.documents) {
              val docRef = document.reference
              batch.delete(docRef)
            }

            // Commit the batch delete operation
            batch
                .commit()
                .addOnSuccessListener {
                  Log.d(TAG, "Successfully deleted owned playlists")
                  onSuccess()
                }
                .addOnFailureListener { e ->
                  Log.e(TAG, "Error committing batch delete", e)
                  onFailure(e)
                }
          }
          .addOnFailureListener { e ->
            Log.e(TAG, "Error retrieving owned playlists", e)
            onFailure(e)
          }
    } catch (e: Exception) {
      Log.e(TAG, "Unexpected error in deleteOwnedPlaylists", e)
      onFailure(e)
    }
  }

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

  override fun uploadPlaylistCover(imageUri: Uri, context: Context, playlist: Playlist) {
    val base64Image = resizeAndCompressImageFromUri(imageUri, context)
    if (base64Image != null) {
      savePlaylistCoverBase64(playlist, base64Image)
    } else {
      Log.e("UPLOAD_PLAYLIST_COVER_ERROR", "Failed to convert image to Base64")
    }
  }

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

  /**
   * Save the playlist cover image as a Base64 string in Firestore.
   *
   * @param playlist The Playlist object.
   * @param base64Image The Base64 encoded string of the playlist cover image.
   */
  fun savePlaylistCoverBase64(playlist: Playlist, base64Image: String) {
    val playlistDoc = db.collection(collectionPath).document(playlist.playlistID)
    val playlistData = mapOf("playlistCover" to base64Image)

    playlistDoc[playlistData] = SetOptions.merge()
  }

  /**
   * Convert a Firestore DocumentSnapshot to a Playlist object.
   *
   * @param doc The DocumentSnapshot containing the playlist data.
   * @return The Playlist object created from the DocumentSnapshot.
   */
  fun documentToPlaylist(doc: DocumentSnapshot): Playlist {
    val playlistID: String = doc.getString("playlistID") ?: ""
    val playlistName: String = doc.getString("playlistName") ?: ""
    val playlistDescription: String = doc.getString("playlistDescription") ?: ""
    val playlistCover: String = doc.getString("playlistCover") ?: ""
    val playlistPublic: Boolean = doc.getBoolean("playlistPublic") ?: false
    val userId: String = doc.getString("userId") ?: ""
    val playlistOwner: String = doc.getString("playlistOwner") ?: ""
    val playlistCollaborators: List<String> =
        doc["playlistCollaborators"] as? List<String> ?: emptyList()
    val playlistTracks: List<PlaylistTrack> =
        (doc["playlistTracks"] as? List<Map<String, Any>>)?.mapNotNull { trackData ->
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

  /**
   * Convert a PlaylistTrack object into a Map<String, Any> suitable for Firestore storage.
   *
   * @param track The PlaylistTrack object to be converted.
   * @return A Map<String, Any> representation of the PlaylistTrack object.
   */
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

  /**
   * Convert a Playlist object into a Map<String, Any?> suitable for Firestore storage.
   *
   * @param playlist The Playlist object to be converted.
   * @return A Map<String, Any?> representation of the Playlist object.
   */
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
}
