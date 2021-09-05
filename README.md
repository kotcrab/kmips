kmips
-----

kmips is a MIPS assembler that is invoked directly from Kotlin code. It implements 
MIPS II instruction set, including FPU (coprocessor 1) instructions. The main purpose of kmips
is to provide simple way of writing code patches for compiled executables. It was successfully used 
in few fan translations and game modding projects.


kmips is available from Maven Central repository
```groovy
compile "com.kotcrab.kmips:kmips:1.5"
```

##### Example code 
Fill 32 bytes of memory at address 0x08804100 with incrementing value
```kotlin
import kmips.Label
import kmips.Reg.*
import kmips.assemble

assemble(startPc = 0x8804000) {
    val loop = Label()
    val target = 0x08804100
    val bytes = 32

    la(s0, target)     // write target address
    li(t1, 0)          // loop counter
    li(t2, bytes)      // how many bytes to write
    label(loop)        // 'loop' label will be placed here
    sb(t1, 0, s0)      // store byte in memory at register s0 with offset 0
    addiu(t1, t1, 1)   // increment loop counter
    addiu(s0, s0, 1)   // increment memory address pointer
    bne(t1, t2, loop)  // jump to `loop` branch if not equal
    nop()              // ignoring branch delay slot for simplicity
}
```
Result
```
0x08804100: 00 01 02 03 | 04 05 06 07 | 08 09 0A 0B | 0C 0D 0E 0F
0x08804110: 10 11 12 13 | 14 15 16 17 | 18 19 1A 1B | 1C 1D 1E 1F
```

Specifying `startPc` (initial program counter) is necessary for calculating address of branch and jump instructions.

Alternatively to `assemble` which returns a `List<Int>` you can also use `assembleAsHexString` or 
`assembleAsByteArray`. In any case, the result is ready to be written 
into target executable. If you're executable is relocatable you might also need to manually update
relocation table. 
 
