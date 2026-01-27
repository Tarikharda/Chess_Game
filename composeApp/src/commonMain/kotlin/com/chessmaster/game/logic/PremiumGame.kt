package com.chessmaster.game.logic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.chessmaster.game.data.*
import com.chessmaster.game.theme.EnhancedThemeManager
import com.chessmaster.game.ui.*
import kotlinx.coroutines.launch

@Composable
fun PremiumGame() {
    val themeManager = remember { EnhancedThemeManager() }
    val preferencesManager = remember { PreferencesManager() }
    val gameHistoryManager = remember { GameHistoryManager() }
    val playerManager = remember { PlayerManager() }
    
    var currentScreen by remember { mutableStateOf(Screen.GAME) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var showPlayerSetup by remember { mutableStateOf(false) }
    var showCheckmateDialog by remember { mutableStateOf(false) }
    var checkmateWinner by remember { mutableStateOf<Pair<String, Boolean>?>(null) }
    
    var isFlipped by remember { mutableStateOf(false) }
    
    // Game state
    var currentBoard by remember { mutableStateOf<MutableList<MutableList<Piece?>>?>(null) }
    var currentMoves by remember { mutableStateOf<ArrayList<Move1>?>(null) }
    var currentMoveIndex by remember { mutableStateOf<Int?>(null) }
    var currentTurn by remember { mutableStateOf<PieceColor?>(null) }
    var gameLoadKey by remember { mutableStateOf(0) }
    var currentMovesList by remember { mutableStateOf<List<Move1>>(emptyList()) }
    
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    
    val appTheme = themeManager.getAppTheme()
    val boardColors = themeManager.getBoardColors()
    
    // Load preferences
    LaunchedEffect(Unit) {
        preferencesManager.loadPreferences()
        themeManager.setDarkMode(preferencesManager.isDarkMode)
        themeManager.setBoardScheme(preferencesManager.boardScheme)
    }
    
    // Player setup for death match
    if (showPlayerSetup) {
        PlayerSetupDialog(
            onDismiss = { 
                showPlayerSetup = false
                preferencesManager.setGameMode(GameMode.CASUAL)
            },
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
    
    Scaffold(
        scaffoldState = scaffoldState,
        drawerContent = {
            NavigationDrawer(
                appTheme = appTheme,
                gameMode = preferencesManager.gameMode,
                onNavigateToGame = { currentScreen = Screen.GAME },
                onNavigateToHistory = { currentScreen = Screen.HISTORY },
                onNavigateToSettings = { currentScreen = Screen.SETTINGS },
                onToggleGameMode = {
                    val newMode = if (preferencesManager.gameMode == GameMode.CASUAL) {
                        showPlayerSetup = true
                        GameMode.DEATH_MATCH
                    } else {
                        GameMode.CASUAL
                    }
                    preferencesManager.setGameMode(newMode)
                },
                onResetStats = {
                    playerManager.resetStats()
                },
                onClose = {
                    scope.launch { scaffoldState.drawerState.close() }
                }
            )
        },
        drawerGesturesEnabled = currentScreen == Screen.GAME,
        drawerBackgroundColor = appTheme.backgroundColor
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(appTheme.backgroundColor)
        ) {
            when (currentScreen) {
                Screen.GAME -> {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Top bar
                        ModernTopBar(
                            appTheme = appTheme,
                            whitePlayer = if (preferencesManager.gameMode == GameMode.DEATH_MATCH) playerManager.whitePlayer else null,
                            blackPlayer = if (preferencesManager.gameMode == GameMode.DEATH_MATCH) playerManager.blackPlayer else null,
                            gameMode = preferencesManager.gameMode,
                            onSaveClick = { showSaveDialog = true },
                            onFlipBoard = { isFlipped = !isFlipped },
                            onOpenDrawer = {
                                scope.launch { scaffoldState.drawerState.open() }
                            }
                        )
                        
                        // Game board
                        key(gameLoadKey) {
                            PremiumBoardView(
                                boardColors = boardColors,
                                appTheme = appTheme,
                                onSaveGame = { board, movesList, moveIndex, turn ->
                                    currentBoard = board
                                    currentMoves = movesList
                                    currentMoveIndex = moveIndex
                                    currentTurn = turn
                                },
                                onCheckmate = { isWhiteWinner ->
                                    if (preferencesManager.gameMode == GameMode.DEATH_MATCH) {
                                        val winner = if (isWhiteWinner) {
                                            playerManager.whitePlayer
                                        } else {
                                            playerManager.blackPlayer
                                        }
                                        playerManager.recordWin(isWhiteWinner)
                                        checkmateWinner = Pair(winner.name, isWhiteWinner)
                                        showCheckmateDialog = true
                                    } else {
                                        val winnerName = if (isWhiteWinner) "White" else "Black"
                                        checkmateWinner = Pair(winnerName, isWhiteWinner)
                                        showCheckmateDialog = true
                                    }
                                },
                                onMovesMade = { moves ->
                                    currentMovesList = moves
                                },
                                loadedBoard = currentBoard,
                                loadedMoves = currentMoves,
                                loadedMoveIndex = currentMoveIndex,
                                loadedTurn = currentTurn,
                                isFlipped = isFlipped,
                                animationEnabled = preferencesManager.animationEnabled
                            )
                        }
                        
                        // Moves panel
                        MovesPanel(
                            appTheme = appTheme,
                            moves = currentMovesList,
                            currentMoveIndex = currentMoveIndex ?: 0
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
                                showSaveDialog = false
                            }
                        )
                    }
                }
                
                Screen.SETTINGS -> {
                    SettingsScreen(
                        appTheme = appTheme,
                        isDarkMode = themeManager.isDarkMode,
                        currentBoardScheme = themeManager.boardScheme,
                        onToggleTheme = { 
                            themeManager.toggleTheme()
                            preferencesManager.setDarkMode(themeManager.isDarkMode)
                        },
                        onBoardSchemeChange = { scheme -> 
                            themeManager.setBoardScheme(scheme)
                            preferencesManager.setBoardScheme(scheme)
                        },
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
}
