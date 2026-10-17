package com.example.chw_app

data class VisitUploadRequest(
    val localId: String,
    val chwId: String,
    val patientName: String,
    val villageName: String,
    val visitType: String,
    val vitalsBp: String,
    val vitalsTemp: Double,
    val urgencyLevel: String,
    val visitNotes: String?,
    val createdAtLocal: String
)

data class UploadResponse(
    val success: Boolean,
    val message: String,
    val localId: String
)

fun VisitRecord.toUploadRequest(): VisitUploadRequest {
    return VisitUploadRequest(
        localId = localId,
        chwId = chwId,
        patientName = patientName,
        villageName = villageName,
        visitType = visitType,
        vitalsBp = vitalsBp,
        vitalsTemp = vitalsTemp,
        urgencyLevel = urgencyLevel,
        visitNotes = visitNotes,
        createdAtLocal = createdAtLocal
    )
}