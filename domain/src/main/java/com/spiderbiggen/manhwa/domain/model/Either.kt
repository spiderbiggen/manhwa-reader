package com.spiderbiggen.manhwa.domain.model

sealed interface Either<L, R> {
    data class Left<L, R>(val left: L) : Either<L, R>
    data class Right<L, R>(val right: R) : Either<L, R>
}

inline fun <L, O, R> Either<L, R>.mapLeft(block: (L) -> O): Either<O, R> =
    when (this) {
        is Either.Left -> Either.Left(block(left))
        is Either.Right -> Either.Right(right)
    }

inline fun <L, O, R> Either<L, R>.andThenLeft(block: (L) -> Either<O, R>): Either<O, R> =
    when (this) {
        is Either.Left -> block(left)
        is Either.Right -> Either.Right(right)
    }

fun <L, O, R> Either<L, R>.andLeft(other: Either<O, R>): Either<Pair<L,O>, R> =
    when (this) {
        is Either.Left -> when (other) {
            is Either.Left -> Either.Left(left to other.left)
            is Either.Right -> Either.Right(other.right)
        }
        is Either.Right -> Either.Right(right)
    }

fun <L, R> Either<L, R>.leftOr(default: L): L =
    when (this) {
        is Either.Left -> left
        is Either.Right -> default
    }

inline fun <L, R> Either<L, R>.leftOrElse(block: (R) -> L): L =
    when (this) {
        is Either.Left -> left
        is Either.Right -> block(right)
    }

inline fun <L, R> Either<L, R>.leftFlip(block: (L) -> R): R =
    when (this) {
        is Either.Left -> block(left)
        is Either.Right -> right
    }

inline fun <L, R, O> Either<L, R>.mapRight(block: (R) -> O): Either<L, O> =
    when (this) {
        is Either.Left -> Either.Left(left)
        is Either.Right -> Either.Right(block(right))
    }

fun <L, R> Either<L, R>.rightOr(default: R): R =
    when (this) {
        is Either.Left -> default
        is Either.Right -> right
    }

inline fun <L, R> Either<L, R>.rightOrElse(block: () -> R): R =
    when (this) {
        is Either.Left -> block()
        is Either.Right -> right
    }

inline fun <L, R> Either<L, R>.rightFlip(block: (R) -> L): L =
    when (this) {
        is Either.Left -> left
        is Either.Right -> block(right)
    }
