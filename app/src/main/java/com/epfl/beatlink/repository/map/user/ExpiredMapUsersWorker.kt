package com.epfl.beatlink.repository.map.user

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.FirebaseFirestore

/**
 * A worker class responsible for removing expired MapUsers from Firestore.
 *
 * This worker utilizes the WorkManager library and is implemented as a CoroutineWorker, allowing
 * for asynchronous operations. It periodically deletes MapUsers whose `lastUpdated` field has
 * exceeded a specified time-to-live (TTL) duration.
 *
 * @param context The application context provided by WorkManager.
 * @param workerParams The parameters for this worker, provided by WorkManager.
 * @constructor Creates an instance of the ExpiredMapUsersWorker.
 */
class ExpiredMapUsersWorker(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

  private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
  private val mapUsersRepository = MapUsersRepositoryFirestore(db)

  override suspend fun doWork(): Result {
    return try {
      val success = mapUsersRepository.deleteExpiredUsers()
      if (success) {
        Log.d("ExpiredMapUsersWorker", "Successfully deleted expired MapUsers.")
        Result.success()
      } else {
        Log.w("ExpiredMapUsersWorker", "No expired MapUsers to delete or an error has occurred.")
        Result.retry()
      }
    } catch (e: Exception) {
      Log.e("ExpiredMapUsersWorker", "Error deleting expired MapUsers: ${e.message}", e)
      Result.retry()
    }
  }
}
