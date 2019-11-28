package com.angelstudio.newsapp.internal




fun edit(content: String?): CharSequence? {

var cnt =content?.split("[+")

    var cntnt = cnt?.get(0)

    if(cntnt != null){
        if(cntnt.contains("[[getSimpleString")){
            return null
        }
    }
    return cntnt
}

