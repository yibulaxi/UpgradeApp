package com.velkonost.upgrade.model

import com.velkonost.upgrade.R

interface Interest {
    val id: Int

     val nameRes: Int
     val descriptionRes: Int

     var selectedValue: Float

     companion object : Interest {
         override val id = 0
         override val nameRes = R.string.welcome_title
         override val descriptionRes = R.string.welcome_description

         override var selectedValue: Float = 5f
     }
}

 class Work : Interest {
     override val id = 1
     override val nameRes = R.string.work_title
     override val descriptionRes = R.string.work_description

     override var selectedValue: Float = 5f
}

 class Spirit : Interest {
     override val id = 2
     override val nameRes = R.string.spirit_title
     override val descriptionRes = R.string.spirit_description

     override var selectedValue: Float = 5f
}

 class Chill : Interest {
     override val id = 3
     override val nameRes = R.string.chill_title
     override val descriptionRes = R.string.chill_description

     override var selectedValue: Float = 5f
}

 class Relationship : Interest {
     override val id = 4
     override val nameRes = R.string.relationship_title
     override val descriptionRes = R.string.relationship_description

     override var selectedValue: Float = 5f
}

 class Health : Interest {
     override val id = 5
     override val nameRes = R.string.health_title
     override val descriptionRes = R.string.health_description

     override var selectedValue: Float = 5f
}

 class Finance : Interest {
     override val id = 6
     override val nameRes = R.string.finance_title
     override val descriptionRes = R.string.finance_description

     override var selectedValue: Float = 5f
}

 class Environment : Interest {
     override val id = 7
     override val nameRes = R.string.environment_title
     override val descriptionRes = R.string.environment_description

     override var selectedValue: Float = 5f
}

 class Creation : Interest {
     override val id = 8
     override val nameRes = R.string.creation_title
     override val descriptionRes = R.string.creation_description

     override var selectedValue: Float = 5f
}