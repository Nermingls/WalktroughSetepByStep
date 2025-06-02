package com.nermingules.walktroughsetepbystep

import android.os.Bundle
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
            WalkthroughStep(
                id = "step_profile",
                title = "Profil",
                description = "Buradan profilinize erişebilirsiniz ve hesap bilgilerinizi görüntüleyebilirsiniz",
                targetKey = "profile"
            ),
            WalkthroughStep(
                id = "step_search",
                title = "Search",
                description = "Buradan profilinize erişebilirsiniz ve hesap bilgilerinizi görüntüleyebilirsiniz",
                targetKey = "search"
            ),
            WalkthroughStep(
                id = "step_balance",
                title = "Bakiye",
                description = "Buradan profilinize erişebilirsiniz ve hesap bilgilerinizi görüntüleyebilirsiniz",
                targetKey = "balance"
            ),
            WalkthroughStep(
                id = "step_transfer",
                title = "Para Transferi",
                description = "Para transferi işlemlerinizi buradan yapabilirsiniz. Hızlı ve güvenli transfer seçenekleri.",
                targetKey = "transfer"
            ),
            WalkthroughStep(
                id = "step_quick_actions",
                title = "Hızlı İşlemler",
                description = "En sık kullandığınız işlemlere buradan erişebilirsiniz. Zamandan tasarruf edin!",
                targetKey = "quick_actions",
                buttonText = "Başlayalım!"
            ),
        )
    )

    val walkthroughState = rememberWalkthroughState(
        steps = walkthroughSteps,
        autoStart = false
    )

    val customConfig = WalkthroughConfig(
        overlayColor = WalktroughBlue.copy(alpha = 0.8f),
        highlightPadding = 20f,
        nextButtonText = "İleri",
        previousButtonText = "Geri",
        finishButtonText = "Anladım!",
        stepCounterFormat = "({current}/{total})"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = RoofLightGray,
            bottomBar = {
                BottomNavigation(
                    modifier = Modifier.fillMaxWidth()
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            ) {

                item {
                    TopAppBar(
                        walkthroughState = walkthroughState,
                        onStartWalkthrough = {
                            walkthroughState.start()
                        }
                    )
                }
                item {
                    BankHomeScreen(
                        walkthroughState = walkthroughState,
                        onStartWalkthrough = {
                            walkthroughState.start()
                        },
                        paddingValues = paddingValues
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            WalkthroughOverlay(
                walkthroughState = walkthroughState,
                config = customConfig,
                onStepChange = { step ->
                    println("Adım değişti: ${step.title}")
                },
                onFinish = {
                    println("Walkthrough tamamlandı!")
                }
            )
        }
    }
}

@Composable
fun BankHomeScreen(
    walkthroughState: WalkthroughState,
    onStartWalkthrough: () -> Unit,
    paddingValues: PaddingValues
) {
    var brusBlue = Brush.horizontalGradient(
        colors = listOf(
            HalkBankBlue,
            Color(0xFF4CC9FE)
        )
    )
    var brusOrange = Brush.horizontalGradient(
        colors = listOf(RoofOrange, Color(0xFFF1BA88))
    )
    Column(
        modifier = Modifier
            .padding(top = 16.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            WalkthroughTarget(
                key = "balance",
                walkthroughState = walkthroughState,
                modifier = Modifier,
                content = {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.AccountCircle,
                                    contentDescription = null,
                                    tint = RoofOrange,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Vadesiz TL Hesabı",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Black
                                )
                            }

                            Row {
                                Spacer(modifier = Modifier.width(12.dp))
                                OutlinedButton(
                                    onClick = { },
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = RoofOrange
                                    ),
                                    border = BorderStroke(
                                        1.dp, RoofOrange
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Tümü", fontSize = 14.sp)
                                }
                            }
                        }

                        Text(
                            text = "752 - 01075579",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Kullanılabilir Bakiye",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "200,00 TL",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(
                                text = "Kullanılabilir Kredili Bakiye",
                                color = Color.Gray,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(
                                text = "200,00 TL",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black
                            )
                        }
                    }
                }
            )
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            WalkthroughTarget(
                key = "quick_actions",
                walkthroughState = walkthroughState,
                modifier = Modifier,
                content = {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ActionButton(
                            icon = painterResource(R.drawable.ic_transfer),
                            title = "Para\nTransferi",
                            backgroundBrush = brusBlue
                        )

                        ActionButton(
                            icon = painterResource(R.drawable.ic_atm),
                            title = "ATM'den\nPara Çek",
                            backgroundBrush = brusBlue
                        )
                        ActionButton(
                            icon = painterResource(R.drawable.ic_all_options),
                            title = ("Tüm\nİşlemler"),
                            backgroundBrush = brusOrange
                        )
                    }
                }

            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            WalkthroughTarget(
                key = "transfer",
                walkthroughState = walkthroughState,
                modifier = Modifier,
                content = {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    brush = brusOrange,
                                    shape = RoundedCornerShape(
                                        topStart = 8.dp,
                                        topEnd = 8.dp
                                    )
                                )
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "HESAP HAREKETLERİ",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "Tümünü Gör >",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Card(
                            modifier = Modifier
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier
                                        .height(70.dp)
                                        .aspectRatio(1f)
                                        .border(
                                            1.dp,
                                            Color.LightGray,
                                            RoundedCornerShape(6.dp)
                                        ),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("MAY", fontSize = 10.sp, color = Color.Gray)
                                    Text(
                                        "13",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black.copy(0.7f)
                                    )
                                    Text("2025", fontSize = 10.sp, color = Color.Gray)
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "Para Transferi",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.Black.copy(alpha = 0.7f)
                                    )
                                    Text(
                                        text = "NERMİN GÜLEŞ 'DAN GELEN FAST O...",
                                        fontSize = 10.sp,
                                        color = Color.Gray,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Column(
                                    horizontalAlignment = Alignment.End
                                ) {
                                    Text(
                                        text = "200,00 TL",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black.copy(alpha = 0.7f)
                                    )
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_receipt),
                                        contentDescription = null,
                                        tint = Color(0xFF2196F3),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }

                }

            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            PromotionCard(
                title = "Diğer Bankalarım",
                subtitle = "Diğer banka hesaplarınızı kolayca yönetin.",
                gradient = Brush.horizontalGradient(
                    colors = listOf(HalkBankBlue, Color(0xFF8E24AA))
                ),
                imageId = R.drawable.user_profile
            )

            Spacer(modifier = Modifier.height(8.dp))

            PromotionCard(
                title = "Size Özel Kampanyalar",
                subtitle = "",
                gradient = Brush.horizontalGradient(
                    colors = listOf(HalkBankBlue, Color(0xFF00ACC1))
                ),
                imageId = R.drawable.user_profile
            )
        }
    }
}

