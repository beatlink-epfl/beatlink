import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.ListenableWorker.Result
import androidx.work.testing.TestListenableWorkerBuilder
import com.epfl.beatlink.repository.map.user.ExpiredMapUsersWorker
import com.epfl.beatlink.repository.map.user.MapUsersRepositoryFirestore
import com.epfl.beatlink.repository.map.user.WorkerFactory
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

@RunWith(AndroidJUnit4::class)
class ExpiredMapUsersWorkerTest {

  private lateinit var context: Context
  private lateinit var mockMapUserRepository: MapUsersRepositoryFirestore

  @Before
  fun setup() {
    context = ApplicationProvider.getApplicationContext()
    FirebaseApp.initializeApp(context)
    mockMapUserRepository = mock(MapUsersRepositoryFirestore::class.java)
  }

  @Test
  fun testDoWork_Success() = runTest {
    // Mock the deleteExpiredUsers method to return true
    `when`(mockMapUserRepository.deleteExpiredUsers()).thenReturn(true)

    // Create the worker with the mocked repository
    val worker =
        TestListenableWorkerBuilder<ExpiredMapUsersWorker>(context)
            .setWorkerFactory(WorkerFactory(mockMapUserRepository))
            .build()

    // Execute the work and verify the result
    runBlocking {
      val result = worker.doWork()
      assertThat(result, `is`(Result.success()))
    }
  }

  @Test
  fun testDoWork_Failure() = runTest {
    // Mock the deleteExpiredUsers method to return false
    `when`(mockMapUserRepository.deleteExpiredUsers()).thenReturn(false)

    // Create the worker with the mocked repository
    val worker =
        TestListenableWorkerBuilder<ExpiredMapUsersWorker>(context)
            .setWorkerFactory(WorkerFactory(mockMapUserRepository))
            .build()

    // Execute the work and verify the result
    runBlocking {
      val result = worker.doWork()
      assertThat(result, `is`(Result.retry()))
    }
  }
}
