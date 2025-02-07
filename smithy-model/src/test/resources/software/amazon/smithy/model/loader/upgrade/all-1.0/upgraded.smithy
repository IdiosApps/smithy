$version: "2.0"

namespace smithy.example

structure Bytes {
    nullable: Byte,

    @default(0)
    nonNull: PrimitiveByte,

    nullable2: PrimitiveByte,
}

structure Shorts {
    nullable: Short,

    @default(0)
    nonNull: PrimitiveShort,

    nullable2: PrimitiveShort,
}

structure Integers {
    nullable: Integer,

    @default(0)
    nonNull: PrimitiveInteger,

    @default(0)
    @range(min: 0, max: 1)
    ranged: PrimitiveInteger,

    nullable2: PrimitiveInteger
}

structure BlobPayload {
    @default("")
    payload: StreamingBlob
}

@streaming
blob StreamingBlob
