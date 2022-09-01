package dev.uten2c.strobo.option

/**
 * @param replaceGiveCommand Giveコマンドをサーバーサイドアイテムの補完などに対応させたものに置き換えます
 * @param replaceSummonCommand Summonコマンドをサーバーサイドエンティティの補完などに対応させたものに置き換えます
 * @param disableStats 統計機能を無効化します
 * @param disableAdvancements 進捗機能を無効化します
 * @param disableRecipes レシピ機能を無効化します
 */
data class StroboOptions(
    @JvmField var replaceGiveCommand: Boolean = true,
    @JvmField var replaceSummonCommand: Boolean = true,
    @JvmField var enableVersionCommand: Boolean = true,
    @JvmField var disableStats: Boolean = false,
    @JvmField var disableAdvancements: Boolean = false,
    @JvmField var disableRecipes: Boolean = false,
) {
    /**
     * disable*をすべて有効化します
     */
    fun disableAll() {
        disableStats = true
        disableAdvancements = true
        disableRecipes = true
    }
}
