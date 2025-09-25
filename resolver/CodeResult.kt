package com.example.app.scanner.resolver

import com.example.app.data.enums.status.TaskStatus

data class CodeResult(
    val code: String,
    val status: TaskStatus,
    val msg: String,
    val typedList: Any?, // Si es positivo, se devuelve en forma de arrayListOf(Any)
)