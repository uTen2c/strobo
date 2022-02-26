package dev.uten2c.strobo.option

data class StroboOptions(
    @JvmField var replaceGiveCommand: Boolean = true,
    @JvmField var replaceSummonCommand: Boolean = true,
    @JvmField var disableStats: Boolean = false,
    @JvmField var disableAdvancements: Boolean = false,
    @JvmField var disableRecipes: Boolean = false,
) {
    fun disableAll() {
        disableStats = true
        disableAdvancements = true
        disableRecipes = true
    }
}
