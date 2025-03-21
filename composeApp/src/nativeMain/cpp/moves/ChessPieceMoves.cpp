//
// Created by itsme on 3/20/25.
//

#include "ChessPieceMoves.h"

Piece::Piece(std::string name, PieceType type, PieceColor color) : pieceName(name) , pieceType(type) , pieceColor(color) {}


std::string Piece::getPieceName() const {return pieceName;}
PieceType Piece::getPieceType() const {return pieceType; }
PieceColor Piece::getPieceColor() const {return pieceColor;}

