package com.example.chw_app

import android.os.Bundle
import androidx.compose.ui.platform.LocalContext
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

private val Teal = Color(0xFF087F8C)
private val DarkText = Color(0xFF183238)
private val MutedText = Color(0xFF667A80)
private val PageBackground = Color(0xFFF4F8F8)
private val PendingBackground = Color(0xFFFFF0D5)
private val PendingText = Color(0xFF8A5A00)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dao = AppDatabase
            .getDatabase(applicationContext)
            .visitDao()

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = PageBackground
                ) {
                    CHWHomeScreen(dao)
                }
            }
        }
    }
}

@Composable
fun CHWHomeScreen(dao: VisitDao) {
    val context = LocalContext.current

    var selectedVisit by remember {
        mutableStateOf<VisitRecord?>(null)
    }

    var showVisitForm by remember {
        mutableStateOf(false)
    }

    val scope = rememberCoroutineScope()

    val visits by dao.getAllVisits()
        .collectAsState(initial = emptyList())

    val pendingCount = visits.count {
        it.syncStatus == "Pending"
    }



    if (showVisitForm) {
        VisitForm(
            onSave = { visit ->
                scope.launch {
                    dao.insertVisit(visit)
                    SyncScheduler.scheduleSync(context)
                    showVisitForm = false
                }
            },
            onCancel = {
                showVisitForm = false
            }
        )
    } else if (selectedVisit != null) {
        VisitDetailsScreen(
            visit = selectedVisit!!,
            onBack = {
                selectedVisit = null
            }
        )
    } else

    Column(
            modifier = Modifier
                .fillMaxSize()
                .background(PageBackground)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 20.dp, vertical = 18.dp)
            ) {

                Text(
                    text = "CHW CONNECT",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Teal,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Community Health",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkText
                )

                Text(
                    text = "Visit Records",
                    fontSize = 16.sp,
                    color = MutedText
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(9.dp)
                            .background(
                                Color(0xFFD18B24),
                                CircleShape
                            )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "CONNECTION STATUS NOT CHECKED",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MutedText
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 18.dp)
            ) {

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Your overview",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkText
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Summary cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SummaryCard(
                        title = "Total visits",
                        value = visits.size.toString(),
                        description = "Saved on device",
                        modifier = Modifier.weight(1f)
                    )

                    SummaryCard(
                        title = "Pending sync",
                        value = pendingCount.toString(),
                        description = "Awaiting upload",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // New visit action
                Button(
                    onClick = {
                        showVisitForm = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Teal
                    )
                ) {
                    Text(
                        text = "+  New Visit Record",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent visits",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkText
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = "${visits.size} records",
                        fontSize = 12.sp,
                        color = MutedText
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (visits.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No visits yet",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DarkText
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Saved patient visits will appear here.",
                                fontSize = 13.sp,
                                color = MutedText
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(
                            items = visits,
                            key = { it.localId }
                        ) { visit ->

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedVisit = visit
                                    },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White
                                ),
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = 1.dp
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                text = visit.patientName,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = DarkText
                                            )

                                            Spacer(
                                                modifier = Modifier.height(4.dp)
                                            )

                                            Text(
                                                text = visit.villageName,
                                                fontSize = 13.sp,
                                                color = MutedText
                                            )
                                        }

                                        SyncStatusBadge(
                                            status = visit.syncStatus
                                        )
                                    }

                                    Spacer(
                                        modifier = Modifier.height(14.dp)
                                    )

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = visit.visitType,
                                            fontSize = 12.sp,
                                            color = Teal,
                                            fontWeight = FontWeight.SemiBold
                                        )

                                        Spacer(
                                            modifier = Modifier.width(8.dp)
                                        )

                                        Text(
                                            text = "•",
                                            color = MutedText
                                        )

                                        Spacer(
                                            modifier = Modifier.width(8.dp)
                                        )

                                        Text(
                                            text = visit.urgencyLevel,
                                            fontSize = 12.sp,
                                            color = MutedText
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }


@Composable
fun SummaryCard(
    title: String,
    value: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                fontSize = 12.sp,
                color = MutedText
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Teal
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = description,
                fontSize = 11.sp,
                color = MutedText
            )
        }
    }
}

@Composable
fun SyncStatusBadge(status: String) {

    val backgroundColor = when (status) {
        "Synced" -> Color(0xFFDDF5E7)
        "Syncing" -> Color(0xFFDDECFB)
        else -> PendingBackground
    }

    val textColor = when (status) {
        "Synced" -> Color(0xFF176B3A)
        "Syncing" -> Color(0xFF245B91)
        else -> PendingText
    }

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(50)
    ) {
        Text(
            text = status,
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 6.dp
            ),
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = textColor
        )
    }
}
