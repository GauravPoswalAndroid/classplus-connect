package com.classplus.connect.base

interface Mapper<in Src, out Des> {
    fun map(srcObject: Src): Des
}