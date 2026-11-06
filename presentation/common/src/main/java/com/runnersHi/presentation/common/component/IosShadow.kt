package com.runnersHi.presentation.common.component

import android.graphics.BlurMaskFilter
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 아이폰 SwiftUI `.shadow(color:radius:x:y:)` 와 같은 그림자.
 * 아이폰 화면에서 잰 번짐(가우스 표준편차)은 radius 값과 같다.
 * 안드로이드 흐림 반경은 표준편차 = 0.57735 × 반경 + 0.5 이므로 거꾸로 바꿔 쓴다.
 */
fun Density.iosShadowBlurPx(radius: Dp): Float =
    ((radius.toPx() - 0.5f) / 0.57735f).coerceAtLeast(0.1f)

fun Modifier.iosShadow(
    shape: Shape,
    color: Color,
    radius: Dp,
    x: Dp = 0.dp,
    y: Dp = 0.dp
): Modifier = drawBehind {
    val outline = shape.createOutline(size, layoutDirection, this)
    val path = Path().apply { addOutline(outline) }.asAndroidPath()
    path.offset(x.toPx(), y.toPx())
    val paint = Paint().asFrameworkPaint().apply {
        isAntiAlias = true
        this.color = color.toArgb()
        maskFilter = BlurMaskFilter(iosShadowBlurPx(radius), BlurMaskFilter.Blur.NORMAL)
    }
    drawIntoCanvas { it.nativeCanvas.drawPath(path, paint) }
}

/**
 * 그림(비트맵) 모양을 따라가는 아이폰 그림자. SwiftUI `.shadow`는 그림의 투명도 모양대로 그림자를 그린다.
 * 원·둥근 사각형 대신 그림 자체의 알파를 흐려서 그린다.
 */
@Composable
fun Modifier.iosImageShadow(
    @DrawableRes id: Int,
    color: Color,
    radius: Dp,
    x: Dp = 0.dp,
    y: Dp = 0.dp
): Modifier {
    val bitmap = ImageBitmap.imageResource(id).asAndroidBitmap()
    val density = LocalDensity.current
    return drawWithCache {
        val sx = size.width / bitmap.width
        val sy = size.height / bitmap.height
        val blur = with(density) { iosShadowBlurPx(radius) } / sx
        val offset = IntArray(2)
        val alpha = bitmap.extractAlpha(
            android.graphics.Paint().apply { maskFilter = BlurMaskFilter(blur.coerceAtLeast(0.1f), BlurMaskFilter.Blur.NORMAL) },
            offset
        )
        val paint = android.graphics.Paint().apply { isAntiAlias = true; this.color = color.toArgb() }
        onDrawBehind {
            drawIntoCanvas {
                val c = it.nativeCanvas
                c.save()
                c.translate(x.toPx(), y.toPx())
                c.scale(sx, sy)
                c.drawBitmap(alpha, offset[0].toFloat(), offset[1].toFloat(), paint)
                c.restore()
            }
        }
    }
}
