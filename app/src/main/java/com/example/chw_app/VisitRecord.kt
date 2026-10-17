package com.example.chw_app

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "visit_records")
data class VisitRecord(

    @PrimaryKey
    @ColumnInfo(name = "local_id")
    val localId: String,

    @ColumnInfo(name = "chw_id")
    val chwId: String,

    @ColumnInfo(name = "patient_name")
    val patientName: String,

    @ColumnInfo(name = "village_name")
    val villageName: String,

    @ColumnInfo(name = "visit_type")
    val visitType: String,

    @ColumnInfo(name = "vitals_bp")
    val vitalsBp: String,

    @ColumnInfo(name = "vitals_temp")
    val vitalsTemp: Double,

    @ColumnInfo(name = "urgency_level")
    val urgencyLevel: String,

    @ColumnInfo(name = "visit_notes")
    val visitNotes: String? = null,

    @ColumnInfo(name = "created_at_local")
    val createdAtLocal: String,

    @ColumnInfo(name = "sync_status")
    val syncStatus: String = "Pending"
)

