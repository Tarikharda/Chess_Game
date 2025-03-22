package com.chess.game.logic

actual fun cpp_pawnMove(pawn: Piece, currentPosition: Position, board: MutableList<MutableList<Piece?>>): ArrayList<Pair<Position , Boolean>> {
    return JniChessHelper.pawnMove(convertPieceModelToNative(pawn) , currentPosition , board);

}

actual fun convertPieceModelToNative(piece:Piece) : String{
    return JniChessHelper.convertPieceModelToNative(piece.p_id ,  piece.p_type.toString() , piece.p_color.toString());
}

actual fun convertPositionToNative(currentPosition: Position) : String{
    return JniChessHelper.convertPieceModelToNative(piece.p_id ,  piece.p_type.toString() , piece.p_color.toString());
}



//JNI Wrapper
private object JniChessHelper {
    init {
        System.loadLibrary("chessGame")
    }
    // this who have reference in cpp code so i can using jni helper for it

    external fun pawnMove(pawn: String, currentPosition: Position, board: MutableList<MutableList<Piece?>>): ArrayList<Pair<Position , Boolean>>

    external fun convertPieceModelToNative(pieceId : String , pieceType : String , pieceColor : String): Pair<Int , Int>

    external fun convertPositionToNative(pieceId : String , pieceType : String , pieceColor : String): String
}