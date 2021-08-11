package io.kotest.assertions.arrow.core

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import io.kotest.property.Arb
import io.kotest.property.arbitrary.choice
import io.kotest.property.arbitrary.map
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * smart casts to [Either.Right] and fails with [failureMessage] otherwise.
 * ```kotlin
 * import arrow.core.Either.Right
 * import arrow.core.Either
 * import io.kotest.assertions.arrow.core.shouldBeRight
 *
 * fun main() {
 *   //sampleStart
 *   fun squared(i: Int): Int = i * i
 *   val result = squared(5)
 *   val either = Either.conditionally(result == 25, { result }, { 25 })
 *   val a = either.shouldBeRight { "5 * 5 == 25 but was $it " }
 *   val smartCasted: Right<Int> = either
 *   //sampleEnd
 *   println(smartCasted)
 * }
 * ```
 */
@OptIn(ExperimentalContracts::class)
public fun <A, B> Either<A, B>.shouldBeRight(failureMessage: (A) -> String = { "Expected Either.Right, but found Either.Left with value $it" }): B {
  contract {
    returns() implies (this@shouldBeRight is Either.Right<B>)
  }
  return when (this) {
    is Either.Right -> value
    is Either.Left -> throw AssertionError(failureMessage(value))
  }
}

/**
 * smart casts to [Either.Left] and fails with [failureMessage] otherwise.
 * ```kotlin
 * import arrow.core.Either.Left
 * import arrow.core.Either
 * import io.kotest.assertions.arrow.core.shouldBeLeft
 *
 * fun main() {
 *   //sampleStart
 *   val either = Either.conditionally(false, { "Always false" }, { throw RuntimeException("Will never execute") })
 *   val a = either.shouldBeLeft()
 *   val smartCasted: Left<String> = either
 *   //sampleEnd
 *   println(smartCasted)
 * }
 * ```
 */
@OptIn(ExperimentalContracts::class)
public fun <A, B> Either<A, B>.shouldBeLeft(failureMessage: (B) -> String = { "Expected Either.Left, but found Either.Right with value $it" }): A {
  contract {
    returns() implies (this@shouldBeLeft is Either.Left<A>)
  }
  return when (this) {
    is Either.Left -> value
    is Either.Right -> throw AssertionError(failureMessage(value))
  }
}

public fun <A, B> Arb.Companion.either(left: Arb<A>, right: Arb<B>): Arb<Either<A, B>> =
  choice(left.map(::Left), right.map(::Right))