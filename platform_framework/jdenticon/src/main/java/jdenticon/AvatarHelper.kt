package jdenticon


/**
 * Created by wanglin on 2018/7/17.
 */

class AvatarHelper private constructor() {
    
    companion object {
        val instance = SingletonHolder.holder
    }

    private object SingletonHolder {
        val holder = AvatarHelper()
    }

    fun getAvatarSvg(hash: String, size: Int, padding: Float?): String {
        return Jdenticon.toSvg(hash, size, padding)

    }

}
