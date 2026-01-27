package com.chessmaster.game.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chessmaster.game.data.GameMode
import com.chessmaster.game.data.Player
import com.chessmaster.game.theme.AppThemeColors
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import chessgame.composeapp.generated.resources.Res
import chessgame.composeapp.generated.resources.reset

@Composable
fun ModernTopBar(
    appTheme: AppThemeColors,
    whitePlayer: Player?,
    blackPlayer: Player?,
    gameMode: GameMode,
    onSaveClick: () -> Unit,
    onFlipBoard: () -> Unit,
    onOpenDrawer: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF262421), // Always black like chess.com
        elevation = 0.dp
    ) {
        Column {
            // System bar spacer for notch/status bar
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Menu button
                IconButton(
                    onClick = onOpenDrawer,
                    modifier = Modifier.size(40.dp)
                ) {
                    Text("☰", fontSize = 28.sp, color = Color.White)
                }
                
                // App Title with mode indicator
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Chess Master",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    if (gameMode == GameMode.DEATH_MATCH) {
                        Text(
                            "⚔️ Death Match",
                            fontSize = 11.sp,
                            color = Color(0xFF81B64C),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Flip board button
                    IconButton(
                        onClick = onFlipBoard,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Text("🔄", fontSize = 20.sp)
                    }
                    
                    // Save button
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable(onClick = onSaveClick),
                        color = Color(0xFF81B64C),
                        elevation = 0.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("Save", fontSize = 14.sp, color = Color.White, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
            
            // Player info bar (only for death match)
            if (gameMode == GameMode.DEATH_MATCH && whitePlayer != null && blackPlayer != null) {
                ModernPlayerInfoBar(appTheme, whitePlayer, blackPlayer)
            }
        }
    }
}

@Composable
fun rememberMenuIcon() = painterResource(Res.drawable.reset)

@Composable
fun NavigationDrawer(
    appTheme: AppThemeColors,
    gameMode: GameMode,
    onNavigateToGame: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onToggleGameMode: () -> Unit,
    onResetStats: () -> Unit,
    onClose: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxHeight()
            .width(280.dp),
        color = appTheme.surfaceColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Drawer Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp)
            ) {
                Text(
                    "♟️ Chess Master",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = appTheme.textColor
                )
                Text(
                    "Play & Improve",
                    fontSize = 14.sp,
                    color = appTheme.textSecondaryColor
                )
            }
            
            Divider(color = appTheme.textSecondaryColor.copy(alpha = 0.3f))
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Navigation items
            DrawerItem(
                icon = "🎮",
                text = "Game",
                appTheme = appTheme,
                onClick = {
                    onNavigateToGame()
                    onClose()
                }
            )
            
            DrawerItem(
                icon = "📜",
                text = "History",
                appTheme = appTheme,
                onClick = {
                    onNavigateToHistory()
                    onClose()
                }
            )
            
            DrawerItem(
                icon = "⚙️",
                text = "Settings",
                appTheme = appTheme,
                onClick = {
                    onNavigateToSettings()
                    onClose()
                }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Divider(color = appTheme.textSecondaryColor.copy(alpha = 0.3f))
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Game mode section
            Text(
                "Game Mode",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = appTheme.textSecondaryColor,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(onClick = onToggleGameMode),
                color = if (gameMode == GameMode.CASUAL) 
                    Color(0xFF2196F3).copy(alpha = 0.1f) 
                else 
                    appTheme.backgroundColor,
                elevation = 0.dp
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("🎯", fontSize = 20.sp)
                        Text(
                            "Casual",
                            fontSize = 16.sp,
                            color = appTheme.textColor
                        )
                    }
                    if (gameMode == GameMode.CASUAL) {
                        Text("✓", fontSize = 20.sp, color = Color(0xFF2196F3))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(onClick = onToggleGameMode),
                color = if (gameMode == GameMode.DEATH_MATCH) 
                    Color(0xFFFF5252).copy(alpha = 0.1f) 
                else 
                    appTheme.backgroundColor,
                elevation = 0.dp
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("⚔️", fontSize = 20.sp)
                        Column {
                            Text(
                                "Death Match",
                                fontSize = 16.sp,
                                color = appTheme.textColor
                            )
                            Text(
                                "ELO Rating",
                                fontSize = 11.sp,
                                color = appTheme.textSecondaryColor
                            )
                        }
                    }
                    if (gameMode == GameMode.DEATH_MATCH) {
                        Text("✓", fontSize = 20.sp, color = Color(0xFFFF5252))
                    }
                }
            }
            
            if (gameMode == GameMode.DEATH_MATCH) {
                Spacer(modifier = Modifier.height(12.dp))
                
                TextButton(
                    onClick = onResetStats,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFFFF5252)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Reset Stats", fontSize = 14.sp)
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Version info
            Text(
                "Version 1.2.0",
                fontSize = 12.sp,
                color = appTheme.textSecondaryColor.copy(alpha = 0.6f),
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
fun DrawerItem(
    icon: String,
    text: String,
    appTheme: AppThemeColors,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(icon, fontSize = 24.sp)
            Text(
                text,
                fontSize = 16.sp,
                color = appTheme.textColor,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun ModernPlayerInfoBar(
    appTheme: AppThemeColors,
    whitePlayer: Player,
    blackPlayer: Player
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = appTheme.backgroundColor,
        elevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ModernPlayerInfoCard(player = whitePlayer, isWhite = true, appTheme = appTheme)
            
            Text(
                "VS",
                color = appTheme.textSecondaryColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            
            ModernPlayerInfoCard(player = blackPlayer, isWhite = false, appTheme = appTheme)
        }
    }
}

@Composable
fun ModernPlayerInfoCard(
    player: Player,
    isWhite: Boolean,
    appTheme: AppThemeColors
) {
    Surface(
        modifier = Modifier.widthIn(max = 140.dp),
        shape = RoundedCornerShape(4.dp),
        color = Color(0xFF312E2B),
        elevation = 0.dp
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(if (isWhite) Color.White else Color(0xFF2C2C2C))
            )
            
            Column {
                Text(
                    player.name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    maxLines = 1
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "${player.elo}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                    Text(
                        "•",
                        fontSize = 10.sp,
                        color = Color.White.copy(alpha = 0.5f)
                    )
                    Text(
                        "${player.wins}W",
                        fontSize = 10.sp,
                        color = Color(0xFF81B64C)
                    )
                }
            }
        }
    }
}
