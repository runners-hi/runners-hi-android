package com.runnersHi.debug

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runnersHi.core.network.ServerEndpoint
import com.runnersHi.presentation.common.theme.BlueGray40
import com.runnersHi.presentation.common.theme.BlueGray5
import com.runnersHi.presentation.common.theme.BlueGray80
import com.runnersHi.presentation.common.theme.BlueGray90
import com.runnersHi.presentation.common.theme.BlueGray95
import com.runnersHi.presentation.common.theme.Primary
import com.runnersHi.presentation.common.theme.PrimaryRed
import com.runnersHi.presentation.common.theme.PrimaryYellow

/**
 * 디버그 빌드 전용. 서버를 고르기 전에는 앱 대신 서버 선택 화면을 보여 주고,
 * 고른 뒤에는 앱 위에 현재 서버 배지를 얹는다. 배지를 누르면 서버를 다시 고른다.
 */
@Composable
fun DebugServerGate(content: @Composable () -> Unit) {
    val context = LocalContext.current
    var selected by remember { mutableStateOf(ServerSelection.current(context)) }
    var picking by remember { mutableStateOf(false) }

    val current = selected
    if (current == null || picking) {
        ServerPicker(
            current = current,
            onPick = { url ->
                when {
                    current == null -> {
                        ServerSelection.selectFirst(context, url)
                        selected = url
                    }
                    url == current -> picking = false
                    else -> ServerSelection.switchAndRestart(context, url)
                }
            },
            onCancel = if (current != null) ({ picking = false }) else null
        )
    } else {
        Box(Modifier.fillMaxSize()) {
            content()
            ServerBadge(
                url = ServerEndpoint.baseUrl,
                onClick = { picking = true },
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@Composable
private fun ServerBadge(url: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val color = when (url) {
        ServerSelection.PROD_URL -> PrimaryRed
        ServerSelection.DEV_URL -> Primary
        else -> PrimaryYellow
    }
    Text(
        text = ServerSelection.label(url),
        color = BlueGray95,
        fontSize = 9.sp,
        fontWeight = FontWeight.Bold,
        maxLines = 1,
        modifier = modifier
            .statusBarsPadding()
            .testTag("server_badge")
            .clip(RoundedCornerShape(6.dp))
            .background(color)
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 1.dp)
    )
}

@Composable
private fun ServerPicker(current: String?, onPick: (String) -> Unit, onCancel: (() -> Unit)?) {
    // 앱 테마의 글자 단위(em)와 텍스트 필드 라벨(sp)이 섞이면 터지므로 기본 타이포그래피로 그린다.
    androidx.compose.material3.MaterialTheme(typography = androidx.compose.material3.Typography()) {
        ServerPickerBody(current, onPick, onCancel)
    }
}

@Composable
private fun ServerPickerBody(current: String?, onPick: (String) -> Unit, onCancel: (() -> Unit)?) {
    val context = LocalContext.current
    var custom by remember {
        mutableStateOf(current?.takeIf {
            it != ServerSelection.PROD_URL && it != ServerSelection.DEV_URL && !it.startsWith(ServerSelection.MOCK_BASE_URL)
        } ?: ServerSelection.DEFAULT_CUSTOM_URL)
    }
    var session by remember {
        mutableStateOf(current?.removePrefix(ServerSelection.MOCK_BASE_URL)?.takeIf { current.startsWith(ServerSelection.MOCK_BASE_URL) }
            ?: ServerSelection.defaultSession())
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BlueGray90)
            .systemBarsPadding()
            .verticalScroll(androidx.compose.foundation.rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "서버 선택 (디버그)",
            color = BlueGray5,
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.testTag("server_picker")
        )
        Text(
            text = current?.let { "현재: ${ServerSelection.label(it)}. 다른 서버를 고르면 로그아웃하고 처음부터 다시 시작해요." }
                ?: "처음 한 번 고르면 저장돼요. 배지를 누르면 다시 고를 수 있어요.",
            color = BlueGray40,
            fontSize = 13.sp
        )
        Spacer(Modifier.height(8.dp))
        PickerButton("상용 (runnershi-api.oguriworks.com)", "server_prod") { onPick(ServerSelection.PROD_URL) }
        PickerButton("개발 (runnershi-dev-api.oguriworks.com)", "server_dev") { onPick(ServerSelection.DEV_URL) }
        OutlinedTextField(
            value = session,
            onValueChange = { session = it },
            singleLine = true,
            label = { Text("모의 서버 세션 이름") },
            colors = pickerFieldColors(),
            modifier = Modifier.fillMaxWidth().testTag("server_mock_session")
        )
        PickerButton("모의 서버 사용 (runnershi-mock /s/<세션>)", "server_mock") { onPick(ServerSelection.mockUrl(session)) }
        OutlinedTextField(
            value = custom,
            onValueChange = { custom = it },
            singleLine = true,
            label = { Text("직접 입력 (프록시 URL)") },
            colors = pickerFieldColors(),
            modifier = Modifier.fillMaxWidth().testTag("server_custom_input")
        )
        PickerButton("직접 입력으로 사용", "server_custom", enabled = custom.startsWith("http")) {
            onPick(custom.trim().trimEnd('/'))
        }
        var analytics by remember { mutableStateOf(ServerSelection.analyticsEnabled(context)) }
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("행동 로그 수집 (DebugView 확인용)", color = BlueGray5, fontSize = 14.sp)
            androidx.compose.material3.Switch(
                checked = analytics,
                onCheckedChange = {
                    analytics = it
                    ServerSelection.setAnalyticsEnabled(context, it)
                },
                modifier = Modifier.testTag("analytics_toggle")
            )
        }
        if (onCancel != null) {
            PickerButton("취소", "server_cancel", onClick = onCancel)
        }
    }
}

@Composable
private fun PickerButton(text: String, tag: String, enabled: Boolean = true, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = Primary,
            contentColor = BlueGray95,
            disabledContainerColor = BlueGray80,
            disabledContentColor = BlueGray40
        ),
        modifier = Modifier.fillMaxWidth().height(52.dp).testTag(tag)
    ) { Text(text) }
}

@Composable
private fun pickerFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = BlueGray5,
    unfocusedTextColor = BlueGray5,
    focusedBorderColor = Primary,
    unfocusedBorderColor = BlueGray40,
    focusedLabelColor = Primary,
    unfocusedLabelColor = BlueGray40
)
