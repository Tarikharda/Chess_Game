package com.chessmaster.game.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chessmaster.game.data.Player
import com.chessmaster.game.theme.AppThemeColors

@Composable
fun ChessTopBar(
    appTheme: AppThemeColors,
    whitePlayer: Player,
    blackPlayer: Player,
    onSaveClick: () -> Unit,
    onMenuClick: () -> Unit,
    onFlipBoard: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = appTheme.surfaceColor,
        elevation = 4.dp
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // App Title
                Text(
                    "Chess Master",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = appTheme.textColor
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Flip board button
                    IconButton(onClick = onFlipBoard) {
                        Text("🔄", fontSize = 20.sp)
                    }
                    
                    // Save button
                    Button(
                        onClick = onSaveClick,
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFF4CAF50),
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text("💾 Save", fontSize = 14.sp)
                    }
                    
                    // Menu button
                    Box {
                        IconButton(onClick = { showMenu = !showMenu }) {
                            Text("☰", fontSize = 24.sp, color = appTheme.textColor)
                        }
                        
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(onClick = {
                                showMenu = false
                                onMenuClick()
                            }) {
                                Text("⚙️  Settings")
                            }
                        }
                    }
                }
            }
            
            // Player info bar
            PlayerInfoBar(appTheme, whitePlayer, blackPlayer)
        }
    }
}

@Composable
fun PlayerInfoBar(
    appTheme: AppThemeColors,
    whitePlayer: Player,
    blackPlayer: Player
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(appTheme.backgroundColor)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        PlayerInfoCard(player = whitePlayer, isWhite = true, appTheme = appTheme)
        Text("vs", color = appTheme.textSecondaryColor, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        PlayerInfoCard(player = blackPlayer, isWhite = false, appTheme = appTheme)
    }
}

@Composable
fun PlayerInfoCard(
    player: Player,
    isWhite: Boolean,
    appTheme: AppThemeColors
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Color indicator
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(if (isWhite) Color.White else Color.Black)
                .then(
                    if (isWhite) Modifier.then(
                        Modifier.background(Color.White)
                    ) else Modifier
                )
        )
        
        Column {
            Text(
                player.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = appTheme.textColor,
                maxLines = 1
            )
            Text(
                "ELO: ${player.elo} • ${player.wins}W/${player.losses}L",
                fontSize = 11.sp,
                color = appTheme.textSecondaryColor
            )
        }
    }
}

@Composable
fun PlayerSetupDialog(
    onDismiss: () -> Unit,
    onStart: (whiteName: String, blackName: String) -> Unit
) {
    var whiteName by remember { mutableStateOf("") }
    var blackName by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                "Death Match Setup",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Enter player names:", fontSize = 14.sp)
                
                OutlinedTextField(
                    value = whiteName,
                    onValueChange = { whiteName = it },
                    label = { Text("White Player") },
                    placeholder = { Text("White") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = blackName,
                    onValueChange = { blackName = it },
                    label = { Text("Black Player") },
                    placeholder = { Text("Black") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Text(
                    "Starting ELO: 1200 • Win: +8 • Loss: -8",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onStart(
                        whiteName.ifBlank { "White" },
                        blackName.ifBlank { "Black" }
                    )
                }
            ) {
                Text("Start Game")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun CheckmateDialog(
    winnerName: String,
    isWhiteWinner: Boolean,
    eloGain: Int,
    onNewGame: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        backgroundColor = Color(0xFF2D2D2D),
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "🏆 Checkmate!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    winnerName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isWhiteWinner) Color.White else Color.Black
                )
                Text(
                    "wins the game!",
                    fontSize = 16.sp,
                    color = Color.White
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    "ELO: +$eloGain",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onNewGame,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF4CAF50)
                )
            ) {
                Text("New Game")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = Color.White)
            }
        }
    )
}
