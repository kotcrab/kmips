package kmips

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kmips.Reg.*
import org.junit.Assert
import org.junit.Assert.assertFalse
import org.junit.Test


/** @author Kotcrab */
class AssemblerTest {
    @Test fun testAdd() = testInstruction("00888020", { add(s0, a0, t0) })
    @Test fun testAddi() = testInstruction("209000CD", { addi(s0, a0, 0xCD) })
    @Test fun testAddiu() = testInstruction("249000CD", { addiu(s0, a0, 0xCD) })
    @Test fun testAddu() = testInstruction("00888021", { addu(s0, a0, t0) })
    @Test fun testAnd() = testInstruction("00888024", { and(s0, a0, t0) })
    @Test fun testAndi() = testInstruction("309000CD", { andi(s0, a0, 0xCD) })
    @Test fun testBeq() = testBranchInstruction("12040001", { beq(s0, a0, it) })
    @Test fun testBgez() = testBranchInstruction("06010001", { bgez(s0, it) })
    @Test fun testBlez() = testBranchInstruction("1A000001", { blez(s0, it) })
    @Test fun testBltz() = testBranchInstruction("06000001", { bltz(s0, it) })
    @Test fun testBne() = testBranchInstruction("16040001", { bne(s0, a0, it) })
    @Test fun testDiv() = testInstruction("0204001A", { div(s0, a0) })
    @Test fun testDivu() = testInstruction("0204001B", { divu(s0, a0) })
    @Test fun testJ() = testInstruction("0A3F48E8", { j(0x08FD23A0) }, 0x0896D6E4)
    @Test fun testJal() = testInstruction("0E3F48E8", { jal(0x08FD23A0) }, 0x0896D6E4)
    @Test fun testJr() = testInstruction("00800008", { jr(a0) })
    @Test fun testLb() = testInstruction("809000CD", { lb(s0, 0xCD, a0) })
    @Test fun testLui() = testInstruction("3C1000CD", { lui(s0, 0xCD) })
    @Test fun testLw() = testInstruction("8C9000CD", { lw(s0, 0xCD, a0) })
    @Test fun testMfhi() = testInstruction("00008010", { mfhi(s0) })
    @Test fun testMflo() = testInstruction("00008012", { mflo(s0) })
    @Test fun testMult() = testInstruction("02040018", { mult(s0, a0) })
    @Test fun testMultu() = testInstruction("02040019", { multu(s0, a0) })
    @Test fun testNop() = testInstruction("00000000", { nop() })
    @Test fun testOr() = testInstruction("00888025", { or(s0, a0, t0) })
    @Test fun testOri() = testInstruction("349000CD", { ori(s0, a0, 0xCD) })
    @Test fun testSb() = testInstruction("A09000CD", { sb(s0, 0xCD, a0) })
    @Test fun testSll() = testInstruction("00048440", { sll(s0, a0, 0x11) })
    @Test fun testSllv() = testInstruction("01048004", { sllv(s0, a0, t0) })
    @Test fun testSlt() = testInstruction("0088802A", { slt(s0, a0, t0) })
    @Test fun testSlti() = testInstruction("289000CD", { slti(s0, a0, 0xCD) })
    @Test fun testSltiu() = testInstruction("2C9000CD", { sltiu(s0, a0, 0xCD) })
    @Test fun testSltu() = testInstruction("0088802B", { sltu(s0, a0, t0) })
    @Test fun testSra() = testInstruction("00048443", { sra(s0, a0, 0x11) })
    @Test fun testSrl() = testInstruction("00048442", { srl(s0, a0, 0x11) })
    @Test fun testSrlv() = testInstruction("01048006", { srlv(s0, a0, t0) })
    @Test fun testSub() = testInstruction("00888022", { sub(s0, a0, t0) })
    @Test fun testSubu() = testInstruction("00888023", { subu(s0, a0, t0) })
    @Test fun testSw() = testInstruction("AC9000CD", { sw(s0, 0xCD, a0) })
    @Test fun testXor() = testInstruction("00888026", { xor(s0, a0, t0) })
    @Test fun testXori() = testInstruction("389000CD", { xori(s0, a0, 0xCD) })

    @Test(expected = IllegalStateException::class)
    fun testJIllegalLsbBits() = testInstruction("0A3F48E8", { j(0x08FD23A1) }, 0x0896D6E4)

    @Test(expected = IllegalStateException::class)
    fun testJIllegalMsbBits() = testInstruction("0A3F48E8", { j(0xF8FD23A0.toInt()) }, 0x0896D6E4)

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
        }.map { it.toHex() }.joinToString(separator = " ")
        assertEquals("02008410 00000000 20208400 20801002", result)
    }

    @Test
    fun testAssembleHelper() {
        val result = assemble {
            nop()
        }
        Assert.assertArrayEquals(arrayOf(0), result.toTypedArray())
    }

    @Test
    fun testAssembleAsHexStringHelper() {
        val result = assembleAsHexString {
            nop()
        }
        Assert.assertEquals("00000000", result)
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

    @Test
    fun testAssignedSet() {
        val label = Label()
        label.address = 0
        assertTrue(label.assigned)
    }

    @Test
    fun testAssignedNotSet() {
        val label = Label()
        assertFalse(label.assigned)
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
        values().forEach {
            assertEquals(it.id, RInstruction(0, zero, it, zero).assemble() ushr 21 and 0b11111)
        }
    }

    @Test
    fun testRtReg() {
        values().forEach {
            assertEquals(it.id, RInstruction(0, zero, zero, it).assemble() ushr 16 and 0b11111)
        }
    }

    @Test
    fun testRdReg() {
        values().forEach {
            assertEquals(it.id, RInstruction(0, it, zero, zero).assemble() ushr 11 and 0b11111)
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

    @Test()
    fun testImm() {
        (0 until 0xFFFF).forEach {
            assertEquals(it, IInstruction(0, zero, zero, it).assemble() and 0xFFFF)
        }
    }

    @Test(expected = IllegalStateException::class)
    fun testIllegalImm() {
        IInstruction(0, zero, zero, 0xFFFF + 1).assemble()
    }

    @Test
    fun testRsReg() {
        values().forEach {
            assertEquals(it.id, IInstruction(0, it, zero, 0).assemble() ushr 21 and 0b11111)
        }
    }

    @Test
    fun testRtReg() {
        values().forEach {
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
