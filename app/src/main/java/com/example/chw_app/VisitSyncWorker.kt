
package com.example.chw_app

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import java.io.IOException
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException

class VisitSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val dao = AppDatabase
            .getDatabase(applicationContext)
            .visitDao()

        return try {
            val pendingVisits = dao.getPendingVisits()

            for (visit in pendingVisits) {
                val response = ApiClient.service.uploadVisit(
                    visit.toUploadRequest()
                )

                if (response.success &&
                    response.localId == visit.localId
                ) {
                    dao.updateSyncStatus(
                        localId = visit.localId,
                        status = "Synced"
                    )
                } else {
                    return Result.retry()
                }
            }

            Result.success()

        } catch (e: CancellationException) {
            throw e
        } catch (e: IOException) {
            Result.retry()
        } catch (e: HttpException) {
            if (e.code() in 500..599) {
                Result.retry()
            } else {
                Result.failure()
            }
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
