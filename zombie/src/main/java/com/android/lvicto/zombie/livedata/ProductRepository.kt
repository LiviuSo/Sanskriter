package com.android.lvicto.zombie.livedata

class ProductRepository {
    private val products: List<Product> = arrayListOf(
            Product("chi1", "111", "Chiloti de dama",
                    hashMapOf(
                            10 to arrayListOf(true, false, false, false, true),
                            20 to arrayListOf(true, true, false, false, false),
                            30 to arrayListOf(true, true, true, true, true),
                            40 to arrayListOf(false, false, false, false, false)
                    )),
            Product("chi2", "222", "Chiloti de barbat",
                    hashMapOf(
                            10 to arrayListOf(true, false, false, false, true),
                            20 to arrayListOf(true, true, false, false, false),
                            30 to arrayListOf(true, true, true, true, true),
                            40 to arrayListOf(false, false, false, false, false)
                    )),
            Product("sut1", "333", "Sutien de domnisoara",
                    hashMapOf(
                            10 to arrayListOf(true, false, false, false, true),
                            20 to arrayListOf(true, true, false, false, false),
                            30 to arrayListOf(true, true, true, true, true),
                            40 to arrayListOf(false, false, false, false, false)
                    )),
            Product("sut2", "444", "Sutien extra",
                    hashMapOf(
                            10 to arrayListOf(true, false, false, false, true),
                            20 to arrayListOf(true, true, false, false, false),
                            30 to arrayListOf(true, true, true, true, true),
                            40 to arrayListOf(false, false, false, false, false)
                    )),
            Product("pan1", "555", "Pantalon dama",
                    hashMapOf(
                            10 to arrayListOf(false, false, false, false, false),
                            20 to arrayListOf(false, false, false, false, false),
                            30 to arrayListOf(false, false, false, false, false),
                            40 to arrayListOf(false, false, false, false, false)
                    )),
            Product("pan2", "666", "Pantalon barbat",
                    hashMapOf(
                            10 to arrayListOf(true, false, false, false, true),
                            20 to arrayListOf(true, true, false, false, false),
                            30 to arrayListOf(true, true, true, true, true),
                            40 to arrayListOf(false, false, false, false, false)
                    )),
            Product("tri1", "777", "Tricou barbat",
                    hashMapOf(
                            10 to arrayListOf(true, false, false, false, true),
                            20 to arrayListOf(true, true, false, false, false),
                            30 to arrayListOf(true, true, true, true, true),
                            40 to arrayListOf(false, false, false, false, false)
                    )),
            Product("cam1", "888", "Camasa dama",
                    hashMapOf(
                            10 to arrayListOf(false, false, false, false, false),
                            20 to arrayListOf(true, true, false, false, false),
                            30 to arrayListOf(true, true, true, true, true),
                            40 to arrayListOf(false, false, false, false, false)
                    ))
    )

    fun getProductByStyle(style: String?): Product? = products.find {
        it.styleColor == style
    }

    fun getProductByPid(pid: String?): Product? = products.find { it.pid == pid }

}