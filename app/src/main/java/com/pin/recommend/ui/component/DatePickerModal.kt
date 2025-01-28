package com.pin.recommend.ui.component

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import com.pin.recommend.ui.character.convertMillisToDate
import java.time.Instant
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    initialValue: Date? = null,
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()
    initialValue?.let {
        datePickerState.selectedDateMillis = initialValue.time
    }
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(showModeToggle = false, state = datePickerState)
    }
}

@Composable
fun DatePickerTextField(
    value: Date = Date(),
    onDateSelected: (Date?) -> Unit
) {
    var showModal by remember { mutableStateOf(false) }

    TextField(
        readOnly = true,
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.White,
        ),
        value = convertMillisToDate(value.toInstant().toEpochMilli()),
        onValueChange = { },
        placeholder = { androidx.compose.material.Text("YYYY/MM/DD") },
        trailingIcon = {
            Icon(Icons.Default.DateRange, contentDescription = "Select date")
        },
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(value) {
                awaitEachGesture {
                    awaitFirstDown(pass = PointerEventPass.Initial)
                    val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                    if (upEvent != null) {
                        showModal = true
                    }
                }
            }
    )

    if (showModal) {
        DatePickerModal(
            initialValue = value,
            onDateSelected = {
                if (it != null) {
                    onDateSelected(Date.from(Instant.ofEpochMilli(it)))
                }
            },
            onDismiss = { showModal = false }
        )
    }
}

