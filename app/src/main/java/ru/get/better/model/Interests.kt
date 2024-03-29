package ru.get.better.model

import ru.get.better.App
import ru.get.better.R

interface Interest {
    val id: String

    var name: String?
//    val nameRes: Int?

    var description: String?
//    val descriptionRes: Int?

    var startValue: Float?
    var currentValue: Float?

    var logoId: String?

    fun getLogo(): Int
}

class EmptyInterest(
    override val id: String = "",
    override var name: String? = null,
//    override val nameRes: Int? = null,
    override var description: String? = null,
//    override val descriptionRes: Int? = null,
    override var startValue: Float? = null,
    override var currentValue: Float? = null,
    override var logoId: String? = null

) : Interest {
    override fun getLogo(): Int =
        AllLogo().logoList.lastOrNull {
            it.id == id
        }?.resId ?: -1

}

class UserCustomInterest(
    override val id: String,
    override var name: String?,
    override var description: String? = null,

    override var startValue: Float? = null,
    override var currentValue: Float? = null,
    val dateLastUpdate: String? = null,
    override var logoId: String?,
    val order: Int? = null
) : Interest {
    override fun getLogo(): Int = AllLogo().getLogoById(logoId!!)

    private fun getLogoResId(id: String): Int =
        AllLogo().logoList.lastOrNull {
            it.id == id
        }?.resId ?: -1

}

interface DefaultInterest : Interest {
    override val id: String

    override var name: String?
    override var description: String?
    val shortDescription: String

    companion object : DefaultInterest {
        override val id = "0"
        override var name: String? = App.Companion.resourcesProvider.getStringLocale(
            R.string.welcome_title,
            App.preferences.locale
        )
        override var description: String? = App.resourcesProvider.getStringLocale(
            R.string.welcome_description,
            App.preferences.locale
        )
        override val shortDescription =
            App.resourcesProvider.getStringLocale(R.string.welcome_short, App.preferences.locale)

        override var startValue: Float? = 5f
        override var currentValue: Float? = 5f

        override var logoId: String? = "0"
//        override var name: String? = null
//        override var description: String? = null

        override fun getLogo(): Int = AllLogo().getLogoById(logoId!!)

        fun getInterestById(id: Int): DefaultInterest {
            when (id) {
                1 -> return Work()
                2 -> return Spirit()
                3 -> return Chill()
                4 -> return Relationship()
                5 -> return Health()
                6 -> return Finance()
                7 -> return Environment()
                8 -> return Creation()
            }
            return this
        }
    }
}

class Work : DefaultInterest {
    override val id = "1"
    override var name: String? =
        App.resourcesProvider.getStringLocale(R.string.work_title, App.preferences.locale)
    override var description: String? =
        App.resourcesProvider.getStringLocale(R.string.work_description, App.preferences.locale)
    override val shortDescription =
        App.resourcesProvider.getStringLocale(R.string.work_short, App.preferences.locale)

    override var currentValue: Float? = 5f
    override var startValue: Float? = 5f

//    override var name: String? = null
//    override var description: String? = null

    override var logoId: String? = id

    override fun getLogo(): Int = AllLogo().getLogoById(logoId!!)
}

class Spirit : DefaultInterest {
    override val id = "2"
    override var name: String? =
        App.resourcesProvider.getStringLocale(R.string.spirit_title, App.preferences.locale)
    override var description: String? =
        App.resourcesProvider.getStringLocale(R.string.spirit_description, App.preferences.locale)
    override val shortDescription =
        App.resourcesProvider.getStringLocale(R.string.spirit_short, App.preferences.locale)

    override var currentValue: Float? = 5f
    override var startValue: Float? = 5f

//    override var name: String? = null
//    override var description: String? = null

    override var logoId: String? = id

    override fun getLogo(): Int = AllLogo().getLogoById(logoId!!)
}

class Chill : DefaultInterest {
    override val id = "3"
    override var name: String? =
        App.resourcesProvider.getStringLocale(R.string.chill_title, App.preferences.locale)
    override var description: String? =
        App.resourcesProvider.getStringLocale(R.string.chill_description, App.preferences.locale)
    override val shortDescription =
        App.resourcesProvider.getStringLocale(R.string.chill_short, App.preferences.locale)

    override var currentValue: Float? = 5f
    override var startValue: Float? = 5f

//    override var name: String? = null
//    override var description: String? = null

    override var logoId: String? = id

    override fun getLogo(): Int = AllLogo().getLogoById(logoId!!)
}

class Relationship : DefaultInterest {
    override val id = "4"
    override var name: String? =
        App.resourcesProvider.getStringLocale(R.string.relationship_title, App.preferences.locale)
    override var description: String? = App.resourcesProvider.getStringLocale(
        R.string.relationship_description,
        App.preferences.locale
    )
    override val shortDescription =
        App.resourcesProvider.getStringLocale(R.string.relationship_short, App.preferences.locale)

    override var currentValue: Float? = 5f
    override var startValue: Float? = 5f

//    override var name: String? = null
//    override var description: String? = null

    override var logoId: String? = id

    override fun getLogo(): Int = AllLogo().getLogoById(logoId!!)
}

class Health : DefaultInterest {
    override val id = "5"
    override var name: String? =
        App.resourcesProvider.getStringLocale(R.string.health_title, App.preferences.locale)
    override var description: String? =
        App.resourcesProvider.getStringLocale(R.string.health_description, App.preferences.locale)
    override val shortDescription =
        App.resourcesProvider.getStringLocale(R.string.health_short, App.preferences.locale)

    override var currentValue: Float? = 5f
    override var startValue: Float? = 5f

//    override var name: String? = null
//    override var description: String? = null

    override var logoId: String? = id

    override fun getLogo(): Int = AllLogo().getLogoById(logoId!!)
}

class Finance : DefaultInterest {
    override val id = "6"
    override var name: String? =
        App.resourcesProvider.getStringLocale(R.string.finance_title, App.preferences.locale)
    override var description: String? =
        App.resourcesProvider.getStringLocale(R.string.finance_description, App.preferences.locale)
    override val shortDescription =
        App.resourcesProvider.getStringLocale(R.string.finance_short, App.preferences.locale)

    override var currentValue: Float? = 5f
    override var startValue: Float? = 5f

//    override var name: String? = null
//    override var description: String? = null

    override var logoId: String? = id

    override fun getLogo(): Int = AllLogo().getLogoById(logoId!!)
}

class Environment : DefaultInterest {
    override val id = "7"
    override var name: String? =
        App.resourcesProvider.getStringLocale(R.string.environment_title, App.preferences.locale)
    override var description: String? = App.resourcesProvider.getStringLocale(
        R.string.environment_description,
        App.preferences.locale
    )
    override val shortDescription =
        App.resourcesProvider.getStringLocale(R.string.environment_short, App.preferences.locale)

    override var currentValue: Float? = 5f
    override var startValue: Float? = 5f

//    override var name: String? = null
//    override var description: String? = null

    override var logoId: String? = id

    override fun getLogo(): Int = AllLogo().getLogoById(logoId!!)
}

class Creation : DefaultInterest {
    override val id = "8"
    override var name: String? =
        App.resourcesProvider.getStringLocale(R.string.creation_title, App.preferences.locale)
    override var description: String? =
        App.resourcesProvider.getStringLocale(R.string.creation_description, App.preferences.locale)
    override val shortDescription =
        App.resourcesProvider.getStringLocale(R.string.creation_short, App.preferences.locale)

    override var currentValue: Float? = 5f
    override var startValue: Float? = 5f

//    override var name: String? = null
//    override var description: String? = null

    override var logoId: String? = id

    override fun getLogo(): Int = AllLogo().getLogoById(logoId!!)
}

data class Logo(
    val id: String,
    val resId: Int
)

class AllLogo(
    val logoList: List<Logo> = listOf(
        Logo("1", R.drawable.work_logo),
        Logo("2", R.drawable.spirit_logo),
        Logo("3", R.drawable.chill_logo),
        Logo("4", R.drawable.relationship_logo),
        Logo("5", R.drawable.health_logo),
        Logo("6", R.drawable.finance_logo),
        Logo("7", R.drawable.environment_logo),
        Logo("8", R.drawable.creation_logo),
        Logo("9", R.drawable.ic_emoji_1),
        Logo("10", R.drawable.ic_emoji_2),
        Logo("11", R.drawable.ic_emoji_3),
        Logo("12", R.drawable.ic_emoji_4),
        Logo("13", R.drawable.ic_emoji_5),
        Logo("14", R.drawable.ic_emoji_6),
        Logo("15", R.drawable.ic_emoji_7),
        Logo("16", R.drawable.ic_emoji_8),
        Logo("17", R.drawable.ic_emoji_9),
        Logo("18", R.drawable.ic_emoji_10),
        Logo("19", R.drawable.ic_emoji_11),
        Logo("20", R.drawable.ic_emoji_12),
        Logo("21", R.drawable.ic_emoji_13),
        Logo("22", R.drawable.ic_emoji_14),
        Logo("23", R.drawable.ic_emoji_15),
        Logo("24", R.drawable.ic_emoji_16),
        Logo("25", R.drawable.ic_emoji_17),
        Logo("26", R.drawable.ic_emoji_18),
        Logo("27", R.drawable.ic_emoji_19),
        Logo("28", R.drawable.ic_emoji_20),
        Logo("29", R.drawable.ic_emoji_21),
        Logo("30", R.drawable.ic_emoji_22),
        Logo("31", R.drawable.ic_emoji_23),
        Logo("32", R.drawable.ic_emoji_24),
        Logo("33", R.drawable.ic_emoji_25),
        Logo("34", R.drawable.ic_emoji_26),
        Logo("35", R.drawable.ic_emoji_27),
        Logo("36", R.drawable.ic_emoji_28),
        Logo("37", R.drawable.ic_emoji_29),
        Logo("38", R.drawable.ic_emoji_30),
        Logo("39", R.drawable.ic_emoji_31),
        Logo("40", R.drawable.ic_emoji_32),
        Logo("41", R.drawable.ic_emoji_33),
    )
) {
    fun getIndexById(id: String): Int {
        var index = 0

        for (i in logoList.indices) {
            if (logoList[i].id == id)
                index = i
        }

        return index
    }

    fun getRandomLogo() = logoList.random().resId

    fun getLogoById(id: String) =
        logoList.firstOrNull { it.id == id }!!.resId
}
