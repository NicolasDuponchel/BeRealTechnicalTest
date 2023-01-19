package com.ndup.berealtechnicaltest.domain

sealed class Either<T>

data class Success<T>(val value: T): Either<T>()

data class Failure<T>(val error: Throwable): Either<T>()

