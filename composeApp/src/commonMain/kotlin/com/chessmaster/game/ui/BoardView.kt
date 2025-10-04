package com.chessmaster.game.ui

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
import chessgame.composeapp.generated.resources.Res
import chessgame.composeapp.generated.resources.reset
import co.touchlab.kermit.Logger
import com.chessmaster.game.logic.Move1
import com.chessmaster.game.logic.Piece
import com.chessmaster.game.logic.PieceColor
import com.chessmaster.game.logic.Position
import com.chessmaster.game.logic.WHITE_INITIAL_POSITION
import com.chessmaster.game.logic.getMoveName
import com.chessmaster.game.logic.p_movement
import com.chessmaster.game.values.BoardColors
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

    var currentMoves by remember { mutableStateOf<List<Move1>>(emptyList()) }
    var moveCnt by remember { mutableStateOf(0) }

    var currentMoveIndex by remember { mutableStateOf(0) }
    var currentBackNextIndex by remember { mutableStateOf(0) }

    var listOfMoves by remember { mutableStateOf<ArrayList<Move1>>(arrayListOf()) }


    var turnColor by remember { mutableStateOf(BoardColors.lightSquare) }
    var pieceTurnColor by remember { mutableStateOf<PieceColor>(PieceColor.WHITE) }

    var squareStatusColor by remember { mutableStateOf(BoardColors.lightSquare) }

    var yState by remember { mutableStateOf(0) }
    var xState by remember { mutableStateOf(0) }


    Column {
        for (y in 0 until 8) {
            Row {
                for (x in 0 until 8) {
                    val blackColor = y % 2 == x % 2
                    var color = if (blackColor) {
                        BoardColors.brownDarkSquare
                    } else {
                        BoardColors.brownLightSquare
                    }

                    //numbers
                    val chessBoardY = 8 - y
                    val chessBoardX = 'a'

                    yState = y
                    xState = x


                    val currentPiece = board[yState][xState]

                    if ((selectedPiece != null) &&
                            (lastPieceClick.y == yState && lastPieceClick.x == xState)) {
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
                            color = Color.White,
                            fontSize = 10.sp
                        )
                        if (y == 7) Text(
                            "${chessBoardX + x}",
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(3.dp),
                            color = Color.White,
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
                                        if (pieceTurnColor == piece.p_color) {
                                            if(listOfMoves.isEmpty()){
                                                currentMoves = p_movement(piece, Position(y, x), board , null)
                                            }else{
                                                if(currentMoveIndex >= 1){
                                                    currentMoves = p_movement(piece, Position(y, x), board , listOfMoves[currentMoveIndex - 1])
                                                }else{
                                                    currentMoves = p_movement(piece, Position(y, x), board , listOfMoves[currentMoveIndex])
                                                }
                                            }
                                        }
                                    }
                            )
                        }

                        if (currentMoves.isNotEmpty()) {
                            currentMoves.forEach { move ->
                                if(
                                    (move.to.y == y && move.to.x == x) &&
                                    (currentMoveIndex == currentBackNextIndex)
                                ){
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .background(BoardColors.hintMoveColor)
                                            .align(Alignment.Center)
                                            .clickable {

                                                previousPosition = Position( lastPieceClick.y , lastPieceClick.x)
                                                currentPosition = Position(move.to.y, move.to.x)


                                                if(selectedPiece != null){
                                                   if (selectedPiece!!.p_color == PieceColor.WHITE) {
                                                        turnColor =  Color.Black
                                                        pieceTurnColor = PieceColor.BLACK
                                                    } else {
                                                        turnColor = BoardColors.lightSquare
                                                        pieceTurnColor = PieceColor.WHITE
                                                    }
                                                }

                                                board[lastPieceClick.y][lastPieceClick.x] = null
                                                if(move.captureSquare != null){
                                                    board[move.captureSquare.y][move.captureSquare.x] = null
                                                }
                                                board[move.to.y][move.to.x] = selectedPiece


                                                listOfMoves.add(
                                                    currentMoveIndex,
                                                    Move1(
                                                        selectedPiece!!,
                                                        Position(lastPieceClick.y, lastPieceClick.x),
                                                        Position(move.to.y, move.to.x),
                                                        move.isCapture ,
                                                        move.captureSquare
                                                    )
                                                )

                                                ++currentMoveIndex
                                                ++currentBackNextIndex
                                                if ( pieceTurnColor == PieceColor.WHITE){
                                                    ++moveCnt
                                                }

                                                val newMove = "$moves ${getMoveName(selectedPiece!!, chessBoardY, chessBoardX + x, chessBoardX + lastPieceClick.x, move.isCapture, moveCnt)}"
                                                Logger.i("CurrentMoveIndex : " + currentMoveIndex)

                                                //onMoveChange("CurrentMoveIndex : $currentMoveIndex")
                                                onMoveChange(newMove)


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
                .background(turnColor) //box of turn color
                .align(Alignment.TopEnd)
                .padding(16.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth(),
             horizontalArrangement = Arrangement.Center
        ) {


            Button(
                modifier = Modifier.padding(10.dp, 0.dp),
                onClick = {
                    if(currentBackNextIndex >= 1){
                        --currentBackNextIndex

                        Logger.i("Back Trace : currentBackNextIndex "  + currentBackNextIndex)
                        Logger.i("currentMoveIndex : "  + currentMoveIndex)

                        val prePiece = listOfMoves[currentBackNextIndex].piece

                        currentPosition = listOfMoves[currentBackNextIndex].from
                        previousPosition = listOfMoves[currentBackNextIndex].to

                        val isTreated = listOfMoves[currentBackNextIndex].isCapture

                        board[currentPosition.y][currentPosition.x] =  prePiece
                        board[previousPosition.y][previousPosition.x] = null

                        if(isTreated){
                            val lastTreatedPieceIndex =  currentBackNextIndex - 1

                            val caputredPiece = listOfMoves[lastTreatedPieceIndex].piece

                            currentPosition = listOfMoves[lastTreatedPieceIndex].from
                            previousPosition = listOfMoves[lastTreatedPieceIndex].to

                            board[currentPosition.y][currentPosition.x] = null
                            board[previousPosition.y][previousPosition.x] = caputredPiece

                        }



                        pieceTurnColor = prePiece.p_color
                        turnColor = if (prePiece.p_color == PieceColor.WHITE) {
                            BoardColors.lightSquare
                        } else {
                            Color.Black
                        }

                        currentMoves = emptyList()
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
                    if(currentBackNextIndex < listOfMoves.size){

                        val nextPiece = listOfMoves[currentBackNextIndex].piece

                        currentPosition = listOfMoves[currentBackNextIndex].to
                        previousPosition = listOfMoves[currentBackNextIndex].from

                        board[currentPosition.y][currentPosition.x] = nextPiece
                        board[previousPosition.y][previousPosition.x] = null

                        if(listOfMoves[currentBackNextIndex].captureSquare != null){
                            board[listOfMoves[currentBackNextIndex].captureSquare!!.y][listOfMoves[currentBackNextIndex].captureSquare!!.x] = null
                        }


                         if (nextPiece.p_color == PieceColor.WHITE) {
                            turnColor = Color.Black
                            pieceTurnColor = PieceColor.BLACK
                        } else {
                            turnColor = BoardColors.lightSquare
                             pieceTurnColor = PieceColor.WHITE
                        }

                        currentMoves = emptyList()
                        ++currentBackNextIndex

                        Logger.i("Next Trace : currentBackNextIndex " + currentBackNextIndex)
                        Logger.i("currentMoveIndex " + currentMoveIndex)
                        Logger.i("List Size " + listOfMoves.size)
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

        Image(
            painterResource(Res.drawable.reset),
            contentDescription = "",
            modifier = Modifier
                .size(39.dp)
                .clickable {

                    var resetedBoard = mutableListOf(
                        mutableListOf("BR0", "BN2", "BB3", "BQ4", "BK5", "BB6", "BN7", "BR8").map { Piece.p_typeFromId(it) }.toMutableList(),
                        mutableListOf("BP0", "BP2", "BP3", "BP4", "BP5", "BP6", "BP7", "BP8").map { Piece.p_typeFromId(it) }.toMutableList(),
                        mutableListOf(null, null, null, null, null, null, null, null).map { Piece.p_typeFromId(it) }.toMutableList(),
                        mutableListOf(null, null, null, null, null, null, null, null).map { Piece.p_typeFromId(it) }.toMutableList(),
                        mutableListOf(null, null, null, null, null, null, null, null).map { Piece.p_typeFromId(it) }.toMutableList(),
                        mutableListOf(null, null, null, null, null, null, null, null).map { Piece.p_typeFromId(it) }.toMutableList(),
                        mutableListOf("WP0", "WP2", "WP3", "WP4", "WP5", "WP6", "WP7", "WP8").map { Piece.p_typeFromId(it) }.toMutableList(),
                        mutableListOf("WR0", "WN2", "WB3", "WQ4", "WK5", "WB6", "WN7", "WR8").map { Piece.p_typeFromId(it) }.toMutableList()
                    )
                    board = resetedBoard

                    selectedPiece = null
                    lastPieceClick  = Position(-1, -1)
                    previousPosition = Position(-1, -1)
                    currentPosition = Position(-1, -1)

                    currentMoves = emptyList()
                    moveCnt = 0

                    currentMoveIndex  = 0
                    currentBackNextIndex = 0

                    listOfMoves = arrayListOf()


                    turnColor =  BoardColors.lightSquare
                    pieceTurnColor = PieceColor.WHITE

                    squareStatusColor = BoardColors.lightSquare

                    yState = 0
                    xState = 0

                    onMoveChange("")
                }
        )
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


@Composable
fun CenterAlignedTopAppBarExample() {

}
