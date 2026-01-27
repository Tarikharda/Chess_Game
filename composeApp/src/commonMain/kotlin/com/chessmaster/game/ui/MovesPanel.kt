package com.chessmaster.game.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chessmaster.game.logic.Move1
import com.chessmaster.game.logic.PieceColor
import com.chessmaster.game.logic.PieceType
import com.chessmaster.game.theme.AppThemeColors

data class MoveNotation(
    val moveNumber: Int,
    val whiteMove: String?,
    val blackMove: String?
)

@Composable
fun MovesPanel(
    appTheme: AppThemeColors,
    moves: List<Move1>,
    currentMoveIndex: Int
) {
    val moveNotations = remember(moves) {
        convertToNotations(moves)
    }
    
    val listState = rememberLazyListState()
    
    LaunchedEffect(currentMoveIndex) {
        if (moveNotations.isNotEmpty()) {
            val targetIndex = (currentMoveIndex - 1) / 2
            if (targetIndex >= 0 && targetIndex < moveNotations.size) {
                listState.animateScrollToItem(targetIndex)
            }
        }
    }
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        color = appTheme.backgroundColor,
        elevation = 0.dp
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(appTheme.surfaceColor)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Moves",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = appTheme.textColor
                )
                
                Text(
                    "${moves.size} moves",
                    fontSize = 12.sp,
                    color = appTheme.textSecondaryColor
                )
            }
            
            Divider(color = appTheme.textSecondaryColor.copy(alpha = 0.2f))
            
            if (moves.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No moves yet",
                        fontSize = 14.sp,
                        color = appTheme.textSecondaryColor.copy(alpha = 0.6f)
                    )
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    itemsIndexed(moveNotations) { index, notation ->
                        MoveRow(
                            notation = notation,
                            appTheme = appTheme,
                            isCurrentMove = (currentMoveIndex - 1) / 2 == index
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MoveRow(
    notation: MoveNotation,
    appTheme: AppThemeColors,
    isCurrentMove: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = if (isCurrentMove) 
            Color(0xFF81B64C).copy(alpha = 0.2f) 
        else 
            appTheme.surfaceColor,
        elevation = 0.dp,
        shape = RoundedCornerShape(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Move number
            Text(
                "${notation.moveNumber}.",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = appTheme.textSecondaryColor,
                modifier = Modifier.width(32.dp)
            )
            
            // White move
            Text(
                notation.whiteMove ?: "",
                fontSize = 14.sp,
                fontWeight = if (isCurrentMove) FontWeight.SemiBold else FontWeight.Normal,
                color = appTheme.textColor,
                modifier = Modifier.weight(1f)
            )
            
            // Black move
            Text(
                notation.blackMove ?: "",
                fontSize = 14.sp,
                fontWeight = if (isCurrentMove) FontWeight.SemiBold else FontWeight.Normal,
                color = appTheme.textColor,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

fun convertToNotations(moves: List<Move1>): List<MoveNotation> {
    val notations = mutableListOf<MoveNotation>()
    var moveNumber = 1
    var whiteMove: String? = null
    
    for ((index, move) in moves.withIndex()) {
        val notation = getMoveNotation(move)
        
        if (move.piece.p_color == PieceColor.WHITE) {
            whiteMove = notation
        } else {
            notations.add(MoveNotation(moveNumber, whiteMove, notation))
            moveNumber++
            whiteMove = null
        }
    }
    
    // Add remaining white move if any
    if (whiteMove != null) {
        notations.add(MoveNotation(moveNumber, whiteMove, null))
    }
    
    return notations
}

fun getMoveNotation(move: Move1): String {
    val piece = when (move.piece.p_type) {
        PieceType.KING -> "K"
        PieceType.QUEEN -> "Q"
        PieceType.ROOK -> "R"
        PieceType.BISHOP -> "B"
        PieceType.KNIGHT -> "N"
        PieceType.PAWN -> ""
        else -> ""
    }
    
    val fromFile = ('a' + move.from.x)
    val fromRank = 8 - move.from.y
    val toFile = ('a' + move.to.x)
    val toRank = 8 - move.to.y
    
    val capture = if (move.isCapture) "x" else ""
    
    return if (move.piece.p_type == PieceType.PAWN) {
        if (move.isCapture) {
            "$fromFile$capture$toFile$toRank"
        } else {
            "$toFile$toRank"
        }
    } else {
        "$piece$capture$toFile$toRank"
    }
}
