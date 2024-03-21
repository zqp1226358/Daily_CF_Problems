import kotlin.math.abs
import java.io.InputStream
import java.io.Writer
import java.io.OutputStreamWriter
import java.io.BufferedWriter
import java.io.OutputStream
import java.io.PrintWriter
import java.io.Closeable

open class FastReader(private val input: InputStream, bufCap: Int = 8192) {
    companion object { private const val NC = 0.toChar() }
    private val buf = ByteArray(bufCap)
    private var bId = 0
    private var size = 0
    private var c = NC
    private val char: Char
        get() {
            while (bId == size) { size = input.read(buf); if (size == -1) return NC; bId = 0 }
            return buf[bId++].toInt().toChar()
        }
    private fun isWhitespace(c: Char) = c.code !in 33..126
    private fun skip(): Char { var b: Char; while (char.also { b = it } != NC && isWhitespace(b)); return b }
    fun ns() = buildString {
        while (true) { c = char; if (!isWhitespace(c)) break }; append(c);
        while (true) { c = char; if (isWhitespace(c)) break; append(c) }
    }
    fun ns(n: Int): CharArray {
        val buf = CharArray(n); var (b, p) = skip() to 0
        while (p < n && !isWhitespace(b)) { buf[p++] = b;b = char }
        return if (n == p) buf else buf.copyOf(p)
    }
    fun ni(): Int {
        var neg = false; if (c == NC) c = char
        while (c < '0' || c > '9') { if (c == '-') neg = true;c = char }
        var res = 0; while (c in '0'..'9') { res = (res shl 3) + (res shl 1) + (c - '0'); c = char }
        return if (neg) -res else res
    }
    fun nl(): Long {
        var neg = false; if (c == NC) c = char
        while (c < '0' || c > '9') { if (c == '-') neg = true; c = char }
        var res = 0L; while (c in '0'..'9') { res = (res shl 3) + (res shl 1) + (c - '0'); c = char }
        return if (neg) -res else res
    }
    fun na(n: Int) = IntArray(n) { ni() }; fun nal(n: Int) = LongArray(n) { nl() }; fun nad(n: Int) = DoubleArray(n) { nd() }
    fun nm(n: Int, m: Int) = Array(n) { ns(m) }; fun nmi(n: Int, m: Int) = Array(n) { na(m) }
    fun nd() = ns().toDouble(); fun nc() = skip()
}
open class FastWriter : Closeable {
    private val writer: PrintWriter
    constructor(output: OutputStream, bufCap: Int = 8192) {
        this.writer = PrintWriter(BufferedWriter(OutputStreamWriter(output), bufCap))
    }
    constructor(writer: Writer, bufCap: Int = 8192) {
        this.writer = PrintWriter(BufferedWriter(writer, bufCap))
    }
    override fun close() { writer.flush(); try { writer.close() } catch (e: Exception) { e.printStackTrace() } }
    fun print(b: Boolean) = writer.print(b); fun print(c: Char) = writer.print(c); fun print(i: Int) = writer.print(i)
    fun print(l: Long) = writer.print(l); fun print(f: Float) = writer.print(f); fun print(d: Double) = writer.print(d)
    fun print(s: CharArray) = writer.print(s); fun print(s: String?) = writer.print(s);
    fun print(obj: Any?) = writer.print(obj)
    fun println() = writer.println(); fun println(x: Boolean) = writer.println(x); fun println(x: Char) = writer.println(x)
    fun println(x: Int) = writer.println(x); fun println(x: Long) = writer.println(x); fun println(x: Float) = writer.println(x)
    fun println(x: Double) = writer.println(x); fun println(x: CharArray) = writer.println(x); fun println(x: String) = writer.println(x)
    fun println(x: Any) = writer.println(x)
    fun printf(format: String, vararg args: Any?) = writer.format(format, *args)
    fun flush() = writer.flush()
}
inline fun <reified T> ar(size: Int, init: (Int) -> T) = Array(size) { init(it) }
inline fun iao(vararg nums: Int) = intArrayOf(*nums)

/**
 * generated by kotlincputil@tauros
 */
private val bufCap = 65536
private val rd = FastReader(System.`in`, bufCap)
private val wt = FastWriter(System.out, bufCap)
private fun solve() {
    // https://codeforces.com/problemset/problem/491/B
    // 想象一下一维的时候，判断点p和一个group的点的最大距离，只用考虑这个group最左和最右即可
    // 回到二维曼哈顿距离上，应该也是只用判断一个group有限的点的
    // 考虑从点p触发只能行走len，这个终点的图形就是由两条斜率为1和两条斜率为-1的线段包住的
    // 也就是只用考虑斜率为±1的最上和最下的直线切到的点，总共只用考虑四个点即可
    val (n, m) = rd.ni() to rd.ni()
    val c = rd.ni()
    val points = ar(c) { rd.na(2) }
    val h = rd.ni()
    val hotels = ar(h) { rd.na(2) }

    // 0: max b1, 1 min b1
    // 2: max b2, 3 min b2
    val bounds = ar(4) { iao(0, 0, n + m + 1) }
    for ((x, y) in points) {
        val b1 = y + x
        if (bounds[0][2] > n + m || bounds[0][2] < b1) {
            bounds[0][0] = x; bounds[0][1] = y; bounds[0][2] = b1
        }
        if (bounds[1][2] > n + m || bounds[1][2] > b1) {
            bounds[1][0] = x; bounds[1][1] = y; bounds[1][2] = b1
        }
        val b2 = y - x
        if (bounds[2][2] > n + m || bounds[2][2] < b2) {
            bounds[2][0] = x; bounds[2][1] = y; bounds[2][2] = b2
        }
        if (bounds[3][2] > n + m || bounds[3][2] > b2) {
            bounds[3][0] = x; bounds[3][1] = y; bounds[3][2] = b2
        }
    }

    fun IntArray.dist(other: IntArray) = abs(this[0] - other[0]) + abs(this[1] - other[1])
    fun IntArray.maxDist() = (0 until 4).maxOf { bounds[it].dist(this) }
    var ans = -1
    for (i in 0 until h) {
        val dist = hotels[i].maxDist()
        if (ans == -1 || dist < hotels[ans].maxDist()) {
            ans = i
        }
    }

    wt.println(hotels[ans].maxDist())
    wt.println("${ans + 1}")
}
fun main(args: Array<String>) {
    solve()
    wt.flush()
}