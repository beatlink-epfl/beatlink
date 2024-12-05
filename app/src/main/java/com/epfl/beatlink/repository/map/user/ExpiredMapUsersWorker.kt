package com.epfl.beatlink.repository.map.user

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import javax.inject.Inject

/**
 * A worker class responsible for removing expired MapUsers from Firestore.
 *
 * This CoroutineWorker runs in the background and deletes MapUsers whose `lastUpdated` timestamp
 * exceeds the configured TTL. The worker will retry if no expired MapUsers are found or if an error
 * occurs.
 *
 * @param context The application context provided by WorkManager.
 * @param workerParams Parameters for the worker provided by WorkManager.
 * @param mapUsersRepository The repository handling Firestore operations for MapUsers.
 */
class ExpiredMapUsersWorker(
    context: Context,
    workerParams: WorkerParameters,
    private val mapUsersRepository: MapUsersRepositoryFirestore
) : CoroutineWorker(context, workerParams) {

  override suspend fun doWork(): Result {
    val success = mapUsersRepository.deleteExpiredUsers()

    return if (success) {
      Log.d("ExpiredMapUsersWorker", "Successfully deleted expired MapUsers.")
      Result.success()
    } else {
      Log.w("ExpiredMapUsersWorker", "Worker did not delete any expired MapUsers.")
      Result.retry()
    }
  }
}

/**
 * A custom WorkerFactory that injects the MapUsersRepositoryFirestore dependency into the
 * ExpiredMapUsersWorker.
 *
 * This factory is invoked every time the worker runs, overriding the default implementation to
 * allow for custom worker-creation logic.
 *
 * @param mapUsersRepository The repository handling Firestore operations for MapUsers.
 */
class WorkerFactory
@Inject
constructor(private val mapUsersRepository: MapUsersRepositoryFirestore) : WorkerFactory() {

  override fun createWorker(
      appContext: Context,
      workerClassName: String,
      workerParameters: WorkerParameters
  ): ListenableWorker? {
    return if (workerClassName == ExpiredMapUsersWorker::class.java.name) {
      ExpiredMapUsersWorker(appContext, workerParameters, mapUsersRepository)
    } else {
      null
    }
  }
}
