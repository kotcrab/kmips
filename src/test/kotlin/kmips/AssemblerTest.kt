package kmips

import junit.framework.TestCase.assertEquals
import kmips.FpuReg.f12
import kmips.FpuReg.f24
import kmips.FpuReg.f4
import kmips.Reg.a0
import kmips.Reg.s0
import kmips.Reg.sp
import kmips.Reg.t0
import kmips.Reg.zero
import org.junit.Assert
import org.junit.Test

class AssemblerTest {
  @Test
  fun testLb() = testInstruction("809000CD", { lb(s0, 0xCD, a0) })

  @Test
  fun testLbu() = testInstruction("909000CD", { lbu(s0, 0xCD, a0) })

  @Test
  fun testSb() = testInstruction("A09000CD", { sb(s0, 0xCD, a0) })

  @Test
  fun testLh() = testInstruction("849000CD", { lh(s0, 0xCD, a0) })

  @Test
  fun testLhu() = testInstruction("949000CD", { lhu(s0, 0xCD, a0) })

  @Test
  fun testSh() = testInstruction("A49000CD", { sh(s0, 0xCD, a0) })

  @Test
  fun testLw() = testInstruction("8C9000CD", { lw(s0, 0xCD, a0) })

  @Test
  fun testSw() = testInstruction("AC9000CD", { sw(s0, 0xCD, a0) })

  @Test
  fun testLwl() = testInstruction("889000CD", { lwl(s0, 0xCD, a0) })

  @Test
  fun testLwr() = testInstruction("989000CD", { lwr(s0, 0xCD, a0) })

  @Test
  fun testSwl() = testInstruction("A89000CD", { swl(s0, 0xCD, a0) })

  @Test
  fun testSwr() = testInstruction("B89000CD", { swr(s0, 0xCD, a0) })

  @Test
  fun testLl() = testInstruction("C09000CD", { ll(s0, 0xCD, a0) })

  @Test
  fun testSc() = testInstruction("E09000CD", { sc(s0, 0xCD, a0) })

  @Test
  fun testAddi() = testInstruction("209000CD", { addi(s0, a0, 0xCD) })

  @Test
  fun testAddiu() = testInstruction("249000CD", { addiu(s0, a0, 0xCD) })

  @Test
  fun testSlti() = testInstruction("289000CD", { slti(s0, a0, 0xCD) })

  @Test
  fun testSltiu() = testInstruction("2C9000CD", { sltiu(s0, a0, 0xCD) })

  @Test
  fun testAndi() = testInstruction("309000CD", { andi(s0, a0, 0xCD) })

  @Test
  fun testOri() = testInstruction("349000CD", { ori(s0, a0, 0xCD) })

  @Test
  fun testXori() = testInstruction("389000CD", { xori(s0, a0, 0xCD) })

  @Test
  fun testLui() = testInstruction("3C1000CD", { lui(s0, 0xCD) })

  @Test
  fun testAdd() = testInstruction("00888020", { add(s0, a0, t0) })

  @Test
  fun testAddu() = testInstruction("00888021", { addu(s0, a0, t0) })

  @Test
  fun testSub() = testInstruction("00888022", { sub(s0, a0, t0) })

  @Test
  fun testSubu() = testInstruction("00888023", { subu(s0, a0, t0) })

  @Test
  fun testSlt() = testInstruction("0088802A", { slt(s0, a0, t0) })

  @Test
  fun testSltu() = testInstruction("0088802B", { sltu(s0, a0, t0) })

  @Test
  fun testAnd() = testInstruction("00888024", { and(s0, a0, t0) })

  @Test
  fun testOr() = testInstruction("00888025", { or(s0, a0, t0) })

  @Test
  fun testXor() = testInstruction("00888026", { xor(s0, a0, t0) })

  @Test
  fun testNor() = testInstruction("00888027", { nor(s0, a0, t0) })

  @Test
  fun testSll() = testInstruction("00048440", { sll(s0, a0, 0x11) })

  @Test
  fun testSrl() = testInstruction("00048442", { srl(s0, a0, 0x11) })

  @Test
  fun testSra() = testInstruction("00048443", { sra(s0, a0, 0x11) })

  @Test
  fun testSllv() = testInstruction("01048004", { sllv(s0, a0, t0) })

  @Test
  fun testSrlv() = testInstruction("01048006", { srlv(s0, a0, t0) })

  @Test
  fun testSrav() = testInstruction("01048007", { srav(s0, a0, t0) })

  @Test
  fun testMult() = testInstruction("02040018", { mult(s0, a0) })

  @Test
  fun testMultu() = testInstruction("02040019", { multu(s0, a0) })

  @Test
  fun testDiv() = testInstruction("0204001A", { div(s0, a0) })

  @Test
  fun testDivu() = testInstruction("0204001B", { divu(s0, a0) })

  @Test
  fun testMfhi() = testInstruction("00008010", { mfhi(s0) })

  @Test
  fun testMthi() = testInstruction("02000011", { mthi(s0) })

  @Test
  fun testMflo() = testInstruction("00008012", { mflo(s0) })

  @Test
  fun testMtlo() = testInstruction("02000013", { mtlo(s0) })

  @Test
  fun testJ() = testInstruction("0A3F48E8", { j(0x08FD23A0) }, 0x0896D6E4)

  @Test
  fun testJal() = testInstruction("0E3F48E8", { jal(0x08FD23A0) }, 0x0896D6E4)

  @Test
  fun testJr() = testInstruction("00800008", { jr(a0) })

  @Test
  fun testJalr() = testInstruction("0200F809", { jalr(s0) })

  @Test
  fun testJalr2() = testInstruction("02002009", { jalr(a0, s0) })

  @Test
  fun testBeq() = testBranchInstruction("12040001") { beq(s0, a0, it) }

  @Test
  fun testBne() = testBranchInstruction("16040001") { bne(s0, a0, it) }

  @Test
  fun testBlez() = testBranchInstruction("1A000001") { blez(s0, it) }

  @Test
  fun testBgtz() = testBranchInstruction("1E000001") { bgtz(s0, it) }

  @Test
  fun testBeql() = testBranchInstruction("52040001") { beql(s0, a0, it) }

  @Test
  fun testBnel() = testBranchInstruction("56040001") { bnel(s0, a0, it) }

  @Test
  fun testBlezl() = testBranchInstruction("5A000001") { blezl(s0, it) }

  @Test
  fun testBgtzl() = testBranchInstruction("5E000001") { bgtzl(s0, it) }

  @Test
  fun testBltz() = testBranchInstruction("06000001") { bltz(s0, it) }

  @Test
  fun testBgez() = testBranchInstruction("06010001") { bgez(s0, it) }

  @Test
  fun testBltzal() = testBranchInstruction("06100001") { bltzal(s0, it) }

  @Test
  fun testBgezal() = testBranchInstruction("06110001") { bgezal(s0, it) }

  @Test
  fun testBltzl() = testBranchInstruction("06020001") { bltzl(s0, it) }

  @Test
  fun testBgezl() = testBranchInstruction("06030001") { bgezl(s0, it) }

  @Test
  fun testBltzall() = testBranchInstruction("06120001") { bltzall(s0, it) }

  @Test
  fun testBgezall() = testBranchInstruction("06130001") { bgezall(s0, it) }

  @Test
  fun testSyscall() = testInstruction("0033734C", { syscall(0xCDCD) })

  @Test
  fun testBreak() = testInstruction("0033734D", { `break`(0xCDCD) })

  @Test
  fun testTge() = testInstruction("02048030", { tge(s0, a0) })

  @Test
  fun testTgeu() = testInstruction("02048031", { tgeu(s0, a0) })

  @Test
  fun testTlt() = testInstruction("02048032", { tlt(s0, a0) })

  @Test
  fun testTltu() = testInstruction("02048033", { tltu(s0, a0) })

  @Test
  fun testTeq() = testInstruction("02048034", { teq(s0, a0) })

  @Test
  fun testTne() = testInstruction("02048036", { tne(s0, a0) })

  @Test
  fun testTgei() = testInstruction("060800CD", { tgei(s0, 0xCD) })

  @Test
  fun testTgeiu() = testInstruction("060900CD", { tgeiu(s0, 0xCD) })

  @Test
  fun testTlti() = testInstruction("060A00CD", { tlti(s0, 0xCD) })

  @Test
  fun testTltiu() = testInstruction("060B00CD", { tltiu(s0, 0xCD) })

  @Test
  fun testTeqi() = testInstruction("060C00CD", { teqi(s0, 0xCD) })

  @Test
  fun testTnei() = testInstruction("060E00CD", { tnei(s0, 0xCD) })

  @Test
  fun testSync() = testInstruction("0000000F", { sync(0) })

  @Test
  fun testNop() = testInstruction("00000000", { nop() })

  @Test
  fun testLwc1() = testInstruction("C48C00CD", { lwc1(f12, 0xCD, a0) })

  @Test
  fun testSwc1() = testInstruction("E48C00CD", { swc1(f12, 0xCD, a0) })

  @Test
  fun testMtc1() = testInstruction("44906000", { mtc1(s0, f12) })

  @Test
  fun testMfc1() = testInstruction("44106000", { mfc1(s0, f12) })

  @Test
  fun testCtc1() = testInstruction("44D06000", { ctc1(s0, f12) })

  @Test
  fun testCfc1() = testInstruction("44506000", { cfc1(s0, f12) })

  @Test
  fun testFpuAddS() = testInstruction("46186100", { add.s(f4, f12, f24) })

  @Test
  fun testFpuAddD() = testInstruction("46386100", { add.d(f4, f12, f24) })

  @Test
  fun testFpuSubS() = testInstruction("46186101", { sub.s(f4, f12, f24) })

  @Test
  fun testFpuSubD() = testInstruction("46386101", { sub.d(f4, f12, f24) })

  @Test
  fun testFpuMulS() = testInstruction("46186102", { mul.s(f4, f12, f24) })

  @Test
  fun testFpuMulD() = testInstruction("46386102", { mul.d(f4, f12, f24) })

  @Test
  fun testFpuDivS() = testInstruction("46186103", { div.s(f4, f12, f24) })

  @Test
  fun testFpuDivD() = testInstruction("46386103", { div.d(f4, f12, f24) })

  @Test
  fun testFpuAbsS() = testInstruction("46006105", { abs.s(f4, f12) })

  @Test
  fun testFpuAbsD() = testInstruction("46206105", { abs.d(f4, f12) })

  @Test
  fun testFpuNegS() = testInstruction("46006107", { neg.s(f4, f12) })

  @Test
  fun testFpuNegD() = testInstruction("46206107", { neg.d(f4, f12) })

  @Test
  fun testFpuSqrtS() = testInstruction("46006104", { sqrt.s(f4, f12) })

  @Test
  fun testFpuSqrtD() = testInstruction("46206104", { sqrt.d(f4, f12) })

  @Test
  fun testFpuRoundWS() = testInstruction("4600610C", { round.w.s(f4, f12) })

  @Test
  fun testFpuRoundWD() = testInstruction("4620610C", { round.w.d(f4, f12) })

  @Test
  fun testFpuTruncWS() = testInstruction("4600610D", { trunc.w.s(f4, f12) })

  @Test
  fun testFpuTruncWD() = testInstruction("4620610D", { trunc.w.d(f4, f12) })

  @Test
  fun testFpuCeilWS() = testInstruction("4600610E", { ceil.w.s(f4, f12) })

  @Test
  fun testFpuCeilWD() = testInstruction("4620610E", { ceil.w.d(f4, f12) })

  @Test
  fun testFpuFloorWS() = testInstruction("4600610F", { floor.w.s(f4, f12) })

  @Test
  fun testFpuFloorWD() = testInstruction("4620610F", { floor.w.d(f4, f12) })

  @Test
  fun testFpuCondEqS() = testInstruction("460C2032", { c.eq.s(f4, f12) })

  @Test
  fun testFpuCondEqD() = testInstruction("462C2032", { c.eq.d(f4, f12) })

  @Test
  fun testFpuCondLeS() = testInstruction("460C203E", { c.le.s(f4, f12) })

  @Test
  fun testFpuCondLeD() = testInstruction("462C203E", { c.le.d(f4, f12) })

  @Test
  fun testFpuCondLtS() = testInstruction("460C203C", { c.lt.s(f4, f12) })

  @Test
  fun testFpuCondLtD() = testInstruction("462C203C", { c.lt.d(f4, f12) })

  @Test
  fun testFpuCvtSD() = testInstruction("46206120", { cvt.s.d(f4, f12) })

  @Test
  fun testFpuCvtSW() = testInstruction("46806120", { cvt.s.w(f4, f12) })

  @Test
  fun testFpuCvtDS() = testInstruction("46006121", { cvt.d.s(f4, f12) })

  @Test
  fun testFpuCvtDW() = testInstruction("46806121", { cvt.d.w(f4, f12) })

  @Test
  fun testFpuCvtWS() = testInstruction("46006124", { cvt.w.s(f4, f12) })

  @Test
  fun testFpuCvtWD() = testInstruction("46206124", { cvt.w.d(f4, f12) })

  @Test
  fun testFpuMovS() = testInstruction("46006106", { mov.s(f4, f12) })

  @Test
  fun testFpuMovD() = testInstruction("46206106", { mov.d(f4, f12) })

  @Test
  fun testBc1f() = testBranchInstruction("45000001") { bc1f(it) }

  @Test
  fun testBc1t() = testBranchInstruction("45010001") { bc1t(it) }

  @Test
  fun testBc1tl() = testBranchInstruction("45030001") { bc1tl(it) }

  @Test
  fun testBc1fl() = testBranchInstruction("45020001") { bc1fl(it) }

  @Test
  fun testB() = testBranchInstruction("10000001") { b(it) }

  @Test
  fun testBlt() = testBranchInstruction("0090082A14200001") { blt(a0, s0, it) }

  @Test
  fun testBge() = testBranchInstruction("0090082A10200001") { bge(a0, s0, it) }

  @Test
  fun testBgt() = testBranchInstruction("0204082A14200001") { bgt(a0, s0, it) }

  @Test
  fun testBle() = testBranchInstruction("0204082A10200001") { ble(a0, s0, it) }

  @Test
  fun testNeg() = testInstruction("00048022", { neg(s0, a0) })

  @Test
  fun testNot() = testInstruction("00808027", { not(s0, a0) })

  @Test
  fun testLa() = testInstruction("3C10ABAB3610CDCD", { la(s0, 0xABABCDCD.toInt()) })

  @Test
  fun testLiAsOr() = testInstruction("341000CD", { li(s0, 0xCD) })

  @Test
  fun testLiAsOr2() = testInstruction("3410FFF0", { li(s0, 0xFFF0) })

  @Test
  fun testLiAsAddiu() = testInstruction("2410FFF0", { li(s0, -16) })

  @Test
  fun testMove() = testInstruction("00808021", { move(s0, a0) })

  @Test
  fun testSge() = testInstruction("0088802A3401000100308023", { sge(s0, a0, t0) })

  @Test
  fun testSgt() = testInstruction("0104802A", { sgt(s0, a0, t0) })

  @Test(expected = IllegalStateException::class)
  fun testJIllegalLsbBits() = testInstruction("0A3F48E8", { j(0x08FD23A1) }, 0x0896D6E4)

  @Test(expected = IllegalStateException::class)
  fun testJIllegalMsbBits() = testInstruction("0A3F48E8", { j(0xF8FD23A0.toInt()) }, 0x0896D6E4)

  @Test
  fun testAddSigned() = testInstruction("23BDFFFC", { addi(sp, sp, -4) })

  @Test
  fun testData() = testInstruction("AABBCCDD", { data(0xAABBCCDD.toInt()) })

  private fun testInstruction(expected: String, instruction: Assembler.() -> Unit, startPc: Int = 0) {
    val assembler = Assembler(startPc, Endianness.Big)
    assembler.instruction()
    assertEquals(expected, assembler.assembleAsHexString())
  }

  private fun testBranchInstruction(expected: String, instruction: Assembler.(label: Label) -> Unit) {
    val assembler = Assembler(0, Endianness.Big)
    val testLabel = Label()
    assembler.instruction(testLabel)
    assembler.nop()
    assembler.label(testLabel)
    assertEquals(expected + "00000000", assembler.assembleAsHexString())
  }

  @Test
  fun testBranches() {
    val result = assemble(0x0896D6E4) {
      val label = Label()
      beq(a0, a0, label)
      nop()
      add(a0, a0, a0)
      label(label)
      add(s0, s0, s0)
    }.joinToString(separator = " ") { it.toHex() }
    assertEquals("02008410 00000000 20208400 20801002", result)
  }

  @Test
  fun testAssembleHelper() {
    val result = assemble(endianness = Endianness.Big) {
      sw(s0, 0xCD, a0)
    }
    Assert.assertArrayEquals(arrayOf(0xAC9000CD.toInt()), result.toTypedArray())
  }

  @Test
  fun testAssembleAsHexStringHelper() {
    val result = assembleAsHexString(endianness = Endianness.Big) {
      sw(s0, 0xCD, a0)
    }
    Assert.assertEquals("AC9000CD", result)
  }

  @Test
  fun testAssembleAsByteArrayHelper() {
    val result = assembleAsByteArray(endianness = Endianness.Big) {
      sw(s0, 0xCD, a0)
    }
    Assert.assertArrayEquals(arrayOf(0xAC, 0x90, 0x00, 0xCD).map { it.toByte() }.toByteArray(), result)
  }
}

class LabelTest {
  @Test(expected = IllegalStateException::class)
  fun testNotAssigned() {
    Label().address
  }

  @Test(expected = IllegalStateException::class)
  fun testTwiceAssigned() {
    val label = Label()
    label.address = 0
    label.address = 0
  }
}

class RInstructionTest {
  @Test
  fun testOpcode() {
    (0 until 0x3F).forEach {
      assertEquals(RInstruction(it, zero, zero, zero).assemble() ushr 26 and 0b111111, it)
    }
  }

  @Test(expected = IllegalStateException::class)
  fun testIllegalOpcode() {
    RInstruction(0x3F + 1, zero, zero, zero).assemble()
  }

  @Test
  fun testShift() {
    (0 until 0x1F).forEach {
      assertEquals(it, RInstruction(0, zero, zero, zero, it).assemble() ushr 6 and 0b11111)
    }
  }

  @Test(expected = IllegalStateException::class)
  fun testIllegalShift() {
    RInstruction(0, zero, zero, zero, 0x1F + 1).assemble()
  }

  @Test
  fun testFunct() {
    (0 until 0x3F).forEach {
      assertEquals(it, RInstruction(0, zero, zero, zero, 0, it).assemble() and 0b111111)
    }
  }

  @Test(expected = IllegalStateException::class)
  fun testIllegalFunct() {
    RInstruction(0, zero, zero, zero, 0, 0x3F + 1).assemble()
  }

  @Test
  fun testRsReg() {
    Reg.values().forEach {
      assertEquals(it.id, RInstruction(0, it, zero, zero).assemble() ushr 21 and 0b11111)
    }
  }

  @Test
  fun testRtReg() {
    Reg.values().forEach {
      assertEquals(it.id, RInstruction(0, zero, it, zero).assemble() ushr 16 and 0b11111)
    }
  }

  @Test
  fun testRdReg() {
    Reg.values().forEach {
      assertEquals(it.id, RInstruction(0, zero, zero, it).assemble() ushr 11 and 0b11111)
    }
  }
}

class IInstructionTest {
  @Test
  fun testOpcode() {
    (0 until 0x3F).forEach {
      assertEquals(it, IInstruction(it, zero, zero, 0).assemble() ushr 26 and 0b111111)
    }
  }

  @Test(expected = IllegalStateException::class)
  fun testIllegalOpcode() {
    IInstruction(0x3F + 1, zero, zero, 0).assemble()
  }

  @Test
  fun testImm() {
    (0 until 0xFFFF).forEach {
      assertEquals(it, IInstruction(0, zero, zero, it).assemble() and 0xFFFF)
    }
  }

  @Test
  fun testRsReg() {
    Reg.values().forEach {
      assertEquals(it.id, IInstruction(0, it, zero, 0).assemble() ushr 21 and 0b11111)
    }
  }

  @Test
  fun testRtReg() {
    Reg.values().forEach {
      assertEquals(it.id, IInstruction(0, zero, it, 0).assemble() ushr 16 and 0b11111)
    }
  }
}

class JInstructionTest {
  @Test
  fun testOpcode() {
    (0 until 0x3F).forEach {
      assertEquals(it, JInstruction(it, 0).assemble() ushr 26 and 0b111111)
    }
  }

  @Test(expected = IllegalStateException::class)
  fun testIllegalOpcode() {
    JInstruction(0x3F + 1, 0).assemble()
  }

  @Test
  fun testAddress() {
    (0 until 0x10000).forEach {
      assertEquals(it, JInstruction(0, it).assemble() and 0x3FFFFFF)
    }
  }

  @Test(expected = IllegalStateException::class)
  fun testIllegalAddress() {
    JInstruction(0, 0x3FFFFFF + 1).assemble()
  }
}

class NopInstructionTest {
  @Test
  fun testNop() {
    assertEquals(0, NopInstruction().assemble())
  }
}

class TestIntHelpers {
  @Test
  fun testToHex() {
    assertEquals("00000000", 0.toHex())
    assertEquals("CDCDCDCD", 0xCDCDCDCD.toInt().toHex())
    assertEquals("FFFFFFFF", 0xFFFFFFFF.toInt().toHex())
  }

  @Test
  fun testToLittleEndian() {
    assertEquals("44332211", 0x11223344.toLittleEndian().toHex())
  }
}
