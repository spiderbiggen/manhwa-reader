package com.spiderbiggen.manga.domain.model

sealed interface Either<L, R> {
    @JvmInline
    value class Left<L, R>(val value: L) : Either<L, R>

    @JvmInline
    value class Right<L, R>(val value: R) : Either<L, R>
}

inline fun <L, R, T> Either<L, R>.fold(ifLeft: (L) -> T, ifRight: (R) -> T): T = when (this) {
    is Either.Left -> ifLeft(value)
    is Either.Right -> ifRight(value)
}

inline fun <L, O, R> Either<L, R>.mapLeft(block: (L) -> O): Either<O, R> = when (this) {
    is Either.Left -> Either.Left(block(value))
    is Either.Right -> Either.Right(value)
}

inline fun <L, O, R> Either<L, R>.andThenLeft(block: (L) -> Either<O, R>): Either<O, R> = when (this) {
    is Either.Left -> block(value)
    is Either.Right -> Either.Right(value)
}

fun <L, O, R> Either<L, R>.andLeft(other: Either<O, R>): Either<Pair<L, O>, R> = when (this) {
    is Either.Left -> when (other) {
        is Either.Left -> Either.Left(value to other.value)
        is Either.Right -> Either.Right(other.value)
    }

    is Either.Right -> Either.Right(value)
}

fun <L, R> Either<L, R>.leftOr(default: L): L = when (this) {
    is Either.Left -> value
    is Either.Right -> default
}

inline fun <L, R> Either<L, R>.leftOrElse(block: (R) -> L): L = when (this) {
    is Either.Left -> value
    is Either.Right -> block(value)
}

inline fun <L, R> Either<L, R>.onLeft(block: (L) -> Unit): Either<L, R> {
    if (this is Either.Left) block(value)
    return this
}

inline fun <L, R> Either<L, R>.leftFlip(block: (L) -> R): R = when (this) {
    is Either.Left -> block(value)
    is Either.Right -> value
}

inline fun <L, R, O> Either<L, R>.mapRight(block: (R) -> O): Either<L, O> = when (this) {
    is Either.Left -> Either.Left(value)
    is Either.Right -> Either.Right(block(value))
}

fun <L, R> Either<L, R>.rightOr(default: R): R = when (this) {
    is Either.Left -> default
    is Either.Right -> value
}

inline fun <L, R> Either<L, R>.rightOrElse(block: () -> R): R = when (this) {
    is Either.Left -> block()
    is Either.Right -> value
}

inline fun <L, R> Either<L, R>.onRight(block: (R) -> Unit): Either<L, R> {
    if (this is Either.Right) block(value)
    return this
}

inline fun <L, R> Either<L, R>.rightFlip(block: (R) -> L): L = when (this) {
    is Either.Left -> value
    is Either.Right -> block(value)
}
