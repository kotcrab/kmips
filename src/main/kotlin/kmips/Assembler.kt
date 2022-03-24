package kmips

import java.io.ByteArrayOutputStream

fun assemble(startPc: Int = 0, endianness: Endianness = Endianness.Little, init: Assembler.() -> Unit): List<Int> {
  val assembler = Assembler(startPc, endianness)
  assembler.init()
  return assembler.assembleAsList()
}

fun assembleAsHexString(startPc: Int = 0, endianness: Endianness = Endianness.Little, init: Assembler.() -> Unit): String {
  val assembler = Assembler(startPc, endianness)
  assembler.init()
  return assembler.assembleAsHexString()
}

fun assembleAsByteArray(
  startPc: Int = 0,
  endianness: Endianness = Endianness.Little,
  init: Assembler.() -> Unit,
): ByteArray {
  val assembler = Assembler(startPc, endianness)
  assembler.init()
  return assembler.assembleAsByteArray()
}

class Assembler(startPc: Int, val endianness: Endianness) {
  companion object {
    /** FPU (coprocessor 1) */
    private const val COP1 = 0b010_001

    /** 32-bit float */
    private const val FMT_S = 16

    /** 64-bit float */
    private const val FMT_D = 17

    /** 32-bit signed magnitude int */
    private const val FMT_W = 20

    /** 64-bit signed magnitude int */
    private const val FMT_L = 21
  }

  var virtualPc = startPc
    private set
  private val instructions = mutableListOf<Instruction>()

  // MIPS II

  fun lb(rt: Reg, offset: Int, base: Reg) = emit(IInstruction(0b100_000, base, rt, offset))
  fun lbu(rt: Reg, offset: Int, base: Reg) = emit(IInstruction(0b100_100, base, rt, offset))
  fun sb(rt: Reg, offset: Int, base: Reg) = emit(IInstruction(0b101_000, base, rt, offset))

  fun lh(rt: Reg, offset: Int, base: Reg) = emit(IInstruction(0b100_001, base, rt, offset))
  fun lhu(rt: Reg, offset: Int, base: Reg) = emit(IInstruction(0b100_101, base, rt, offset))
  fun sh(rt: Reg, offset: Int, base: Reg) = emit(IInstruction(0b101_001, base, rt, offset))

  fun lw(rt: Reg, offset: Int, base: Reg) = emit(IInstruction(0b100_011, base, rt, offset))
  fun sw(rt: Reg, offset: Int, base: Reg) = emit(IInstruction(0b101_011, base, rt, offset))

  fun lwl(rt: Reg, offset: Int, base: Reg) = emit(IInstruction(0b100_010, base, rt, offset))
  fun lwr(rt: Reg, offset: Int, base: Reg) = emit(IInstruction(0b100_110, base, rt, offset))
  fun swl(rt: Reg, offset: Int, base: Reg) = emit(IInstruction(0b101_010, base, rt, offset))
  fun swr(rt: Reg, offset: Int, base: Reg) = emit(IInstruction(0b101_110, base, rt, offset))

  fun ll(rt: Reg, offset: Int, base: Reg) = emit(IInstruction(0b110_000, base, rt, offset))
  fun sc(rt: Reg, offset: Int, base: Reg) = emit(IInstruction(0b111_000, base, rt, offset))

  fun addi(rt: Reg, rs: Reg, imm: Int) = emit(IInstruction(0b001_000, rs, rt, imm))
  fun addiu(rt: Reg, rs: Reg, imm: Int) = emit(IInstruction(0b001_001, rs, rt, imm))
  fun slti(rt: Reg, rs: Reg, imm: Int) = emit(IInstruction(0b001_010, rs, rt, imm))
  fun sltiu(rt: Reg, rs: Reg, imm: Int) = emit(IInstruction(0b001_011, rs, rt, imm))
  fun andi(rt: Reg, rs: Reg, imm: Int) = emit(IInstruction(0b001_100, rs, rt, imm))
  fun ori(rt: Reg, rs: Reg, imm: Int) = emit(IInstruction(0b001_101, rs, rt, imm))
  fun xori(rt: Reg, rs: Reg, imm: Int) = emit(IInstruction(0b001_110, rs, rt, imm))
  fun lui(rt: Reg, imm: Int) = emit(IInstruction(0b001_111, Reg.zero, rt, imm))

  fun add(rd: Reg, rs: Reg, rt: Reg) = emit(RInstruction(0, rs, rt, rd, 0, 0b100_000))
  fun addu(rd: Reg, rs: Reg, rt: Reg) = emit(RInstruction(0, rs, rt, rd, 0, 0b100_001))
  fun sub(rd: Reg, rs: Reg, rt: Reg) = emit(RInstruction(0, rs, rt, rd, 0, 0b100_010))
  fun subu(rd: Reg, rs: Reg, rt: Reg) = emit(RInstruction(0, rs, rt, rd, 0, 0b100_011))

  fun slt(rd: Reg, rs: Reg, rt: Reg) = emit(RInstruction(0, rs, rt, rd, 0, 0b101_010))
  fun sltu(rd: Reg, rs: Reg, rt: Reg) = emit(RInstruction(0, rs, rt, rd, 0, 0b101_011))
  fun and(rd: Reg, rs: Reg, rt: Reg) = emit(RInstruction(0, rs, rt, rd, 0, 0b100_100))
  fun or(rd: Reg, rs: Reg, rt: Reg) = emit(RInstruction(0, rs, rt, rd, 0, 0b100_101))
  fun xor(rd: Reg, rs: Reg, rt: Reg) = emit(RInstruction(0, rs, rt, rd, 0, 0b100_110))
  fun nor(rd: Reg, rs: Reg, rt: Reg) = emit(RInstruction(0, rs, rt, rd, 0, 0b100_111))

  fun sll(rd: Reg, rt: Reg, sa: Int) = emit(RInstruction(0, Reg.zero, rt, rd, sa, 0))
  fun srl(rd: Reg, rt: Reg, sa: Int) = emit(RInstruction(0, Reg.zero, rt, rd, sa, 0b000_010))
  fun sra(rd: Reg, rt: Reg, sa: Int) = emit(RInstruction(0, Reg.zero, rt, rd, sa, 0b000_011))
  fun sllv(rd: Reg, rt: Reg, rs: Reg) = emit(RInstruction(0, rs, rt, rd, 0, 0b000_100))
  fun srlv(rd: Reg, rt: Reg, rs: Reg) = emit(RInstruction(0, rs, rt, rd, 0, 0b000_110))
  fun srav(rd: Reg, rt: Reg, rs: Reg) = emit(RInstruction(0, rs, rt, rd, 0, 0b000_111))

  fun mult(rs: Reg, rt: Reg) = emit(RInstruction(0, rs, rt, Reg.zero, 0, 0b011_000))
  fun multu(rs: Reg, rt: Reg) = emit(RInstruction(0, rs, rt, Reg.zero, 0, 0b011_001))
  fun div(rs: Reg, rt: Reg) = emit(RInstruction(0, rs, rt, Reg.zero, 0, 0b011_010))
  fun divu(rs: Reg, rt: Reg) = emit(RInstruction(0, rs, rt, Reg.zero, 0, 0b011_011))
  fun mfhi(rd: Reg) = emit(RInstruction(0, Reg.zero, Reg.zero, rd, 0, 0b010_000))
  fun mthi(rs: Reg) = emit(RInstruction(0, rs, Reg.zero, Reg.zero, 0, 0b010_001))
  fun mflo(rd: Reg) = emit(RInstruction(0, Reg.zero, Reg.zero, rd, 0, 0b010_010))
  fun mtlo(rs: Reg) = emit(RInstruction(0, rs, Reg.zero, Reg.zero, 0, 0b010_011))

  fun j(address: Int) = emitJumpInstruction(0b000_010, address)
  fun jal(address: Int) = emitJumpInstruction(0b000_011, address)
  fun jr(rs: Reg) = emit(RInstruction(0, rs, Reg.zero, Reg.zero, 0, 0b001_000))
  fun jalr(rs: Reg) = jalr(Reg.ra, rs)
  fun jalr(rd: Reg, rs: Reg) = emit(RInstruction(0, rs, Reg.zero, rd, 0, 0b001_001))

  fun beq(rs: Reg, rt: Reg, label: Label) = emitBranchInstruction(0b000_100, rs, rt, label)
  fun bne(rs: Reg, rt: Reg, label: Label) = emitBranchInstruction(0b000_101, rs, rt, label)
  fun blez(rs: Reg, label: Label) = emitBranchInstruction(0b000_110, rs, Reg.zero, label)
  fun bgtz(rs: Reg, label: Label) = emitBranchInstruction(0b000_111, rs, Reg.zero, label)
  fun beql(rs: Reg, rt: Reg, label: Label) = emitBranchInstruction(0b010_100, rs, rt, label)
  fun bnel(rs: Reg, rt: Reg, label: Label) = emitBranchInstruction(0b010_101, rs, rt, label)
  fun blezl(rs: Reg, label: Label) = emitBranchInstruction(0b010_110, rs, Reg.zero, label)
  fun bgtzl(rs: Reg, label: Label) = emitBranchInstruction(0b010_111, rs, Reg.zero, label)

  fun bltz(rs: Reg, label: Label) = emitBranchInstruction(0b000_001, rs.id, 0b00000, label)
  fun bgez(rs: Reg, label: Label) = emitBranchInstruction(0b000_001, rs.id, 0b00001, label)
  fun bltzal(rs: Reg, label: Label) = emitBranchInstruction(0b000_001, rs.id, 0b10000, label)
  fun bgezal(rs: Reg, label: Label) = emitBranchInstruction(0b000_001, rs.id, 0b10001, label)
  fun bltzl(rs: Reg, label: Label) = emitBranchInstruction(0b000_001, rs.id, 0b00010, label)
  fun bgezl(rs: Reg, label: Label) = emitBranchInstruction(0b000_001, rs.id, 0b00011, label)
  fun bltzall(rs: Reg, label: Label) = emitBranchInstruction(0b000_001, rs.id, 0b10010, label)
  fun bgezall(rs: Reg, label: Label) = emitBranchInstruction(0b000_001, rs.id, 0b10011, label)

  fun syscall(code: Int) = emit(CodeInstruction(0, code, 0b001_100))
  fun `break`(code: Int) = emit(CodeInstruction(0, code, 0b001_101))

  fun tge(rs: Reg, rt: Reg, code: Int = 0x200) = emit(RInstruction(0, rs.id, rt.id, code ushr 5, code and 0x1F, 0b110_000))
  fun tgeu(rs: Reg, rt: Reg, code: Int = 0x200) =
    emit(RInstruction(0, rs.id, rt.id, code ushr 5, code and 0x1F, 0b110_001))

  fun tlt(rs: Reg, rt: Reg, code: Int = 0x200) = emit(RInstruction(0, rs.id, rt.id, code ushr 5, code and 0x1F, 0b110_010))
  fun tltu(rs: Reg, rt: Reg, code: Int = 0x200) =
    emit(RInstruction(0, rs.id, rt.id, code ushr 5, code and 0x1F, 0b110_011))

  fun teq(rs: Reg, rt: Reg, code: Int = 0x200) = emit(RInstruction(0, rs.id, rt.id, code ushr 5, code and 0x1F, 0b110_100))
  fun tne(rs: Reg, rt: Reg, code: Int = 0x200) = emit(RInstruction(0, rs.id, rt.id, code ushr 5, code and 0x1F, 0b110_110))

  fun tgei(rs: Reg, imm: Int) = emit(IInstruction(0b000_001, rs.id, 0b01000, imm))
  fun tgeiu(rs: Reg, imm: Int) = emit(IInstruction(0b000_001, rs.id, 0b01001, imm))
  fun tlti(rs: Reg, imm: Int) = emit(IInstruction(0b000_001, rs.id, 0b01010, imm))
  fun tltiu(rs: Reg, imm: Int) = emit(IInstruction(0b000_001, rs.id, 0b01011, imm))
  fun teqi(rs: Reg, imm: Int) = emit(IInstruction(0b000_001, rs.id, 0b01100, imm))
  fun tnei(rs: Reg, imm: Int) = emit(IInstruction(0b000_001, rs.id, 0b01110, imm))

  fun sync(stype: Int = 0) = emit(RInstruction(0, 0, 0, 0, stype, 0b001_111))

  fun nop() = emit(NopInstruction())

  // FPU (MIPS II)

  fun lwc1(ft: FpuReg, offset: Int, base: Reg) = emit(IInstruction(0b110_001, base.id, ft.id, offset))
  fun swc1(ft: FpuReg, offset: Int, base: Reg) = emit(IInstruction(0b111_001, base.id, ft.id, offset))

  fun mtc1(rt: Reg, fs: FpuReg) = emit(RInstruction(COP1, 0b00100, rt.id, fs.id, 0, 0))
  fun mfc1(rt: Reg, fs: FpuReg) = emit(RInstruction(COP1, 0b00000, rt.id, fs.id, 0, 0))
  fun ctc1(rt: Reg, fs: FpuReg) = emit(RInstruction(COP1, 0b00110, rt.id, fs.id, 0, 0))
  fun cfc1(rt: Reg, fs: FpuReg) = emit(RInstruction(COP1, 0b00010, rt.id, fs.id, 0, 0))

  val add = AddFpu()
  val sub = SubFpu()
  val mul = MulFpu()
  val div = DivFpu()
  val abs = AbsFpu()
  val neg = NegFpu()
  val sqrt = SqrtFpu()
  val round = RoundFpu()
  val trunc = TruncFpu()
  val ceil = CeilFpu()
  val floor = FloorFpu()

  inner class AddFpu {
    fun s(fd: FpuReg, fs: FpuReg, ft: FpuReg) = emit(FpuInstruction(COP1, FMT_S, ft, fs, fd, 0b000_000))
    fun d(fd: FpuReg, fs: FpuReg, ft: FpuReg) = emit(FpuInstruction(COP1, FMT_D, ft, fs, fd, 0b000_000))
  }

  inner class SubFpu {
    fun s(fd: FpuReg, fs: FpuReg, ft: FpuReg) = emit(FpuInstruction(COP1, FMT_S, ft, fs, fd, 0b000_001))
    fun d(fd: FpuReg, fs: FpuReg, ft: FpuReg) = emit(FpuInstruction(COP1, FMT_D, ft, fs, fd, 0b000_001))
  }

  inner class MulFpu {
    fun s(fd: FpuReg, fs: FpuReg, ft: FpuReg) = emit(FpuInstruction(COP1, FMT_S, ft, fs, fd, 0b000_010))
    fun d(fd: FpuReg, fs: FpuReg, ft: FpuReg) = emit(FpuInstruction(COP1, FMT_D, ft, fs, fd, 0b000_010))
  }

  inner class DivFpu {
    fun s(fd: FpuReg, fs: FpuReg, ft: FpuReg) = emit(FpuInstruction(COP1, FMT_S, ft, fs, fd, 0b000_011))
    fun d(fd: FpuReg, fs: FpuReg, ft: FpuReg) = emit(FpuInstruction(COP1, FMT_D, ft, fs, fd, 0b000_011))
  }

  inner class AbsFpu {
    fun s(fd: FpuReg, fs: FpuReg) = emit(FpuInstruction(COP1, FMT_S, FpuReg.f0, fs, fd, 0b000_101))
    fun d(fd: FpuReg, fs: FpuReg) = emit(FpuInstruction(COP1, FMT_D, FpuReg.f0, fs, fd, 0b000_101))
  }

  inner class NegFpu {
    fun s(fd: FpuReg, fs: FpuReg) = emit(FpuInstruction(COP1, FMT_S, FpuReg.f0, fs, fd, 0b000_111))
    fun d(fd: FpuReg, fs: FpuReg) = emit(FpuInstruction(COP1, FMT_D, FpuReg.f0, fs, fd, 0b000_111))
  }

  inner class SqrtFpu {
    fun s(fd: FpuReg, fs: FpuReg) = emit(FpuInstruction(COP1, FMT_S, FpuReg.f0, fs, fd, 0b000_100))
    fun d(fd: FpuReg, fs: FpuReg) = emit(FpuInstruction(COP1, FMT_D, FpuReg.f0, fs, fd, 0b000_100))
  }

  inner class RoundFpu {
    val w = RoundWFpu()

    inner class RoundWFpu {
      fun s(fd: FpuReg, fs: FpuReg) = emit(FpuInstruction(COP1, FMT_S, FpuReg.f0, fs, fd, 0b001_100))
      fun d(fd: FpuReg, fs: FpuReg) = emit(FpuInstruction(COP1, FMT_D, FpuReg.f0, fs, fd, 0b001_100))
    }
  }

  inner class TruncFpu {
    val w = TruncWFpu()

    inner class TruncWFpu {
      fun s(fd: FpuReg, fs: FpuReg) = emit(FpuInstruction(COP1, FMT_S, FpuReg.f0, fs, fd, 0b001_101))
      fun d(fd: FpuReg, fs: FpuReg) = emit(FpuInstruction(COP1, FMT_D, FpuReg.f0, fs, fd, 0b001_101))
    }
  }

  inner class CeilFpu {
    val w = CeilWFpu()

    inner class CeilWFpu {
      fun s(fd: FpuReg, fs: FpuReg) = emit(FpuInstruction(COP1, FMT_S, FpuReg.f0, fs, fd, 0b001_110))
      fun d(fd: FpuReg, fs: FpuReg) = emit(FpuInstruction(COP1, FMT_D, FpuReg.f0, fs, fd, 0b001_110))
    }
  }

  inner class FloorFpu {
    val w = FloorWFpu()

    inner class FloorWFpu {
      fun s(fd: FpuReg, fs: FpuReg) = emit(FpuInstruction(COP1, FMT_S, FpuReg.f0, fs, fd, 0b001_111))
      fun d(fd: FpuReg, fs: FpuReg) = emit(FpuInstruction(COP1, FMT_D, FpuReg.f0, fs, fd, 0b001_111))
    }
  }

  val c = CompareFpu()

  inner class CompareFpu {
    val eq = CondEq()
    val le = CondLe()
    val lt = CondLt()

    inner class CondEq {
      fun s(fs: FpuReg, ft: FpuReg) = emit(FpuInstruction(COP1, FMT_S, ft, fs, FpuReg.f0, 0b11_0010))
      fun d(fs: FpuReg, ft: FpuReg) = emit(FpuInstruction(COP1, FMT_D, ft, fs, FpuReg.f0, 0b11_0010))
    }

    inner class CondLe {
      fun s(fs: FpuReg, ft: FpuReg) = emit(FpuInstruction(COP1, FMT_S, ft, fs, FpuReg.f0, 0b11_1110))
      fun d(fs: FpuReg, ft: FpuReg) = emit(FpuInstruction(COP1, FMT_D, ft, fs, FpuReg.f0, 0b11_1110))
    }

    inner class CondLt {
      fun s(fs: FpuReg, ft: FpuReg) = emit(FpuInstruction(COP1, FMT_S, ft, fs, FpuReg.f0, 0b11_1100))
      fun d(fs: FpuReg, ft: FpuReg) = emit(FpuInstruction(COP1, FMT_D, ft, fs, FpuReg.f0, 0b11_1100))
    }
  }

  val cvt = CvtFpu()

  inner class CvtFpu {
    val s = CvtS()
    val d = CvtD()
    val w = CvtW()

    inner class CvtS {
      fun d(fd: FpuReg, fs: FpuReg) = emit(FpuInstruction(COP1, FMT_D, FpuReg.f0, fs, fd, 0b100_000))
      fun w(fd: FpuReg, fs: FpuReg) = emit(FpuInstruction(COP1, FMT_W, FpuReg.f0, fs, fd, 0b100_000))
    }

    inner class CvtD {
      fun s(fd: FpuReg, fs: FpuReg) = emit(FpuInstruction(COP1, FMT_S, FpuReg.f0, fs, fd, 0b100_001))
      fun w(fd: FpuReg, fs: FpuReg) = emit(FpuInstruction(COP1, FMT_W, FpuReg.f0, fs, fd, 0b100_001))
    }

    inner class CvtW {
      fun s(fd: FpuReg, fs: FpuReg) = emit(FpuInstruction(COP1, FMT_S, FpuReg.f0, fs, fd, 0b100_100))
      fun d(fd: FpuReg, fs: FpuReg) = emit(FpuInstruction(COP1, FMT_D, FpuReg.f0, fs, fd, 0b100_100))
    }
  }

  val mov = MovFpu()

  inner class MovFpu {
    fun s(fd: FpuReg, fs: FpuReg) = emit(FpuInstruction(COP1, FMT_S, FpuReg.f0, fs, fd, 0b000_110))
    fun d(fd: FpuReg, fs: FpuReg) = emit(FpuInstruction(COP1, FMT_D, FpuReg.f0, fs, fd, 0b000_110))
  }

  fun bc1f(label: Label) = emitBranchInstruction(COP1, 0b01000, 0b00, label)
  fun bc1t(label: Label) = emitBranchInstruction(COP1, 0b01000, 0b01, label)
  fun bc1tl(label: Label) = emitBranchInstruction(COP1, 0b01000, 0b11, label)
  fun bc1fl(label: Label) = emitBranchInstruction(COP1, 0b01000, 0b10, label)

  // Pseudo instructions / aliases

  fun b(label: Label) {
    beq(Reg.zero, Reg.zero, label)
  }

  fun blt(rt: Reg, rs: Reg, label: Label) {
    slt(Reg.at, rt, rs)
    bne(Reg.at, Reg.zero, label)
  }

  fun bge(rt: Reg, rs: Reg, label: Label) {
    slt(Reg.at, rt, rs)
    beq(Reg.at, Reg.zero, label)
  }

  fun bgt(rt: Reg, rs: Reg, label: Label) {
    slt(Reg.at, rs, rt)
    bne(Reg.at, Reg.zero, label)
  }

  fun ble(rt: Reg, rs: Reg, label: Label) {
    slt(Reg.at, rs, rt)
    beq(Reg.at, Reg.zero, label)
  }

  fun neg(rd: Reg, rt: Reg) = sub(rd, Reg.zero, rt)

  fun not(rd: Reg, rs: Reg) = nor(rd, rs, Reg.zero)

  fun la(rs: Reg, imm: Int) {
    lui(rs, imm ushr 16)
    ori(rs, rs, imm and 0xFFFF)
  }

  fun li(rs: Reg, imm: Int) {
    if (imm < 0) {
      addiu(rs, Reg.zero, imm)
    } else {
      ori(rs, Reg.zero, imm)
    }
  }

  fun move(rd: Reg, rs: Reg) {
    addu(rd, rs, Reg.zero)
  }

  fun sge(rd: Reg, rs: Reg, rt: Reg) {
    slt(rd, rs, rt)
    li(Reg.at, 1)
    subu(rd, Reg.at, rd)
  }

  fun sgt(rd: Reg, rs: Reg, rt: Reg) {
    slt(rd, rt, rs)
  }

  fun data(data: Int) = emit(DataPseudoInstruction(data))

  fun label(label: Label) {
    label.address = virtualPc
  }

  private fun emitBranchInstruction(opcode: Int, rs: Reg, rt: Reg, label: Label) {
    emitBranchInstruction(opcode, rs.id, rt.id, label)
  }

  private fun emitBranchInstruction(opcode: Int, rs: Int, rt: Int, label: Label) {
    val instrVirtualPc = virtualPc
    emit(IInstruction(opcode, rs, rt) { (label.address - instrVirtualPc - 0x4) / 0x4 })
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

  fun assembleAsList(): List<Int> {
    return when (endianness) {
      Endianness.Little -> instructions.map { it.assemble().toLittleEndian() }
      Endianness.Big -> instructions.map { it.assemble() }
    }
  }

  fun assembleAsHexString(): String {
    return assembleAsList().joinToString(separator = "") { it.toHex() }
  }

  fun assembleAsByteArray(): ByteArray {
    val instr = assembleAsList()
    val out = ByteArrayOutputStream(instr.size * 4)
    instr.forEach {
      out.write(it ushr 24)
      out.write(it ushr 16 and 0xFF)
      out.write(it ushr 8 and 0xFF)
      out.write(it and 0xFF)
    }
    return out.toByteArray()
  }
}

class Label {
  var address: Int = -1
    get() {
      if (field == -1) error("label was not assigned")
      return field
    }
    set(value) {
      if (value < 0) error("value can't be < 0")
      if (field != -1) error("label was already assigned")
      field = value
    }
}

class RInstruction internal constructor(
  private val opcode: Int,
  private val rs: Int,
  private val rt: Int,
  private val rd: Int,
  private val shift: Int = 0,
  private val funct: Int = 0,
) : Instruction {
  constructor(opcode: Int, rs: Reg, rt: Reg, rd: Reg, shift: Int = 0, funct: Int = 0)
    : this(opcode, rs.id, rt.id, rd.id, shift, funct)

  override fun assemble(): Int {
    if (opcode > 0x3F) error("opcode value is too big: $opcode")
    if (shift > 0x1F) error("shift value is too big: $shift")
    if (funct > 0x3F) error("funct value is too big: $funct")
    return (opcode shl 26) or (rs shl 21) or (rt shl 16) or (rd shl 11) or (shift shl 6) or funct
  }
}

class IInstruction internal constructor(
  private val opcode: Int,
  private val rs: Int,
  private val rt: Int,
  private val imm: () -> Int,
) : Instruction {
  constructor(opcode: Int, rs: Int, rt: Int, imm: Int) : this(opcode, rs, rt, { imm })
  constructor(opcode: Int, rs: Reg, rt: Reg, imm: Int) : this(opcode, rs, rt, { imm })
  constructor(opcode: Int, rs: Reg, rt: Reg, imm: () -> Int) : this(opcode, rs.id, rt.id, imm)

  override fun assemble(): Int {
    if (opcode > 0x3F) error("opcode value is too big: $opcode")
    return (opcode shl 26) or (rs shl 21) or (rt shl 16) or (imm().toShort().toInt() and 0xFFFF)
  }
}

class JInstruction(
  private val opcode: Int,
  private val address: () -> Int,
) : Instruction {
  constructor(opcode: Int, address: Int) : this(opcode, { address })

  override fun assemble(): Int {
    if (opcode > 0x3F) error("opcode value is too big: $opcode")
    if (Integer.compareUnsigned(address(), 0x3FFFFFF) > 0) error("address value is too big: 0x${address().toHex()}")
    return (opcode shl 26) or address()
  }
}

class CodeInstruction(
  private val opcode: Int,
  private val code: Int,
  private val funct: Int = 0,
) : Instruction {
  override fun assemble(): Int {
    if (opcode > 0x3F) error("opcode value is too big: $opcode")
    if (code > 0xFFFF) error("code value is too big: $opcode")
    if (funct > 0x3F) error("funct value is too big: $funct")
    return (opcode shl 26) or (code and 0xFFFF shl 6) or funct
  }
}

class FpuInstruction internal constructor(
  private val opcode: Int,
  private val fmt: Int,
  private val ft: Int,
  private val fs: Int,
  private val fd: Int,
  private val funct: Int = 0,
) : Instruction {
  constructor(opcode: Int, fmt: Int, ft: FpuReg, fs: FpuReg, fd: FpuReg, funct: Int = 0)
    : this(opcode, fmt, ft.id, fs.id, fd.id, funct)

  override fun assemble(): Int {
    if (opcode > 0x3F) error("opcode value is too big: $opcode")
    if (fmt > 0x1F) error("fmt value is too big: $fmt")
    if (funct > 0x3F) error("funct value is too big: $funct")
    return (opcode shl 26) or (fmt shl 21) or (ft shl 16) or (fs shl 11) or (fd shl 6) or funct
  }
}

class DataPseudoInstruction(
  private val data: Int,
) : Instruction {
  override fun assemble(): Int = data
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

@Suppress("EnumEntryName", "unused")
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

@Suppress("EnumEntryName", "unused")
enum class FpuReg(val id: Int) {
  f0(0), f1(1), f2(2), f3(3), f4(4),
  f5(5), f6(6), f7(7), f8(8), f9(9),
  f10(10), f11(11), f12(12), f13(13), f14(14),
  f15(15), f16(16), f17(17), f18(18), f19(19),
  f20(20), f21(21), f22(22), f23(23), f24(24),
  f25(25), f26(26), f27(27), f28(28), f29(29),
  f30(30), f31(31)
}

internal fun Int.toHex() = String.format("%08X", this)

internal fun Int.toLittleEndian(): Int {
  return (this and 0xFF shl 24) or
    (this and 0xFF00 shl 8) or
    (this and 0xFF0000 ushr 8) or
    (this and 0xFF000000.toInt() ushr 24)
}
