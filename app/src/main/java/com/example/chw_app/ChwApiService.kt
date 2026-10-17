package com.example.chw_app

import retrofit2.http.Body
import retrofit2.http.POST

interface ChwApiService {

    @POST("api/visits")
    suspend fun uploadVisit(
        @Body visit: VisitUploadRequest
    ): UploadResponse
}