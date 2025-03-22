package com.chess.game.logic

import chessgame.composeapp.generated.resources.*
import org.jetbrains.compose.resources.DrawableResource

data class Piece(
    var p_id: String,
    var p_type: PieceType,
    var p_color: PieceColor,
) {


    companion object {
        fun p_typeFromId(p_id: String?): Piece? {
            if (p_id == null) return null
            else {
                val piece = p_fromId(p_id)
                return Piece(p_id, piece.first, piece.second)
            }
        }

        fun p_image(p_type: PieceType?, p_color: PieceColor): DrawableResource {
            return when (p_type) {
                PieceType.PAWN -> if (p_color is PieceColor.WHITE) Res.drawable.w_pawn else Res.drawable.b_pawn
                PieceType.KNIGHT -> if (p_color is PieceColor.WHITE) Res.drawable.w_knight else Res.drawable.b_knight
                PieceType.BISHOP -> if (p_color is PieceColor.WHITE) Res.drawable.w_bishop else Res.drawable.b_bishop
                PieceType.ROOK -> if (p_color is PieceColor.WHITE) Res.drawable.w_rook else Res.drawable.b_rook
                PieceType.QUEEN -> if (p_color is PieceColor.WHITE) Res.drawable.w_queen else Res.drawable.b_queen
                PieceType.KING -> if (p_color is PieceColor.WHITE) Res.drawable.w_king else Res.drawable.b_king
                null -> throw IllegalStateException("The Piece Type not valid")
            }
        }
    }

}

sealed class PieceType(val value: Int) {
    object PAWN : PieceType(1)
    object KNIGHT : PieceType(3)
    object BISHOP : PieceType(3)
    object ROOK : PieceType(5)
    object QUEEN : PieceType(9)
    object KING : PieceType(0)

    override fun toString(): String {
        return this::class.simpleName ?:"Unknown"
    }
}

sealed class PieceColor {
    object WHITE : PieceColor()
    object BLACK : PieceColor()

    override fun toString(): String {
        return this::class.simpleName ?: "Unknown"
    }
}

private fun p_fromId(p_id: String): Pair<PieceType, PieceColor> {
    val p_id = p_id.toCharArray()

    if (p_id.size != 3) throw IllegalStateException("ID Should have 3 character")

    val p_color = if (p_id[0] == 'W') PieceColor.WHITE else PieceColor.BLACK
    val p_type = when (p_id[1]) {
        'P' -> PieceType.PAWN
        'N' -> PieceType.KNIGHT
        'B' -> PieceType.BISHOP
        'R' -> PieceType.ROOK
        'Q' -> PieceType.QUEEN
        'K' -> PieceType.KING
        else -> {
            throw IllegalStateException("This Type not Found")
        }
    }

    return p_type to p_color
}

var WHITE_INITIAL_POSITION = mutableListOf(
    mutableListOf("BR1", "BN2", "BB3", "BQ4", "BK5", "BB6", "BN7", "BR8").map { Piece.p_typeFromId(it) }.toMutableList(),
    mutableListOf("BP1", "BP2", "BP3", "BP4", "BP5", "BP6", "BP7", "BP8").map { Piece.p_typeFromId(it) }.toMutableList(),
    mutableListOf(null, null, null, null, null, null, null, null).map { Piece.p_typeFromId(it) }.toMutableList(),
    mutableListOf(null, null, null, null, null, null, null, null).map { Piece.p_typeFromId(it) }.toMutableList(),
    mutableListOf(null, null, null, null, null, null, null, null).map { Piece.p_typeFromId(it) }.toMutableList(),
    mutableListOf(null, null, null, null, null, null, null, null).map { Piece.p_typeFromId(it) }.toMutableList(),
    mutableListOf("WP1", "WP2", "WP3", "WP4", "WP5", "WP6", "WP7", "WP8").map { Piece.p_typeFromId(it) }.toMutableList(),
    mutableListOf("WR1", "WN2", "WB3", "WQ4", "WK5", "WB6", "WN7", "WR8").map { Piece.p_typeFromId(it) }.toMutableList()
)

data class Position(val y: Int, val x: Int)

fun p_movement(current_p: Piece, currentPosition: Position, board: MutableList<MutableList<Piece?>>): ArrayList<Pair<Position , Boolean>> {
    var listOfPositions: ArrayList<Pair<Position , Boolean>> = ArrayList()
    val pChar = current_p.p_id.toCharArray()

    listOfPositions =
        when (pChar[1]) {
        'P' -> pawnMove(current_p, currentPosition, board)
        'K'-> kingMove(current_p, currentPosition, board)
//        'Q'-> queenMove(current_p, currentPosition, board)
//        'R'-> rookMove(current_p, currentPosition, board)
//        'N'-> knightMove(current_p, currentPosition, board)
//        'B'-> bishopMove(current_p, currentPosition, board)
        null -> throw IllegalStateException("The Piece Type not valid")

        else -> {
            return ArrayList()
        }
    }
    return listOfPositions
}

private fun pawnMove(pawn: Piece, currentPosition: Position, board: MutableList<MutableList<Piece?>>): ArrayList<Pair<Position , Boolean>> {
    var listOfPositions: ArrayList<Pair<Position , Boolean>> = ArrayList()
    val b_defaultPosition = currentPosition.y == 1
    val w_defaultPosition = currentPosition.y == 6

    if (pawn.p_color is PieceColor.WHITE) {

        //White
        if(currentPosition.y >= 1){
            if (board[currentPosition.y - 1][currentPosition.x] == null) {
                listOfPositions.add(Pair(Position(currentPosition.y - 1, currentPosition.x) , false))
            }
            if (w_defaultPosition && board[currentPosition.y - 2][currentPosition.x] == null && board[currentPosition.y - 1][currentPosition.x] == null) {
                listOfPositions.add(Pair(Position(currentPosition.y - 2, currentPosition.x) ,false))
            }
            listOfPositions.addAll((pawnThreats(pawn, currentPosition, board)))
        }

    } else {
        //Black
        if(currentPosition.y <= 6 ){
            if (board[currentPosition.y + 1][currentPosition.x] == null) {
                listOfPositions.add(Pair(Position(currentPosition.y + 1, currentPosition.x) , false))
            }
            if (b_defaultPosition && board[currentPosition.y + 2][currentPosition.x] == null && board[currentPosition.y + 1][currentPosition.x] == null) {
                listOfPositions.add(Pair(Position(currentPosition.y + 2, currentPosition.x) , false))
            }
            listOfPositions.addAll((pawnThreats(pawn, currentPosition, board)))
        }
    }
    return listOfPositions
}

private fun pawnThreats(piece: Piece, currentPosition: Position, board: MutableList<MutableList<Piece?>>):ArrayList<Pair<Position , Boolean>>  {
    var listOfPositions: ArrayList<Pair<Position , Boolean>>  = ArrayList()
    //
    if (piece.p_color == PieceColor.WHITE) {
        //leftPiece
        if(currentPosition.x >= 1){
            val threadLeftPiece = board[currentPosition.y - 1][currentPosition.x - 1]
            if (threadLeftPiece != null && threadLeftPiece.p_color == PieceColor.BLACK) {
                listOfPositions.add(Pair(Position(currentPosition.y - 1, currentPosition.x - 1) , true))
            }
        }

        //rightPiece
        if(currentPosition.x <= 6){
            val threadRightPiece = board[currentPosition.y - 1][currentPosition.x + 1]
            if (threadRightPiece != null && threadRightPiece.p_color == PieceColor.BLACK) {
                listOfPositions.add(Pair(Position(currentPosition.y - 1, currentPosition.x + 1), true))
            }
        }
    } else {

        //leftPiece
        if(currentPosition.x >= 1 ){
            val threadLeftPiece = board[currentPosition.y + 1][currentPosition.x - 1]
            if (threadLeftPiece != null && threadLeftPiece.p_color == PieceColor.WHITE) {
                listOfPositions.add(Pair(Position(currentPosition.y + 1, currentPosition.x - 1), true))
            }
        }

        //rightPiece
        if(currentPosition.x <= 6) {
            val threadRightPiece = board[currentPosition.y + 1][currentPosition.x + 1]
            if (threadRightPiece != null && threadRightPiece.p_color == PieceColor.WHITE) {
                listOfPositions.add(Pair(Position(currentPosition.y + 1, currentPosition.x + 1) , true))
            }
        }
    }
    return listOfPositions
}

private fun kingMove(king: Piece, currentPosition: Position, board: MutableList<MutableList<Piece?>>): ArrayList<Pair<Position , Boolean>> {

    var listOfPositions: ArrayList<Pair<Position, Boolean>> = ArrayList()

    val b_defaultPosition = currentPosition.y == 0
    val w_defaultPosition = currentPosition.y == 7

    var listOfMovesCases: ArrayList<Position> = ArrayList()

    if (king.p_color is PieceColor.BLACK) {

        listOfMovesCases.add(Position(currentPosition.y, currentPosition.x + 1))
        listOfMovesCases.add(Position(currentPosition.y, currentPosition.x - 1))
        listOfMovesCases.add(Position(currentPosition.y + 1, currentPosition.x))
        listOfMovesCases.add( Position( currentPosition.y + 1, currentPosition.x + 1))
        listOfMovesCases.add( Position( currentPosition.y + 1, currentPosition.x - 1))

        if (!b_defaultPosition) {
            listOfMovesCases.add(Position(currentPosition.y - 1, currentPosition.x))
            listOfMovesCases.add(Position(currentPosition.y - 1, currentPosition.x + 1))
            listOfMovesCases.add(Position(currentPosition.y - 1, currentPosition.x - 1))
        }

    } else {

        listOfMovesCases.add(Position(currentPosition.y, currentPosition.x + 1))
        listOfMovesCases.add(Position(currentPosition.y, currentPosition.x - 1))
        listOfMovesCases.add(Position(currentPosition.y - 1, currentPosition.x))
        listOfMovesCases.add( Position( currentPosition.y - 1, currentPosition.x + 1))
        listOfMovesCases.add( Position( currentPosition.y - 1, currentPosition.x - 1))

        if (!w_defaultPosition) {
            listOfMovesCases.add(Position(currentPosition.y + 1, currentPosition.x))
            listOfMovesCases.add(Position(currentPosition.y + 1, currentPosition.x + 1))
            listOfMovesCases.add(Position(currentPosition.y + 1, currentPosition.x - 1))
        }

    }


    var list: ArrayList<Piece?> = ArrayList()

    for (position in listOfMovesCases) {
        list.add(board[position.y][position.x])
    }


    for (i in 0 until list.size) {
        if (list[i] == null) {
            listOfPositions.add(Pair(listOfMovesCases[i], false))
        }else if (list[i] != null && king.p_color != list[i]!!.p_color){
            listOfPositions.add(Pair(listOfMovesCases[i], true))
        }
    }


    return listOfPositions
}


private fun kingthreads(){

}
//
//private fun knightMove(pawn: Piece, currentPosition: Position, board: MutableList<MutableList<Piece?>>): ArrayList<Pair<Position , Boolean>> {
//
//}
//private fun knightThreads(){
//
//}
//
//private fun rookMove(pawn: Piece, currentPosition: Position, board: MutableList<MutableList<Piece?>>): ArrayList<Pair<Position , Boolean>> {
//
//}
//
//private fun rookThreads(){
//
//}
//
//private fun queenMove(pawn: Piece, currentPosition: Position, board: MutableList<MutableList<Piece?>>): ArrayList<Pair<Position , Boolean>> {
//
//}
//private fun queenThreads(){
//
//}
//
//private fun bishopMove(pawn: Piece, currentPosition: Position, board: MutableList<MutableList<Piece?>>): ArrayList<Pair<Position , Boolean>> {
//
//}
//
//private fun bishopThreads(){
//
//}


data class Move(
    val piece: Piece,
    val from: Position,
    val to: Position,
    var isTreated: Boolean = false
)

fun getMoveName(piece : Piece , y : Int , x : Char, preX : Char , isThreat : Boolean , cnt : Int) : String{
    var isWhite = piece.p_color == PieceColor.WHITE
    var moveCnt = if(isWhite)  " $cnt - " else ""
    return when (piece.p_type) {
        PieceType.PAWN -> moveCnt + if (!isThreat) "$x$y" else "${preX}x$x$y"
        PieceType.KNIGHT -> moveCnt + "N$x$y"
        PieceType.BISHOP -> moveCnt + "B$x$y"
        PieceType.ROOK -> moveCnt + "R$x$y"
        PieceType.QUEEN -> moveCnt + "Q$x$y"
        PieceType.KING -> moveCnt + "K$x$y"
        null -> throw IllegalStateException("The Piece Type not valid")
    }
}

//cpp functions
expect fun cpp_pawnMove(pawn: Piece, currentPosition: Position, board: MutableList<MutableList<Piece?>>): ArrayList<Pair<Position , Boolean>>

expect fun convertPieceModelToNative(piece: Piece):String

