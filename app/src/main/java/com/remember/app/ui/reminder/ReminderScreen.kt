package com.remember.app.ui.reminder

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.remember.app.data.database.entity.Reminder
import com.remember.app.model.ReminderType
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderScreen(
    viewModel: ReminderViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("提醒", fontWeight = FontWeight.Bold) })
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                uiState.errorMessage != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center).padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("加载失败", style = MaterialTheme.typography.titleMedium)
                        Text(
                            uiState.errorMessage ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                uiState.dueReminders.isEmpty() && uiState.upcomingReminders.isEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.NotificationsNone,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("还没有提醒事项", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    }
                }
                else -> {
                    ReminderList(
                        dueReminders = uiState.dueReminders,
                        upcomingReminders = uiState.upcomingReminders,
                        getDaysRemaining = viewModel::getDaysRemaining,
                        onDelete = { viewModel.deleteReminder(it) },
                        onToggle = { reminder -> viewModel.toggleReminderActive(reminder.id, !reminder.isActive) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ReminderList(
    dueReminders: List<Reminder>,
    upcomingReminders: List<Reminder>,
    getDaysRemaining: (Long) -> Int,
    onDelete: (Reminder) -> Unit,
    onToggle: (Reminder) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (dueReminders.isNotEmpty()) {
            item {
                Text(
                    "最近提醒",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            items(dueReminders.size) { index ->
                val reminder = dueReminders[index]
                ReminderRow(
                    reminder = reminder,
                    daysRemaining = getDaysRemaining(reminder.targetDate),
                    onDelete = { onDelete(reminder) },
                    onToggle = { onToggle(reminder) }
                )
            }
        }

        if (upcomingReminders.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "接下来",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            items(upcomingReminders.size) { index ->
                val reminder = upcomingReminders[index]
                ReminderRow(
                    reminder = reminder,
                    daysRemaining = getDaysRemaining(reminder.targetDate),
                    onDelete = { onDelete(reminder) },
                    onToggle = { onToggle(reminder) }
                )
            }
        }
    }
}

@Composable
private fun ReminderRow(
    reminder: Reminder,
    daysRemaining: Int,
    onDelete: () -> Unit,
    onToggle: () -> Unit
) {
    val typeInfo = try {
        ReminderType.fromName(reminder.type)
    } catch (e: Throwable) {
        ReminderType.CUSTOM
    }

    val dateText = try {
        val fmt = SimpleDateFormat("MM/dd", Locale.getDefault())
        fmt.format(Date(reminder.targetDate))
    } catch (e: Throwable) {
        "?"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 图标
            Text(text = typeInfo.icon, style = MaterialTheme.typography.headlineSmall)

            Spacer(modifier = Modifier.width(10.dp))

            // 标题和日期
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = reminder.title.ifBlank { "未命名" },
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${typeInfo.displayName} · $dateText${if (reminder.isRepeatYearly) " · 每年" else ""}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                if (reminder.notes.isNotBlank()) {
                    Text(
                        text = reminder.notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // 剩余天数
            Text(
                text = if (daysRemaining == 0) "今天" else "${daysRemaining}天",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (daysRemaining <= reminder.advanceDays)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.width(6.dp))

            // 操作按钮
            IconButton(onClick = onToggle, modifier = Modifier.size(32.dp)) {
                Icon(
                    imageVector = if (reminder.isActive) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f)
                )
            }
        }
    }
}
