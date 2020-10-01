package com.android.lvicto.zombie.coroutines.retailxsimu.viewmodel

import com.android.lvicto.zombie.coroutines.retailxsimu.data.StlResult

class StlResultsViewModel {

    private val urls = arrayListOf(
            "https://secure-images.nike.com/is/image/DotCom/AT3378_060",
            "https://secure-images.nike.com/is/image/DotCom/AT3378_053",
            "https://secure-images.nike.com/is/image/DotCom/AT3378_104",
            "https://secure-images.nike.com/is/image/DotCom/AT3378_001",
            "https://secure-images.nike.com/is/image/DotCom/AT3378_011",
            "https://secure-images.nike.com/is/image/DotCom/AT3378_034",
            "https://secure-images.nike.com/is/image/DotCom/AT3378_510",
            "https://secure-images.nike.com/is/image/DotCom/AT3378_040",
            "https://secure-images.nike.com/is/image/DotCom/AT3378_301",
            "https://secure-images.nike.com/is/image/DotCom/AT3378_030",
            "https://secure-images.nike.com/is/image/DotCom/AT3378_471",
            "https://qwerty",
//            "https://secure-images.nike.com/is/image/DotCom/AT3378_010",
            "https://secure-images.nike.com/is/image/DotCom/AT3378_101 (made invalid on purpose)" // failed on purpose
    )

    fun getStlResults() = arrayListOf(
            StlResult("image1", urls[0])
            , StlResult("image2", urls[1])
            , StlResult("image3", urls[2])
            , StlResult("image4", urls[3])
            , StlResult("image5", urls[4])
            , StlResult("image6", urls[5])
            , StlResult("image7", urls[6])
            , StlResult("image8", urls[7])
            , StlResult("image9", urls[8])
            , StlResult("image10", urls[9])
            , StlResult("image11", urls[10])
            , StlResult("image12", urls[11])
            , StlResult("image13", urls[12])
    )

    // for debugging - to test the speed
    fun getStlResults2(): ArrayList<StlResult> {
        val res = arrayListOf<StlResult>()
        for (i in 1..200){
            getStlResults().forEach {
                res.add(it)
            }
        }
        return res
    }

}