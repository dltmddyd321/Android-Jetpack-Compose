package com.example.composeunderstand

enum class CalAction(val info: String, val symbol: String) {
    Plus("더하기", "+"),
    Minus("빼기", "-"),
    Divide("나누기", "/"),
    Multiply("곱하기", "*"),
    AllClear("전체삭제", "AC"),
    Delete("지우기", "del"),
    Calculate("계산하기", "=")
}