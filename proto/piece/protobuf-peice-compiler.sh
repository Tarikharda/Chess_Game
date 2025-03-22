cd $(dirname $0)

SRC_DIR=.
PIECE_DIR=../../composeApp/src/commonMain/kotlin/com/chess/game/logic
protoc -I=$SRC_DIR --kotlin_out=$PIECE_DIR $SRC_DIR/piece.proto
