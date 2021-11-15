package com.velkonost.upgrade.model

import com.velkonost.upgrade.R

interface Interest {
    val id: Int

    val name: String?
    val nameRes: Int?

    val startValue: Float?
    var currentValue: Float?

}

data class UserCustomInterest(
    override val id: Int,
    override val name: String,
    override val nameRes: Int? = null,
    val description: String? = null,
    override val startValue: Float? = null,
    override var currentValue: Float? = null,
    val dateLastUpdate: String? = null,
    val icon: String,
    val order: Int? = null
): Interest

interface DefaultInterest: Interest {
    override val id: Int

    override val nameRes: Int
    val descriptionRes: Int
    val shortDescriptionRes: Int


    val logo: Int

    companion object : DefaultInterest {
        override val id = 0
        override val nameRes = R.string.welcome_title
        override val descriptionRes = R.string.welcome_description
        override val shortDescriptionRes = R.string.welcome_short

        override val startValue: Float = 5f
        override var currentValue: Float? = 5f

        override val logo = R.drawable.logo
        override val name: String? = null

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
    override val id = 1
    override val nameRes = R.string.work_title
    override val descriptionRes = R.string.work_description
    override val shortDescriptionRes = R.string.work_short

    override var currentValue: Float? = 5f
    override val startValue: Float = 5f

    override val name: String? = null

    override val logo = R.drawable.work_logo
}

class Spirit : DefaultInterest {
    override val id = 2
    override val nameRes = R.string.spirit_title
    override val descriptionRes = R.string.spirit_description
    override val shortDescriptionRes = R.string.spirit_short

    override var currentValue: Float? = 5f
    override val startValue: Float = 5f

    override val name: String? = null

    override val logo = R.drawable.spirit_logo
}

class Chill : DefaultInterest {
    override val id = 3
    override val nameRes = R.string.chill_title
    override val descriptionRes = R.string.chill_description
    override val shortDescriptionRes = R.string.chill_short

    override var currentValue: Float? = 5f
    override val startValue: Float = 5f

    override val name: String? = null

    override val logo = R.drawable.chill_logo
}

class Relationship : DefaultInterest {
    override val id = 4
    override val nameRes = R.string.relationship_title
    override val descriptionRes = R.string.relationship_description
    override val shortDescriptionRes = R.string.relationship_short

    override var currentValue: Float? = 5f
    override val startValue: Float = 5f

    override val name: String? = null

    override val logo = R.drawable.relationship_logo
}

class Health : DefaultInterest {
    override val id = 5
    override val nameRes = R.string.health_title
    override val descriptionRes = R.string.health_description
    override val shortDescriptionRes = R.string.health_short

    override var currentValue: Float? = 5f
    override val startValue: Float = 5f

    override val name: String? = null

    override val logo = R.drawable.health_logo
}

class Finance : DefaultInterest {
    override val id = 6
    override val nameRes = R.string.finance_title
    override val descriptionRes = R.string.finance_description
    override val shortDescriptionRes = R.string.finance_short

    override var currentValue: Float? = 5f
    override val startValue: Float = 5f

    override val name: String? = null

    override val logo = R.drawable.finance_logo
}

class Environment : DefaultInterest {
    override val id = 7
    override val nameRes = R.string.environment_title
    override val descriptionRes = R.string.environment_description
    override val shortDescriptionRes = R.string.environment_short

    override var currentValue: Float? = 5f
    override val startValue: Float = 5f

    override val name: String? = null

    override val logo = R.drawable.environment_logo
}

class Creation : DefaultInterest {
    override val id = 8
    override val nameRes = R.string.creation_title
    override val descriptionRes = R.string.creation_description
    override val shortDescriptionRes = R.string.creation_short

    override var currentValue: Float? = 5f
    override val startValue: Float = 5f

    override val name: String? = null

    override val logo = R.drawable.creation_logo
}