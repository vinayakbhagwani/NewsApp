package com.vinayak.apps.cardstacksdemoapp

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.window.OnBackInvokedDispatcher
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.vinayak.apps.cardstacksdemoapp.data.NewsRepository
import com.vinayak.apps.cardstacksdemoapp.models.NewsArticle
import com.vinayak.apps.cardstacksdemoapp.ui.theme.CardStacksDemoAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

//    var isBackPressed: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CardStacksDemoAppTheme {

                val viewModel: NewsListViewmodel = hiltViewModel()

                val response = viewModel.responseFlow.collectAsState().value

                var showLoader by remember {
                    mutableStateOf(true)
                }

                var tutorialVisible by remember {
                    mutableStateOf(
                        sharedPreferences.getBoolean("isTutorialVisible", true)
                    )
                }

                var res = remember(response) {
                    response
                }

                LaunchedEffect(Unit) {
                    viewModel.fetchNews()
                }

//                var startIndex = 0
//                var endIndex = 6

                if(showLoader) {
                    Column(
                        Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                    }
                }

                if(res.isNotEmpty()) {
                    showLoader = false
                    Log.d("Response_here:","response: ${res}")
                    if(res.isEmpty()) {
                        Column(
                            Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Sorry Folks!! \nNo articles available today",
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        Log.d("Sp------a","start index: ${sharedPreferences.getInt("startIndex", 0)}")
                        Log.d("Sp------a","end index: ${sharedPreferences.getInt("endIndex", 6)}")
                        if(res.size <= sharedPreferences.getInt("endIndex", 6)) {
                            sharedPreferences.edit().apply {
                                putInt("startIndex", 0)
                                apply()
                                putInt("endIndex", 6)
                                apply()
                            }
                        }

                        val listNewsArticle = res.toMutableList().subList(
                            sharedPreferences.getInt("startIndex", 0),
                            sharedPreferences.getInt("endIndex", 6)
                        )


                        MainFunction(
                            listNewsArticle = listNewsArticle,
                            onBackClick = {

                            },
                            addMoreNews = {
                                sharedPreferences.edit().apply {
                                    putInt("startIndex", sharedPreferences.getInt("endIndex", 0))
                                    apply()
                                    putInt("endIndex", sharedPreferences.getInt("startIndex", 0)+6)
                                    apply()
                                }
//                                startIndex = endIndex
//                                endIndex = startIndex+6
                                Log.d("Response size: ","response size: ${res.size}")
                                Log.d("Response size: ","end index: ${sharedPreferences.getInt("endIndex", 6)}")
                                if(res.size >= sharedPreferences.getInt("endIndex", 6)) {
                                    Log.d("Sp------b","start index: ${sharedPreferences.getInt("startIndex", 0)}")
                                    Log.d("Sp------b","end index: ${sharedPreferences.getInt("endIndex", 6)}")
                                    res.toMutableList().subList(
                                        sharedPreferences.getInt("startIndex", 0),
                                        sharedPreferences.getInt("endIndex", 6)
                                    )
                                } else {
                                    val url = ""
                                    listOf(
                                        NewsArticle(
                                            title = "That's all, the\n\nNews for today,\n\nFolks .................☺\uFE0F",
                                            image = url,
                                            description = "",
                                            newsUrl = ""
                                        )
                                    )
                                }
                            }
                        )

                        if(tutorialVisible) {
                            TutorialOverlayScreen(
                                onDismiss = {
                                    tutorialVisible = false
                                    sharedPreferences.edit().apply {
                                        putBoolean("isTutorialVisible", false)
                                        apply()
                                    }
                                }
                            )
                        }

                    }
                }

            }
        }
    }

//    override fun onBackPressed() {
//        super.onBackPressed()
//        isBackPressed = true
//    }

    override fun onDestroy() {
        super.onDestroy()
//        if (!isBackPressed) {
//            sharedPreferences.edit().apply {
//                remove("startIndex")
//                remove("endIndex")
//                apply()
//            }
//        }
    }
}

const val DEFAULT_MINIMUM_TEXT_LINE = 3

@Composable
fun ExpandableText(
    modifier: Modifier = Modifier,
    textModifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    fontStyle: FontStyle? = null,
    text: String,
    newsUrl: String,
    collapsedMaxLine: Int = DEFAULT_MINIMUM_TEXT_LINE,
    showMoreText: String = "... Show More",
    showMoreStyle: SpanStyle = SpanStyle(fontWeight = FontWeight.W500),
    showLessText: String = "... Read More",
    showLessStyle: SpanStyle = SpanStyle(fontWeight = FontWeight.W500, color = Color(0xFF56DAFC)),
    textAlign: TextAlign? = null
) {
    var isExpanded by remember { mutableStateOf(false) }
    var clickable by remember { mutableStateOf(false) }
    var lastCharIndex by remember { mutableStateOf(0) }
    val localUriHandler = LocalUriHandler.current

    Box(modifier = Modifier
        .clickable(clickable) {
//            isExpanded = !isExpanded
            if (!isExpanded) {
                isExpanded = true
            } else {
                localUriHandler.openUri(newsUrl)
            }
        }
        .then(modifier)
    ) {
        Text(
            modifier = textModifier
                .fillMaxWidth()
                .animateContentSize(),
            text = buildAnnotatedString {
                if (clickable) {
                    if (isExpanded) {
                        append(text)
                        withStyle(style = showLessStyle) { append(showLessText) }
                    } else {
                        val adjustText = text.substring(startIndex = 0, endIndex = lastCharIndex)
                            .dropLast(showMoreText.length)
                            .dropLastWhile { Character.isWhitespace(it) || it == '.' }
                        append(adjustText)
                        withStyle(style = showMoreStyle) { append(showMoreText) }
                    }
                } else {
                    append(text)
                }
            },
            color = Color.White,
            maxLines = if (isExpanded) Int.MAX_VALUE else collapsedMaxLine,
            fontStyle = fontStyle,
            onTextLayout = { textLayoutResult ->
                if (!isExpanded && textLayoutResult.hasVisualOverflow) {
                    clickable = true
                    lastCharIndex = textLayoutResult.getLineEnd(collapsedMaxLine - 1)
                }
            },
            style = style,
            textAlign = textAlign
        )
    }

}

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
//                            .offset(y = offset)
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


//private fun openUrlInChrome(url: String) {
//
//    val packageManager =
//    val parkingUUID = UUID.randomUUID()
//    val chromeIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
//    chromeIntent.putExtra(Intent.EXTRA_REFERRER, Uri.parse("android-app://com.android.chrome"))
//
//
//
//    // Check if Chrome is installed
//    if (isPackageInstalled("com.android.chrome", packageManager)) {
//        chromeIntent.setPackage("com.android.chrome")
//    }
//
//    // If Chrome is installed, open the URL in Chrome
//    try {
//        startActivity(chromeIntent)
//    } catch (e: Exception) {
//        // If Chrome is not installed, open the URL in the default browser
//        Toast.makeText(
//            this,
//            "Chrome not installed, opening in default browser",
//            Toast.LENGTH_SHORT
//        ).show()
//        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
//    }
//}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainFunction(
    listNewsArticle: List<NewsArticle>,
    onBackClick: () -> Unit ={},
    addMoreNews: () -> List<NewsArticle> = {  -> emptyList() }
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Scaffold(
            containerColor = Color.White,
            topBar = {
                CenterAlignedTopAppBar(
                    modifier = Modifier.height(94.dp),
                    title = { Text(text = "Daily Digest", modifier = Modifier.padding(top=22.dp)) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Black, // Set the background color
                        titleContentColor = Color.White // Set the title color
                    ),
//                    navigationIcon = {
//                        IconButton(onClick = { onBackClick() }, modifier = Modifier.padding(top=16.dp)) {
//                            Icon(
//                                imageVector = Icons.Default.ArrowBack,
//                                contentDescription = "Back",
//                                tint = Color.White // Set the arrow color
//                            )
//                        }
//                    },
                )
            },
            content = { paddingValues ->
                Box(modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)){
                    Column(modifier = Modifier
                        .padding(top = 15.dp)
                        .fillMaxWidth()
                        .fillMaxHeight(1f), horizontalAlignment = Alignment.Start){
                        SwipeableCardStack(
                            listNewsArticle = listNewsArticle,
                            addMoreNews = {
                                addMoreNews()
                            })
                    }
                }
            }
        )
    }
}


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
            ShimmerEffect(modifier = if(isImageLoading)Modifier.fillMaxSize() else Modifier.size(0.dp))
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
//                VerticalDivider(
//                    modifier = Modifier
//                        .height(50.dp)
//                        .width(5.dp)
//                        .padding(horizontal = 15.dp, vertical = 35.dp),
//                    color = Color.White
//                )
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

@Composable
fun ShimmerEffect(
    modifier: Modifier,
    widthOfShadowBrush: Int = 500,
    angleOfAxisY: Float = 270f,
    durationMillis: Int = 1000,
) {


    val shimmerColors = listOf(
        Color.White.copy(alpha = 0.3f),
        Color.White.copy(alpha = 0.5f),
        Color.White.copy(alpha = 1.0f),
        Color.White.copy(alpha = 0.5f),
        Color.White.copy(alpha = 0.3f),
    )

    val transition = rememberInfiniteTransition(label = "")

    val translateAnimation = transition.animateFloat(
        initialValue = 0f,
        targetValue = (durationMillis + widthOfShadowBrush).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = durationMillis,
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Restart,
        ),
        label = "Shimmer loading animation",
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(x = translateAnimation.value - widthOfShadowBrush, y = 0.0f),
        end = Offset(x = translateAnimation.value, y = angleOfAxisY),
    )

    Box(
        modifier = modifier
    ) {
        Spacer(
            modifier = Modifier
                .matchParentSize()
                .background(brush)
        )
    }
}

@Composable
fun SwipeableCardStack(
    listNewsArticle: List<NewsArticle>,
    addMoreNews: () -> List<NewsArticle> = {  -> emptyList() }
) {
    val newsList = remember { mutableStateListOf(*listNewsArticle.toTypedArray()) }

    LaunchedEffect(newsList.size) {
        if(newsList.isEmpty()) {
            Log.d("NewsList","NewsList size: ${newsList.size}")
            if(addMoreNews().size>0) newsList.addAll(addMoreNews())
        }
//        if (newsList.isEmpty()) {
//            newsList.addAll(listNewsArticle) // Reset cards when all are swiped
//        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        newsList.forEachIndexed { index, news ->
            val cardOffset = 10.dp // Offset each card slightly
            val cardScale = 1f // Decrease scale for background cards


            SwipeableCard(
                cardText = news.title,
                cardImage = news.image,
                cardDescription = news.description,
                cardNewsUrl = news.newsUrl,
                cardIndex = index,
                cardOffset = cardOffset,
                cardScale = cardScale,
                onSwiped = {
                    newsList.remove(news)
                }
            )
        }
    }
}