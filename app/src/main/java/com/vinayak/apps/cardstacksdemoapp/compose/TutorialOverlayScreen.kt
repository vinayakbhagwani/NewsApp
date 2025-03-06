package com.vinayak.apps.cardstacksdemoapp.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TutorialOverlayScreen(onDismiss: () -> Unit) {
    var step by remember { mutableStateOf(0) }
    val transition = updateTransition(targetState = step, label = "Tutorial Steps")

    val alpha by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 500) },
        label = "Alpha Animation"
    ) { if (it > 0) 1f else 0.8f }

    val offset by transition.animateDp(
        transitionSpec = { spring() },
        label = "Offset Animation"
    ) { if (it > 0) 0.dp else 60.dp }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
            .clickable { onDismiss() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(visible = step == 0) {
                Row {
                    Text(
                        text = "Swipe Left / Right to \nsee News Articles",
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        color = Color.White,
                        modifier = Modifier
                            .alpha(alpha)
                    )
                }
            }

            AnimatedVisibility(visible = step == 1) {
                Text(
                    text = "All set!!\nStart Exploring.\n",
                    fontSize = 20.sp,
                    color = Color.White,
                    modifier = Modifier
                        .offset(y = offset)
                        .alpha(alpha)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { if (step < 1) step++ else onDismiss() },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text(text = if (step < 1) "Next" else "Got it", color = Color.Black)
            }
        }
    }
}