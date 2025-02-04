// (C) 2025 A.Voß, a.voss@fh-aachen.de, info@codebasedlearning.dev
//
// see https://adventofcode.com/2023/day/24

package dev.codebasedlearning.adventofcode.day24

import dev.codebasedlearning.adventofcode.commons.input.linesOf
import dev.codebasedlearning.adventofcode.commons.input.parseNumbers
import dev.codebasedlearning.adventofcode.commons.timing.checkResult
import dev.codebasedlearning.adventofcode.commons.visualization.print
import kotlin.math.abs
import kotlin.math.roundToLong

val examples = listOf(
"""
19, 13, 30 @ -2,  1, -2
18, 19, 22 @ -1, -1, -2
20, 25, 34 @ -2, -2, -4
12, 31, 28 @ -1, -2, -1
20, 19, 15 @  1, -5, -3
"""
)

// see day 22, but now for Long
data class Location(val x: Long, val y: Long, val z: Long) {
    constructor(xyz: List<Long>) : this(xyz[0], xyz[1], xyz[2])
    constructor(xyz: Triple<Long, Long, Long>) : this(xyz.first, xyz.second, xyz.third)
}
operator fun Location.minus(other: Location) = Location(this.x-other.x, this.y-other.y, this.z-other.z)

data class Hailstone(var start: Location, var velocity: Location)

fun main() {
    val story = object {
        val day = 24
        val year = 2023
        val example = 0
        val lines = when (example) {
            0 -> linesOf(day, year, fetchAoCInput = true)
            else -> linesOf(input = examples[example-1])
        }
    }.apply {
        lines.print(indent = 2, description = "Day $day, Input:", take = 2)
    }

    val hailstones = story.lines.map { line -> line.split("@").let { (start,end) ->
        Hailstone(Location(start.parseNumbers<Long>(',')),Location(end.parseNumbers<Long>(',')))
    } }

    fun intersection(hailstone1: Hailstone, hailstone2: Hailstone): Triple<Double,Double, Boolean>? {
        val (x1,y1) = hailstone1.start
        val (dx1,dy1) = hailstone1.velocity
        val (x2,y2) = hailstone2.start
        val (dx2,dy2) = hailstone2.velocity
        val denominator = (dx2*dy1 - dx1*dy2).toDouble()
        if (denominator == 0.0) return null
        val t1 = (((x2 - x1) * -dy2 + (y2 - y1) * dx2) / denominator)
        val t2 = (((x2 - x1) * -dy1 + (y2 - y1) * dx1) / denominator)
        return Triple(x1 + t1 * dx1, y1 + t1 * dy1, t1>0 && t2>0)
    }

    // part 1: solutions: 2 / 21843

    checkResult(21843) { // [M3 9.906625ms]
        val dims = if (story.example == 0) 200000000000000.0..400000000000000.0 else 7.0..27.0
        hailstones.withIndex().sumOf { (i, hailstone1) ->
            hailstones.slice(i+1..<hailstones.size).count { hailstone2 ->
                val intersection = intersection(hailstone1, hailstone2)
                val b = (intersection!=null) && (intersection.third) && (intersection.first in dims) && (intersection.second in dims)
                b
            }
        }
    }.let { (dt,result,check) -> println("[part 1] result: $result $check, dt: $dt (hailstones crossing)") }

    /*
        omg... task is to find the (unknowns) t_i (scalar), P and V (3d-vectors) such that
            P_i + t_i * V_i = P + t_i + V  or  (P - P_i) + t_i (V - V_i) = 0  for all i

        problem is, that this is not a linear equation as we have t_i*V (in all components)
        what makes it a bi-linear equation...

        first, one can observe that this implies (x means the cross or vector product)
            (P - P_i) x (V - V_i) = 0 as (P-P_i) and (V-V_i) lie on the same line
        what basically eliminates t_i -> good
        second, if we consider this product (in fact, these are 3 equations) for,
        say i=1 and i=2, then we can get rid of the (non-linear) PxV term
        and end up in
            P x (V2-V1) - P1 x (V-V1) + P2 x (V-V2)  (subtract equations, apply cross product rules)
        and, similarly, for i=1 and i=3 in
            P x (V3-V1) - P1 x (V-V1) + P3 x (V-V3)
        -> very good, as these are 6 truly linear equations for 6 unknowns (P and V) :-)
        -> assembleEquations in A and b and solveGaussian

        note, that there are rounding errors, and we choose Hailstones 1,2 and 3 in order to
        get the "correct" integer; I wanted to move these general solvers into commons
     */

    // part 2: solutions: 47 / 540355811503157

    checkResult(540355811503157) { // [M3 473us]
        val (A,b) = assembleEquations(hailstones[1], hailstones[2], hailstones[3])
        val x = solveGaussian(A, b)
        (x[0]+x[1]+x[2]).roundToLong()
    }.let { (dt,result,check) -> println("[part 2] result: $result $check, dt: $dt (shoot em)") }
}

fun assembleEquations(hs1: Hailstone, hs2: Hailstone, hs3: Hailstone): Pair<Array<DoubleArray>, DoubleArray> {
    fun Location.toDouble() = doubleArrayOf(x.toDouble(),y.toDouble(),z.toDouble())
    val (p1,v1) = hs1.start to hs1.velocity
    val (p2,v2) = hs2.start to hs2.velocity
    val (p3,v3) = hs3.start to hs3.velocity
    val q21 = p2-p1
    val q31 = p3-p1
    val w21 = v2-v1
    val w31 = v3-v1
    // unknowns are P and V, i.e. p_x, p_y, p_z, v_x, v_y, v_z
    val A = arrayOf( // coefficients for the unknowns from the notes and cross products above
        doubleArrayOf(0.0, w21.z.toDouble(), -w21.y.toDouble(), 0.0, -q21.z.toDouble(), q21.y.toDouble()),
        doubleArrayOf(-w21.z.toDouble(), 0.0, w21.x.toDouble(), q21.z.toDouble(), 0.0, -q21.x.toDouble()),
        doubleArrayOf(w21.y.toDouble(), -w21.x.toDouble(), 0.0, -q21.y.toDouble(), q21.x.toDouble(), 0.0),
        doubleArrayOf(0.0, w31.z.toDouble(), -w31.y.toDouble(), 0.0, -q31.z.toDouble(), q31.y.toDouble()),
        doubleArrayOf(-w31.z.toDouble(), 0.0, w31.x.toDouble(), q31.z.toDouble(), 0.0, -q31.x.toDouble()),
        doubleArrayOf(w31.y.toDouble(), -w31.x.toDouble(), 0.0, -q31.y.toDouble(), q31.x.toDouble(), 0.0),
    )
    val c11 = crossProduct(p1.toDouble(), v1.toDouble())
    val c22 = crossProduct(p2.toDouble(), v2.toDouble())
    val c33 = crossProduct(p3.toDouble(), v3.toDouble())
    val b = doubleArrayOf( // constant terms from above
        -c11[0] + c22[0], -c11[1] + c22[1], -c11[2] + c22[2],
        -c11[0] + c33[0], -c11[1] + c33[1], -c11[2] + c33[2],
    )
    return A to b
}

// both fcts in commons?

fun crossProduct(v1: DoubleArray, v2: DoubleArray): DoubleArray {
    require(v1.size == 3 && v2.size == 3) { "requires two DoubleArrays of size 3" }
    return doubleArrayOf(v1[1]*v2[2] - v1[2]*v2[1], v1[2]*v2[0] - v1[0]*v2[2], v1[0]*v2[1] - v1[1]*v2[0])
}

// solves A x = b using Gaussian elimination with partial pivoting;
// A: n x n matrix, b: dim n, both mutated in-place
fun solveGaussian(A: Array<DoubleArray>, b: DoubleArray): DoubleArray {
    val n = A.size
    require(n == b.size) { "A and b must have compatible sizes" }
    require(A.all { it.size == n }) { "A must be square (n x n)" }
    val eps = 1e-14

    // elimination
    for (k in 0 until n) {
        // partial pivot: find largest pivot in k
        var pivotRow = k
        var maxVal = abs(A[k][k])
        for (r in (k+1) until n) {
            val absVal = abs(A[r][k])
            if (absVal > maxVal) {
                pivotRow = r
                maxVal = absVal
            }
        }
        // swap rows if needed
        if (pivotRow != k) {
            val tempRow = A[k]
            A[k] = A[pivotRow]
            A[pivotRow] = tempRow

            val tempB = b[k]
            b[k] = b[pivotRow]
            b[pivotRow] = tempB
        }

        // eliminate below pivot
        val pivot = A[k][k]
        if (abs(pivot) < eps) continue  // open, could handle or throw an error

        for (i in (k+1) until n) {
            val factor = A[i][k] / pivot
            A[i][k] = 0.0
            for (j in (k+1) until n) {
                A[i][j] -= factor * A[k][j]
            }
            b[i] -= factor * b[k]
        }
    }

    // back-substitution
    val x = DoubleArray(n) { 0.0 }
    for (i in (n-1) downTo 0) {
        var sum = b[i]
        for (j in (i+1) until n) {
            sum -= A[i][j]*x[j]
        }
        // same as above, if A[i][i] is small, system might be singular
        x[i] = if (abs(A[i][i]) < eps) 0.0 else sum / A[i][i]
    }
    return x
}
