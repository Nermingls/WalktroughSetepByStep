package com.nermingules.walktroughsetepbystep

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nermingules.walktroughsetepbystep.ui.theme.HalkBankBlue
import com.nermingules.walktroughsetepbystep.ui.theme.RoofDarkGrayBlue
import com.nermingules.walktroughsetepbystep.ui.theme.RoofOrange


@Composable
fun BottomNavigation(
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp) // Daha yüksek elevation
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomNavItem(painterResource(R.drawable.ic_home), "Ana Sayfa", true)
                BottomNavItem(painterResource(R.drawable.ic_wallet), "Hesap/Kart", false)
                Spacer(modifier = Modifier.width(70.dp))
                BottomNavItem(painterResource(R.drawable.ic_plus_circle), "Başvuru", false)
                BottomNavItem(painterResource(R.drawable.ic_balace), "Varlıklar", false)
            }
        }
        Box(
            modifier = Modifier
                .size(70.dp)
                .align(Alignment.TopCenter)
                .offset(y = (-25).dp)
                .shadow(elevation = 16.dp, shape = CircleShape)
                .clip(CircleShape)
                .background(RoofOrange)
                .border(4.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    painter = painterResource(R.drawable.ic_options),
                    contentDescription = "İşlemler",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "İşlemler",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun TabItem(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.8f),
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
        if (isSelected) {
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(2.dp)
                    .background(Color.White)
                    .padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun ActionButton(
    icon: Painter,
    title: String,
    backgroundBrush: Brush? = null,
    backgroundColor: Color? = null
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .then(
                    if (backgroundBrush != null) {
                        Modifier.background(backgroundBrush)
                    } else {
                        Modifier.background(backgroundColor ?: Color.Gray)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            fontSize = 10.sp,
            color = Color.Black,
            textAlign = TextAlign.Center,
            lineHeight = 12.sp
        )
    }
}

@Composable
fun PromotionCard(
    title: String,
    subtitle: String,
    gradient: Brush,
    imageId: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(75.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Image(
                    painter = painterResource(id = imageId),
                    contentDescription = "Image",
                    modifier = Modifier
                        .height(50.dp)
                        .aspectRatio(1f)
                        .padding(end = 8.dp),
                    contentScale = ContentScale.Crop
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (subtitle.isNotEmpty()) {
                        Text(
                            text = subtitle,
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 12.sp
                        )
                    }
                }
                Icon(
                    Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun BottomNavItem(icon: Painter, label: String, isSelected: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            tint = if (isSelected) Color(0xFF2196F3) else Color.Gray,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = if (isSelected) Color(0xFF2196F3) else Color.Gray
        )
    }
}

@Composable
fun TopAppBar(
    walkthroughState: WalkthroughState,
    onStartWalkthrough: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        HalkBankBlue,
                        Color(0xFF4CC9FE)
                    )
                )
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    top = 32.dp,
                    end = 16.dp,
                    bottom = 16.dp,
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                WalkthroughTarget(
                    key = "profile",
                    walkthroughState = walkthroughState
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.user_profile),
                            contentDescription = "Profile Image",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .border(2.dp, Color.White, CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .align(Alignment.BottomEnd)
                                .clip(CircleShape)
                                .background(Color.White)
                                .border(
                                    1.dp,
                                    Color.Gray.copy(alpha = 0.2f),
                                    CircleShape
                                )
                                .clickable { },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = HalkBankBlue,
                                modifier = Modifier.size(24.dp * 0.6f)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                if (!walkthroughState.isVisible) {
                    Button(
                        onClick = onStartWalkthrough,
                        modifier = Modifier
                            .height(60.dp)
                            .aspectRatio(1.5f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White.copy(alpha = 0.1f),
                            contentColor = RoofDarkGrayBlue
                        )
                    ) {
                        Text("Başlat")
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Search, contentDescription = null, tint = Color.White)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TabItem("Hesaplarım", selectedTab == 0) { selectedTab = 0 }
            TabItem("Kartlarım", selectedTab == 1) { selectedTab = 1 }
            TabItem("Yatırımlarım", selectedTab == 2) { selectedTab = 2 }
        }

    }
}
