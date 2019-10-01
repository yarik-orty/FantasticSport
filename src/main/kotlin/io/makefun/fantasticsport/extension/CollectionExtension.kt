package io.makefun.fantasticsport.extension

inline fun <T> Iterable<T>.sumByLong(selector: (T) -> Long): Long {
    return map { selector(it) }.sum()
}