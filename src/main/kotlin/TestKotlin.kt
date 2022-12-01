// create main function
fun main(args: Array<String>) {

    val a = ArrayList<String>()
    a.add("a")
    a.add("b")
    a.add("c")
    a.add("d")

    val b = ArrayList<String>()
    b.add("a")
    b.add("b")
    b.add("f")
    b.add("d")

    println("${a.equals(b)}")
}