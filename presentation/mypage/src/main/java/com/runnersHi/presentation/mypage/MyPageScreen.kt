package com.runnersHi.presentation.mypage

import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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

@Composable
fun MyPageRoute(
    versionName: String,
    onNavigateToRegion: () -> Unit,
    onNavigateToTerms: () -> Unit,
    onNavigateToAccount: () -> Unit,
    onNavigateToNotices: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MyPageViewModel = hiltViewModel()
) {
    val state by viewModel.collectState()
    val context = LocalContext.current

    viewModel.collectEffect { effect ->
        when (effect) {
            MyPageContract.Effect.NavigateToRegion -> onNavigateToRegion()
            MyPageContract.Effect.NavigateToTerms -> onNavigateToTerms()
            MyPageContract.Effect.NavigateToAccount -> onNavigateToAccount()
            MyPageContract.Effect.NavigateToNotices -> onNavigateToNotices()
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
                .height(56.dp)
                .padding(start = 24.dp, end = 24.dp, top = 12.dp)
                .wrapContentHeight(Alignment.CenterVertically)
        )
        Spacer(modifier = Modifier.height(20.dp))
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            ProfileAvatar(imageUrl = state.profileImageUrl, size = 80.dp, iconSize = 48.dp)
            Text(text = state.nickname, color = Color.White, fontSize = 20.sp, lineHeight = 24.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))
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
                onClick = { onEvent(MyPageContract.Event.NotReadyClicked) }
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
                title = "버전정보",
                trailing = versionName
            )
            SettingRow(
                icon = CommonR.drawable.ic_comment_mono,
                title = "개발자에게",
                onClick = { onEvent(MyPageContract.Event.NotReadyClicked) }
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
        Spacer(modifier = Modifier.height(24.dp))
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
            lineHeight = 20.sp,
            modifier = Modifier.padding(start = 20.dp, top = 20.dp, bottom = 7.dp)
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
            lineHeight = 24.sp,
            modifier = Modifier.weight(1f)
        )
        if (trailing != null) {
            Text(text = trailing, color = BlueGray40, fontSize = 14.sp)
        }
        if (onClick != null) {
            Icon(
                painter = painterResource(id = CommonR.drawable.ic_arrow_right_w),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
