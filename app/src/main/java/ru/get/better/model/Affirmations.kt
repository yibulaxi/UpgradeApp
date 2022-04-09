package ru.get.better.model

import android.content.Context
import ru.get.better.App
import ru.get.better.R

data class Affirmation(
    val id: Int,
    val title: String,
    val desc: String? = null
)

fun Context.getTodayAffirmation() =
    getAffirmations().first { it.id == App.preferences.currentAffirmationNumber }

fun getAffirmations(): List<Affirmation> {
    return listOf<Affirmation>(
        Affirmation(
            id = 0,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_0),
            desc = App.resourcesProvider.getStringLocale(R.string.affirmation_desc_0)
        ),
        Affirmation(
            id = 1,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_1),
            desc = App.resourcesProvider.getStringLocale(R.string.affirmation_desc_1)
        ),
        Affirmation(
            id = 2,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_2),
        ),
        Affirmation(
            id = 3,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_3),
        ),
        Affirmation(
            id = 4,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_4),
            desc = App.resourcesProvider.getStringLocale(R.string.affirmation_desc_4)
        ),
        Affirmation(
            id = 5,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_5)
        ),
        Affirmation(
            id = 6,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_6),
            desc = App.resourcesProvider.getStringLocale(R.string.affirmation_desc_6)
        ),
        Affirmation(
            id = 7,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_7)
        ),
        Affirmation(
            id = 8,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_8),
        ),
        Affirmation(
            id = 9,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_9),
            desc = App.resourcesProvider.getStringLocale(R.string.affirmation_desc_9)
        ),
        Affirmation(
            id = 10,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_10)
        ),
        Affirmation(
            id = 11,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_11),
            desc = App.resourcesProvider.getStringLocale(R.string.affirmation_desc_11)
        ),
        Affirmation(
            id = 12,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_12)
        ),
        Affirmation(
            id = 13,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_13),
            desc = App.resourcesProvider.getStringLocale(R.string.affirmation_desc_13)
        ),
        Affirmation(
            id = 14,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_14)
        ),
        Affirmation(
            id = 15,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_15),
            desc = App.resourcesProvider.getStringLocale(R.string.affirmation_desc_15)
        ),
        Affirmation(
            id = 16,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_16)
        ),
        Affirmation(
            id = 17,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_17)
        ),
        Affirmation(
            id = 18,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_18),
            desc = App.resourcesProvider.getStringLocale(R.string.affirmation_desc_18)
        ),
        Affirmation(
            id = 19,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_19)
        ),
        Affirmation(
            id = 20,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_20),
            desc = App.resourcesProvider.getStringLocale(R.string.affirmation_desc_20)
        ),
        Affirmation(
            id = 21,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_21)
        ),
        Affirmation(
            id = 22,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_22),
            desc = App.resourcesProvider.getStringLocale(R.string.affirmation_desc_22)
        ),
        Affirmation(
            id = 23,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_23)
        ),
        Affirmation(
            id = 24,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_24),
            desc = App.resourcesProvider.getStringLocale(R.string.affirmation_desc_24)
        ),
        Affirmation(
            id = 25,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_25)
        ),
        Affirmation(
            id = 26,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_26),
            desc = App.resourcesProvider.getStringLocale(R.string.affirmation_desc_26)
        ),
        Affirmation(
            id = 27,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_27)
        ),
        Affirmation(
            id = 28,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_28),
            desc = App.resourcesProvider.getStringLocale(R.string.affirmation_desc_28)
        ),
        Affirmation(
            id = 29,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_29)
        ),
        Affirmation(
            id = 30,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_30),
            desc = App.resourcesProvider.getStringLocale(R.string.affirmation_desc_30)
        ),
        Affirmation(
            id = 31,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_31)
        ),
        Affirmation(
            id = 32,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_32),
            desc = App.resourcesProvider.getStringLocale(R.string.affirmation_desc_32)
        ),
        Affirmation(
            id = 33,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_33)
        ),
        Affirmation(
            id = 34,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_34),
            desc = App.resourcesProvider.getStringLocale(R.string.affirmation_desc_34)
        ),
        Affirmation(
            id = 35,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_35)
        ),
        Affirmation(
            id = 36,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_36),
            desc = App.resourcesProvider.getStringLocale(R.string.affirmation_desc_36)
        ),
        Affirmation(
            id = 37,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_37)
        ),
        Affirmation(
            id = 38,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_38),
            desc = App.resourcesProvider.getStringLocale(R.string.affirmation_desc_38)
        ),
        Affirmation(
            id = 39,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_39)
        ),
        Affirmation(
            id = 40,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_40),
            desc = App.resourcesProvider.getStringLocale(R.string.affirmation_desc_40)
        ),
        Affirmation(
            id = 41,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_41)
        ),
        Affirmation(
            id = 42,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_42),
            desc = App.resourcesProvider.getStringLocale(R.string.affirmation_desc_42)
        ),
        Affirmation(
            id = 43,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_43)
        ),
        Affirmation(
            id = 44,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_44),
            desc = App.resourcesProvider.getStringLocale(R.string.affirmation_desc_44)
        ),
        Affirmation(
            id = 45,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_45)
        ),
        Affirmation(
            id = 46,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_46),
            desc = App.resourcesProvider.getStringLocale(R.string.affirmation_desc_46)
        ),
        Affirmation(
            id = 47,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_47)
        ),
        Affirmation(
            id = 48,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_48),
            desc = App.resourcesProvider.getStringLocale(R.string.affirmation_desc_48)
        ),
        Affirmation(
            id = 49,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_49)
        ),
        Affirmation(
            id = 50,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_50),
            desc = App.resourcesProvider.getStringLocale(R.string.affirmation_desc_50)
        ),
        Affirmation(
            id = 51,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_51)
        ),
        Affirmation(
            id = 52,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_52),
            desc = App.resourcesProvider.getStringLocale(R.string.affirmation_desc_52)
        ),
        Affirmation(
            id = 53,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_53)
        ),
        Affirmation(
            id = 54,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_54),
            desc = App.resourcesProvider.getStringLocale(R.string.affirmation_desc_54)
        ),
        Affirmation(
            id = 55,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_55)
        ),
        Affirmation(
            id = 56,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_56),
            desc = App.resourcesProvider.getStringLocale(R.string.affirmation_desc_56)
        ),
        Affirmation(
            id = 57,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_57)
        ),
        Affirmation(
            id = 58,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_58),
            desc = App.resourcesProvider.getStringLocale(R.string.affirmation_desc_58)
        ),
        Affirmation(
            id = 59,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_59)
        ),
        Affirmation(
            id = 60,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_60),
            desc = App.resourcesProvider.getStringLocale(R.string.affirmation_desc_60)
        ),
        Affirmation(
            id = 61,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_61)
        ),
        Affirmation(
            id = 62,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_62),
            desc = App.resourcesProvider.getStringLocale(R.string.affirmation_desc_62)
        ),
        Affirmation(
            id = 63,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_63),
        ),
        Affirmation(
            id = 64,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_64),
            desc = App.resourcesProvider.getStringLocale(R.string.affirmation_desc_64)
        ),
        Affirmation(
            id = 65,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_65)
        ),
        Affirmation(
            id = 66,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_66),
            desc = App.resourcesProvider.getStringLocale(R.string.affirmation_desc_66)
        ),
        Affirmation(
            id = 67,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_67),
        ),
        Affirmation(
            id = 68,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_68),
            desc = App.resourcesProvider.getStringLocale(R.string.affirmation_desc_68)
        ),
        Affirmation(
            id = 69,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_69)
        ),
        Affirmation(
            id = 70,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_70),
            desc = App.resourcesProvider.getStringLocale(R.string.affirmation_desc_70)
        ),
        Affirmation(
            id = 71,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_71)
        ),
        Affirmation(
            id = 72,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_72),
            desc = App.resourcesProvider.getStringLocale(R.string.affirmation_desc_72)
        ),
        Affirmation(
            id = 73,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_73)
        ),
        Affirmation(
            id = 74,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_74),
            desc = App.resourcesProvider.getStringLocale(R.string.affirmation_desc_74)
        ),
        Affirmation(
            id = 75,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_75)
        ),
        Affirmation(
            id = 76,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_76),
            desc = App.resourcesProvider.getStringLocale(R.string.affirmation_desc_76)
        ),
        Affirmation(
            id = 77,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_77),
            desc = App.resourcesProvider.getStringLocale(R.string.affirmation_desc_77)
        ),
        Affirmation(
            id = 78,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_78),
            desc = App.resourcesProvider.getStringLocale(R.string.affirmation_desc_78)
        ),
        Affirmation(
            id = 79,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_79),
            desc = App.resourcesProvider.getStringLocale(R.string.affirmation_desc_79)
        ),
        Affirmation(
            id = 80,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_80),
            desc = App.resourcesProvider.getStringLocale(R.string.affirmation_desc_80)
        ),
        Affirmation(
            id = 81,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_81),
            desc = App.resourcesProvider.getStringLocale(R.string.affirmation_desc_81)
        ),
        Affirmation(
            id = 82,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_82),
            desc = App.resourcesProvider.getStringLocale(R.string.affirmation_desc_82)
        ),
        Affirmation(
            id = 83,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_83),
            desc = App.resourcesProvider.getStringLocale(R.string.affirmation_desc_83)
        ),
        Affirmation(
            id = 84,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_84),
            desc = App.resourcesProvider.getStringLocale(R.string.affirmation_desc_84)
        ),
        Affirmation(
            id = 85,
            title = App.resourcesProvider.getStringLocale(R.string.affirmation_title_85),
            desc = App.resourcesProvider.getStringLocale(R.string.affirmation_desc_85)
        ),
    )
}

