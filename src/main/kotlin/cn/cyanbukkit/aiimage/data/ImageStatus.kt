package cn.cyanbukkit.aiimage.data


enum class ImageStatus(
    val chinese: String) {

    IN_WAIT("§d等待中"),
    IN_CREATE("§c创建中"),
    SUCCESS("§a创建成功"),
    FAIL("§4失败"),
    CANCEL("§7已取消"),
    DISABLE("§4§被禁用");
//    success
    companion object {
        fun findById(id: String): ImageStatus =
            ImageStatus.values().firstOrNull { it.chinese == id } ?: IN_WAIT
    }

}