package com.angelstudio.newsapp.internal

import android.util.Log


fun edit(content: String?): CharSequence? {

var cnt =content?.split("[+")

    return cnt?.get(0)
}
