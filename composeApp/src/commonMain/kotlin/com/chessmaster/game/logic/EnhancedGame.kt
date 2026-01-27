package com.chessmaster.game.logic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.chessmaster.game.data.GameHistoryManager
import com.chessmaster.game.data.PlayerManager
import com.chessmaster.game.data.toRuntimeBoard
import com.chessmaster.game.data.toRuntimeMoves
import com.chessmaster.game.theme.EnhancedThemeManager
import com.chessmaster.game.ui.*

enum class Screen {
    GAME, SETTINGS, HISTORY
}

@Composable
fun EnhancedGame() {
    val themeManager = remember { EnhancedThemeManager() }
    val gameHistoryManager = remember { GameHistoryManager() }
    val playerManager = remember { PlayerManager() }
    
    var currentScreen by remember { mutableStateOf(Screen.GAME) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var showPlayerSetup by remember { mutableStateOf(true) }
    var showCheckmateDialog by remember { mutableStateOf(false) }
    var checkmateWinner by remember { mutableStateOf<Pair<String, Boolean>?>(null) }
    
    var isFlipped by remember { mutableStateOf(false) }
    
    // Game state for save/load
    var currentBoard by remember { mutableStateOf<MutableList<MutableList<Piece?>>?>(null) }
    var currentMoves by remember { mutableStateOf<ArrayList<Move1>?>(null) }
    var currentMoveIndex by remember { mutableStateOf<Int?>(null) }
    var currentTurn by remember { mutableStateOf<PieceColor?>(null) }
    var gameLoadKey by remember { mutableStateOf(0) }
    
    val appTheme = themeManager.getAppTheme()
    val boardColors = themeManager.getBoardColors()
    
    // Player setup dialog
    if (showPlayerSetup) {
        PlayerSetupDialog(
            onDismiss = { showPlayerSetup = false },
            onStart = { whiteName, blackName ->
                playerManager.setPlayerNames(whiteName, blackName)
                showPlayerSetup = false
            }
        )
    }
    
    // Checkmate dialog
    if (showCheckmateDialog && checkmateWinner != null) {
        CheckmateDialog(
            winnerName = checkmateWinner!!.first,
            isWhiteWinner = checkmateWinner!!.second,
            eloGain = 8,
            onNewGame = {
                showCheckmateDialog = false
                currentBoard = null
                currentMoves = null
                currentMoveIndex = null
                currentTurn = null
                gameLoadKey++
            },
            onDismiss = {
                showCheckmateDialog = false
            }
        )
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(appTheme.backgroundColor)
    ) {
        when (currentScreen) {
            Screen.GAME -> {
                // Top bar
                ChessTopBar(
                    appTheme = appTheme,
                    whitePlayer = playerManager.whitePlayer,
                    blackPlayer = playerManager.blackPlayer,
                    onSaveClick = { showSaveDialog = true },
                    onMenuClick = { currentScreen = Screen.SETTINGS },
                    onFlipBoard = { isFlipped = !isFlipped }
                )
                
                // Game board
                key(gameLoadKey) {
                    EnhancedBoardView(
                        boardColors = boardColors,
                        appTheme = appTheme,
                        onSaveGame = { board, movesList, moveIndex, turn ->
                            currentBoard = board
                            currentMoves = movesList
                            currentMoveIndex = moveIndex
                            currentTurn = turn
                        },
                        onCheckmate = { isWhiteWinner ->
                            val winner = if (isWhiteWinner) {
                                playerManager.whitePlayer
                            } else {
                                playerManager.blackPlayer
                            }
                            playerManager.recordWin(isWhiteWinner)
                            checkmateWinner = Pair(winner.name, isWhiteWinner)
                            showCheckmateDialog = true
                        },
                        loadedBoard = currentBoard,
                        loadedMoves = currentMoves,
                        loadedMoveIndex = currentMoveIndex,
                        loadedTurn = currentTurn,
                        isFlipped = isFlipped
                    )
                }
                
                // Save dialog
                if (showSaveDialog) {
                    SaveGameDialog(
                        onDismiss = { showSaveDialog = false },
                        onSave = { gameName ->
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
            
            Screen.SETTINGS -> {
                SettingsScreen(
                    appTheme = appTheme,
                    isDarkMode = themeManager.isDarkMode,
                    currentBoardScheme = themeManager.boardScheme,
                    onToggleTheme = { themeManager.toggleTheme() },
                    onBoardSchemeChange = { scheme -> themeManager.setBoardScheme(scheme) },
                    onBack = { currentScreen = Screen.GAME }
                )
            }
            
            Screen.HISTORY -> {
                GameHistoryScreen(
                    gameHistoryManager = gameHistoryManager,
                    theme = com.chessmaster.game.theme.ChessTheme(
                        lightSquare = boardColors.lightSquare,
                        darkSquare = boardColors.darkSquare,
                        hintMoveColor = boardColors.hintMoveColor,
                        moveTraceColor = boardColors.moveTraceColor,
                        backgroundColor = appTheme.backgroundColor,
                        textColor = appTheme.textColor,
                        buttonColor = appTheme.buttonColor,
                        turnIndicatorWhite = appTheme.turnIndicatorWhite,
                        turnIndicatorBlack = appTheme.turnIndicatorBlack
                    ),
                    onLoadGame = { savedGame ->
                        currentBoard = savedGame.toRuntimeBoard()
                        currentMoves = savedGame.toRuntimeMoves()
                        currentMoveIndex = savedGame.currentMoveIndex
                        currentTurn = if (savedGame.currentTurn == "WHITE") PieceColor.WHITE else PieceColor.BLACK
                        gameLoadKey++
                        currentScreen = Screen.GAME
                    },
                    onBack = { currentScreen = Screen.GAME }
                )
            }
        }
    }
}
