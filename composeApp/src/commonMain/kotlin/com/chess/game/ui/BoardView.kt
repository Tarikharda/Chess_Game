package com.chess.game.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chess.game.logic.Move
import com.chess.game.logic.Piece
import com.chess.game.logic.PieceColor
import com.chess.game.logic.PieceType
import com.chess.game.logic.Position
import com.chess.game.logic.WHITE_INITIAL_POSITION
import com.chess.game.logic.getMoveName
import com.chess.game.logic.p_movement
import com.chess.game.values.BoardColors
import org.jetbrains.compose.resources.painterResource


@Composable
fun BoardView(
    moves: String,
    onMoveChange: (String) -> Unit,
) {

    var board by remember { mutableStateOf(WHITE_INITIAL_POSITION) }

    var selectedPiece by remember { mutableStateOf<Piece?>(null) }
    var lastPieceClick by remember { mutableStateOf(Position(-1, -1)) }
    var previousPosition by remember { mutableStateOf(Position(-1, -1)) }
    var currentPosition by remember { mutableStateOf(Position(-1, -1)) }

    var currentMoves by remember { mutableStateOf<List<Pair<Position, Boolean>>>(emptyList()) }
    var moveCnt by remember { mutableStateOf(0) }
    var currentMoveIndex by remember { mutableStateOf(0) }
    var listOfMoves by remember { mutableStateOf<ArrayList<Move>>(arrayListOf()) }


    var currentPieceTurn by remember { mutableStateOf<Piece?>(null) }
    var turnColor by remember { mutableStateOf(BoardColors.lightSquare) }

    var squareStatusColor by remember { mutableStateOf(BoardColors.lightSquare) }

    var yState by remember { mutableStateOf(0) }
    var xState by remember { mutableStateOf(0) }

    var prePiece by remember { mutableStateOf<Piece?>(null) }
    var nextPiece by remember { mutableStateOf<Piece?>(null) }

    Column {
        for (y in 0 until 8) {
            Row {
                for (x in 0 until 8) {
                    val blackColor = y % 2 == x % 2
                    var color = if (blackColor) {
                        BoardColors.darkSquare
                    } else {
                        BoardColors.lightSquare
                    }

                    //numbers
                    val chessBoardY = 8 - y
                    val chessBoardX = 'a'

                    yState = y
                    xState = x

                    val currentPiece = board[yState][xState]
                    if (currentPieceTurn == null) currentPieceTurn = Piece("", PieceType.KING, PieceColor.WHITE)

                    if ((selectedPiece != null) && (lastPieceClick.y == yState && lastPieceClick.x == xState)) {
                        squareStatusColor = BoardColors.hintMoveColor
                    } else if (previousPosition.y == yState && previousPosition.x == xState) {
                        squareStatusColor = BoardColors.moveTraceColor
                    } else if (currentPosition.y == yState && currentPosition.x == xState) {
                        squareStatusColor = BoardColors.moveTraceColor
                    } else {
                        squareStatusColor = color
                    }


                    Box(
                        modifier = Modifier
                            .weight(1.0f)
                            .background(squareStatusColor)
                            .aspectRatio(1.0f)
                    ) {
                        if (x == 0) Text(
                            "$chessBoardY",
                            modifier = Modifier.padding(3.dp),
                            color = Color.Black,
                            fontSize = 10.sp
                        )
                        if (y == 7) Text(
                            "${chessBoardX + x}",
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(3.dp),
                            color = Color.Black,
                            fontSize = 10.sp
                        )

                        currentPiece?.let { piece ->
                            Image(
                                painterResource(Piece.p_image(piece.p_type, piece.p_color)),
                                contentDescription = "",
                                modifier = Modifier
                                    .size(40.dp)
                                    .align(Alignment.Center)
                                    .clickable {
                                        lastPieceClick = Position(y, x)
                                        currentMoves = emptyList()
                                        selectedPiece = piece
                                        if (currentPieceTurn!!.p_color == piece.p_color) {
                                            currentMoves = p_movement(piece, Position(y, x), board)
                                        }
                                    }
                            )
                        }

                        if (currentMoves.isNotEmpty()) {
                            currentMoves.forEach { (move, isTreated) ->
                                if (move.y == y && move.x == x) {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .background(BoardColors.hintMoveColor)
                                            .align(Alignment.Center)
                                            .clickable {

                                                previousPosition = Position( lastPieceClick.y , lastPieceClick.x)
                                                currentPosition = Position(move.y, move.x)

                                                if (currentPieceTurn!!.p_color == PieceColor.WHITE) {
                                                    currentPieceTurn = Piece("", PieceType.KING, PieceColor.BLACK)
                                                    turnColor = Color.Black
                                                } else {
                                                    currentPieceTurn = Piece("", PieceType.KING, PieceColor.WHITE)
                                                    turnColor = BoardColors.lightSquare
                                                }


                                                board[lastPieceClick.y][lastPieceClick.x] = null
                                                board[move.y][move.x] = selectedPiece


                                                listOfMoves.add(
                                                    currentMoveIndex,
                                                    Move(
                                                        selectedPiece!!,
                                                        Position(lastPieceClick.y, lastPieceClick.x),//from
                                                        Position(move.y, move.x),//to
                                                        isTreated
                                                    )
                                                )

                                                ++currentMoveIndex
                                                if (selectedPiece!!.p_color == PieceColor.WHITE){
                                                    ++moveCnt
                                                }

                                                val newMove = "$moves ${getMoveName(selectedPiece!!, chessBoardY, chessBoardX + x, chessBoardX + lastPieceClick.x, isTreated, moveCnt)}"
                                                onMoveChange("$currentMoveIndex")
                                                //onMoveChange(newMove)


                                                selectedPiece = null
                                                currentMoves = emptyList()
                                            }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier.padding(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(turnColor)
                .align(Alignment.TopEnd)
                .padding(16.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                modifier = Modifier.padding(10.dp, 0.dp),
                onClick = {
                    if(currentMoveIndex >= 1){
                        --currentMoveIndex

                        prePiece = listOfMoves[currentMoveIndex].piece

                        currentPosition = listOfMoves[currentMoveIndex].from
                        previousPosition = listOfMoves[currentMoveIndex].to

                        val isTreated = listOfMoves[currentMoveIndex].isTreated

                        board[currentPosition.y][currentPosition.x] =  prePiece
                        board[previousPosition.y][previousPosition.x] = null

                        if(isTreated){
                           val lastTreatedPieceIndex =  currentMoveIndex - 1

                            prePiece = listOfMoves[lastTreatedPieceIndex].piece

                            currentPosition = listOfMoves[lastTreatedPieceIndex].from
                            previousPosition = listOfMoves[lastTreatedPieceIndex].to

                            board[currentPosition.y][currentPosition.x] = null
                            board[previousPosition.y][previousPosition.x] = prePiece
                        }


                        currentPieceTurn = prePiece
                        currentMoves = emptyList()

                        turnColor = if (prePiece!!.p_color == PieceColor.WHITE) {
                            BoardColors.lightSquare
                        } else {
                            Color.Black
                        }

                    }
                },

                colors = ButtonDefaults.buttonColors(
                    backgroundColor = /*if !(currentMoveIndex > 0) Color.LightGray else */Color.Gray,
                    contentColor = Color.White
                )
            ) {
                Text("Back")
            }

            Button(
                modifier = Modifier.padding(10.dp, 0.dp),
                onClick = {
                    if(currentMoveIndex < listOfMoves.size){
                        ++currentMoveIndex

                        nextPiece = listOfMoves[currentMoveIndex].piece

                        currentPosition = listOfMoves[currentMoveIndex].to
                        previousPosition = listOfMoves[currentMoveIndex].from

                        val isTreated = listOfMoves[currentMoveIndex].isTreated

                        board[currentPosition.y][currentPosition.x] = nextPiece
                        board[previousPosition.y][previousPosition.x] = null

                       if(isTreated){
                          val lastTreatedPieceIndex =  currentMoveIndex + 1

                          nextPiece = listOfMoves[lastTreatedPieceIndex].piece

                          previousPosition = listOfMoves[lastTreatedPieceIndex].from
                          currentPosition = listOfMoves[lastTreatedPieceIndex].to

                          board[currentPosition.y][currentPosition.x] = nextPiece
                          board[previousPosition.y][previousPosition.x] = null
                      }


                      currentPieceTurn = nextPiece
                      currentMoves = emptyList()

                      turnColor = if (prePiece!!.p_color == PieceColor.WHITE) {
                          BoardColors.lightSquare
                      } else {
                          Color.Black
                      }
                   }
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = /*if !(currentMoveIndex < listOfMoves.size - 1) Color.LightGray else */Color.Gray,
                    contentColor = Color.White
                )
            ) {
                Text("Next")
            }
        }
    }
}

@Composable
fun MovesView(move: String) {
    Text(
        move,
        modifier = Modifier
            .padding(10.dp)
    )
}
