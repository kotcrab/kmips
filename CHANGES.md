#### Version: 1.7

#### Version: 1.6
- Updated to Kotlin 1.6.10

#### Version: 1.5

- Updated to Kotlin 1.5.30
- JVM target is now JVM 11

#### Version: 1.4

- Fixed incorrect assembly for pseudo-instructions: `blt`, `bge`, `bgt`, `ble`
- Updated to Kotlin 1.3.50

#### Version: 1.3

- Updated to Kotlin 1.3.20

#### Version: 1.2

- Added MIPS II instruction set (including FPU instructions)

#### Version: 1.1

- Added FPU instruction set
- Updated instruction set to closer match reference document
- `Int`s are now used as asm instructions argument types instead of `Short`s
- `li` pseudo instruction will get converted to `addiu`  for negative values and to `ori` for positive values

#### Version: 1.0

- Initial release
