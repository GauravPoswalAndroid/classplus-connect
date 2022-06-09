package com.fankonnect.app.base

interface Mapper<in Src, out Des> {
    fun map(srcObject: Src): Des
}