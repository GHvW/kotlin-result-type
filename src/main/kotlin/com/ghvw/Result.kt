package com.ghvw

fun <T> tryOf(operation: () -> T): Result<T, Exception> =
        try { Ok(operation()) }
        catch (e: Exception) { Err(e) }

//out variance annotation makes T a covariant type parameter
sealed class Result<out T, out E>

data class Ok<T>(val value: T) : Result<T, Nothing>()
data class Err<E>(val error: E) : Result<Nothing, E>()



fun <T, U, E> Result<T, E>.map(transform: (T) -> U): Result<U, E> {
    return when(this) {
        is Ok -> Ok(transform(this.value))
        is Err -> Err(this.error)
    }
}

fun <T, E, F> Result<T, E>.mapErr(transform: (E) -> F): Result<T, F> {
    return when(this) {
        is Ok -> this
        is Err -> Err(transform(this.error))
    }
}

fun <T, E> Result<T, E>.isOk(): Boolean {
    return when(this) {
        is Ok -> true
        is Err -> false
    }
}

fun <T, E> Result<T, E>.isErr(): Boolean {
    return when(this) {
        is Ok -> false
        is Err -> true
    }
}

//ok() converts Result<T, E> to Option<T> in Rust. May need remove or redo this one to be more useful
fun <T, E> Result<T, E>.ok(): T? {
    return when(this) {
        is Ok -> this.value
        is Err -> null
    }
}

//err() converts Result<T, E> to Option<E> in Rust. May need to remove or redo this one to be more useful
fun <T, E> Result<T, E>.err(): E? {
    return when(this) {
        is Ok -> null
        is Err -> this.error
    }
}

//Rust = Result.iter()
fun <T, E> Result<T, E>.iterator(): Iterator<T>? {
    return when(this) {
        is Ok -> object : Iterator<T> {
            private var yielded = false

            override fun hasNext() = !yielded

            override fun next(): T {
                yielded = true
                return value
            }
        }
        is Err -> null
    }
}

fun <T, U, E> Result<T, E>.and(res: Result<U, E>): Result<U, E> {
    return when(this) {
        is Ok -> res
        is Err -> this
    }
}

fun <T, U, E> Result<T, E>.andThen(f: (T) -> Result<U, E>): Result<U, E> {
    return when(this) {
        is Ok -> f(this.value)
        is Err -> this
    }
}

//test heavily
fun <T, E, F> Result<T, E>.or(res: Result<T, F>): Result<T, F> {
    return when(this) {
        is Ok -> this
        is Err -> res
    }
}

fun <T, E, F> Result<T, E>.orElse(f: (E) -> Result<T, F>): Result<T, F> {
    return when(this) {
        is Ok -> this
        is Err -> f(this.error)
    }
}

//rename to something more Kotlin-y?
fun <T, E> Result<T, E>.unwrapOr(other: T): T {
    return when(this) {
        is Ok -> this.value
        is Err -> other
    }
}

//rename to something more Kotlin-y?
fun <T, E> Result<T, E>.unwrapOrElse(f: (E) -> T): T {
    return when(this) {
        is Ok -> this.value
        is Err -> f(this.error)
    }
}

fun <T, E: Exception> Result<T, E>.unwrap(): T {
    return when(this) {
        is Ok -> this.value
        is Err -> throw this.error
    }
}

//test
fun <T, E: Exception> Result<T, E>.expect(message: String): T {
    return when(this) {
        is Ok -> this.value
        is Err -> throw Exception("$message: $this.error")
    }
}

