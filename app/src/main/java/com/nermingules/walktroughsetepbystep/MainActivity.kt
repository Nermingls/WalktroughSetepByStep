package com.nermingules.walktroughsetepbystep

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nermingules.walktroughsetepbystep.ui.theme.HalkBankBlue
import com.nermingules.walktroughsetepbystep.ui.theme.RoofLightGray
import com.nermingules.walktroughsetepbystep.ui.theme.RoofOrange
import com.nermingules.walktroughsetepbystep.ui.theme.WalktroughBlue
import com.nermingules.walktroughsetepbystep.ui.theme.WalktroughSetepByStepTheme
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val BrusBlue = Brush.horizontalGradient(
    colors = listOf(HalkBankBlue, Color(0xFF4CC9FE))
)
private val BrusOrange = Brush.horizontalGradient(
    colors = listOf(RoofOrange, Color(0xFFF1BA88))
)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WalktroughSetepByStepTheme {
                WalktroughSetepByStepApp()
            }
        }
    }
}


@Composable
fun WalktroughSetepByStepApp() {
    val walkthroughSteps = rememberWalkthroughStepsFromAssets(
        fileName = "walkthrough.json",
        fallbackSteps = listOf(
            WalkthroughStep(id = "step_profile", title = "Profil", description = "Buradan profilinize erişebilirsiniz ve hesap bilgilerinizi görüntüleyebilirsiniz", targetKey = "profile"),
            WalkthroughStep(id = "step_search", title = "Search", description = "Buradan profilinize erişebilirsiniz ve hesap bilgilerinizi görüntüleyebilirsiniz", targetKey = "search"),
            WalkthroughStep(id = "step_balance", title = "Bakiye", description = "Buradan profilinize erişebilirsiniz ve hesap bilgilerinizi görüntüleyebilirsiniz", targetKey = "balance"),
            WalkthroughStep(id = "step_transfer", title = "Para Transferi", description = "Para transferi işlemlerinizi buradan yapabilirsiniz. Hızlı ve güvenli transfer seçenekleri.", targetKey = "transfer"),
            WalkthroughStep(id = "step_bottom", title = "Bottom", description = "BottomBar ", targetKey = "bottomCard"),
            WalkthroughStep(id = "step_quick_actions", title = "Hızlı İşlemler", description = "En sık kullandığınız işlemlere buradan erişebilirsiniz. Zamandan tasarruf edin!", targetKey = "quick_actions", buttonText = "Başlayalım!"),
        )
    )

    val walkthroughState = rememberWalkthroughState(
        steps = walkthroughSteps,
        autoStart = false
    )

    val customConfig = WalkthroughConfig(
        overlayColor = WalktroughBlue.copy(alpha = 0.8f),
        nextButtonText = "İleri",
        previousButtonText = "Geri",
        finishButtonText = "Anladım!",
        stepCounterFormat = "({current}/{total})",
    )
    val context = LocalContext.current
    var screenWidth by remember { mutableStateOf(0f) }
    var screenHeight by remember { mutableStateOf(0f) }
    var bottomNavBounds by remember { mutableStateOf<Rect?>(null) }
    var bottomNavFABBounds by remember { mutableStateOf<Rect?>(null) }

    walkthroughState.start()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned { coordinates ->
                screenWidth = coordinates.size.width.toFloat()
                screenHeight = coordinates.size.height.toFloat()
            }
    ) {
        Scaffold(
            containerColor = RoofLightGray,
            bottomBar = {
                AndroidView(
                    factory = { context ->
                        LayoutInflater.from(context).inflate(R.layout.bottom_navigation, null).apply {
                            val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
                            val fab = findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fab)

                            this.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                                override fun onGlobalLayout() {
                                    val bottomNavLocation = IntArray(2)
                                    bottomNav.getLocationInWindow(bottomNavLocation)

                                    bottomNavBounds = Rect(
                                        bottomNavLocation[0].toFloat(),
                                        bottomNavLocation[1].toFloat(),
                                        (bottomNavLocation[0] + bottomNav.width).toFloat(),
                                        (bottomNavLocation[1] + bottomNav.height).toFloat()
                                    )

                                    val fabLocation = IntArray(2)
                                    fab.getLocationInWindow(fabLocation)

                                    bottomNavFABBounds = Rect(
                                        fabLocation[0].toFloat(),
                                        fabLocation[1].toFloat(),
                                        (fabLocation[0] + fab.width).toFloat(),
                                        (fabLocation[1] + fab.height).toFloat()
                                    )

                                    this@apply.viewTreeObserver.removeOnGlobalLayoutListener(this)
                                }
                            })

                            bottomNav.setOnItemSelectedListener { item ->
                                when (item.itemId) {
                                    R.id.navigation_home -> true
                                    R.id.navigation_account -> true
                                    R.id.navigation_application -> true
                                    R.id.navigation_assets -> true
                                    else -> false
                                }
                            }

                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                val density = LocalDensity.current
                val configuration = LocalConfiguration.current
                LaunchedEffect(screenWidth, screenHeight, bottomNavBounds, bottomNavFABBounds) {
                    if (screenWidth > 0 && screenHeight > 0) {
                        walkthroughState.clearTargetPositions("bottomCard")
                        val topOffset = with(density) { 30.dp.toPx() }

                        bottomNavBounds?.let { bounds ->
                            walkthroughState.addTargetPosition(
                                key = "bottomCard",
                                x = bounds.left,
                                y = bounds.top - topOffset,
                                width = bounds.width,
                                height = bounds.height + topOffset,
                                shape = WalkthroughShape.RoundedRect
                            )
                        }

                        bottomNavFABBounds?.let { fabBounds ->
                            walkthroughState.addTargetPosition(
                                key = "bottomCard",
                                position = CustomTargetPosition.BOTTOM_CENTER,
                                width = fabBounds.width,
                                height = fabBounds.height,
                                screenWidth = screenWidth,
                                screenHeight = screenHeight - with(density) { 15.dp.toPx() },
                                shape = WalkthroughShape.Circle
                            )
                        }
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                TopAppBar(
                    walkthroughState = walkthroughState,
                )
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 140.dp),
                ) {
                    item {
                        BankHomeScreen(
                            walkthroughState = walkthroughState,
                        )
                    }
                }
            }
            val scope = rememberCoroutineScope()

            WalkthroughOverlay(
                walkthroughState = walkthroughState,
                config = customConfig,
                onStepChange = { step ->
                    println("Adım değişti: ${step.title}")
                },
                onFinish = {
                    scope.launch {
                        walkthroughCompletedJob(context)
                    }
                    println("Walkthrough tamamlandı!")
                }
            )
        }
    }
}

@Composable
fun BankHomeScreen(walkthroughState: WalkthroughState) {
    Column {
        BalanceCard(walkthroughState = walkthroughState)
        QuickActionsCard(walkthroughState = walkthroughState)
        TransactionHistoryCard(walkthroughState = walkthroughState)
        PromotionsSection()
    }
}

@Composable
fun BalanceCard(walkthroughState: WalkthroughState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp),
        shape = RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        WalkthroughTarget(
            key = "balance",
            walkthroughState = walkthroughState,
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AccountCircle, contentDescription = null, tint = RoofOrange, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Vadesiz TL Hesabı", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
                    }
                    OutlinedButton(
                        onClick = { },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = RoofOrange),
                        border = BorderStroke(1.dp, RoofOrange),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("Tümü", fontSize = 12.sp)
                    }
                }
                Column(modifier = Modifier.padding(horizontal = 2.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "752 - 01075579", color = Color.Gray, fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(painter = painterResource(R.drawable.ic_share), contentDescription = "Share IBAN", modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = "Kullanılabilir Bakiye", color = Color.Gray, fontSize = 12.sp)
                    Text(text = "200,00 TL", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.Black.copy(alpha = 0.8f))
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(text = "Kullanılabilir Kredili Bakiye", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(end = 6.dp))
                        Text(text = "200,00 TL", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Black)
                    }
                }
            }
        }
    }
}

@Composable
fun QuickActionsCard(walkthroughState: WalkthroughState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 16.dp, start = 16.dp, bottom = 6.dp),
        shape = RoundedCornerShape(bottomStart = 6.dp, bottomEnd = 6.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        WalkthroughTarget(
            key = "quick_actions",
            walkthroughState = walkthroughState
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ActionButton(icon = painterResource(R.drawable.ic_transfer), title = "Para\nTransferi", backgroundBrush = BrusBlue)
                ActionButton(icon = painterResource(R.drawable.ic_atm), title = "ATM'den\nPara Çek", backgroundBrush = BrusBlue)
                ActionButton(icon = painterResource(R.drawable.ic_all_options), title = "Tüm\nİşlemler", backgroundBrush = BrusOrange)
            }
        }
    }
}

@Composable
fun TransactionHistoryCard(walkthroughState: WalkthroughState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        shape = RoundedCornerShape(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        WalkthroughTarget(
            key = "transfer",
            walkthroughState = walkthroughState
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(brush = BrusOrange, shape = RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("HESAP HAREKETLERİ", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("Tümünü Gör >", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(bottomStart = 6.dp, bottomEnd = 6.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.height(48.dp).aspectRatio(1f).border(1.dp, Color.LightGray, RoundedCornerShape(4.dp)),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(text = "MAY", fontSize = 9.sp, color = Color.Gray, lineHeight = 9.sp, modifier = Modifier.padding(0.dp))
                            Text(text = "13", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black.copy(0.7f), lineHeight = 12.sp, modifier = Modifier.padding(0.dp))
                            Text(text = "2025", fontSize = 9.sp, color = Color.Gray, lineHeight = 9.sp, modifier = Modifier.padding(0.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Para Transferi", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.Black.copy(alpha = 0.7f))
                            Text(text = "NERMİN GÜLEŞ 'DAN GELEN FAST O...", fontSize = 9.sp, color = Color.Gray, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "200,00 TL", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black.copy(alpha = 0.7f))
                            Icon(painter = painterResource(id = R.drawable.ic_receipt), contentDescription = "Dekont", tint = Color(0xFF2196F3), modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PromotionsSection() {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        PromotionCard(
            title = "Diğer Bankalarım",
            subtitle = "Diğer banka hesaplarınızı kolayca yönetin.",
            gradient = Brush.horizontalGradient(colors = listOf(Color(0xFF0072B3), Color(0xFF8E24AA))),
            imageId = R.drawable.bank_item
        )
        Spacer(modifier = Modifier.height(16.dp))
        PromotionCard(
            title = "Size Özel Kampanyalar",
            subtitle = "",
            gradient = Brush.horizontalGradient(colors = listOf(HalkBankBlue, Color(0xFF00ACC1))),
            imageId = R.drawable.ic_megaphone
        )
    }
}

suspend fun walkthroughCompletedJob(context: android.content.Context) {
    try {
        delay(1000)
        Toast.makeText(
            context,
            "🎉 Tebrikler! Uygulama tanıtımı tamamlandı!",
            Toast.LENGTH_LONG
        ).show()

    } catch (e: Exception) {
        Toast.makeText(
            context,
            "Kurulum tamamlandı ancak bazı ayarlar eksik kalabilir.",
            Toast.LENGTH_SHORT
        ).show()
    }
}
