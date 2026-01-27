package com.chessmaster.game.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chessmaster.game.data.GameHistoryManager
import com.chessmaster.game.data.SavedGame
import com.chessmaster.game.theme.ChessTheme

@Composable
fun GameHistoryScreen(
    gameHistoryManager: GameHistoryManager,
    theme: ChessTheme,
    onLoadGame: (SavedGame) -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.backgroundColor)
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Saved Games",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = theme.textColor
            )
            
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = theme.buttonColor,
                    contentColor = Color.White
                )
            ) {
                Text("Back")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Games list
        if (gameHistoryManager.savedGames.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No saved games yet",
                    color = theme.textColor.copy(alpha = 0.6f),
                    fontSize = 16.sp
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(gameHistoryManager.savedGames) { game ->
                    SavedGameCard(
                        game = game,
                        theme = theme,
                        onLoad = { onLoadGame(game) },
                        onDelete = { gameHistoryManager.deleteGame(game.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun SavedGameCard(
    game: SavedGame,
    theme: ChessTheme,
    onLoad: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onLoad() },
        elevation = 0.dp,
        shape = RoundedCornerShape(6.dp),
        backgroundColor = theme.buttonColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    game.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Saved: ${game.date}",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
                Text(
                    "Moves: ${game.moves.size} | Turn: ${game.currentTurn}",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
            
            IconButton(
                onClick = { showDeleteDialog = true }
            ) {
                Text("🗑️", fontSize = 20.sp)
            }
        }
    }
    
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Game") },
            text = { Text("Are you sure you want to delete '${game.name}'?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Red
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun SaveGameDialog(
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var gameName by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Save Game") },
        text = {
            Column {
                Text("Enter a name for this game:")
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = gameName,
                    onValueChange = { gameName = it },
                    placeholder = { Text("Game name...") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (gameName.isNotBlank()) {
                        onSave(gameName)
                        onDismiss()
                    }
                },
                enabled = gameName.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
