package com.vinayak.apps.cardstacksdemoapp

import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.amplitude.core.Amplitude
import com.google.android.gms.location.FusedLocationProviderClient
import com.vinayak.apps.cardstacksdemoapp.compose.SwipeableCard
import com.vinayak.apps.cardstacksdemoapp.compose.TutorialOverlayScreen
import com.vinayak.apps.cardstacksdemoapp.models.NewsArticle
import com.vinayak.apps.cardstacksdemoapp.ui.theme.CardStacksDemoAppTheme
import com.vinayak.apps.cardstacksdemoapp.utils.FontUtils
import com.vinayak.apps.cardstacksdemoapp.utils.LocationUtils.areLocationPermissionsAlreadyGranted
import com.vinayak.apps.cardstacksdemoapp.utils.LocationUtils.decideCurrentPermissionStatus
import com.vinayak.apps.cardstacksdemoapp.utils.LocationUtils.openApplicationSettings
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var fusedLocatiionProviderClient: FusedLocationProviderClient

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

                var locationPermissionsGranted by remember { mutableStateOf(areLocationPermissionsAlreadyGranted(
                    context = baseContext)) }
                var shouldShowPermissionRationale by remember {
                    mutableStateOf(
                        shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_COARSE_LOCATION)
                    )
                }

                var shouldDirectUserToApplicationSettings by remember {
                    mutableStateOf(false)
                }

                var currentPermissionsStatus by remember {
                    mutableStateOf(decideCurrentPermissionStatus(locationPermissionsGranted, shouldShowPermissionRationale))
                }

                val locationPermissions = arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                )

                val locationPermissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestMultiplePermissions(),
                    onResult = { permissions ->
                        locationPermissionsGranted = permissions.values.reduce { acc, isPermissionGranted ->
                            acc && isPermissionGranted
                        }

                        if (!locationPermissionsGranted) {
                            shouldShowPermissionRationale =
                                shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        }
                        shouldDirectUserToApplicationSettings = !shouldShowPermissionRationale && !locationPermissionsGranted
                        currentPermissionsStatus = decideCurrentPermissionStatus(locationPermissionsGranted, shouldShowPermissionRationale)
                    })

                val lifecycleOwner = LocalLifecycleOwner.current
                DisposableEffect(key1 = lifecycleOwner, effect = {
                    val observer = LifecycleEventObserver { _, event ->
                        if (event == Lifecycle.Event.ON_START &&
                            !locationPermissionsGranted &&
                            !shouldShowPermissionRationale) {
                            locationPermissionLauncher.launch(locationPermissions)
                        }
                    }
                    lifecycleOwner.lifecycle.addObserver(observer)
                    onDispose {
                        lifecycleOwner.lifecycle.removeObserver(observer)
                    }
                }
                )

                if(locationPermissionsGranted) {
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        fusedLocatiionProviderClient.lastLocation
                            .addOnSuccessListener { location ->
                                viewModel.getCountryInfo(context = baseContext,location)
                            }
                    }
                }

                LaunchedEffect(Unit) {
                    viewModel.fetchNews()
                }

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
                            shouldShowPermissionRationale = shouldShowPermissionRationale,
                            shouldDirectUserToApplicationSettings = shouldDirectUserToApplicationSettings,
                            onBackClick = {

                            },
                            addMoreNews = {
                                sharedPreferences.edit().apply {
                                    putInt("startIndex", sharedPreferences.getInt("endIndex", 0))
                                    apply()
                                    putInt("endIndex", sharedPreferences.getInt("startIndex", 0)+6)
                                    apply()
                                }
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
                            },
                            onLaunchingPermissionLauncher = {
                                locationPermissionLauncher.launch(locationPermissions)
                            },
                            onSnackbarDismiss = {
                                shouldShowPermissionRationale = false
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainFunction(
    listNewsArticle: List<NewsArticle>,
    shouldShowPermissionRationale: Boolean,
    shouldDirectUserToApplicationSettings: Boolean,
    onBackClick: () -> Unit ={},
    addMoreNews: () -> List<NewsArticle> = {  -> emptyList() },
    onSnackbarDismiss: () -> Unit ={},
    onLaunchingPermissionLauncher: () -> Unit ={}
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            containerColor = Color.White,
            topBar = {
                CenterAlignedTopAppBar(
                    modifier = Modifier.height(94.dp),
                    title = {
                        Text(
                            text = "Snip.itt",
                            fontSize = 29.sp,
                            fontFamily = FontUtils.fontFamily,
                            modifier = Modifier.padding(bottom = 10.dp, top = 5.dp),
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Italic
                        )
                    },
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

                if (shouldShowPermissionRationale) {
                    LaunchedEffect(Unit) {
                        scope.launch {
                            val userAction = snackbarHostState.showSnackbar(
                                message ="Please approve location permiss ions to show you news from your region",
                                actionLabel = "Approve",
                                duration = SnackbarDuration.Indefinite,
                                withDismissAction = true
                            )
                            when (userAction) {
                                SnackbarResult.ActionPerformed -> {
                                    onSnackbarDismiss()
                                    onLaunchingPermissionLauncher()
                                }
                                SnackbarResult.Dismissed -> {
                                    onSnackbarDismiss()
                                }
                            }
                        }
                    }
                }
                if (shouldDirectUserToApplicationSettings) {
                    openApplicationSettings(packageName = LocalContext.current.packageName,LocalContext.current)
                }

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

@Composable
fun SwipeableCardStack(
    listNewsArticle: List<NewsArticle>,
    addMoreNews: () -> List<NewsArticle> = {  -> emptyList() }
) {
    val newsList = remember { mutableStateListOf(*listNewsArticle.toTypedArray()) }

    LaunchedEffect(newsList.size) {
        if(newsList.isEmpty()) {
            if(addMoreNews().isNotEmpty()) newsList.addAll(addMoreNews())
        }
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