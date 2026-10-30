package com.example.myapplication.support

import org.junit.Assert.fail

/** Exige que [block] lance [E] y devuelve la excepción para poder revisar su detalle. */
suspend inline fun <reified E : Throwable> expectFailure(block: () -> Unit): E {
    try {
        block()
    } catch (e: Throwable) {
        if (e is E) return e
        throw AssertionError("Se esperaba ${E::class.java.simpleName} pero se lanzó ${e.javaClass.simpleName}: ${e.message}", e)
    }
    fail("Se esperaba ${E::class.java.simpleName} pero la operación terminó sin error")
    throw IllegalStateException("inalcanzable")
}
