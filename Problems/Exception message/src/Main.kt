fun main() {
    // ...
    try {
        throw RuntimeException()
    } catch (e: Exception) {
        println(e.message)
    }
// ...

}