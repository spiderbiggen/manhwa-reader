package com.spiderbiggen.manhwa.domain.model

sealed interface Either<L, R> {
    data class Left<L, R>(val left: L) : Either<L, R>
    data class Right<L, R>(val right: R) : Either<L, R>
}

inline fun <L, O, R> Either<L, R>.mapLeft(crossinline block: (L) -> O): Either<O, R> =
    when (this) {
        is Either.Left -> Either.Left(block(this.left))
        is Either.Right -> Either.Right(this.right)
    }

fun <L, R> Either<L, R>.leftOr(default: L): L =
    when (this) {
        is Either.Left -> this.left
        is Either.Right -> default
    }

inline fun <L, R> Either<L, R>.leftOrElse(crossinline block: () -> L): L =
    when (this) {
        is Either.Left -> this.left
        is Either.Right -> block()
    }

inline fun <L, R> Either<L, R>.leftFlip(crossinline block: (L) -> R): R =
    when (this) {
        is Either.Left -> block(this.left)
        is Either.Right -> this.right
    }

inline fun <L, R, O> Either<L, R>.mapRight(crossinline block: (R) -> O): Either<L, O> =
    when (this) {
        is Either.Left -> Either.Left(this.left)
        is Either.Right -> Either.Right(block(this.right))
    }

fun <L, R> Either<L, R>.rightOr(default: R): R =
    when (this) {
        is Either.Left -> default
        is Either.Right -> this.right
    }

inline fun <L, R> Either<L, R>.rightOrElse(crossinline block: () -> R): R =
    when (this) {
        is Either.Left -> block()
        is Either.Right -> this.right
    }

inline fun <L, R> Either<L, R>.rightFlip(crossinline block: (R) -> L): L =
    when (this) {
        is Either.Left -> this.left
        is Either.Right -> block(this.right)
    }
