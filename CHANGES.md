#### Version: 1.1
- Added FPU instruction set
- Updated instruction set to closer match reference document
- `Int`s are now used as asm instructions argument types instead of `Short`s
- `li` pseudo instruction will get converted to `addiu`  for negative values and to `ori` for positive values

#### Version: 1.0
-Initial release