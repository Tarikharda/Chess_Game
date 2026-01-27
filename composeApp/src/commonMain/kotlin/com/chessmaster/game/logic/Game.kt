package com.chessmaster.game.logic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Divider
import androidx.compose.runtime.*
import com.chessmaster.game.data.GameHistoryManager
import com.chessmaster.game.data.toRuntimeBoard
import com.chessmaster.game.data.toRuntimeMoves
import com.chessmaster.game.theme.rememberThemeManager
import com.chessmaster.game.ui.BoardView
import com.chessmaster.game.ui.GameHistoryScreen
import com.chessmaster.game.ui.MovesView
import com.chessmaster.game.ui.SaveGameDialog

enum class GameScreen {
    GAME, HISTORY
}

@Composable
fun Game() {
    var moves by remember { mutableStateOf("") }
    val themeManager = rememberThemeManager()
    val gameHistoryManager = remember { GameHistoryManager() }
    
    var currentScreen by remember { mutableStateOf(GameScreen.GAME) }
    var showSaveDialog by remember { mutableStateOf(false) }
    
    // Game state for save/load
    var currentBoard by remember { mutableStateOf<MutableList<MutableList<Piece?>>?>(null) }
    var currentMoves by remember { mutableStateOf<ArrayList<Move1>?>(null) }
    var currentMoveIndex by remember { mutableStateOf<Int?>(null) }
    var currentTurn by remember { mutableStateOf<PieceColor?>(null) }
    var gameLoadKey by remember { mutableStateOf(0) } // Used to force recomposition when loading a game
    
    val theme = themeManager.getTheme()
    
    when (currentScreen) {
        GameScreen.GAME -> {
            Column(
                modifier = androidx.compose.ui.Modifier
                    .fillMaxSize()
                    .background(theme.backgroundColor)
            ) {
                key(gameLoadKey) { // Force recomposition when loading a game
                    BoardView(
                        moves = moves,
                        onMoveChange = { newMove -> moves = newMove },
                        theme = theme,
                        onSaveGame = { board, movesList, moveIndex, turn ->
                            currentBoard = board
                            currentMoves = movesList
                            currentMoveIndex = moveIndex
                            currentTurn = turn
                            showSaveDialog = true
                        },
                        onShowHistory = { currentScreen = GameScreen.HISTORY },
                        onToggleTheme = { themeManager.toggleTheme() },
                        loadedBoard = currentBoard,
                        loadedMoves = currentMoves,
                        loadedMoveIndex = currentMoveIndex,
                        loadedTurn = currentTurn
                    )
                }
                Divider()
                MovesView(moves, theme)
            }
            
            if (showSaveDialog) {
                SaveGameDialog(
                    onDismiss = { showSaveDialog = false },
                    onSave = { gameName ->
                        // Save the current game state
                        // Note: We need to get these from BoardView somehow
                        // For now, we'll save an empty game as a placeholder
                        gameHistoryManager.saveGame(
                            name = gameName,
                            board = currentBoard ?: WHITE_INITIAL_POSITION,
                            moves = currentMoves ?: ArrayList(),
                            currentMoveIndex = currentMoveIndex ?: 0,
                            currentTurn = currentTurn ?: PieceColor.WHITE
                        )
                    }
                )
            }
        }
        
        GameScreen.HISTORY -> {
            GameHistoryScreen(
                gameHistoryManager = gameHistoryManager,
                theme = theme,
                onLoadGame = { savedGame ->
                    currentBoard = savedGame.toRuntimeBoard()
                    currentMoves = savedGame.toRuntimeMoves()
                    currentMoveIndex = savedGame.currentMoveIndex
                    currentTurn = if (savedGame.currentTurn == "WHITE") PieceColor.WHITE else PieceColor.BLACK
                    gameLoadKey++ // Force BoardView to reinitialize with loaded state
                    moves = "" // Reset moves display
                    currentScreen = GameScreen.GAME
                },
                onBack = { currentScreen = GameScreen.GAME }
            )
        }
    }
}
