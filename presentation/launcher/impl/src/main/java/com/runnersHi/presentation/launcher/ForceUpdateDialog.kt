package com.runnersHi.presentation.launcher

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun ForceUpdateDialog(
    onUpdateClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { /* 닫기 불가 */ },
        title = { Text("업데이트 필요") },
        text = { Text("새로운 버전이 있습니다. 업데이트 후 이용해 주세요.") },
        confirmButton = {
            TextButton(onClick = onUpdateClick) {
                Text("업데이트")
            }
        }
    )
}
