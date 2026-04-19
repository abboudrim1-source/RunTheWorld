package com.runtheworld.ui.theme

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.*

// ─── Brand colours ────────────────────────────────────────────────────────────

val NeonOrange  = Color(0xFFFF5733)
val NeonPink    = Color(0xFFFF1F8E)
val ElectricBlue = Color(0xFF00D4FF)
val DeepNavy    = Color(0xFF050510)
val DarkSurface = Color(0xFF0D0D2B)

// ─── Brushes ──────────────────────────────────────────────────────────────────

val BrandGradient: Brush get() = Brush.linearGradient(
    colors = listOf(NeonOrange, NeonPink)
)

val BrandGradientVertical: Brush get() = Brush.verticalGradient(
    colors = listOf(NeonOrange, NeonPink)
)

val BackgroundGradient: Brush get() = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF050510),
        Color(0xFF0A0A20),
        Color(0xFF0D0D2B)
    )
)

val GlassBrush: Brush get() = SolidColor(Color(0x22FFFFFF))

val GlassBorderBrush: Brush get() = Brush.linearGradient(
    colors = listOf(Color(0x55FFFFFF), Color(0x0AFFFFFF))
)

// ─── Modifier extensions ──────────────────────────────────────────────────────

fun Modifier.glassSurface(
    shape: Shape = RoundedCornerShape(20.dp)
): Modifier = this
    .clip(shape)
    .background(Color(0x22FFFFFF))
    .border(width = 1.dp, brush = GlassBorderBrush, shape = shape)

fun Modifier.mapGlassSurface(
    shape: Shape = RoundedCornerShape(20.dp)
): Modifier = this
    .clip(shape)
    .background(Color(0xCC0D0D2B))
    .border(width = 1.dp, brush = GlassBorderBrush, shape = shape)

fun Modifier.brandGlow(
    shape: Shape = RoundedCornerShape(14.dp),
    elevation: Dp = 20.dp
): Modifier = shadow(
    elevation = elevation,
    shape = shape,
    ambientColor = NeonOrange.copy(alpha = 0.6f),
    spotColor = NeonPink.copy(alpha = 0.6f)
)

fun Modifier.colorGlow(
    color: Color,
    shape: Shape = RoundedCornerShape(50.dp),
    elevation: Dp = 16.dp
): Modifier = shadow(
    elevation = elevation,
    shape = shape,
    ambientColor = color.copy(alpha = 0.7f),
    spotColor = color.copy(alpha = 0.7f)
)

// ─── Full-screen background with decorative radial glows ─────────────────────

@Composable
fun AppBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(modifier = modifier.fillMaxSize().background(BackgroundGradient)) {
        // Decorative ambient glows
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Orange nebula — top-left
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(NeonOrange.copy(alpha = 0.18f), Color.Transparent),
                    center = Offset(size.width * 0.05f, size.height * 0.12f),
                    radius = size.width * 0.55f
                )
            )
            // Pink nebula — bottom-right
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(NeonPink.copy(alpha = 0.12f), Color.Transparent),
                    center = Offset(size.width * 0.95f, size.height * 0.85f),
                    radius = size.width * 0.5f
                )
            )
            // Blue accent — mid-right
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(ElectricBlue.copy(alpha = 0.08f), Color.Transparent),
                    center = Offset(size.width * 0.9f, size.height * 0.3f),
                    radius = size.width * 0.35f
                )
            )
        }
        content()
    }
}

// ─── Glass card ───────────────────────────────────────────────────────────────

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .glassSurface(shape)
            .padding(24.dp),
        content = content
    )
}

// ─── Gradient button ─────────────────────────────────────────────────────────

@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .height(54.dp)
            .then(if (enabled) Modifier.brandGlow() else Modifier)
            .clip(RoundedCornerShape(14.dp))
            .background(if (enabled) BrandGradient else SolidColor(Color(0x33FFFFFF)))
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            leadingIcon?.invoke()
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = 0.5.sp
            )
        }
    }
}

// ─── Glass outline button ─────────────────────────────────────────────────────

@Composable
fun GlassButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .height(54.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0x22FFFFFF))
            .border(1.dp, GlassBorderBrush, RoundedCornerShape(14.dp))
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            leadingIcon?.invoke()
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}

// ─── Gradient text ────────────────────────────────────────────────────────────

@Composable
fun GradientText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.headlineLarge,
    fontWeight: FontWeight = FontWeight.ExtraBold,
    gradient: Brush = BrandGradient
) {
    Text(
        text = text,
        modifier = modifier,
        style = style.copy(brush = gradient),
        fontWeight = fontWeight
    )
}

// ─── Section label ────────────────────────────────────────────────────────────

@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = Color.White.copy(alpha = 0.55f),
        letterSpacing = 1.sp,
        modifier = modifier
    )
}

// ─── 3D animated globe (Canvas — works on all platforms, no model file needed) ─

@Composable
fun AnimatedGlobe3D(
    modifier: Modifier = Modifier,
    primaryColor: Color = NeonOrange,
    accentColor: Color = NeonPink
) {
    val inf = rememberInfiniteTransition(label = "globe")
    // Longitude rotation
    val rotDeg by inf.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(10_000, easing = LinearEasing)),
        label = "globe_rot"
    )
    // Subtle vertical bob
    val bob by inf.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            tween(3_000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "globe_bob"
    )

    Canvas(modifier = modifier.aspectRatio(1f)) {
        val cx = size.width / 2f
        val cy = size.height / 2f + bob.dp.toPx()
        val r  = size.minDimension / 2f * 0.88f
        val rotRad = rotDeg * (PI / 180.0).toFloat()
        val lw = 1.6.dp.toPx()

        // ── Base sphere: radial gradient simulating a light source top-left ──
        drawCircle(
            brush = Brush.radialGradient(
                0.00f to primaryColor.copy(alpha = 0.95f),
                0.45f to primaryColor.copy(alpha = 0.70f),
                0.85f to primaryColor.copy(alpha = 0.30f),
                1.00f to primaryColor.copy(alpha = 0.05f),
                center = Offset(cx - r * 0.28f, cy - r * 0.28f),
                radius = r * 1.3f
            ),
            radius = r,
            center = Offset(cx, cy)
        )

        // ── Longitude lines (12 meridians × 30°, rotating) ──────────────────
        for (i in 0 until 12) {
            val theta = i * (PI / 6.0).toFloat()
            val angle = theta + rotRad
            val cosA  = cos(angle)
            val ellipseW = abs(cosA) * r * 2f
            if (ellipseW < 2f) continue
            drawArc(
                color = primaryColor.copy(alpha = if (cosA > 0f) 0.38f else 0.10f),
                startAngle  = -90f,
                sweepAngle  = 360f,
                useCenter   = false,
                topLeft     = Offset(cx - ellipseW / 2f, cy - r),
                size        = Size(ellipseW, r * 2f),
                style       = Stroke(width = lw)
            )
        }

        // ── Latitude lines (±60°, ±30°, 0° equator) ─────────────────────────
        for (latDeg in listOf(-60, -30, 0, 30, 60)) {
            val phi  = latDeg * (PI / 180.0).toFloat()
            val latY = cy + sin(phi) * r
            val latR = cos(phi) * r
            val latH = latR * 0.20f   // foreshortening for orthographic view
            drawArc(
                color = if (latDeg == 0) accentColor.copy(alpha = 0.55f)
                        else primaryColor.copy(alpha = 0.22f),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter  = false,
                topLeft    = Offset(cx - latR, latY - latH),
                size       = Size(latR * 2f, latH * 2f),
                style      = Stroke(width = if (latDeg == 0) lw * 1.8f else lw)
            )
        }

        // ── Specular highlight (top-left glint) ───────────────────────────────
        drawCircle(
            brush = Brush.radialGradient(
                0.0f to Color.White.copy(alpha = 0.60f),
                1.0f to Color.Transparent,
                center = Offset(cx - r * 0.32f, cy - r * 0.32f),
                radius = r * 0.38f
            ),
            radius = r,
            center = Offset(cx, cy)
        )

        // ── Rim shadow (gives depth / 3D edge) ────────────────────────────────
        drawCircle(
            brush = Brush.radialGradient(
                0.00f to Color.Transparent,
                0.68f to Color.Transparent,
                1.00f to Color.Black.copy(alpha = 0.55f),
                center = Offset(cx, cy),
                radius = r
            ),
            radius = r,
            center = Offset(cx, cy)
        )
    }
}

// ─── expect / actual hook for SceneView (Android) 3D model rendering ─────────
// Drop a GLB model at app/src/androidMain/assets/models/globe.glb and the
// Android actual will render it with Filament PBR. iOS/other falls back to
// the Canvas globe above.

@Composable
expect fun SceneViewGlobe(modifier: Modifier = Modifier)
