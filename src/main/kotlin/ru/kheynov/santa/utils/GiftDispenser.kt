package ru.kheynov.santa.utils

interface GiftDispenser {
    fun getRandomDistribution(users: List<String>): List<Pair<String, String>>
}

class SimpleCycleGiftDispenser : GiftDispenser {
    override fun getRandomDistribution(users: List<String>): List<Pair<String, String>> {
        val result: MutableList<Pair<String, String>> = mutableListOf()
        val tmp = users.toMutableList()
        tmp.shuffle()
        tmp.forEachIndexed { index, userId ->
            if (index == users.size - 1) {
                result.add(userId to tmp[0])
            } else {
                result.add(userId to tmp[index + 1])
            }
        }
        return result.toList()
    }
}