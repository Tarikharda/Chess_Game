package com.chessmaster.game.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chessgame.composeapp.generated.resources.Res
import chessgame.composeapp.generated.resources.reset
import com.chessmaster.game.logic.*
import com.chessmaster.game.theme.AppThemeColors
import com.chessmaster.game.theme.BoardColors
import org.jetbrains.compose.resources.painterResource
import kotlinx.coroutines.delay

data class PieceAnimation(
    val from: Position,
    val to: Position,
    val piece: Piece
)

@Composable
fun PremiumBoardView(
    boardColors: BoardColors,
    appTheme: AppThemeColors,
    onSaveGame: (
        board: MutableList<MutableList<Piece?>>,
        moves: ArrayList<Move1>,
        moveIndex: Int,
        turn: PieceColor
    ) -> Unit,
    onCheckmate: (isWhiteWinner: Boolean) -> Unit,
    onMovesMade: (List<Move1>) -> Unit,
    loadedBoard: MutableList<MutableList<Piece?>>? = null,
    loadedMoves: ArrayList<Move1>? = null,
    loadedMoveIndex: Int? = null,
    loadedTurn: PieceColor? = null,
    isFlipped: Boolean = false,
    animationEnabled: Boolean = true
) {
    var board by remember { mutableStateOf(loadedBoard ?: WHITE_INITIAL_POSITION) }
    var selectedPiece by remember { mutableStateOf<Piece?>(null) }
    var selectedPosition by remember { mutableStateOf(Position(-1, -1)) }
    var previousPosition by remember { mutableStateOf(Position(-1, -1)) }
    var currentPosition by remember { mutableStateOf(Position(-1, -1)) }
    
    var currentMoves by remember { mutableStateOf<List<Move1>>(emptyList()) }
    var listOfMoves by remember { mutableStateOf(loadedMoves ?: arrayListOf()) }
    var currentMoveIndex by remember { mutableStateOf(loadedMoveIndex ?: 0) }
    var currentBackNextIndex by remember { mutableStateOf(loadedMoveIndex ?: 0) }
    
    var pieceTurnColor by remember { mutableStateOf(loadedTurn ?: PieceColor.WHITE) }
    
    var animatingPiece by remember { mutableStateOf<PieceAnimation?>(null) }
    var animationProgress by remember { mutableStateOf(0f) }
    
    var showCheckIndicator by remember { mutableStateOf(false) }
    
    // Piece selection scale animation
    val selectionScale by animateFloatAsState(
        targetValue = if (selectedPiece != null) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    
    // Check indicator pulse
    val checkPulse by animateFloatAsState(
        targetValue = if (showCheckIndicator) 1f else 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    // Animate piece movement
    LaunchedEffect(animatingPiece) {
        if (animatingPiece != null && animationEnabled) {
            val duration = 300f
            val step = 16f
            val totalSteps = duration / step
            
            for (i in 0..totalSteps.toInt()) {
                animationProgress = i / totalSteps
                delay(step.toLong())
            }
            
            animatingPiece = null
            animationProgress = 0f
        }
    }
    
    // Check detection
    LaunchedEffect(board, pieceTurnColor) {
        showCheckIndicator = isKingInCheck(board, pieceTurnColor)
        
        if (isCheckmate(board, pieceTurnColor)) {
            delay(500) // Small delay before showing checkmate
            onCheckmate(pieceTurnColor == PieceColor.BLACK)
        }
    }
    
    // Notify about moves
    LaunchedEffect(listOfMoves) {
        onMovesMade(listOfMoves)
    }
    
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Check indicator
        if (showCheckIndicator) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer(alpha = checkPulse),
                color = Color(0xFFE06C75),
                elevation = 0.dp
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "⚠️ CHECK!",
                        color = Color.White,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }
        }
        
        // Chess board
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            elevation = 0.dp,
            shape = RoundedCornerShape(0.dp)
        ) {
            Box {
                Column {
                    for (boardY in 0 until 8) {
                        val y = if (isFlipped) 7 - boardY else boardY
                        
                        Row(modifier = Modifier.weight(1f)) {
                            for (boardX in 0 until 8) {
                                val x = if (isFlipped) 7 - boardX else boardX
                                
                                ChessSquare(
                                    y = y,
                                    x = x,
                                    boardColors = boardColors,
                                    board = board,
                                    selectedPosition = selectedPosition,
                                    previousPosition = previousPosition,
                                    currentPosition = currentPosition,
                                    currentMoves = currentMoves,
                                    currentMoveIndex = currentMoveIndex,
                                    currentBackNextIndex = currentBackNextIndex,
                                    pieceTurnColor = pieceTurnColor,
                                    animatingPiece = animatingPiece,
                                    animationProgress = animationProgress,
                                    selectionScale = selectionScale,
                                    onSquareClick = { clickY, clickX ->
                                        handleSquareClick(
                                            clickY = clickY,
                                            clickX = clickX,
                                            board = board,
                                            selectedPiece = selectedPiece,
                                            selectedPosition = selectedPosition,
                                            currentMoves = currentMoves,
                                            pieceTurnColor = pieceTurnColor,
                                            listOfMoves = listOfMoves,
                                            currentMoveIndex = currentMoveIndex,
                                            currentBackNextIndex = currentBackNextIndex,
                                            animationEnabled = animationEnabled,
                                            onStateUpdate = { newBoard, newSelectedPiece, newSelectedPos, newCurrentMoves, 
                                                            newPrevPos, newCurrPos, newListOfMoves, 
                                                            newMoveIndex, newBackNextIndex, newTurnColor, newAnimatingPiece ->
                                                board = newBoard
                                                selectedPiece = newSelectedPiece
                                                selectedPosition = newSelectedPos
                                                currentMoves = newCurrentMoves
                                                previousPosition = newPrevPos
                                                currentPosition = newCurrPos
                                                listOfMoves = newListOfMoves
                                                currentMoveIndex = newMoveIndex
                                                currentBackNextIndex = newBackNextIndex
                                                pieceTurnColor = newTurnColor
                                                animatingPiece = newAnimatingPiece
                                            }
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
        
        // Control buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ModernButton(
                text = "◀ Back",
                enabled = currentBackNextIndex >= 1,
                appTheme = appTheme,
                onClick = {
                    if (currentBackNextIndex >= 1) {
                        currentBackNextIndex--
                        val prePiece = listOfMoves[currentBackNextIndex].piece
                        
                        currentPosition = listOfMoves[currentBackNextIndex].from
                        previousPosition = listOfMoves[currentBackNextIndex].to
                        
                        board[currentPosition.y][currentPosition.x] = prePiece
                        board[previousPosition.y][previousPosition.x] = null
                        
                        pieceTurnColor = prePiece.p_color
                        currentMoves = emptyList()
                        selectedPiece = null
                    }
                }
            )
            
            ModernButton(
                text = "Next ▶",
                enabled = currentBackNextIndex < listOfMoves.size,
                appTheme = appTheme,
                onClick = {
                    if (currentBackNextIndex < listOfMoves.size) {
                        val nextPiece = listOfMoves[currentBackNextIndex].piece
                        
                        currentPosition = listOfMoves[currentBackNextIndex].to
                        previousPosition = listOfMoves[currentBackNextIndex].from
                        
                        board[currentPosition.y][currentPosition.x] = nextPiece
                        board[previousPosition.y][previousPosition.x] = null
                        
                        if (listOfMoves[currentBackNextIndex].captureSquare != null) {
                            board[listOfMoves[currentBackNextIndex].captureSquare!!.y][listOfMoves[currentBackNextIndex].captureSquare!!.x] = null
                        }
                        
                        pieceTurnColor = if (nextPiece.p_color == PieceColor.WHITE) {
                            PieceColor.BLACK
                        } else {
                            PieceColor.WHITE
                        }
                        
                        currentMoves = emptyList()
                        selectedPiece = null
                        currentBackNextIndex++
                    }
                }
            )
            
            Surface(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .clickable {
                        board = WHITE_INITIAL_POSITION
                        selectedPiece = null
                        selectedPosition = Position(-1, -1)
                        previousPosition = Position(-1, -1)
                        currentPosition = Position(-1, -1)
                        currentMoves = emptyList()
                        currentMoveIndex = 0
                        currentBackNextIndex = 0
                        listOfMoves = arrayListOf()
                        pieceTurnColor = PieceColor.WHITE
                    },
                color = Color(0xFFE06C75),
                elevation = 0.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Image(
                        painterResource(Res.drawable.reset),
                        contentDescription = "Reset",
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun RowScope.ChessSquare(
    y: Int,
    x: Int,
    boardColors: BoardColors,
    board: MutableList<MutableList<Piece?>>,
    selectedPosition: Position,
    previousPosition: Position,
    currentPosition: Position,
    currentMoves: List<Move1>,
    currentMoveIndex: Int,
    currentBackNextIndex: Int,
    pieceTurnColor: PieceColor,
    animatingPiece: PieceAnimation?,
    animationProgress: Float,
    selectionScale: Float,
    onSquareClick: (Int, Int) -> Unit
) {
    val isLightSquare = (y + x) % 2 != 0
    val squareColor = if (isLightSquare) boardColors.lightSquare else boardColors.darkSquare
    
    val finalColor = when {
        selectedPosition.y == y && selectedPosition.x == x -> 
            boardColors.hintMoveColor.copy(alpha = 0.8f)
        previousPosition.y == y && previousPosition.x == x -> 
            boardColors.moveTraceColor
        currentPosition.y == y && currentPosition.x == x -> 
            boardColors.moveTraceColor
        else -> squareColor
    }
    
    val currentPiece = board[y][x]
    val isAnimatingHere = animatingPiece?.to?.y == y && animatingPiece?.to?.x == x
    
    Box(
        modifier = Modifier
            .weight(1f)
            .aspectRatio(1f)
            .background(finalColor)
            .clickable { onSquareClick(y, x) }
    ) {
        // Coordinates
        if (x == 0) {
            Text(
                "${8 - y}",
                modifier = Modifier.padding(3.dp),
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 10.sp
            )
        }
        if (y == 7) {
            Text(
                "${'a' + x}",
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(3.dp),
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 10.sp
            )
        }
        
        // Piece image
        if (currentPiece != null && animatingPiece?.from != Position(y, x)) {
            val scale = if (selectedPosition.y == y && selectedPosition.x == x) selectionScale else 1f
            
            Image(
                painterResource(Piece.p_image(currentPiece.p_type, currentPiece.p_color)),
                contentDescription = "",
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.Center)
                    .scale(scale)
            )
        }
        
        // Move hints
        if (currentMoves.any { it.to.y == y && it.to.x == x } && 
            currentMoveIndex == currentBackNextIndex) {
            val hintScale by animateFloatAsState(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy
                )
            )
            
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .scale(hintScale)
                    .clip(CircleShape)
                    .background(boardColors.hintMoveColor.copy(alpha = 0.8f))
                    .align(Alignment.Center)
            )
        }
    }
}

@Composable
fun ModernButton(
    text: String,
    enabled: Boolean,
    appTheme: AppThemeColors,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .height(40.dp)
            .widthIn(min = 100.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color(0xFF81B64C),
            contentColor = Color.White,
            disabledBackgroundColor = Color(0xFF81B64C).copy(alpha = 0.4f),
            disabledContentColor = Color.White.copy(alpha = 0.6f)
        ),
        shape = RoundedCornerShape(6.dp),
        elevation = ButtonDefaults.elevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
            disabledElevation = 0.dp
        )
    ) {
        Text(
            text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

fun handleSquareClick(
    clickY: Int,
    clickX: Int,
    board: MutableList<MutableList<Piece?>>,
    selectedPiece: Piece?,
    selectedPosition: Position,
    currentMoves: List<Move1>,
    pieceTurnColor: PieceColor,
    listOfMoves: ArrayList<Move1>,
    currentMoveIndex: Int,
    currentBackNextIndex: Int,
    animationEnabled: Boolean,
    onStateUpdate: (
        board: MutableList<MutableList<Piece?>>,
        selectedPiece: Piece?,
        selectedPosition: Position,
        currentMoves: List<Move1>,
        previousPosition: Position,
        currentPosition: Position,
        listOfMoves: ArrayList<Move1>,
        currentMoveIndex: Int,
        currentBackNextIndex: Int,
        pieceTurnColor: PieceColor,
        animatingPiece: PieceAnimation?
    ) -> Unit
) {
    val currentPiece = board[clickY][clickX]
    
    if (selectedPiece == null) {
        // Select piece
        if (currentPiece != null && currentPiece.p_color == pieceTurnColor) {
            val validMoves = getValidMoves(
                currentPiece,
                Position(clickY, clickX),
                board,
                if (listOfMoves.isNotEmpty() && currentMoveIndex > 0) 
                    listOfMoves[currentMoveIndex - 1] 
                else null
            )
            
            onStateUpdate(
                board, currentPiece, Position(clickY, clickX), validMoves,
                Position(-1, -1), Position(-1, -1), listOfMoves,
                currentMoveIndex, currentBackNextIndex, pieceTurnColor, null
            )
        }
    } else {
        // Try to move
        val moveToHere = currentMoves.find { it.to.y == clickY && it.to.x == clickX }
        
        if (moveToHere != null && currentMoveIndex == currentBackNextIndex) {
            // Perform move
            val newBoard = board.toMutableList().map { it.toMutableList() }.toMutableList()
            val previousPos = selectedPosition
            val currentPos = Position(clickY, clickX)
            
            newBoard[selectedPosition.y][selectedPosition.x] = null
            if (moveToHere.captureSquare != null) {
                newBoard[moveToHere.captureSquare.y][moveToHere.captureSquare.x] = null
            }
            newBoard[clickY][clickX] = selectedPiece
            
            val newListOfMoves = ArrayList(listOfMoves)
            newListOfMoves.add(
                currentMoveIndex,
                Move1(
                    selectedPiece,
                    selectedPosition,
                    Position(clickY, clickX),
                    moveToHere.isCapture,
                    moveToHere.captureSquare
                )
            )
            
            val newTurnColor = if (pieceTurnColor == PieceColor.WHITE) {
                PieceColor.BLACK
            } else {
                PieceColor.WHITE
            }
            
            val animating = if (animationEnabled) {
                PieceAnimation(selectedPosition, Position(clickY, clickX), selectedPiece)
            } else null
            
            onStateUpdate(
                newBoard, null, Position(-1, -1), emptyList(),
                previousPos, currentPos, newListOfMoves,
                currentMoveIndex + 1, currentBackNextIndex + 1, newTurnColor, animating
            )
        } else {
            // Deselect or select new piece
            if (currentPiece != null && currentPiece.p_color == pieceTurnColor) {
                val validMoves = getValidMoves(
                    currentPiece,
                    Position(clickY, clickX),
                    board,
                    if (listOfMoves.isNotEmpty() && currentMoveIndex > 0) 
                        listOfMoves[currentMoveIndex - 1] 
                    else null
                )
                
                onStateUpdate(
                    board, currentPiece, Position(clickY, clickX), validMoves,
                    Position(-1, -1), Position(-1, -1), listOfMoves,
                    currentMoveIndex, currentBackNextIndex, pieceTurnColor, null
                )
            } else {
                onStateUpdate(
                    board, null, Position(-1, -1), emptyList(),
                    Position(-1, -1), Position(-1, -1), listOfMoves,
                    currentMoveIndex, currentBackNextIndex, pieceTurnColor, null
                )
            }
        }
    }
}
