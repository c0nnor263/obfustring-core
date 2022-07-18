package io.github.c0nnor263.obfustring_core

data class ObfustringEncoder(private val key: String) {
    fun vigenere(string: String, encrypt: Boolean = false): String = with(string) {
        val sb = StringBuilder()
        var keyIndex = 0
        var leftSkipSymbols = 0
        string.forEachIndexed { currentIndex, currentChar ->
            if (leftSkipSymbols != 0) {
                leftSkipSymbols--
                sb.append(currentChar)
                if (leftSkipSymbols == 0 && encrypt) {
                    sb.append("¦")
                }

                return@forEachIndexed
            }
//TODO no quotes
            val symbolCodeAdd = when (currentChar) {
                in 'A'..'Z' -> 65
                in 'a'..'z' -> 97
                '\$' -> {
                    var indexEmpty: Int = -1
                    run index@{
                        string.forEachIndexed loop@{ index, char ->
                            if (index >= currentIndex &&
                                (char == '"' || char == ' ' ||
                                        (if (string[currentIndex + 1] == '{') char == '}' else false))
                            ) {
                                indexEmpty = index
                                return@index
                            }
                        }
                    }
                    if(indexEmpty == -1){
                        indexEmpty = string.length -1
                    }
                    leftSkipSymbols = indexEmpty - currentIndex

                    sb.append('¦')
                    sb.append(currentChar)
                    return@forEachIndexed
                }
                '¦' -> {
                    var indexEmpty = -1
                    string.forEachIndexed loop@{ index, char ->
                        if (index > currentIndex && char == '¦') {
                            indexEmpty = index
                            return@loop
                        }
                    }

                    leftSkipSymbols = indexEmpty - currentIndex -1
                    return@forEachIndexed
                }
                '\\' -> {
                    leftSkipSymbols = 1
                    sb.append(currentChar)
                    return@forEachIndexed
                }
                else -> {
                    sb.append(currentChar)
                    return@forEachIndexed
                }
            }

            val encryptInt = 90
            val decryptInt = 26 + when (symbolCodeAdd) {
                65 -> 38
                else -> 0
            }

            val value = if (encrypt) {
                (currentChar.code + key[keyIndex].code - encryptInt) % 26
            } else {
                (currentChar.code - key[keyIndex].code + decryptInt) % 26
            }

            keyIndex++
            if (keyIndex > key.length - 1) keyIndex = 0
            sb.append(value.plus(symbolCodeAdd).toChar())
        }
        return sb.toString()
    }
}