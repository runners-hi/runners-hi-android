package com.runnersHi.presentation.mypage

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.runnersHi.presentation.common.R as CommonR
import com.runnersHi.presentation.common.component.ProfileAvatar
import com.runnersHi.presentation.common.mvi.collectEffect
import com.runnersHi.presentation.common.mvi.collectState
import com.runnersHi.presentation.common.theme.BlueGray20
import com.runnersHi.presentation.common.theme.BlueGray40
import com.runnersHi.presentation.common.theme.BlueGray5
import com.runnersHi.presentation.common.theme.BlueGray70
import com.runnersHi.presentation.common.theme.BlueGray80
import com.runnersHi.presentation.common.theme.Primary
import kotlinx.coroutines.launch

@Composable
fun MyPageRoute(
    versionName: String,
    onNavigateToRegion: () -> Unit,
    onNavigateToTerms: () -> Unit,
    onNavigateToAccount: () -> Unit,
    onNavigateToNotices: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToFeedback: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MyPageViewModel = hiltViewModel()
) {
    val state by viewModel.collectState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val photoPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        scope.launch {
            val bytes = ProfileImageEncoder.encode(context, uri)
            viewModel.sendEvent(
                if (bytes != null) MyPageContract.Event.PhotoEncoded(bytes)
                else MyPageContract.Event.PhotoEncodeFailed
            )
        }
    }

    viewModel.collectEffect { effect ->
        when (effect) {
            MyPageContract.Effect.NavigateToRegion -> onNavigateToRegion()
            MyPageContract.Effect.NavigateToTerms -> onNavigateToTerms()
            MyPageContract.Effect.NavigateToAccount -> onNavigateToAccount()
            MyPageContract.Effect.NavigateToNotices -> onNavigateToNotices()
            MyPageContract.Effect.NavigateToNotifications -> onNavigateToNotifications()
            MyPageContract.Effect.NavigateToFeedback -> onNavigateToFeedback()
            MyPageContract.Effect.OpenPhotoPicker -> photoPicker.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
            is MyPageContract.Effect.ShowToast ->
                Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
        }
    }

    MyPageScreen(
        state = state,
        versionName = versionName,
        onEvent = viewModel::sendEvent,
        modifier = modifier
    )
}

@Composable
fun MyPageScreen(
    state: MyPageContract.State,
    versionName: String,
    onEvent: (MyPageContract.Event) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "마이페이지",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, top = 14.dp)
        )
        Spacer(modifier = Modifier.height(42.dp))
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            EditableAvatar(state = state, onEvent = onEvent)
            Text(text = state.nickname, color = Color.White, fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.height(37.dp))
        SettingCard(title = "설정", radius = 24.dp) {
            SettingRow(
                icon = CommonR.drawable.ic_location_main,
                iconTint = Primary,
                title = "내 지역 설정",
                onClick = { onEvent(MyPageContract.Event.RegionClicked) }
            )
            SettingRow(
                icon = CommonR.drawable.ic_notification_main,
                iconTint = Primary,
                title = "알림",
                onClick = { onEvent(MyPageContract.Event.NotificationsClicked) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        SettingCard(title = "고객지원", radius = 20.dp) {
            SettingRow(
                icon = CommonR.drawable.ic_volume_mono,
                title = "공지사항",
                onClick = { onEvent(MyPageContract.Event.NoticesClicked) }
            )
            SettingRow(
                icon = CommonR.drawable.ic_info_mono,
                title = "버전정보"
            )
            SettingRow(
                icon = CommonR.drawable.ic_comment_mono,
                title = "개발자에게",
                onClick = { onEvent(MyPageContract.Event.FeedbackClicked) }
            )
            SettingRow(
                icon = CommonR.drawable.ic_info2_mono,
                title = "이용약관",
                onClick = { onEvent(MyPageContract.Event.TermsClicked) }
            )
            SettingRow(
                icon = CommonR.drawable.ic_user_mono,
                title = "계정 관리",
                onClick = { onEvent(MyPageContract.Event.AccountClicked) }
            )
        }
        // 아이폰 하단 여백 120에서 탭 바에 가려지는 48을 뺀 값
        Spacer(modifier = Modifier.height(72.dp))
    }
}

@Composable
private fun EditableAvatar(
    state: MyPageContract.State,
    onEvent: (MyPageContract.Event) -> Unit
) {
    Box(contentAlignment = Alignment.Center) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .clip(CircleShape)
                .semantics(mergeDescendants = true) { contentDescription = "프로필 사진 변경" }
                .clickable { onEvent(MyPageContract.Event.AvatarClicked) }
        ) {
            ProfileAvatar(imageUrl = state.profileImageUrl, size = 80.dp, iconSize = 40.dp)
            if (state.isUpdatingPhoto) {
                CircularProgressIndicator(color = Primary, modifier = Modifier.size(32.dp))
            }
        }
        DropdownMenu(
            expanded = state.showPhotoMenu,
            onDismissRequest = { onEvent(MyPageContract.Event.PhotoMenuDismissed) },
            containerColor = BlueGray80
        ) {
            DropdownMenuItem(
                text = { Text(text = "사진 변경", color = BlueGray5) },
                onClick = { onEvent(MyPageContract.Event.ChangePhotoClicked) }
            )
            DropdownMenuItem(
                text = { Text(text = "사진 삭제", color = BlueGray5) },
                onClick = { onEvent(MyPageContract.Event.RemovePhotoClicked) }
            )
        }
    }
}

@Composable
private fun SettingCard(
    title: String,
    radius: Dp,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(radius)
    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .clip(shape)
            .background(BlueGray80)
            .border(1.dp, BlueGray70, shape)
            .padding(bottom = 20.dp)
    ) {
        Text(
            text = title,
            color = BlueGray20,
            fontSize = 16.sp,
            modifier = Modifier.padding(start = 20.dp, top = 20.dp, bottom = 8.dp)
        )
        content()
    }
}

@Composable
private fun SettingRow(
    @DrawableRes icon: Int,
    title: String,
    iconTint: Color = BlueGray40,
    trailing: String? = null,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Figma: 줄 전체 높이 48, 화살표 앞 간격 8
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = title,
            color = BlueGray5,
            fontSize = 16.sp,
            maxLines = 1,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        if (trailing != null) {
            Text(text = trailing, color = BlueGray40, fontSize = 14.sp)
        }
        Icon(
            painter = painterResource(id = CommonR.drawable.ic_arrow_right_w_fit),
            contentDescription = null,
            tint = BlueGray5,
            modifier = Modifier.size(18.dp)
        )
    }
}
