package com.vinayak.apps.cardstacksdemoapp.compose

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.vinayak.apps.cardstacksdemoapp.ExpandableText
import com.vinayak.apps.cardstacksdemoapp.R
import kotlinx.coroutines.launch

@SuppressLint("UnrememberedMutableState")
@Composable
fun SwipeableCard(
    cardText: String,
    cardImage: String,
    cardDescription: String,
    cardNewsUrl: String,
    cardIndex: Int,
    cardOffset: Dp,
    cardScale: Float,
    onSwiped: () -> Unit
) {
    Log.d("CARD_DET","card details: ${cardText} || ${cardImage}")
    val offsetX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    var isImageLoading by remember {
        mutableStateOf(false)
    }

    // 3D rotation factor: maps offsetX to -15° to 15° rotation
    var rotationYs = derivedStateOf { (offsetX.value / 10).coerceIn(-15f, 15f) }.value

    val gradients = listOf(
        listOf(Color(0x60B7E3EE), Color(0x6D230396)), // Purple to Blue
        listOf(Color(0x63EEB7B7), Color(0x60960303)), // Orange to Red
        listOf(Color(0x63B7EECB), Color(0x5E5E9603)), // Cyan to Blue
        listOf(Color(0x63B7CBEE), Color(0x79032396)), // Green Gradient
        listOf(Color(0x63EEC4B7), Color(0x79963E03)), // Purple to Blue
        listOf(Color(0x63B7EEE6), Color(0x8D03968A)), // Orange to Red
        listOf(Color(0x63D3B7EE), Color(0x772F0396)), // Cyan to Blue
        listOf(Color(0x63EEB7DE), Color(0x74960387))
    )

    val cardGradient = gradients[cardIndex % gradients.size]

    Box(
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .fillMaxHeight(0.95f)
            .padding(16.dp)
            .offset(y = cardOffset) // Stack effect
            .graphicsLayer {
                translationX = offsetX.value
                scaleX = cardScale
                scaleY = cardScale
                rotationY = rotationYs // 3D rotation effect
                cameraDistance = 12 * density // Enhance 3D perspective
            }
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        when {
                            offsetX.value > 300f -> { // Swipe Right
                                scope.launch {
                                    offsetX.animateTo(1000f, tween(300))
                                    onSwiped()
                                }
                            }

                            offsetX.value < -300f -> { // Swipe Left
                                scope.launch {
                                    offsetX.animateTo(-1000f, tween(300))
                                    onSwiped()
                                }
                            }

                            else -> { // Reset if not enough swipe
                                scope.launch { offsetX.animateTo(0f, tween(300)) }
                            }
                        }
                    },
                    onHorizontalDrag = { _, dragAmount ->
                        scope.launch { offsetX.snapTo(offsetX.value + dragAmount) }
                    }
                )
            }
            .shadow(10.dp, RoundedCornerShape(16.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = cardGradient,
                    start = Offset(0f, 0f),
                    end = Offset(800f, 1000f)
                ),
                shape = RoundedCornerShape(16.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        ShimmerEffect(modifier = if(isImageLoading) Modifier.fillMaxSize() else Modifier.size(0.dp))
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(if(cardImage.isNullOrBlank()) R.drawable.defaultimg else cardImage)
                .crossfade(true)
                .build(),
            onLoading = {
                isImageLoading = true
            },
            onSuccess = {
                isImageLoading = false
            },
            onError = {
                isImageLoading = false
            },
            contentDescription = stringResource(R.string.app_name),
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
//        }

        Column(
            Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = cardGradient,
                        start = Offset(0f, 0f),
                        end = Offset(700f, 700f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ),
            verticalArrangement = Arrangement.Bottom
        ) {
            Row {
                Text(
                    modifier = Modifier.padding(horizontal = 15.dp, vertical = 0.dp),
                    text = cardText,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            ExpandableText(
                modifier = Modifier
                    .padding(horizontal = 15.dp, vertical = 9.dp),
                style = TextStyle(
                    fontStyle = FontStyle.Italic,
                    fontSize = 17.sp
                ),
                text = cardDescription,
                newsUrl = cardNewsUrl
            )
        }
    }
}