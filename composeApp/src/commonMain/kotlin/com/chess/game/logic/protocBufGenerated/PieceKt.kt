//Generated by the protocol buffer compiler. DO NOT EDIT!
// source: piece.proto

package protocBufGenerated;

@kotlin.jvm.JvmName("-initializepiece")
public inline fun piece(block: protocBufGenerated.PieceKt.Dsl.() -> kotlin.Unit): protocBufGenerated.Piece =
  protocBufGenerated.PieceKt.Dsl._create(protocBufGenerated.Piece.newBuilder()).apply { block() }._build()
public object PieceKt {
  @kotlin.OptIn(com.google.protobuf.kotlin.OnlyForUseByGeneratedProtoCode::class)
  @com.google.protobuf.kotlin.ProtoDslMarker
  public class Dsl private constructor(
    private val _builder: protocBufGenerated.Piece.Builder
  ) {
    public companion object {
      @kotlin.jvm.JvmSynthetic
      @kotlin.PublishedApi
      internal fun _create(builder: protocBufGenerated.Piece.Builder): Dsl = Dsl(builder)
    }

    @kotlin.jvm.JvmSynthetic
    @kotlin.PublishedApi
    internal fun _build(): protocBufGenerated.Piece = _builder.build()

    /**
     * <code>required string p_id = 1;</code>
     */
    public var pId: kotlin.String
      @JvmName("getPId")
      get() = _builder.getPId()
      @JvmName("setPId")
      set(value) {
        _builder.setPId(value)
      }
    /**
     * <code>required string p_id = 1;</code>
     */
    public fun clearPId() {
      _builder.clearPId()
    }
    /**
     * <code>required string p_id = 1;</code>
     * @return Whether the pId field is set.
     */
    public fun hasPId(): kotlin.Boolean {
      return _builder.hasPId()
    }
  }
}
@kotlin.jvm.JvmSynthetic
public inline fun protocBufGenerated.Piece.copy(block: protocBufGenerated.PieceKt.Dsl.() -> kotlin.Unit): protocBufGenerated.Piece =
  protocBufGenerated.PieceKt.Dsl._create(this.toBuilder()).apply { block() }._build()

