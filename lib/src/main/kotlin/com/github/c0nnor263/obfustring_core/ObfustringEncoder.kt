package io.github.c0nnor263.obfustring_core

open class ObfustringEncoder(private val key: String) {
    open fun vigenere(string: String, encrypt: Boolean = false): String = with(string) {
        val sb = StringBuilder()
        var index = 0
        var leftEscapeSymbols = 0
        string.forEachIndexed { currentIndex, currentChar ->
            if (leftEscapeSymbols != 0) {
                leftEscapeSymbols--
                sb.append(currentChar)
                return@forEachIndexed
            }

            val symbolCodeAdd = when (currentChar) {
                in 'A'..'Z' -> 65
                in 'a'..'z' -> 97
                '\$' -> {
                    var indexEmpty: Int = -1
                    run index@{
                        string.forEachIndexed loop@{ index, char ->
                            if (index > currentIndex &&
                                (char == '"' || char == ' ' ||
                                        (if (string[currentIndex + 1] == '{') char == '}' else false))
                            ) {
                                indexEmpty = index
                                return@index
                            }
                        }
                    }
                    leftEscapeSymbols = indexEmpty - currentIndex
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
                (currentChar.code + key[index].code - encryptInt) % 26
            } else {
                (currentChar.code - key[index].code + decryptInt) % 26
            }

            index++
            if (index > key.length - 1) index = 0
            sb.append(value.plus(symbolCodeAdd).toChar())
        }
        return sb.toString()
    }
}