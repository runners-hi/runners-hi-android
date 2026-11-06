package com.runnersHi.presentation.common.component

import android.content.Context
import android.graphics.fonts.Font
import android.graphics.fonts.FontStyle
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle as ComposeFontStyle
import androidx.compose.ui.text.font.FontSynthesis
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.Typeface
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.unit.sp
import com.runnersHi.presentation.common.R
import com.runnersHi.presentation.common.theme.Pretendard
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.abs
import android.graphics.Typeface as AndroidTypeface

// 아이폰은 한글을 Apple SD Gothic Neo, 영문·숫자·기호·공백을 SF로 그린다. 한글 폭은 Pretendard와 같다("가" 16pt 13.84 / 13.83).
// SF 영문·기호는 글자 크기마다 tracking이 달라 Pretendard와 폭이 다르다. 예) 16pt 공백 4.19 / 4.02, "(" 5.80 / 5.45.
// 그래서 크기별 영문 전용 글꼴 rh_latin_*(Pretendard 모양·커닝 + SF 글자 폭)을 앞에 두고, 나머지 글자는 Pretendard로 넘긴다.
// 실측(CoreText): "7.4 km" 20pt SF 60.15 / 이 글꼴 60.14, "Progress to next level" 12pt 121.81 / 121.92.
// 만드는 법: scripts/sf-latin-fonts/gen_latin.py (macOS CoreText로 SF 폭을 재서 글자 폭만 바꾸고 모양을 새 폭 가운데로 옮김). 안드로이드 10(API 29) 미만은 Pretendard만 쓴다.
private val SF_SIZES = intArrayOf(12, 13, 14, 16, 18, 20, 24, 40)
private val LATIN: Map<Int, IntArray> = mapOf(
        400 to intArrayOf(R.font.rh_latin_regular_12, R.font.rh_latin_regular_13, R.font.rh_latin_regular_14, R.font.rh_latin_regular_16, R.font.rh_latin_regular_18, R.font.rh_latin_regular_20, R.font.rh_latin_regular_24, R.font.rh_latin_regular_40),
        500 to intArrayOf(R.font.rh_latin_medium_12, R.font.rh_latin_medium_13, R.font.rh_latin_medium_14, R.font.rh_latin_medium_16, R.font.rh_latin_medium_18, R.font.rh_latin_medium_20, R.font.rh_latin_medium_24, R.font.rh_latin_medium_40),
        600 to intArrayOf(R.font.rh_latin_semibold_12, R.font.rh_latin_semibold_13, R.font.rh_latin_semibold_14, R.font.rh_latin_semibold_16, R.font.rh_latin_semibold_18, R.font.rh_latin_semibold_20, R.font.rh_latin_semibold_24, R.font.rh_latin_semibold_40),
        700 to intArrayOf(R.font.rh_latin_bold_12, R.font.rh_latin_bold_13, R.font.rh_latin_bold_14, R.font.rh_latin_bold_16, R.font.rh_latin_bold_18, R.font.rh_latin_bold_20, R.font.rh_latin_bold_24, R.font.rh_latin_bold_40),
)
private val FULL = mapOf(400 to R.font.pretendard_regular, 500 to R.font.pretendard_medium, 600 to R.font.pretendard_semibold, 700 to R.font.pretendard_bold)
private val cache = ConcurrentHashMap<Long, FontFamily>()

private fun bucket(w: FontWeight) = when {
    w.weight >= 700 -> 700
    w.weight >= 600 -> 600
    w.weight >= 500 -> 500
    else -> 400
}

/** 아이폰 SF 글자 폭을 따르는 글꼴. 안드로이드 10 미만이면 null. */
fun sfFontFamily(context: Context, fontSize: TextUnit, weight: FontWeight): FontFamily? {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q || !fontSize.isSpecified) return null
    val i = SF_SIZES.indices.minBy { abs(SF_SIZES[it] - fontSize.value) }
    val w = bucket(weight)
    return cache.getOrPut(w * 100L + i) { FontFamily(Typeface(build(context.applicationContext, LATIN.getValue(w)[i], FULL.getValue(w), w))) }
}

@RequiresApi(Build.VERSION_CODES.Q)
private fun build(context: Context, latin: Int, full: Int, weight: Int): AndroidTypeface {
    val style = FontStyle(weight, FontStyle.FONT_SLANT_UPRIGHT)
    fun family(res: Int) = android.graphics.fonts.FontFamily.Builder(Font.Builder(context.resources, res).setWeight(weight).build()).build()
    return AndroidTypeface.CustomFallbackBuilder(family(latin)).addCustomFallback(family(full)).setStyle(style).build()
}

/** material3 Text와 같다. 영문·숫자·기호 폭만 아이폰 SF에 맞춘다. */
@Composable
fun Text(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: ComposeFontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null,
    style: TextStyle = LocalTextStyle.current
) = Text(
    AnnotatedString(text), modifier, color, fontSize, fontStyle, fontWeight, fontFamily, letterSpacing, textDecoration,
    textAlign, lineHeight, overflow, softWrap, maxLines, minLines, emptyMap(), onTextLayout ?: {}, style
)

@Composable
fun Text(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: ComposeFontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    inlineContent: Map<String, InlineTextContent> = emptyMap(),
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current
) {
    val context = LocalContext.current
    val size = if (fontSize.isSpecified) fontSize else if (style.fontSize.isSpecified) style.fontSize else 16.sp
    val weight = fontWeight ?: style.fontWeight ?: FontWeight.Normal
    val usesPretendard = fontFamily == null && (style.fontFamily == null || style.fontFamily == Pretendard)
    val family = if (usesPretendard) sfFontFamily(context, size, weight) else null
    if (family == null) {
        androidx.compose.material3.Text(
            text, modifier, color, fontSize, fontStyle, fontWeight, fontFamily, letterSpacing, textDecoration,
            textAlign, lineHeight, overflow, softWrap, maxLines, minLines, inlineContent, onTextLayout, style
        )
        return
    }
    // 글자 크기·굵기를 바꾼 구간은 그 크기·굵기의 글꼴로 바꾼다
    val spans = text.spanStyles.mapNotNull { r ->
        val s = r.item
        if (!s.fontSize.isSpecified && s.fontWeight == null || s.fontFamily != null) return@mapNotNull null
        val spanSize = when {
            !s.fontSize.isSpecified -> size
            s.fontSize.isSp -> s.fontSize
            else -> (size.value * s.fontSize.value).sp
        }
        val f = sfFontFamily(context, spanSize, s.fontWeight ?: weight) ?: return@mapNotNull null
        AnnotatedString.Range(SpanStyle(fontFamily = f), r.start, r.end)
    }
    val styled = if (spans.isEmpty()) text else AnnotatedString(text.text, text.spanStyles + spans, text.paragraphStyles)
    androidx.compose.material3.Text(
        styled, modifier, color, fontSize, fontStyle, fontWeight, family, letterSpacing, textDecoration,
        textAlign, lineHeight, overflow, softWrap, maxLines, minLines, inlineContent, onTextLayout,
        // 커닝은 켠다(SF도 커닝한다). 가짜 굵게는 끈다(글꼴이 이미 그 굵기다).
        style.copy(fontFeatureSettings = "'kern' 1", fontSynthesis = FontSynthesis.None)
    )
}
