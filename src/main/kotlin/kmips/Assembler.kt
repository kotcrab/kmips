package kmips

import java.nio.ByteBuffer
import java.nio.ByteOrder

/** @author Kotcrab */
fun assemble(startPc: Int = 0, endianness: Endianness = Endianness.Little, init: Assembler.() -> Unit): List<Int> {
    val assembler = Assembler(startPc, endianness)
    assembler.init()
    return assembler.assembleToList()
}

fun assembleAsHexString(startPc: Int = 0, endianness: Endianness = Endianness.Little, init: Assembler.() -> Unit): String {
    val assembler = Assembler(startPc, endianness)
    assembler.init()
    return assembler.assembleAsHexString()
}

class Assembler(startPc: Int, val endianness: Endianness) {
    private var virtualPc = startPc
    private val instructions = mutableListOf<Instruction>()

    fun add(rd: Reg, rs: Reg, rt: Reg) = emit(RInstruction(0, rd, rs, rt, 0, 0b100_000))
    fun addi(rt: Reg, rs: Reg, imm: Int) = emit(IInstruction(0b001_000, rs, rt, imm))
    fun addiu(rt: Reg, rs: Reg, imm: Int) = emit(IInstruction(0b001_001, rs, rt, imm))
    fun addu(rd: Reg, rs: Reg, rt: Reg) = emit(RInstruction(0, rd, rs, rt, 0, 0b100_001))
    fun and(rd: Reg, rs: Reg, rt: Reg) = emit(RInstruction(0, rd, rs, rt, 0, 0b100_100))
    fun andi(rt: Reg, rs: Reg, imm: Int) = emit(IInstruction(0b001_100, rs, rt, imm))
    fun beq(rs: Reg, rt: Reg, label: Label) = emitBranchInstruction(0b000_100, rs, rt, label)
    fun bgez(rs: Reg, label: Label) = emitBranchInstruction(0b000_001, rs, Reg.at, label)
    fun blez(rs: Reg, label: Label) = emitBranchInstruction(0b000_110, rs, Reg.zero, label)
    fun bltz(rs: Reg, label: Label) = emitBranchInstruction(0b000_001, rs, Reg.zero, label)
    fun bne(rs: Reg, rt: Reg, label: Label) = emitBranchInstruction(0b000_101, rs, rt, label)
    fun div(rs: Reg, rt: Reg) = emit(RInstruction(0, Reg.zero, rs, rt, 0, 0b011_010))
    fun divu(rs: Reg, rt: Reg) = emit(RInstruction(0, Reg.zero, rs, rt, 0, 0b011_011))
    fun j(address: Int) = emitJumpInstruction(0b000_010, address)
    fun jal(address: Int) = emitJumpInstruction(0b000_011, address)
    fun jr(rs: Reg) = emit(RInstruction(0b000_000, Reg.zero, rs, Reg.zero, 0, 0b001_000))
    fun lb(rt: Reg, offset: Int, rs: Reg) = emit(IInstruction(0b100_000, rs, rt, offset))
    fun lui(rt: Reg, imm: Int) = emit(IInstruction(0b001_111, Reg.zero, rt, imm))
    fun lw(rt: Reg, offset: Int, rs: Reg) = emit(IInstruction(0b100_011, rs, rt, offset))
    fun mfhi(rd: Reg) = emit(RInstruction(0, rd, Reg.zero, Reg.zero, 0, 0b010_000))
    fun mflo(rd: Reg) = emit(RInstruction(0, rd, Reg.zero, Reg.zero, 0, 0b010_010))
    fun mult(rs: Reg, rt: Reg) = emit(RInstruction(0, Reg.zero, rs, rt, 0, 0b011_000))
    fun multu(rs: Reg, rt: Reg) = emit(RInstruction(0, Reg.zero, rs, rt, 0, 0b011_001))
    fun nop() = emit(NopInstruction())
    fun nor(rd: Reg, rs: Reg, rt: Reg) = emit(RInstruction(0, rd, rs, rt, 0, 0b100_111))
    fun or(rd: Reg, rs: Reg, rt: Reg) = emit(RInstruction(0, rd, rs, rt, 0, 0b100_101))
    fun ori(rt: Reg, rs: Reg, imm: Int) = emit(IInstruction(0b001_101, rs, rt, imm))
    fun sb(rt: Reg, offset: Int, rs: Reg) = emit(IInstruction(0b101_000, rs, rt, offset))
    fun sll(rd: Reg, rt: Reg, h: Int) = emit(RInstruction(0, rd, Reg.zero, rt, h, 0))
    fun sllv(rd: Reg, rt: Reg, rs: Reg) = emit(RInstruction(0, rd, rs, rt, 0, 0b000_100))
    fun slt(rd: Reg, rs: Reg, rt: Reg) = emit(RInstruction(0, rd, rs, rt, 0, 0b101_010))
    fun slti(rt: Reg, rs: Reg, imm: Int) = emit(IInstruction(0b001_010, rs, rt, imm))
    fun sltiu(rt: Reg, rs: Reg, imm: Int) = emit(IInstruction(0b001_011, rs, rt, imm))
    fun sltu(rd: Reg, rs: Reg, rt: Reg) = emit(RInstruction(0, rd, rs, rt, 0, 0b101_011))
    fun sra(rd: Reg, rt: Reg, h: Int) = emit(RInstruction(0, rd, Reg.zero, rt, h, 0b000_011))
    fun srl(rd: Reg, rt: Reg, h: Int) = emit(RInstruction(0, rd, Reg.zero, rt, h, 0b000_010))
    fun srlv(rd: Reg, rt: Reg, rs: Reg) = emit(RInstruction(0, rd, rs, rt, 0, 0b000_110))
    fun sub(rd: Reg, rs: Reg, rt: Reg) = emit(RInstruction(0, rd, rs, rt, 0, 0b100_010))
    fun subu(rd: Reg, rs: Reg, rt: Reg) = emit(RInstruction(0, rd, rs, rt, 0, 0b100_011))
    fun sw(rt: Reg, offset: Int, rs: Reg) = emit(IInstruction(0b101_011, rs, rt, offset))
    fun xor(rd: Reg, rs: Reg, rt: Reg) = emit(RInstruction(0, rd, rs, rt, 0, 0b100_110))
    fun xori(rt: Reg, rs: Reg, imm: Int) = emit(IInstruction(0b001_110, rs, rt, imm))

    fun label(label: Label) {
        label.address = virtualPc
    }

    private fun emitBranchInstruction(opcode: Int, rs: Reg, rt: Reg, label: Label) {
        val instrVirtualPc = virtualPc
        emit(IInstruction(opcode, rs, rt, { (label.address - instrVirtualPc - 0x4) / 0x4 }))
    }

    private fun emitJumpInstruction(opcode: Int, address: Int) {
        val instrVirtualPc = virtualPc + 0x4
        if (address and 0xf0000000.toInt() != instrVirtualPc and 0xf0000000.toInt()) {
            error("can't calculate jump address because address is too far from current pc (pc bits 31-28 mismatch)")
        }
        if (address and 0b11 != 0) {
            error("can't calculate jump address because last two bits of address are != 0")
        }
        emit(JInstruction(opcode, address and 0xFFFFFFF ushr 2))
    }

    fun emit(instruction: Instruction) {
        instructions.add(instruction)
        virtualPc += 4
    }

    fun assembleToList(): List<Int> {
        val out: List<Int>
        when (endianness) {
            Endianness.Little -> out = instructions.map { it.assemble().toLittleEndian() }
            Endianness.Big -> out = instructions.map { it.assemble() }
        }
        return out
    }

    fun assembleAsHexString(): String {
        return assembleToList().map { it.toHex() }.joinToString(separator = "")
    }
}

class Label {
    internal var assigned = false

    var address: Int = -1
        get() {
            if (!assigned) error("label was not assigned")
            return field
        }
        set(value) {
            if (value < 0) error("value can't be < 0")
            if (assigned) error("label was already assigned")
            assigned = true
            field = value
        }
}

class RInstruction(val opcode: Int, val rd: Reg, val rs: Reg, val rt: Reg, val shift: Int = 0, val funct: Int = 0) : Instruction {
    override fun assemble(): Int {
        if (opcode > 0x3F) error("opcode value is too big: $opcode")
        if (shift > 0x1F) error("shift value is too big: $shift")
        if (funct > 0x3F) error("funct value is too big: $funct")
        return (opcode shl 26) or (rs.id shl 21) or (rt.id shl 16) or (rd.id shl 11) or (shift shl 6) or funct
    }
}

class IInstruction(val opcode: Int, val rs: Reg, val rt: Reg, val imm: () -> Int) : Instruction {
    constructor(opcode: Int, rs: Reg, rt: Reg, imm: Int) : this(opcode, rs, rt, { imm })

    override fun assemble(): Int {
        if (opcode > 0x3F) error("opcode value is too big: $opcode")
        if (imm() > 0xFFFF) error("imm value is too big: ${imm()}")
        return (opcode shl 26) or (rs.id shl 21) or (rt.id shl 16) or imm()
    }
}

class JInstruction(val opcode: Int, val address: () -> Int) : Instruction {
    constructor(opcode: Int, address: Int) : this(opcode, { address })

    override fun assemble(): Int {
        if (opcode > 0x3F) error("opcode value is too big: $opcode")
        if (address() > 0x3FFFFFF) error("address value is too big: 0x${address().toHex()}")
        return (opcode shl 26) or address()
    }
}

class NopInstruction : Instruction {
    override fun assemble(): Int = 0
}

interface Instruction {
    fun assemble(): Int
}

enum class Endianness {
    Little, Big
}

enum class Reg(val id: Int) {
    zero(0),
    at(1),
    v0(2), v1(3),
    a0(4), a1(5), a2(6), a3(7),
    t0(8), t1(9), t2(10), t3(11), t4(12), t5(13), t6(14), t7(15), t8(24), t9(25),
    s0(16), s1(17), s2(18), s3(19), s4(20), s5(21), s6(22), s7(23),
    k0(26), k1(27),
    gp(28), sp(29), fp(30), ra(31);
}

internal fun Int.toHex() = String.format("%08X", this)

internal fun Int.toLittleEndian(): Int {
    val buffer = ByteBuffer.allocate(4).putInt(this)
    buffer.rewind()
    return buffer.order(ByteOrder.LITTLE_ENDIAN).int
}
