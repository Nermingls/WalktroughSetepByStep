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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
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
    modifier: Modifier = Modifier,
    onStartWalkthrough: () -> Unit,
    walkthroughState: WalkthroughState,
) {
    var bottomNavBounds by remember { mutableStateOf<Rect?>(null) }
    var bottomNavFABBounds by remember { mutableStateOf<Rect?>(null) }

    val updateTargetPositions = remember {
        {
            if (bottomNavBounds != null && bottomNavFABBounds != null) {
                walkthroughState.clearTargetPositions("bottomCard")

                bottomNavBounds?.let { bounds ->
                    walkthroughState.addTargetPosition(
                        "bottomCard",
                        TargetPosition(
                            offset = bounds.topLeft,
                            size = bounds.size,
                            shape = WalkthroughShape.RoundedRect
                        )
                    )
                }

                bottomNavFABBounds?.let { fabBounds ->
                    walkthroughState.addTargetPosition(
                        "bottomCard",
                        TargetPosition(
                            offset = fabBounds.topLeft,
                            size = fabBounds.size,
                            shape = WalkthroughShape.Circle
                        )
                    )
                }
            }
        }
    }

    LaunchedEffect(bottomNavBounds, bottomNavFABBounds) {
        updateTargetPositions()
    }

    Box(
        modifier = modifier
            .onGloballyPositioned { coordinates ->
                bottomNavBounds = Rect(
                    offset = coordinates.positionInRoot(),
                    size = Size(
                        width = coordinates.size.width.toFloat(),
                        height = coordinates.size.height.toFloat()
                    )
                )
            }
    ) {
        BottomNavigationCard()

        FloatingActionButton(
            modifier = Modifier.align(Alignment.TopCenter),
            onStartWalkthrough = onStartWalkthrough,
            onPositioned = { bounds ->
                bottomNavFABBounds = bounds
            }
        )
    }
}

@Composable
private fun BottomNavigationCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(painterResource(R.drawable.ic_home), "Ana Sayfa", true)
            BottomNavItem(painterResource(R.drawable.ic_receipt), "Hesap/Kart", false)
            Spacer(modifier = Modifier.width(70.dp))
            BottomNavItem(painterResource(R.drawable.ic_plus_circle), "Başvuru", false)
            BottomNavItem(painterResource(R.drawable.ic_balace), "Varlıklar", false)
        }
    }
}

@Composable
private fun FloatingActionButton(
    modifier: Modifier = Modifier,
    onStartWalkthrough: () -> Unit,
    onPositioned: (Rect) -> Unit
) {
    Box(
        modifier = modifier
            .size(70.dp)
            .offset(y = (-25).dp)
            .shadow(elevation = 16.dp, shape = CircleShape)
            .clip(CircleShape)
            .background(RoofOrange)
            .border(4.dp, Color.White, CircleShape)
            .onGloballyPositioned { coordinates ->
                onPositioned(
                    Rect(
                        offset = coordinates.positionInRoot(),
                        size = Size(
                            coordinates.size.width.toFloat(),
                            coordinates.size.height.toFloat()
                        )
                    )
                )
            }
            .clickable { onStartWalkthrough() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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

@Composable
fun TabItem(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.8f),
            fontSize = 16.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
        if (isSelected) {
            Box(
                modifier = Modifier
                    .width(90.dp)
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
    Row(
        verticalAlignment = Alignment.CenterVertically
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
                modifier = Modifier
                    .size(24.dp)
                    .padding(4.dp)
            )
        }

        Spacer(modifier = Modifier.width(6.dp))

        Text(
            text = title,
            fontSize = 12.sp,
            color = Color.Black,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 14.sp
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
            .height(60.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .padding(horizontal = 12.dp) // biraz azaltıldı
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
                        .height(40.dp)
                        .aspectRatio(1f)
                        .padding(end = 6.dp),
                    contentScale = ContentScale.Crop
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    if (subtitle.isNotEmpty()) {
                        Text(
                            text = subtitle,
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 10.sp
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
                    top = 24.dp,
                    bottom = 16.dp,
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                WalkthroughTarget(
                    key = "profile",
                    walkthroughState = walkthroughState,
                    shape = WalkthroughShape.Circle,
                    modifier = Modifier,
                    content = {
                        Box(
                            modifier = Modifier
                                .size(70.dp)
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
                                    .onGloballyPositioned { coordinates ->
                                        val position = coordinates.positionInRoot()
                                        val size = coordinates.size

                                        walkthroughState.addTargetPosition(
                                            "profile",
                                            TargetPosition(
                                                offset = position,
                                                size = Size(size.width.toFloat(), size.height.toFloat()),
                                                shape = WalkthroughShape.Circle
                                            )
                                        )
                                    }
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
                )

                Spacer(modifier = Modifier.width(8.dp))
                if (!walkthroughState.isVisible) {
                }
            }
            WalkthroughTarget(
                key = "search",
                walkthroughState = walkthroughState,
                modifier = Modifier.padding(horizontal = 16.dp),
                shape = WalkthroughShape.Circle,
                content = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Search, contentDescription = null, tint = Color.White, modifier = Modifier. padding(8.dp))
                    }
                })
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    bottom = 40.dp,
                ),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TabItem("Hesaplarım", selectedTab == 0) { selectedTab = 0 }
            TabItem("Kartlarım", selectedTab == 1) { selectedTab = 1 }
            TabItem("Yatırımlarım", selectedTab == 2) { selectedTab = 2 }
        }
    }
}