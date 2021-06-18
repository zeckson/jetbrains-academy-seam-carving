fun main() {
    val input = readLine()!!.split(" ");
    val left = input[0].toLong();
    val op = input[1]
    val right = input[2].toLong()

    println(when (op) {
        "+" -> left + right
        "-" -> left - right
        "*" -> left * right
        "/" -> if (right == 0L) "Division by 0!" else left / right
        else -> "Unknown operator"
    })
}
