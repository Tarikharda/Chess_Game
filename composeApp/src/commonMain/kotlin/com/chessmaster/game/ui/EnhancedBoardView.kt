package com.chessmaster.game.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chessgame.composeapp.generated.resources.Res
import chessgame.composeapp.generated.resources.reset
import com.chessmaster.game.logic.*
import com.chessmaster.game.theme.AppThemeColors
import com.chessmaster.game.theme.BoardColors
import org.jetbrains.compose.resources.painterResource

data class AnimatedMove(
    val from: Position,
    val to: Position,
    val piece: Piece,
    val progress: Float = 0f
)

@Composable
fun EnhancedBoardView(
    boardColors: BoardColors,
    appTheme: AppThemeColors,
    onSaveGame: (
        board: MutableList<MutableList<Piece?>>,
        moves: ArrayList<Move1>,
        moveIndex: Int,
        turn: PieceColor
    ) -> Unit,
    onCheckmate: (isWhiteWinner: Boolean) -> Unit,
    loadedBoard: MutableList<MutableList<Piece?>>? = null,
    loadedMoves: ArrayList<Move1>? = null,
    loadedMoveIndex: Int? = null,
    loadedTurn: PieceColor? = null,
    isFlipped: Boolean = false
) {
    var board by remember { mutableStateOf(loadedBoard ?: WHITE_INITIAL_POSITION) }
    var selectedPiece by remember { mutableStateOf<Piece?>(null) }
    var selectedPosition by remember { mutableStateOf(Position(-1, -1)) }
    var previousPosition by remember { mutableStateOf(Position(-1, -1)) }
    var currentPosition by remember { mutableStateOf(Position(-1, -1)) }
    
    var currentMoves by remember { mutableStateOf<List<Move1>>(emptyList()) }
    var moveCnt by remember { mutableStateOf(0) }
    
    var currentMoveIndex by remember { mutableStateOf(loadedMoveIndex ?: 0) }
    var currentBackNextIndex by remember { mutableStateOf(loadedMoveIndex ?: 0) }
    
    var listOfMoves by remember { mutableStateOf(loadedMoves ?: arrayListOf()) }
    var pieceTurnColor by remember { mutableStateOf(loadedTurn ?: PieceColor.WHITE) }
    
    var animatedMove by remember { mutableStateOf<AnimatedMove?>(null) }
    var showCheckmate by remember { mutableStateOf(false) }
    
    // Animation
    val animationProgress by animateFloatAsState(
        targetValue = if (animatedMove != null) 1f else 0f,
        animationSpec = tween(durationMillis = 300),
        finishedListener = { animatedMove = null }
    )
    
    // Checkmate detection
    LaunchedEffect(board, pieceTurnColor) {
        if (isCheckmate(board, pieceTurnColor)) {
            showCheckmate = true
            onCheckmate(pieceTurnColor == PieceColor.BLACK)
        }
    }
    
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Chess board
        for (boardY in 0 until 8) {
            val y = if (isFlipped) 7 - boardY else boardY
            
            Row(modifier = Modifier.fillMaxWidth()) {
                for (boardX in 0 until 8) {
                    val x = if (isFlipped) 7 - boardX else boardX
                    
                    val isLightSquare = (y + x) % 2 != 0
                    val squareColor = if (isLightSquare) {
                        boardColors.lightSquare
                    } else {
                        boardColors.darkSquare
                    }
                    
                    val finalColor = when {
                        selectedPiece != null && selectedPosition.y == y && selectedPosition.x == x -> 
                            boardColors.hintMoveColor
                        previousPosition.y == y && previousPosition.x == x -> 
                            boardColors.moveTraceColor
                        currentPosition.y == y && currentPosition.x == x -> 
                            boardColors.moveTraceColor
                        else -> squareColor
                    }
                    
                    val currentPiece = board[y][x]
                    
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .background(finalColor)
                            .clickable {
                                if (selectedPiece == null) {
                                    // Select piece
                                    if (currentPiece != null && currentPiece.p_color == pieceTurnColor) {
                                        selectedPiece = currentPiece
                                        selectedPosition = Position(y, x)
                                        currentMoves = getValidMoves(
                                            currentPiece,
                                            Position(y, x),
                                            board,
                                            if (listOfMoves.isNotEmpty() && currentMoveIndex > 0) 
                                                listOfMoves[currentMoveIndex - 1] 
                                            else null
                                        )
                                    }
                                } else {
                                    // Try to move
                                    val moveToHere = currentMoves.find { it.to.y == y && it.to.x == x }
                                    if (moveToHere != null && currentMoveIndex == currentBackNextIndex) {
                                        // Perform move
                                        animatedMove = AnimatedMove(selectedPosition, Position(y, x), selectedPiece!!)
                                        
                                        previousPosition = selectedPosition
                                        currentPosition = Position(y, x)
                                        
                                        board[selectedPosition.y][selectedPosition.x] = null
                                        if (moveToHere.captureSquare != null) {
                                            board[moveToHere.captureSquare.y][moveToHere.captureSquare.x] = null
                                        }
                                        board[y][x] = selectedPiece
                                        
                                        listOfMoves.add(
                                            currentMoveIndex,
                                            Move1(
                                                selectedPiece!!,
                                                selectedPosition,
                                                Position(y, x),
                                                moveToHere.isCapture,
                                                moveToHere.captureSquare
                                            )
                                        )
                                        
                                        currentMoveIndex++
                                        currentBackNextIndex++
                                        if (pieceTurnColor == PieceColor.WHITE) moveCnt++
                                        
                                        // Switch turn
                                        pieceTurnColor = if (pieceTurnColor == PieceColor.WHITE) {
                                            PieceColor.BLACK
                                        } else {
                                            PieceColor.WHITE
                                        }
                                        
                                        selectedPiece = null
                                        currentMoves = emptyList()
                                    } else {
                                        // Deselect or select new piece
                                        if (currentPiece != null && currentPiece.p_color == pieceTurnColor) {
                                            selectedPiece = currentPiece
                                            selectedPosition = Position(y, x)
                                            currentMoves = getValidMoves(
                                                currentPiece,
                                                Position(y, x),
                                                board,
                                                if (listOfMoves.isNotEmpty() && currentMoveIndex > 0) 
                                                    listOfMoves[currentMoveIndex - 1] 
                                                else null
                                            )
                                        } else {
                                            selectedPiece = null
                                            currentMoves = emptyList()
                                        }
                                    }
                                }
                            }
                    ) {
                        // Coordinates
                        if (x == 0) {
                            Text(
                                "${8 - y}",
                                modifier = Modifier.padding(3.dp),
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 10.sp
                            )
                        }
                        if (y == 7) {
                            Text(
                                "${'a' + x}",
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(3.dp),
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 10.sp
                            )
                        }
                        
                        // Piece image
                        currentPiece?.let { piece ->
                            Image(
                                painterResource(Piece.p_image(piece.p_type, piece.p_color)),
                                contentDescription = "",
                                modifier = Modifier
                                    .size(40.dp)
                                    .align(Alignment.Center)
                            )
                        }
                        
                        // Move hints
                        if (currentMoves.any { it.to.y == y && it.to.x == x } && 
                            currentMoveIndex == currentBackNextIndex) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(boardColors.hintMoveColor.copy(alpha = 0.7f))
                                    .align(Alignment.Center)
                            )
                        }
                    }
                }
            }
        }
        
        // Control buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                modifier = Modifier.padding(horizontal = 5.dp),
                onClick = {
                    if (currentBackNextIndex >= 1) {
                        currentBackNextIndex--
                        val prePiece = listOfMoves[currentBackNextIndex].piece
                        
                        currentPosition = listOfMoves[currentBackNextIndex].from
                        previousPosition = listOfMoves[currentBackNextIndex].to
                        
                        board[currentPosition.y][currentPosition.x] = prePiece
                        board[previousPosition.y][previousPosition.x] = null
                        
                        if (listOfMoves[currentBackNextIndex].isCapture) {
                            val lastTreatedPieceIndex = currentBackNextIndex - 1
                            if (lastTreatedPieceIndex >= 0) {
                                val capturedPiece = listOfMoves[lastTreatedPieceIndex].piece
                                board[listOfMoves[lastTreatedPieceIndex].to.y][listOfMoves[lastTreatedPieceIndex].to.x] = capturedPiece
                            }
                        }
                        
                        pieceTurnColor = prePiece.p_color
                        currentMoves = emptyList()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = appTheme.buttonColor,
                    contentColor = Color.White
                )
            ) {
                Text("◀ Back")
            }
            
            Button(
                modifier = Modifier.padding(horizontal = 5.dp),
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
                        currentBackNextIndex++
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = appTheme.buttonColor,
                    contentColor = Color.White
                )
            ) {
                Text("Next ▶")
            }
            
            Image(
                painterResource(Res.drawable.reset),
                contentDescription = "Reset",
                modifier = Modifier
                    .size(39.dp)
                    .clickable {
                        board = WHITE_INITIAL_POSITION
                        selectedPiece = null
                        selectedPosition = Position(-1, -1)
                        previousPosition = Position(-1, -1)
                        currentPosition = Position(-1, -1)
                        currentMoves = emptyList()
                        moveCnt = 0
                        currentMoveIndex = 0
                        currentBackNextIndex = 0
                        listOfMoves = arrayListOf()
                        pieceTurnColor = PieceColor.WHITE
                    }
            )
        }
    }
}
