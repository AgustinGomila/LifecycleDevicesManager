package com.example.app.scanner.resolver

class Trie<T : SearchType> {
    private data class Node<T>(
        val children: MutableMap<Char, Node<T>> = mutableMapOf(),
        var value: T? = null
    )

    private val root = Node<T>()

    // Inserta un prefijo con su valor asociado
    fun insert(value: T) {
        var currentNode = root
        var prefix = value.prefix.orEmpty().lowercase()
        for (char in prefix) {
            currentNode = currentNode.children.getOrPut(char) { Node() }
        }
        currentNode.value = value
    }

    // Busca el prefijo más largo en el Trie
    fun findLongestPrefix(input: String): T? {
        var currentNode = root
        var lastFoundValue: T? = null
        for (char in input) {
            currentNode = currentNode.children[char] ?: break
            currentNode.value?.let { lastFoundValue = it }
        }
        return lastFoundValue
    }
}