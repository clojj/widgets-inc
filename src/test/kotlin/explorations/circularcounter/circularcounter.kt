package explorations.circularcounter

import java.util.concurrent.atomic.AtomicInteger

fun main() {

    val list = listOf("111", "222", "333", "444", "555")
    val circularCounter = AtomicInteger(0)

    fun next(): Int = circularCounter.updateAndGet { if (it == list.size - 1) 0 else it + 1 }
    for (i in 1..4) {
        Thread {
            repeat(20) { /*Thread.sleep(2); */ println("${System.nanoTime()} ${Thread.currentThread().id} ${list[next()]}") }
        }.start()
    }
}

